package at.timeguess.backend.services;

import static at.timeguess.backend.utils.TestSetup.createEntities;
import static at.timeguess.backend.utils.TestSetup.createTerm;
import static at.timeguess.backend.utils.TestSetup.createTopic;
import static at.timeguess.backend.utils.TestUtils.assertLists;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.utils.TestSetup;

/**
 * Some very basic tests for {@link TermService}.
 */
@SpringBootTest
@WebAppConfiguration
public class TermServiceTest {

    @Autowired
    private TermService termService;

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "1|AFRICA", "2|MOUNTAIN", "3|LAKE", "4|RIVER", "5|MEXICO",
            "6|THE LORD OF THE RINGS", "7|MATRIX", "8|TITANIC", "9|INCEPTION", "10|FIGHT CLUB", "11|C3PO",
            "12|CHEWBACCA", "13|DEATHSTAR", "14|DARTH VADER", "15|R2D2", "16|HAMBURGER", "17|HOT DOG", "18|TIRAMISU",
            "19|LASAGNE", "20|PIZZA QUATTRO FORMAGGI" })
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void testLoadTerm(long termId, String termNameExpected) {
        Term result = assertLoadTerm(termId, true, "Term '%s' could not be loaded from test data source");
        assertEquals(termNameExpected, result.getName());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "MANAGER" })
    public void testGetAllTerms() {
        List<Term> expected = createEntities(TestSetup::createTerm, LongStream.rangeClosed(1, 20).boxed());
        List<Term> result = termService.getAllTerms();

        assertLists(expected, result);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "1|1;2;3;4;5", "2|6;7;8;9;10", "3|11;12;13;14;15", "4|16;17;18;19;20" })
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void getAllTermsOfTopic(long topicId, final String termIdsExpected) {
        List<Term> expected = createEntities(TestSetup::createTerm, termIdsExpected);
        List<Term> result = termService.getAllTermsOfTopic(createTopic(topicId));

        assertLists(expected, result);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void testSaveTerm() {
        Term term = new Term();
        term.setName("TestTerm1");
        assertNull(term.getId());

        term.setId(0L);
        assertTrue(term.isNew());
        term.setId(null);
        assertTrue(term.isNew());

        term.setTopic(createTopic(2L));
        Term t0 = termService.saveTerm(term);
        assertNotNull(t0.getId());

        t0.setName("TestTerm2");
        Term t1 = termService.saveTerm(term);
        assertEquals(t1.getName(), "TestTerm2");
        assertEquals(t0.toString(), t1.toString());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "MANAGER" })
    @DirtiesContext
    public void testSaveTermWithEmptyName() {
        assertDoesNotThrow(() -> {
            Term toBeCreatedTerm = new Term();
            termService.saveTerm(toBeCreatedTerm);
        });
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "MANAGER" })
    @DirtiesContext
    public void testSaveTermWithExistingName() {
        assertDoesNotThrow(() -> {
            Term term = assertLoadTerm(1, true, "Term '%s' could not be loaded from test data source");
            term.setName("LAKE");
            termService.saveTerm(term);
        });
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "MANAGER" })
    public void testDeleteTerm() {
        long termId = 16;
        int ctBefore = termService.getAllTerms().size();
        Term term = assertLoadTerm(termId, true, "Term '%s' could not be loaded from test data source");

        termService.deleteTerm(term);

        assertEquals(ctBefore - 1, termService.getAllTerms().size(), "No term has been deleted after calling termService.deleteTerm");
        assertThrows(NoSuchElementException.class, () -> termService.loadTerm(termId));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "MANAGER" })
    public void testDeleteFailureNull() {
        assertDoesNotThrow(() -> {
            termService.deleteTerm(null);
        }, "Deletion failure should not throw an exception, but does");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "MANAGER" })
    public void testDeleteFailureConstraint() {
        assertDoesNotThrow(() -> {
            termService.deleteTerm(createTerm(1L));
        }, "Deletion failure should not throw an exception, but does");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "PLAYER" })
    public void testDeleteTermUnauthorized() {
        assertThrows(AccessDeniedException.class, () -> {
            Term term = assertLoadTerm(1L, true, "Term '%s' could not be loaded from test data source");
            termService.deleteTerm(term);
        }, "PLAYER should not be able to delete a term, but was");
    }

    private Term assertLoadTerm(long termId, boolean shouldExist, String msgFormat) {
        Term term = termService.loadTerm(termId);
        if (shouldExist) assertNotNull(term, String.format(msgFormat, String.valueOf(termId)));
        else assertNull(term, String.format(msgFormat, term));
        return term;
    }
}