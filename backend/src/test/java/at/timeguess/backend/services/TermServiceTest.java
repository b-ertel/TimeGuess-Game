package at.timeguess.backend.services;

import at.timeguess.backend.model.Term;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    public void testDatainitialization(){
        Assertions.assertTrue( termService.getAllTerms().size() >= 19, "Insufficient amount of terms initialized for test data source");
        for (Term term : termService.getAllTerms()) {
            if (term.getId() == 1){
                Assertions.assertEquals(  "AFRICA", term.getName(),"Term \"1\" does not have the initialized name");
                Assertions.assertEquals(  topicService.getAllTopics().get(1), term.getTopic(),"Term \"1\" is not in topic \"1\" as initialized");
            }
        }
    }

}
