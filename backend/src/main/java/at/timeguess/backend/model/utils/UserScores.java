package at.timeguess.backend.model.utils;

import at.timeguess.backend.model.User;

public class UserScores {
	
	private User user;
	
	private Integer gamesWon;
	
	private Integer termsCorrect;
	
	private Integer termsWrong;
	
	private double percentage;

	public UserScores(User user, Integer gamesWon, Integer termsCorrect, Integer termsWrong) {
		super();
		this.user = user;
		this.gamesWon = gamesWon;
		this.termsCorrect = termsCorrect;
		this.termsWrong = termsWrong;
		if(termsCorrect==0 && termsWrong==0){
			this.percentage = 0;
		} else {
			double percentage =  ((double)termsCorrect)/((double) (termsCorrect + termsWrong));
			percentage = percentage*100;
			percentage = Math.round(percentage);
			this.percentage = percentage/100;
		}
		
	}

	public UserScores() {}

	public User getUser() {
		return user;
	}

	public void setUser(User username) {
		this.user = username;
	}

	public Integer getGamesWon() {
		return gamesWon;
	}

	public void setGamesWon(int gamesWon) {
		this.gamesWon = gamesWon;
	}

	public Integer getTermsCorrect() {
		return termsCorrect;
	}

	public void setTermsCorrect(int termsCorrect) {
		this.termsCorrect = termsCorrect;
	}

	public Integer getTermsWrong() {
		return termsWrong;
	}

	public void setTermsWrong(int termsWrong) {
		this.termsWrong = termsWrong;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
}
