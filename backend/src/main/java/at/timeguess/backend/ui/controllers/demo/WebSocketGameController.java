package at.timeguess.backend.ui.controllers.demo;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.User;
import at.timeguess.backend.repositories.UserRepository;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.TermService;
import at.timeguess.backend.ui.beans.SessionInfoBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.*;


/**
 * This controller is responsible for showing a term in the game window with websockets
 *
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */

@Controller
@Scope("application")
@CDIContextRelated
public class WebSocketGameController {
    @Autowired
    private SessionInfoBean sessionInfoBean;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TermService termService;
    @Autowired
    private ChatManagerController chatController;
    @Autowired
    private GameService gameService;
    @CDIAutowired
    private WebSocketManager websocketManager;

    private Term currentTerm;
    private Term controllTerm;

    private Queue<Team> teams = new LinkedList<>();

    private List<User> guessingUsers = new LinkedList<>();
    private List<User> controllingUsers = new LinkedList<>();


    @PostConstruct
    public void setup() {
        this.currentTerm = this.termService.getAllTerms().get((int) (Math.random()*20));

        this.controllTerm = new Term();
        this.controllTerm.setName("WAIT FOR THE NEXT TERM");

        //todo: make this chooosable during the game
        Game testGame = gameService.loadGame(1L);
        this.teams = new LinkedList<>(gameService.getTeams(testGame));
        //List<Team> teamsList = new ArrayList<Team>(teams);
        //this.guessingUsers.addAll(teamsList.get(0).getTeamMembers());
        //this.controllingUsers.addAll(teamsList.get(1).getTeamMembers());
        Team guessingTeam = this.teams.poll();
        this.guessingUsers.addAll(guessingTeam.getTeamMembers());
        for (Team controllTeam : this.teams){
            this.controllingUsers.addAll(controllTeam.getTeamMembers());
        }
        this.teams.add(guessingTeam);
    }


    /**
     * When a user logs out, there shouldn't be the possibility to send him terms
     * anymore and hence it should be removed from any undelivered
     * term-recipient-list
     */
    public void synchronizeRecipients() {
            this.guessingUsers.removeIf(user -> !this.chatController.getPossibleRecipients().contains(user));
            this.controllingUsers.removeIf(user -> !this.chatController.getPossibleRecipients().contains(user));
    }

    public Term getCurrentTerm() {
            return this.currentTerm;
    }
    
    

    public void termChange() {
        this.currentTerm = this.termService.getAllTerms().get((int) (Math.random()*20));
        this.websocketManager.getFirstTermChannel().send("termUpdate");
        Team guessingTeam = this.teams.poll();
        this.guessingUsers = new LinkedList<>();
        this.controllingUsers = new LinkedList<>();
        this.guessingUsers.addAll(guessingTeam.getTeamMembers());
        for (Team controllTeam : this.teams){
            this.controllingUsers.addAll(controllTeam.getTeamMembers());
        }
        this.teams.add(guessingTeam);
    }




    public List<User> getGuessingUsers() {
        return guessingUsers;
    }

    public List<User> getControllingUsers() {
        return controllingUsers;
    }

    public Term getControllTerm() {
        return controllTerm;
    }
}
