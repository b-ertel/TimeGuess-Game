package at.timeguess.backend.model.utils;

import java.util.Comparator;

/**
 * Helper class for sorting players in games.
 */
public class PlayerGamesComparator implements Comparator<UserScores> {

    @Override
    public int compare(UserScores o1, UserScores o2) {
        if (o1.getGamesWon() > o2.getGamesWon())
            return -1;
        else if (o1.getGamesWon() < o2.getGamesWon())
            return 1;
        else if (o1.getTermsCorrect() > o2.getTermsCorrect())
            return -1;
        else if (o1.getTermsWrong() < o2.getTermsWrong())
            return 1;
        else if (o1.getPercentage() > o2.getPercentage())
            return -1;
        else if (o1.getPercentage() < o2.getPercentage())
            return 1;
        else
            return 0;
    }
}
