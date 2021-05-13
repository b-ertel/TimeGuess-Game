package at.timeguess.backend.ui.controllers;

import static at.timeguess.backend.utils.TestSetup.createTopic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Topic;
import at.timeguess.backend.services.TopicService;
import at.timeguess.backend.services.TopicStatisticService;
import at.timeguess.backend.ui.beans.MessageBean;

/**
 * Tests for {@link TopicDetailController}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class TopicDetailControllerTest {

    @InjectMocks
    private TopicDetailController topicDetailController;

    @Mock
    private TopicService topicService;
    @Mock
    private TopicStatisticService topicStatisticService;
    @Mock
    private MessageBean messageBean;

    @ParameterizedTest
    @ValueSource(longs = { 5, 15 })
    public void testDoReloadTopic(Long topicId) {
        assertMockTopic(topicId);

        topicDetailController.doReloadTopic();

        verify(topicService, times(2)).loadTopicId(topicId);
        assertEquals(topicId, topicDetailController.getTopic().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 5, 15 })
    public void testDoSaveTopicEnabled(Long topicId) {
        Topic topic = assertMockTopic(topicId, true, false);

        topicDetailController.doSaveTopic(topic);

        verify(topicService).saveTopic(topic);
        verify(messageBean).alertInformation(anyString(), anyString());
    }

    @ParameterizedTest
    @ValueSource(longs = { 5, 15 })
    public void testDoSaveTopicDisabled(Long topicId) {
        Topic topic = assertMockTopic(topicId);

        topicDetailController.doSaveTopic(topic);

        verify(topicService).saveTopic(topic);
        verify(messageBean).alertInformation(anyString(), anyString());
    }

    @ParameterizedTest
    @ValueSource(longs = { 5, 15 })
    public void testGetNumberOfTerms(Long topicId) {
        Topic topic = assertMockTopic(topicId);
        Integer expected = topicId.intValue();
        when(topicStatisticService.getNrOfTermsPerTopic(topic)).thenReturn(expected);

        assertEquals(expected, topicDetailController.getNumberOfTerms(topic));

        verify(topicStatisticService).getNrOfTermsPerTopic(topic);
    }

    private Topic assertMockTopic(Long topicId) {
        return assertMockTopic(topicId, false, false);
    }

    private Topic assertMockTopic(Long topicId, boolean full, boolean resetService) {
        Topic topic = createTopic(topicId);
        if (full) {
            topic.setName("atopic");
            topic.setEnabled(true);
        }
        else topic.setEnabled(false);

        when(topicService.loadTopicId(topicId)).thenReturn(topic);

        topicDetailController.setTopic(topic);

        verify(topicService).loadTopicId(topicId);
        assertEquals(topicId, topicDetailController.getTopic().getId());
        if (resetService) reset(topicService);

        return topic;
    }
}
