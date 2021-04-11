package at.timeguess.backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;
import at.timeguess.backend.repositories.GameRepository;
import at.timeguess.backend.repositories.RoundRepository;
import at.timeguess.backend.repositories.TermRepository;
import at.timeguess.backend.repositories.TopicRepository;
import at.timeguess.backend.repositories.UserRepository;

/**
 * Service for accessing and visualizing data.
 *
 */
@Component
@Scope("application")
public class TopicStatisticService {

	@Autowired
	private GameRepository gameRepository;
	
	@Autowired
	private TopicRepository topicRepository;
	
	@Autowired
	private RoundRepository roundRepository;
	
	@Autowired
	private TermRepository termRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	
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
	
	public Integer nrOfInCorrectRounds(Topic topic) {
		return roundRepository.getNrIncorrectAnswerByTopic(topic.getId());
	}
	
	public Integer nrGamePlayedPerTopic(Topic topic) {
		return gameRepository.countByTopicId(topic.getId());
	}
	
	public List<Topic> getTopics() {
		return topicRepository.findAll();
	}
	
	public Integer nrOfTopics() {
		return topicRepository.nrOfTopics();
	}
	
	public Integer nrOfTerms() {
		return termRepository.nrOfTerms();
	}
	
	public Integer nrOfPlayers () {
		return userRepository.findByRole(UserRole.PLAYER).size();
	}
	
	public Topic getMostUsedTopic() {
		List<Object[]> list = gameRepository.getTopicAndOccurency();
		return (Topic) list.get(0)[0];	
	}
	
	public Topic getLeastUsedTopic() {
		List<Object[]> list = gameRepository.getTopicAndOccurency();
		List<Topic> usedTopics = new ArrayList<>();
		for(Object[] ob : list) {
			usedTopics.add((Topic) ob[0]);
		}
		List<Topic> allTopics = getTopics();
		for(Topic topic : allTopics) {
			if(!usedTopics.contains(topic)) {
				return topic;
			}
		}
		return (Topic) list.get(list.size()-1)[0];	
	}
	
	public Integer getNrOfTermsPerTopic(Topic topic) {
		return termRepository.nrOfTermPerTopic(topic);
	}
	
	public User getMostSuccessfullUserOfTopic(Topic topic) {
		List<User> users = userRepository.findAll();
		User[] mostSuccessfullUser = {null};
		Integer[] gamesWon = {0};
		users.stream().forEach(user -> {
				if(userService.getTotalGamesWonByTopic(user).get(topic.getName()) > gamesWon[0]) {
					mostSuccessfullUser[0] = user;
					gamesWon[0] = userService.getTotalGamesWonByTopic(user).get(topic.getName());
				}
		});
		return mostSuccessfullUser[0];
	}
}
