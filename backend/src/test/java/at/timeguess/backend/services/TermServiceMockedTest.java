package at.timeguess.backend.services;

import static at.timeguess.backend.utils.TestSetup.createTerm;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.omnifaces.cdi.PushContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.repositories.TermRepository;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;

/**
 * Some mock tests for {@link TermService}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class TermServiceMockedTest {

    @InjectMocks
    private TermService termService;

    @Mock
    private TermRepository termRepository;
    @Mock
    private MessageBean messageBean;
    @Mock
    private WebSocketManager websocketManager;

    @Test
    public void testSaveTerm() {
        Term term = createTerm(5L);
        term.setName("aname");
        PushContext context = mock(PushContext.class);
        when(termRepository.save(term)).thenReturn(term);
        when(websocketManager.getUserRegistrationChannel()).thenReturn(context);

        assertDoesNotThrow(() -> termService.saveTerm(term));

        verify(termRepository).save(term);
        verify(messageBean).alertInformation(nullable(String.class), anyString());
        verify(context).send(anyMap());
    }

    @Test
    public void testInfoOnSaveSuccess() {
        Term term = createTerm(5L);
        term.setName("aname");
        PushContext context = mock(PushContext.class);
        when(termRepository.save(term)).thenReturn(term);
        when(websocketManager.getUserRegistrationChannel()).thenReturn(context);

        termService.setInfoOnSave(false);
        assertDoesNotThrow(() -> termService.saveTerm(term));

        verify(termRepository).save(term);
        verifyNoInteractions(messageBean);
        verifyNoInteractions(context);

        termService.setInfoOnSave(true);
        verify(messageBean).alertInformation(nullable(String.class), anyString());
        verify(context).send(anyMap());
    }

    @Test
    public void testInfoOnSaveNoSuccess() {
        Term term = createTerm(5L);
        PushContext context = mock(PushContext.class);
        when(termRepository.save(term)).thenThrow(RuntimeException.class);
        when(websocketManager.getUserRegistrationChannel()).thenReturn(context);

        termService.setInfoOnSave(false);
        assertThrows(NullPointerException.class, () -> termService.saveTerm(term));
        termService.setInfoOnSave(true);

        verifyNoInteractions(messageBean);
        verifyNoInteractions(context);
    }

    @Test
    public void testDeleteTerm() {
        Term term = createTerm(5L);
        term.setName("aname");
        PushContext context = mock(PushContext.class);
        when(websocketManager.getUserRegistrationChannel()).thenReturn(context);

        assertDoesNotThrow(() -> termService.deleteTerm(term));

        verify(termRepository).delete(term);
        verify(messageBean).alertInformation(nullable(String.class), anyString());
        verify(context).send(anyMap());
    }
}
