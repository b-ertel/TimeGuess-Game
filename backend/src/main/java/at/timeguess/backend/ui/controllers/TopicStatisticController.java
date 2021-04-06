package at.timeguess.backend.ui.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Topic;
import at.timeguess.backend.services.TopicStatisticService;

@Component
@Scope("view")
public class TopicStatisticController {

	@Autowired
	TopicStatisticService statService;
	
	private Topic topic;
	
	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	
	public Topic getTopic() {
		return this.topic;
	}
	
	public List<Topic> getTopics() {
		return statService.getTopics();
	}
	
	public Integer getCorrectRounds() {
		return statService.nrOfCorrectRounds(topic);
	}
	
	public Integer getIncorrectRounds() {
		return statService.nrOfInCorrectRounds(topic);
	}
	
	public Integer getGamesPlayedWithTopic() {
		return statService.nrGamePlayedPerTopic(topic);
	}
	
	
	public Integer getNumberOfTopics() {
		return statService.nrOfTopics();
	}
	
	public Integer getNumberOfTerms() {
		return statService.nrOfTerms();
	}
	
	public Integer getNumberOfPlayers() {
		return statService.nrOfPlayers();
	}
	
	public Topic getMostUsedTopic() {
		return statService.getMostUsedTopic();
	}
	
	public Topic getLeastUsedTopic() {
		return statService.getLeastUsedTopic();
	}
	
	public Integer getNrOfTermsPerTopic() {
		return statService.getNrOfTermsPerTopic(topic);
	}
	
}
