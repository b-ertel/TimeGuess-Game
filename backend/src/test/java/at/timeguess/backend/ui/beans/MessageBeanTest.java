package at.timeguess.backend.ui.beans;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import static javax.faces.application.FacesMessage.SEVERITY_WARN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

    private static FacesContext facesContext;

    @BeforeAll
    public static void setup() {
        facesContext = ContextMocker.mockFacesContext();
    }

    @AfterAll
    public static void release() {
        facesContext.release();
        facesContext = null;
    }

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
    public void testAlertErrorList() {
        assertAlertErrorList((h, ts) -> messageBean.alertError(h, ts), true);
    }

    @Test
    public void testAlertNoContext() {
        MessageBean bean = new MessageBean();
        assertDoesNotThrow(() -> bean.alertInformation("header", "text"));
        assertDoesNotThrow(() -> bean.alertErrorFailValidation("header", "text"));
    }

    @Test
    public void testRedirect() {
        assertRedirect(false);
    }

    @Test
    public void testRedirectFailure() {
        assertRedirect(true);
    }

    private void assertRedirect(boolean success) {
        assertDoesNotThrow(() -> {
            String expected = "somewhere";
            ExternalContext external = mock(ExternalContext.class);
            when(facesContext.getExternalContext()).thenReturn(external);

            if (success) doNothing().when(external).redirect(expected);
            else doThrow(IOException.class).when(external).redirect(expected);

            messageBean.redirect(expected);

            verify(facesContext).getExternalContext();
            verify(external).redirect(expected);
        });
    }

    private void assertAlert(BiConsumer<String, String> alert, FacesMessage.Severity expected,
        boolean assertValidationFailed) {
        String header = "header", text = "text";
        reset(facesContext);
        ArgumentCaptor<FacesMessage> arg = ArgumentCaptor.forClass(FacesMessage.class);

        alert.accept(header, text);

        verify(facesContext).addMessage(nullable(String.class), arg.capture());
        FacesMessage result = arg.getValue();
        assertEquals(expected, result.getSeverity());
        assertEquals(header, result.getSummary());
        assertEquals(text, result.getDetail());

        if (assertValidationFailed) verify(facesContext).validationFailed();
        else verifyNoMoreInteractions(facesContext);
    }

    private void assertAlertErrorList(BiConsumer<String, List<String>> alert, boolean assertValidationFailed) {
        String header = "header", text = "text";
        List<String> texts = List.of(text, text, text);
        reset(facesContext);
        ArgumentCaptor<FacesMessage> arg = ArgumentCaptor.forClass(FacesMessage.class);

        alert.accept(header, texts);

        verify(facesContext, times(texts.size())).addMessage(nullable(String.class), arg.capture());
        FacesMessage result = arg.getValue();
        assertEquals(SEVERITY_ERROR, result.getSeverity());
        assertEquals(header, result.getSummary());
        assertEquals(text, result.getDetail());

        if (assertValidationFailed) verify(facesContext, times(texts.size())).validationFailed();
        else verifyNoMoreInteractions(facesContext);
    }
}
