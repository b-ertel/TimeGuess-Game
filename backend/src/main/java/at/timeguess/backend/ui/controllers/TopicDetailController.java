package at.timeguess.backend.ui.controllers;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.services.TermService;
import at.timeguess.backend.services.TopicService;
import at.timeguess.backend.services.TopicStatisticService;

@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class TopicDetailController implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicStatisticService topicStatisticService;

    /**
     * Attribute to cache the currently displayed Topic
     */
    private Topic topic;

    /**
     * Sets the currently displayed topic and reloads it form db. This term is
     * targeted by any further calls of
     * {@link #doReloadTopic()}, {@link #doSaveTopic()}.
     *
     * @param term
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
        doReloadTopic();
    }

    /**
     * Returns the currently displayed topic.
     *
     * @return the currently displayed topic
     */
    public Topic getTopic() {
        return topic;
    }

    public void doSaveTopic(Topic selectedTopic) {
        this.topicService.saveTopic(selectedTopic);
    }

    /**
     * Action to force a reload of the currently displayed topic.
     */
    public void doReloadTopic() {
        topic = topicService.loadTopicId(topic.getId());
    }

    public Integer getNumberOfTerms(Topic topic) {
        Integer num = topicStatisticService.getNrOfTermsPerTopic(topic);
        return num;
    }
    
}
