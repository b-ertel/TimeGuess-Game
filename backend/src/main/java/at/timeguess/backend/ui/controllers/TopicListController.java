package at.timeguess.backend.ui.controllers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.services.TermService;
import at.timeguess.backend.services.TopicService;
import at.timeguess.backend.ui.beans.MessageBean;

/**
 * Controller for topic listing views.
 */
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class TopicListController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    TopicService topicService;
    @Autowired
    TermService termService;

    @Autowired
    private MessageBean messageBean;

    /**
     * Returns a list of all topics.
     * @return list of topics
     */
    public List<Topic> getTopics() {
        return topicService.getAllTopics();
    }

    /**
     * Handles primefaces file upload events.
     * @param event the primefaces file upload event to be handled.
     */
    public void doUploadTerms(FileUploadEvent event) {
        try {
            UploadedFile file = event.getFile();
            String filename = file.getFileName();

            termService.setInfoOnSave(false);

            if (this.importJSON(filename, file.getInputStream()))
                messageBean.alertInformation("Terms upload", String.format("%s was successfully imported", filename));

            termService.setInfoOnSave(true);
        }
        catch (Exception e) {
            messageBean.alertErrorFailValidation("Terms upload error", e.getMessage());
        }
    }

    /**
     * Imports terms for topics in given JSON file.
     * @param filename
     * @param file
     */
    @SuppressWarnings("unchecked")
    private boolean importJSON(String filename, InputStream file) {
        try (InputStreamReader reader = new InputStreamReader(file)) {
            // get all existing topics
            final List<Topic> topics = this.getTopics();

            // loop through file for topic names
            JSONObject content = (JSONObject) new JSONParser().parse(reader);
            content.forEach((toName, terms) -> {
                String topicName = (String) toName;
                String topicPattern = "(?i)" + topicName;
                Optional<Topic> topic = topics.stream().filter(t -> t.getName().matches(topicPattern)).findFirst();

                // no topic exists for name -> create new
                if (topic.isEmpty()) {
                    Topic newTopic = new Topic();
                    newTopic.setName(topicName.toUpperCase());
                    newTopic.setEnabled(true);
                    topic = Optional.ofNullable(topicService.saveTopic(newTopic));
                }

                // import terms for topic
                if (topic.isPresent()) {
                    final Topic currTopic = topic.get();
                    ((JSONArray) terms).forEach(teName -> {
                        // create term only if not already exists
                        String termName = (String) teName;
                        String termPattern = "(?i)" + termName;
                        if (!currTopic.getTerms().stream().anyMatch(t -> t.getName().matches(termPattern))) {
                            Term newTerm = new Term();
                            newTerm.setName(termName.toUpperCase());
                            newTerm.setEnabled(true);
                            newTerm.setTopic(currTopic);
                            if (termService.saveTerm(newTerm) == null)
                                messageBean.alertErrorFailValidation(termName, "Importing term failed: could not create term");
                        }
                    });
                }
                else
                    messageBean.alertErrorFailValidation(topicName, "Importing terms failed: could not find or create topic");
            });
            return true;
        }
        catch (Exception e) {
            messageBean.alertErrorFailValidation(filename, "Importing terms failed: could not parse file");
            e.printStackTrace();
        }
        return false;
    }
}
