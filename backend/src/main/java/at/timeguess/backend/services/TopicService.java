package at.timeguess.backend.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.exceptions.TopicAlreadyExistsException;
import at.timeguess.backend.repositories.TopicRepository;
import at.timeguess.backend.ui.beans.MessageBean;

/**
 * Provides an interface to the model for managing {@link Topic} entities.
 */
@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class TopicService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicService.class);

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private MessageBean messageBean;

    /**
     * Returns a list of all topics.
     * @return list of all topics
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    /**
     * Loads a single Topic identified by it's name.
     * @param name the name of the topic to load
     * @return a single Topic
     */
    @PreAuthorize("hasAuthority('MANAGER')")
    public Topic loadTopic(String name) {
        return topicRepository.findByName(name);
    }

    @PreAuthorize("hasAuthority('MANAGER') ")
    public Topic loadTopicId(Long topicId) {
        return topicRepository.findById(topicId).get();
    }

    /**
     * Saves the Topic.
     * @param topic the topic to save
     * @return the new topic
     * @throws TopicAlreadyExistsException
     */
    @PreAuthorize("hasAuthority('MANAGER')")
    public Topic saveTopic(Topic topic) {
        Topic ret = null;
        try {
            boolean isNew = topic.isNew();

            ret = topicRepository.save(topic);

            // show ui message and log
            messageBean.alertInformation(ret.getName(), isNew ? "New topic created" : "Topic updated");

            LOGGER.info("Topic '{}' (id={}) was {}", ret.getName(), ret.getId(), isNew ? "created" : "updated");
        }
        catch (Exception e) {
            String msg = "Saving topic failed";
            if (e.getMessage().contains("TOPIC(NAME)"))
                msg += String.format(": topic named '%s' already exists", topic.getName());
            messageBean.alertError(topic.getName(), msg);

            LOGGER.info("Saving topic '{}' (id={}) failed, stack trace:", topic.getName(), topic.getId());
            e.printStackTrace();
        }
        return ret;
    }
}
