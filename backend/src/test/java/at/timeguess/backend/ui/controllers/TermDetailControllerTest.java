package at.timeguess.backend.ui.controllers;

import static at.timeguess.backend.utils.TestSetup.createTerm;
import static at.timeguess.backend.utils.TestSetup.createTopic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.services.TeamService;
import at.timeguess.backend.services.TermService;
import at.timeguess.backend.ui.beans.MessageBean;

/**
 * Tests for {@link TermDetailController}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class TermDetailControllerTest {

    @InjectMocks
    private TermDetailController termDetailController;

    @Mock
    private TermService termService;
    @Mock
    private TeamService teamService;
    @Mock
    private MessageBean messageBean;

    @ParameterizedTest
    @ValueSource(longs = { 5, 15 })
    public void testDoReloadTerm(Long termId) {
        assertMockTerm(termId);

        termDetailController.doReloadTerm();

        verify(termService, times(2)).loadTerm(termId);
        assertEquals(termId, termDetailController.getTerm().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 5, 15 })
    public void testDoSaveTerm(Long termId) {
        Term term = assertMockTerm(termId, true, false);
        when(termService.saveTerm(term)).thenReturn(term);

        termDetailController.doSaveTerm();

        verify(termService).saveTerm(term);
        assertEquals(termId, termDetailController.getTerm().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testDoSaveTermFailure(Long termId) {
        Term term = assertMockTerm(termId, true, false);
        when(termService.saveTerm(term)).thenReturn(null);

        termDetailController.doSaveTerm();

        verify(termService).saveTerm(term);
        verifyNoInteractions(messageBean);
        assertEquals(term, termDetailController.getTerm());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testDoSaveTermInvalid(Long termId) {
        assertMockTerm(termId);
        reset(termService);

        termDetailController.doSaveTerm();

        verifyNoInteractions(termService);
        verify(messageBean).alertErrorFailValidation(anyString(), anyString());
        assertEquals(termId, termDetailController.getTerm().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 5, 15 })
    public void testDoSaveTermEnabled(Long termId) {
        Term term = assertMockTerm(termId, true, false);

        termDetailController.doSaveTerm(term);

        verify(termService).saveTerm(term);
        verify(messageBean).alertInformation(anyString(), anyString());
    }

    @ParameterizedTest
    @ValueSource(longs = { 5, 15 })
    public void testDoSaveTermDisabled(Long termId) {
        Term term = assertMockTerm(termId);

        termDetailController.doSaveTerm(term);

        verify(termService).saveTerm(term);
        verify(messageBean).alertInformation(anyString(), anyString());
    }

    @ParameterizedTest
    @ValueSource(longs = { 1, 2, 3, 40, 444 })
    public void testDoDeleteTerm(Long termId) {
        Term term = assertMockTerm(termId);

        termDetailController.doDeleteTerm();

        verify(termService).deleteTerm(term);
        assertNull(termDetailController.getTerm());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testDoValidateTerm(Long termId) {
        Term term = assertMockTermAndReset(termId);

        term.setName(null);
        assertFalse(termDetailController.doValidateTerm());
        term.setName("");
        assertFalse(termDetailController.doValidateTerm());
        term.setName("aterm");
        assertFalse(termDetailController.doValidateTerm());
        Topic topic = null;
        term.setTopic(topic);
        assertFalse(termDetailController.doValidateTerm());
        topic = createTopic(0L);
        term.setTopic(topic);
        assertFalse(termDetailController.doValidateTerm());
        topic = createTopic(5L);
        term.setTopic(topic);
        assertTrue(termDetailController.doValidateTerm());

        verifyNoInteractions(termService);
    }

    private Term assertMockTermAndReset(Long termId) {
        return assertMockTerm(termId, false, true);
    }

    private Term assertMockTerm(Long termId) {
        return assertMockTerm(termId, false, false);
    }

    private Term assertMockTerm(Long termId, boolean full, boolean resetService) {
        Term term = createTerm(termId);
        if (full) {
            term.setName("aterm");
            term.setTopic(createTopic(15L));
            term.setEnabled(true);
        }
        when(termService.loadTerm(termId)).thenReturn(term);

        termDetailController.setTerm(term);

        verify(termService).loadTerm(termId);
        assertEquals(termId, termDetailController.getTerm().getId());
        if (resetService) reset(termService);

        return term;
    }
}
