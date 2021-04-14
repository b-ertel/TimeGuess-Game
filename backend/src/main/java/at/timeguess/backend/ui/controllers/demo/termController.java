package at.timeguess.backend.ui.controllers.demo;

import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.demo.LogEntry;
import at.timeguess.backend.model.demo.LogEntryType;
import at.timeguess.backend.model.demo.UserStatus;
import at.timeguess.backend.model.demo.UserStatusInfo;
import at.timeguess.backend.repositories.UserRepository;
import at.timeguess.backend.services.TermService;
import at.timeguess.backend.spring.UserStatusInitializationHandler;
import at.timeguess.backend.ui.controllers.TermListController;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
public class termController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TermService termService;
    @CDIAutowired
    private WebSocketManager websocketManager;

    private Term currentTerm;


    @PostConstruct
    public void setup() {
        this.currentTerm = this.termService.getAllTerms().get((int) (Math.random()*20)) ;
    }




    public Term getCurrentTerm() {
       return this.currentTerm;
    }

    public void termChange() {
        this.currentTerm = this.termService.getAllTerms().get((int) (Math.random()*20));
        this.websocketManager.getTermChannel().send("termUpdate");
    }

}
