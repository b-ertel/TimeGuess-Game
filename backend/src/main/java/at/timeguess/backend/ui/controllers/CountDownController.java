package at.timeguess.backend.ui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.services.GameService;
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
	private Map<Long, Set<String>> membersPerGame = new ConcurrentHashMap<>(); // for more games to play
	
	private Timer timer;
	int delay = 1000; //milliseconds
	
    @CDIAutowired
	private WebSocketManager webSocketManager;
    @Autowired
    private GameService gameService;
	
	/**
	 * starts countDown with given variables
	 * 
	 * @param min
	 * @param sec
	 */
	public void startCountDown(int min, int sec) {
		setMin(min);
		setSec(sec);
		startCountDown(null);
	}
	
	public void startCountDown(CubeFace cubeFace, Game game) {
		setMin(cubeFace.getTime());
		setSec(0);

		for(Team t : gameService.getTeams(game)){
			addMembersPerGame(game, t.getTeamMembers());
		}
		startCountDown(game.getId());
	}
	
	/**
	 * starts countDown
	 */
	public void startCountDown(Long gameId) {		
		ActionListener task = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				count(gameId);
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
	public void resetCountDown(Long gameId){
		setMin(0);
		setSec(0);
		Set<String> players = this.membersPerGame.get(gameId);
		webSocketManager.getCountDownChannel().send("countDownUpdate", players);	
	}

	/**
	 * step in counting down - informs websocket listener after each update
	 */
	public void count(Long gameId) {
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
		Set<String> players = this.membersPerGame.get(gameId);
		webSocketManager.getCountDownChannel().send("countDownUpdate", players);		
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

	public Map<Long, Set<String>> getMembersPerGame() {
		return membersPerGame;
	}
	
	public void addMembersPerGame(Game game, Set<User> teamMembers) {
		
		if(!this.membersPerGame.containsKey(game.getId())) {
			this.membersPerGame.put(game.getId(), new HashSet<>());
		}
		
		for(User u : teamMembers) {
			this.membersPerGame.get(game.getId()).add(u.getUsername());
		}
		
		
	}
}


