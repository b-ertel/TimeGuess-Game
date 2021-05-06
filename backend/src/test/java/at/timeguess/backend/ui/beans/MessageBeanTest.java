package at.timeguess.backend.ui.beans;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import static javax.faces.application.FacesMessage.SEVERITY_WARN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.function.BiConsumer;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.utils.ContextMocker;

/**
 * Tests for {@link MessageBean}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class MessageBeanTest {

    @InjectMocks
    private MessageBean messageBean;

    @Test
    public void testAlertInformation() {
        assertAlert((h, t) -> messageBean.alertInformation(h, t), SEVERITY_INFO, false);
    }

    @Test
    public void testAlertWarning() {
        assertAlert((h, t) -> messageBean.alertWarning(h, t), SEVERITY_WARN, false);
    }

    @Test
    public void testAlertError() {
        assertAlert((h, t) -> messageBean.alertError(h, t), SEVERITY_ERROR, false);
    }

    @Test
    public void testAlertErrorFailValidation() {
        assertAlert((h, t) -> messageBean.alertErrorFailValidation(h, t), SEVERITY_ERROR, true);
    }

    @Test
    public void testAlertNoContext() {
        MessageBean bean = new MessageBean();
        assertDoesNotThrow(() -> bean.alertInformation("header", "text"));
        assertDoesNotThrow(() -> bean.alertErrorFailValidation("header", "text"));
    }

    private void assertAlert(BiConsumer<String, String> alert, FacesMessage.Severity expected,
            boolean assertValidationFailed) {
        String header = "header", text = "text";
        FacesContext context = ContextMocker.mockFacesContext();
        ArgumentCaptor<FacesMessage> arg = ArgumentCaptor.forClass(FacesMessage.class);

        alert.accept(header, text);

        verify(context).addMessage(nullable(String.class), arg.capture());
        FacesMessage result = arg.getValue();
        assertEquals(expected, result.getSeverity());
        assertEquals(header, result.getSummary());
        assertEquals(text, result.getDetail());

        if (assertValidationFailed) verify(context).validationFailed();
        else verifyNoMoreInteractions(context);
    }
}
