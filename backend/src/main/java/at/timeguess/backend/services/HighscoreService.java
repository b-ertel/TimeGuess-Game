package at.timeguess.backend.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;
import at.timeguess.backend.model.utils.UserScores;
import at.timeguess.backend.repositories.RoundRepository;
import at.timeguess.backend.repositories.UserRepository;

@Component
@Scope("application")
public class HighscoreService {
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired 
	RoundRepository roundRepo;
	
	
	public List<UserScores> getUserScores() {
		List<User> users = userRepo.findAll();
		List<UserScores> scores = new ArrayList<>();
		users.stream().filter(user -> user.getRoles().contains(UserRole.PLAYER)).forEach(user -> 
				scores.add(new UserScores(user, userService.getTotalGamesWon(user), roundRepo.getNrOfCorrectAnswerByUser(user), roundRepo.getNrOfIncorrectAnswerByUser(user))));
		return scores;
	}
	
	
	public List<UserScores> getHighscoresByGamesWon() {
		List<UserScores>  ls = getUserScores();
		ls.sort(Comparator.comparing(UserScores::getGamesWon));
		return ls;
	}
	
}
