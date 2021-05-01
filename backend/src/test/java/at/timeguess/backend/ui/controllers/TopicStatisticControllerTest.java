package at.timeguess.backend.ui.controllers;

import static at.timeguess.backend.utils.TestSetup.createEntities;
import static at.timeguess.backend.utils.TestSetup.createTopic;
import static at.timeguess.backend.utils.TestSetup.createUser;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.User;
import at.timeguess.backend.services.TopicStatisticService;
import at.timeguess.backend.utils.TestSetup;

/**
 * Tests for {@link TopicStatisticController}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class TopicStatisticControllerTest {

    @InjectMocks
    private TopicStatisticController topicStatController;

    @Mock
    private TopicStatisticService topicStatService;

    private static Integer expected = 15;

    @Test
    public void testGetTopics() {
        List<Topic> expected = createEntities(TestSetup::createTopic, 5);
        when(topicStatService.getTopics()).thenReturn(expected);

        List<Topic> result = topicStatController.getTopics();

        verify(topicStatService).getTopics();
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 12, 35, 150 })
    public void testGetCorrectRounds(long topicId) {
        Topic topic = assertMockTopic(topicId);
        when(topicStatService.nrOfCorrectRounds(topic)).thenReturn(expected);

        Integer result = topicStatController.getCorrectRounds();

        verify(topicStatService).nrOfCorrectRounds(topic);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 12, 35, 150 })
    public void testGetIncorrectRounds(long topicId) {
        Topic topic = assertMockTopic(topicId);
        when(topicStatService.nrOfInCorrectRounds(topic)).thenReturn(expected);

        Integer result = topicStatController.getIncorrectRounds();

        verify(topicStatService).nrOfInCorrectRounds(topic);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 12, 35, 150 })
    public void testGetGamesPlayedWithTopic(long topicId) {
        Topic topic = assertMockTopic(topicId);
        when(topicStatService.nrOfGamesWithTopic(topic)).thenReturn(expected);

        Integer result = topicStatController.getGamesPlayedWithTopic();

        verify(topicStatService).nrOfGamesWithTopic(topic);
        assertEquals(expected, result);
    }

    @Test
    public void testGetNumberOfTopics() {
        when(topicStatService.nrOfTopics()).thenReturn(expected);

        Integer result = topicStatController.getNumberOfTopics();

        verify(topicStatService).nrOfTopics();
        assertEquals(expected, result);
    }

    @Test
    public void testGetNumberOfTerms() {
        when(topicStatService.nrOfTerms()).thenReturn(expected);

        Integer result = topicStatController.getNumberOfTerms();

        verify(topicStatService).nrOfTerms();
        assertEquals(expected, result);
    }

    @Test
    public void testGetNumberOfPlayers() {
        when(topicStatService.nrOfPlayers()).thenReturn(expected);

        Integer result = topicStatController.getNumberOfPlayers();

        verify(topicStatService).nrOfPlayers();
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 12, 35, 150 })
    public void testGetMostUsedTopic(long topicId) {
        Topic expected = createTopic(topicId);
        when(topicStatService.getMostUsedTopic()).thenReturn(expected);

        Topic result = topicStatController.getMostUsedTopic();

        verify(topicStatService).getMostUsedTopic();
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 12, 35, 150 })
    public void testGetLeastUsedTopic(long topicId) {
        Topic expected = createTopic(topicId);
        when(topicStatService.getLeastUsedTopic()).thenReturn(expected);

        Topic result = topicStatController.getLeastUsedTopic();

        verify(topicStatService).getLeastUsedTopic();
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 12, 35, 150 })
    public void testGetNrOfTermsPerTopic(long topicId) {
        Topic topic = assertMockTopic(topicId);
        when(topicStatService.getNrOfTermsPerTopic(topic)).thenReturn(expected);

        Integer result = topicStatController.getNrOfTermsPerTopic();

        verify(topicStatService).getNrOfTermsPerTopic(topic);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 12, 35, 150 })
    public void testGetMostSuccessfullUser(long topicId) {
        Topic topic = assertMockTopic(topicId);
        User expected = createUser(topicId);
        when(topicStatService.getMostSuccessfullUserOfTopic(topic)).thenReturn(expected);

        User result = topicStatController.getMostSuccessfullUser();

        verify(topicStatService).getMostSuccessfullUserOfTopic(topic);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 12, 35, 150 })
    public void testGetPlayedWithTopic(long topicId) {
        Topic topic = assertMockTopic(topicId);
        when(topicStatService.nrOfGamesWithTopic(topic)).thenReturn(12);

        assertTrue(topicStatController.getPlayedWithTopic());
        verify(topicStatService).nrOfGamesWithTopic(topic);
        
        when(topicStatService.nrOfGamesWithTopic(topic)).thenReturn(0);
        
        assertFalse(topicStatController.getPlayedWithTopic());
        verify(topicStatService, times(2)).nrOfGamesWithTopic(topic);
    }

    private Topic assertMockTopic(Long topicId) {
        Topic topic = createTopic(topicId);
        topicStatController.setTopic(topic);

        assertEquals(topicId, topicStatController.getTopic().getId());
        return topic;
    }
}
