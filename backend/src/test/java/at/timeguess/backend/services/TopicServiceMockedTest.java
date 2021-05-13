package at.timeguess.backend.services;

import static at.timeguess.backend.utils.TestSetup.createTopic;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.omnifaces.cdi.PushContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Topic;
import at.timeguess.backend.repositories.TopicRepository;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;

/**
 * Some mock tests for {@link TopicService}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class TopicServiceMockedTest {

    @InjectMocks
    private TopicService topicService;

    @Mock
    private TopicRepository topicRepository;
    @Mock
    private MessageBean messageBean;
    @Mock
    private WebSocketManager websocketManager;

    @Test
    public void testSaveTopic() {
        Topic topic = createTopic(5L);
        topic.setName("aname");
        PushContext context = mock(PushContext.class);
        when(topicRepository.save(topic)).thenReturn(topic);
        when(websocketManager.getUserRegistrationChannel()).thenReturn(context);

        assertDoesNotThrow(() -> topicService.saveTopic(topic));

        verify(topicRepository).save(topic);
        verify(messageBean).alertInformation(nullable(String.class), anyString());
        verify(context).send(anyMap());
    }
}