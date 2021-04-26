package at.timeguess.backend.services;

import org.junit.Assert;
import org.junit.Test;
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

    @Test
    public void testDataInitialization(){
        Assert.assertEquals("Insufficient amount of terms initialized for test data source", 20, termService.getAllTerms().size());
    }

}
