package at.timeguess.backend.model;

public class TopicStatistics {

	private Topic currentDisplayedTopic;
	
	private Integer nrGamesPlayed;
	
	private Integer nrOfCorrectRounds;
	
	private Integer nrOfIncorrectRounds;

	
	public TopicStatistics(Topic currentDisplayedTopic, Integer nrGamesPlayed, Integer nrOfCorrectRounds,
			Integer nrOfIncorrectRounds) {
		super();
		this.currentDisplayedTopic = currentDisplayedTopic;
		this.nrGamesPlayed = nrGamesPlayed;
		this.nrOfCorrectRounds = nrOfCorrectRounds;
		this.nrOfIncorrectRounds = nrOfIncorrectRounds;
	}

	public Topic getCurrentDisplayedTopic() {
		return currentDisplayedTopic;
	}

	public void setCurrentDisplayedTopic(Topic currentDisplayedTopic) {
		this.currentDisplayedTopic = currentDisplayedTopic;
	}
	
	
	public Integer getNrGamesPlayed() {
		return nrGamesPlayed;
	}

	public void setNrGamesPlayed(Integer nrGamesPlayed) {
		this.nrGamesPlayed = nrGamesPlayed;
	}

	public Integer getNrOfCorrectRounds() {
		return nrOfCorrectRounds;
	}

	public void setNrOfCorrectRounds(Integer nrOfCorrectRounds) {
		this.nrOfCorrectRounds = nrOfCorrectRounds;
	}

	public Integer getNrOfIncorrectRounds() {
		return nrOfIncorrectRounds;
	}

	public void setNrOfIncorrectRounds(Integer nrOfIncorrectRounds) {
		this.nrOfIncorrectRounds = nrOfIncorrectRounds;
	}
	
	
}
