package at.timeguess.backend.model.utils;

import java.util.Comparator;

public class TeamScoreComparator implements Comparator<TeamScore> {

	
	@Override
	public int compare(TeamScore o1, TeamScore o2) {
		if(o1.getScore()>o2.getScore()) {
			return -1;
		} else if (o1.getScore()==o2.getScore()) {
			return 0;
		} else {
			return 1;
		}
	}

}
