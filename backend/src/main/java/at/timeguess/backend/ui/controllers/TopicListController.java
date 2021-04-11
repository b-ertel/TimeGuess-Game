package at.timeguess.backend.ui.controllers;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Topic;
import at.timeguess.backend.services.TopicService;

/**
 * Controller for topic listing views.
 *
 */
@Component
@Scope("view")
public class TopicListController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    TopicService topicService;

    /**
     * Returns a list of all topics.
     * @return
     */
    public Collection<Topic> getTopics() {
        return topicService.getAllTopics();
    }
}
