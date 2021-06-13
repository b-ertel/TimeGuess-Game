package at.timeguess.backend.ui.beans;

import static at.timeguess.backend.utils.TestSetup.createTerm;
import static at.timeguess.backend.utils.TestSetup.createTopic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.services.TermService;
import at.timeguess.backend.services.UserService;

/**
 * Tests for {@link NewTermBean}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class NewTermBeanTest {

    @InjectMocks
    private NewTermBean newTermBean;

    @Mock
    private TermService termService;
    @Mock
    private UserService userService;
    @Mock
    private MessageBean messageBean;

    @BeforeEach
    public void beforeEach() {
        newTermBean.clearFields();
    }

    @Test
    public void testClearFields() {
        String expected = fillBean();
        assertFields(expected);

        newTermBean.clearFields();

        assertFieldsClear();
    }

    @Test
    public void testCreateTerm() {
        String name = fillBean();
        Term expected = createTerm(6L);
        expected.setName(name);
        when(termService.saveTerm(any(Term.class))).thenReturn(expected);

        Term result = newTermBean.createTerm();

        verify(termService).saveTerm(any(Term.class));
        verifyNoInteractions(messageBean);
        assertEquals(expected, result);
        assertFieldsClear();
    }

    @Test
    public void testCreateTermFailure() {
        newTermBean.createTerm();

        verifyNoInteractions(termService);
        verify(messageBean).alertErrorFailValidation(anyString(), anyString());
    }

    @Test
    public void testCreateTermFailureSave() {
        String expected = fillBean();
        when(termService.saveTerm(any(Term.class))).thenReturn(null);

        Term result = newTermBean.createTerm();

        verify(termService).saveTerm(any(Term.class));
        verifyNoInteractions(messageBean);
        assertNull(result);
        assertFields(expected);
    }

    @Test
    public void testValidateInput() {
        newTermBean.setTermName(null);
        assertFalse(newTermBean.validateInput());
        newTermBean.setTermName("");
        assertFalse(newTermBean.validateInput());
        newTermBean.setTermName("aterm");
        assertFalse(newTermBean.validateInput());

        Topic topic = null;
        newTermBean.setTopic(topic);
        assertFalse(newTermBean.validateInput());
        topic = new Topic();
        newTermBean.setTopic(topic);
        assertFalse(newTermBean.validateInput());
        topic = createTopic(0L);
        newTermBean.setTopic(topic);
        assertFalse(newTermBean.validateInput());
        topic = createTopic(5L);
        newTermBean.setTopic(topic);
        assertTrue(newTermBean.validateInput());

        verifyNoInteractions(termService);
    }

    private String fillBean() {
        String foo = "foobarbat";

        newTermBean.setTermName(foo);
        newTermBean.setTopic(createTopic(2L));
        return foo;
    }

    private void assertFields(String expected) {
        assertEquals(expected, newTermBean.getTermName());
        assertNotNull(newTermBean.getTopic());
    }

    private void assertFieldsClear() {
        assertNull(newTermBean.getTermName());
        assertNull(newTermBean.getTopic());
    }
}
