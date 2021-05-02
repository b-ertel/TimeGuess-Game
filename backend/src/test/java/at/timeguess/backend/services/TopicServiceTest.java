package at.timeguess.backend.services;

import static at.timeguess.backend.utils.TestSetup.*;
import static at.timeguess.backend.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Topic;
import at.timeguess.backend.utils.TestSetup;

/**
 * Some very basic tests for {@link TopicService}.
 */
@SpringBootTest
@WebAppConfiguration
public class TopicServiceTest {

    @Autowired
    private TopicService topicService;

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "GEOGRAPHY|1", "MOVIES|2", "STAR WARS|3", "FOOD|4" })
    @WithMockUser(username = "admin", authorities = { "MANAGER" })
    public void testLoadTopic(String topicName, long topicIdExpected) {
        Topic result = topicService.loadTopic(topicName);
        assertEquals(topicName, result.getName(), "Call to topicService.loadTopic returned wrong topic");
        assertEquals(topicIdExpected, result.getId());
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "1|GEOGRAPHY", "2|MOVIES", "3|STAR WARS", "4|FOOD" })
    @WithMockUser(username = "admin", authorities = { "MANAGER" })
    public void testLoadTopicById(long topicId, String topicNameExpected) {
        Topic result = assertLoadTopic(topicId, true, "Topic '%s' could not be loaded from test data source");
        assertEquals(topicNameExpected, result.getName());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "PLAYER", "MANAGER" })
    public void testGetAllTopics() {
        List<Topic> expected = createEntities(TestSetup::createTopic, LongStream.rangeClosed(1, 4).boxed());
        List<Topic> result = topicService.getAllTopics();

        assertLists(expected, result);
        
        expected = createEntities(id -> topicService.loadTopicId(id), LongStream.rangeClosed(1, 4).boxed());
        assertListsCompare(expected, result);
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void testSaveTopic() {
        Topic topic = new Topic();
        topic.setName("TestTopic1");
        assertNull(topic.getId());

        topic.setId(0L);
        assertTrue(topic.isNew());
        topic.setId(null);
        assertTrue(topic.isNew());

        Topic t0 = topicService.saveTopic(topic);
        assertNotNull(t0.getId());

        t0.setName("TestTopic2");
        Topic t1 = topicService.saveTopic(topic);
        assertEquals(t1.getName(), "TestTopic2");
        assertEquals(t0.toString(), t1.toString());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "MANAGER" })
    @DirtiesContext
    public void testSaveTopicWithEmptyName() {
        assertDoesNotThrow(() -> {
            Topic toBeCreatedTopic = new Topic();
            topicService.saveTopic(toBeCreatedTopic);
        });
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "MANAGER" })
    @DirtiesContext
    public void testSaveTopicWithExistingName() {
        assertDoesNotThrow(() -> {
            Topic topic = assertLoadTopic(1, true, "Topic '%s' could not be loaded from test data source");
            topic.setName("FOOD");
            topicService.saveTopic(topic);
        });
    }

    private Topic assertLoadTopic(long topicId, boolean shouldExist, String msgFormat) {
        Topic topic = topicService.loadTopicId(topicId);
        if (shouldExist) assertNotNull(topic, String.format(msgFormat, String.valueOf(topicId)));
        else assertNull(topic, String.format(msgFormat, topic));
        return topic;
    }
}
