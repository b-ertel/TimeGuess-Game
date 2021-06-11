package at.timeguess.backend.services;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Topic;
import at.timeguess.backend.repositories.TopicRepository;
import at.timeguess.backend.spring.CDIAwareBeanPostProcessor;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * Provides an interface to the model for managing {@link Topic} entities.
 */
@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
@CDIContextRelated
public class TopicService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicService.class);

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private MessageBean messageBean;

    @CDIAutowired
    private WebSocketManager websocketManager;

    /**
     * @apiNote neither {@link Autowired} nor {@link CDIAutowired} work for a {@link Component},
     * and {@link PostConstruct} is not invoked, so autowiring is done manually
     */
    public TopicService() {
        if (websocketManager == null) {
            new CDIAwareBeanPostProcessor().postProcessAfterInitialization(this, "websocketManager");
        }
    }

    /**
     * Returns a list of all topics.
     * @return list of all topics
     */
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MANAGER') OR hasAuthority('PLAYER')")
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    /**
     * Loads a single Topic identified by it's name.
     * @param name the name of the topic to load
     * @return a single Topic
     */
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MANAGER')")
    public Topic loadTopic(String name) {
        return topicRepository.findByName(name);
    }

    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MANAGER')")
    public Topic loadTopicId(Long topicId) {
        return topicRepository.findById(topicId).orElse(null);
    }

    /**
     * Saves the Topic.
     * @param topic the topic to save
     * Additionally fills gui message with success or failure info and triggers a push update.
     * @param topic the topic to save
     * @return the saved topic
     * @apiNote Message handling ist done here, because this is the central place for saving topics.
     */
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('MANAGER')")
    public Topic saveTopic(Topic topic) {
        Topic ret = null;
        try {
            boolean isNew = topic.isNew();

            ret = topicRepository.save(topic);

            // fill ui message, send update and log
            messageBean.alertInformation(ret.getName(), isNew ? "New topic created" : "Topic updated");

            if (websocketManager != null)
                websocketManager.getUserRegistrationChannel().send(
                    Map.of("type", "topicUpdate", "name", topic.getName(), "id", topic.getId()));

            LOGGER.info("Topic '{}' (id={}) was {}", ret.getName(), ret.getId(), isNew ? "created" : "updated");
        }
        catch (Exception e) {
            String msg = "Saving topic failed";
            if (e.getMessage().contains("TOPIC(NAME)"))
                msg += String.format(": topic named '%s' already exists", topic.getName());
            messageBean.alertErrorFailValidation(topic.getName(), msg);

            LOGGER.info("Saving topic '{}' (id={}) failed, stack trace:", topic.getName(), topic.getId());
            e.printStackTrace();
        }
        return ret;
    }
}
