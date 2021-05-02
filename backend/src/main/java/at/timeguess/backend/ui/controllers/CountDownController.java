package at.timeguess.backend.ui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Timer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 *  Controller for managing CountDown.
 *
 */
@Controller
@CDIContextRelated
@Scope("application")
public class CountDownController {

	private int min;
	private int sec;
	
	private List<String> gameMembers = new CopyOnWriteArrayList<>();  // for one game to play
	private Map<Game, String> membersPerGame = new ConcurrentHashMap<>(); // for more games to play
	
	private Timer timer;
	int delay = 1000; //milliseconds
	
    @CDIAutowired
	private WebSocketManager webSocketManager;
	
	/**
	 * starts countDown with given variables
	 * 
	 * @param min
	 * @param sec
	 */
	public void startCountDown(int min, int sec) {
		setMin(min);
		setSec(sec);
		startCountDown();
	}
	
	/**
	 * starts countDown
	 */
	public void startCountDown() {		
		ActionListener task = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				count();
			}
		};
		
		this.timer = new Timer(delay, task);
		this.timer.start();
	}
	
	/**
	 * stops countDown
	 */
	public void endCountDown() {
		this.timer.stop();
	}
	
	/**
	 * @return countDown as string to display
	 */
	public String getCountDown() {
		return getMin() + " : " + getSec();
	}
	
	/**
	 * reset countDown
	 */
	public void resetCountDown(){
		setMin(0);
		setSec(0);
		webSocketManager.getCountDownChannel().send("countDownUpdate", gameMembers);	
	}

	/**
	 * step in counting down - informs websocket listener after each update
	 */
	public void count() {
		if (min > 0) {
			if(sec > 0) {
				sec--;
			}
			else {
				min--;
				sec = 59;
			}
		}
		else if (min==0 && sec>0){
			sec--;
		}
		else {
			endCountDown();
		}
		webSocketManager.getCountDownChannel().send("countDownUpdate", gameMembers);		
	}
	
	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}

	public List<String> getGameMembers() {
		return gameMembers;
	}
	
	public void setGameMembers(List<String> gameMembers) {
		this.gameMembers = gameMembers;
	}

	public void addGameMember(String newGameMember) {
		this.gameMembers.add(newGameMember);
	}

	public Map<Game, String> getMembersPerGame() {
		return membersPerGame;
	}

	public void setMembersPerGame(Map<Game, String> membersPerGame) {
		this.membersPerGame = membersPerGame;
	}
	
	public void addMembersPerGame(Game game, String gameMember) {
		this.membersPerGame.put(game, gameMember);
	}
}


