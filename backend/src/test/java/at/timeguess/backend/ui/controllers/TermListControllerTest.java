package at.timeguess.backend.ui.controllers;

import static at.timeguess.backend.utils.TestSetup.createEntities;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.services.TermService;
import at.timeguess.backend.utils.TestSetup;

/**
 * Tests for {@link TermListController}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class TermListControllerTest {

    @InjectMocks
    private TermListController termListController;

    @Mock
    private TermService termService;

    private static List<Term> expected;

    @BeforeAll
    private static void setup() {
        expected = createEntities(TestSetup::createTerm, 10);
    }

    @Test
    public void testGetTerms() {
        when(termService.getAllTerms()).thenReturn(expected);

        List<Term> result = termListController.getTerms();

        verify(termService).getAllTerms();
        assertEquals(expected, result);
    }

    @Test
    public void testFilterTerms() {
        when(termService.getAllTerms()).thenReturn(expected);

        // test setting filter to null
        termListController.setFilterTerms(null);
        Collection<Term> result = termListController.getFilterTerms();

        verify(termService).getAllTerms();
        assertEquals(expected, result);

        // test setting filter to list
        reset(termService);
        termListController.setFilterTerms(expected);
        result = termListController.getFilterTerms();

        verifyNoInteractions(termService);
        assertTrue(expected == result);
    }

    @Test
    public void testSelectedTerms() {
        Term term = expected.get(5);
        Term result = termListController.getSelectedTerm();
        assertNotEquals(term, result);

        termListController.setSelectedTerm(null);
        assertNull(termListController.getSelectedTerm());

        termListController.setSelectedTerm(term);
        assertTrue(term == termListController.getSelectedTerm());
    }
}
