package at.timeguess.backend.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.GameTeam;
import at.timeguess.backend.model.Team;

@Component
@Scope("application")
public class GameLogicService {

	
	/**
	 * A method that generates a random order of the teams. It generates to every team in the set a random integer, that represents the place of the team.
	 * @param teams: the GameTeams that should be ordered
	 * @return a map with the place of the team as key and the team as value
	 */
	public Map<Integer, Team> getRandomTeamOrder(Set<GameTeam> teams){
		Map<Integer, Team> order = new HashMap<>();
		List<Integer> number = new ArrayList<>();
		Iterator<GameTeam> teamsIterator = teams.iterator();
		for(int i = 1; i<=teams.size(); i++) {
			number.add(i);
		}
		while(teamsIterator.hasNext()) {
			order.put(number.remove(ThreadLocalRandom.current().nextInt(0, number.size())), teamsIterator.next().getTeam());
		}
		return order;
	}
}
