package at.timeguess.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.Exceptions.TopicAlreadyExistsException;
import at.timeguess.backend.repositories.TopicRepository;

/**
 * Provides an interface to the model for managing {@link Topic} entities.
 */
@Component
@Scope("application")
public class TopicService {
    
    @Autowired
    private TopicRepository topicRepository;

    /**
     * Returns a list of all topics.
     *
     * @return list of all topics
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    /**
     * Loads a single Topic identified by it's name.
     *
     * @param name the name of the topic to load
     * @return a single Topic
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Topic loadTopic(String name) {
        return topicRepository.findByName(name);
    }

    /**
     * Saves the Topic. 
     *
     * @param topic the topic to save
     * @return the new topic
     * @throws TopicAlreadyExistsException
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Topic saveTopic(Topic topic) throws TopicAlreadyExistsException {
        if (topic.isNew()) {
            Topic newTopic = topicRepository.save(topic);
            return newTopic;
        } else {
            throw new TopicAlreadyExistsException();
        }
    }
    
    @PreAuthorize("hasAuthority('MANAGER') ")
    public Topic loadTopicId(Long topicId) {
        return topicRepository.findById(topicId).get();
    }
}
