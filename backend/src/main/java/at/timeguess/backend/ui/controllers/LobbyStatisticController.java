package at.timeguess.backend.ui.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.TopicStatistics;
import at.timeguess.backend.services.LobbyStatisticService;

@Component
@Scope("view")
public class LobbyStatisticController {

	@Autowired
	LobbyStatisticService statService;
	
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
	
}
