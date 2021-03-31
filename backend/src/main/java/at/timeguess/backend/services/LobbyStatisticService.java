package at.timeguess.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Topic;
import at.timeguess.backend.repositories.GameRepository;
import at.timeguess.backend.repositories.RoundRepository;
import at.timeguess.backend.repositories.TopicRepository;

/**
 * Service for accessing and visualizing data.
 *
 */
@Component
@Scope("application")
public class LobbyStatisticService {

	@Autowired
	private GameRepository gameRepository;
	
	@Autowired
	private TopicRepository topicRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	
	public Integer nrOfGamesWithTopic(Topic topic) {
		return gameRepository.countByTopicId(topic.getId());
	}
	
	public Topic mostUsedTopic() {
		List<Topic> listOfAllTopics = topicRepository.findAll();
		int max = 0;
		Topic currentTopic = null;
		for(Topic topic : listOfAllTopics) {
			if (nrOfGamesWithTopic(topic) > max) {
				max = nrOfGamesWithTopic(topic);
				currentTopic = topic;
			}
		}
		return currentTopic;
	}
	
	public Topic leastusedTopic() {
		List<Topic> listOfAllTopics = topicRepository.findAll();
		int min = Integer.MAX_VALUE;
		Topic currentTopic = null;
		for(Topic topic : listOfAllTopics) {
			if (nrOfGamesWithTopic(topic) < min) {
				min = nrOfGamesWithTopic(topic);
				currentTopic = topic;
			}
		}
		return currentTopic;
	}
	
	public Integer nrOfCorrectRounds(Topic topic) {
		return roundRepository.getNrCorrectAnswerByTopic(topic.getId());
	}
	
}
