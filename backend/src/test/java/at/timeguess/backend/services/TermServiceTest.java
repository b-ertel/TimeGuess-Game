package at.timeguess.backend.services;


import at.timeguess.backend.model.Term;

import at.timeguess.backend.model.exceptions.TermAlreadyExistsException;
import org.hibernate.mapping.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Some very basic tests for {@link TermService}.
 */
@SpringBootTest
@WebAppConfiguration
public class TermServiceTest {
    @Autowired
    TermService termService;

    @Autowired
    TopicService topicService;

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER", "PLAYER" })
    public void testDatainitialization(){
        Assertions.assertTrue( termService.getAllTerms().size() >= 19, "Insufficient amount of terms initialized for test data source");
        for (Term term : termService.getAllTerms()) {
            if (term.getId() == 1){
                Assertions.assertEquals(  "AFRICA", term.getName(),"Term \"1\" does not have the initialized name");
                Assertions.assertEquals(  topicService.loadTopicId(1L), term.getTopic(),"Term \"1\" is not in topic \"1\" as initialized");
            }
            if (term.getId() == 2){
                Assertions.assertEquals(  "MOUNTAIN", term.getName(),"Term \"2\" does not have the initialized name");
                Assertions.assertEquals(  topicService.loadTopicId(1L), term.getTopic(),"Term \"2\" is not in topic \"1\" as initialized");
            }
            if (term.getId() == 6){
                Assertions.assertEquals(  "THE LORD OF THE RINGS", term.getName(),"Term \"6\" does not have the initialized name");
                Assertions.assertEquals(  topicService.loadTopicId(2L), term.getTopic(),"Term \"6\" is not in topic \"1\" as initialized");
            }
        }
    }


    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void canFindTerm() {
        for (long id = 1; id < 5; id++){
        Term term;
        term = termService.loadTerm(id);
        Assertions.assertNotNull(term, "Term \"" + id + "\" could not be loaded from test data source");
        }
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void canSaveAndLoadTerm() throws TermAlreadyExistsException {
        for (long id = 0; id < 5; id++){
            Term term = new Term();
            term.setTopic(topicService.loadTopicId(1L));
            term.setName("TEST");
            termService.saveTerm(term);

            Assertions.assertEquals(term, termService.loadTerm(term.getId()));
        }
    }




}
