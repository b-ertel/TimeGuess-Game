package at.timeguess.backend.ui.controllers.demo;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import at.timeguess.backend.utils.CDIContextRelated;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

/**
 * This controller is responsible for showing the information from the cube in the game window using websockets
 *
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */


@Controller
@Scope("application")
@CDIContextRelated
public class WebSocketCubeFace {

    @Autowired
    CubeService cubeService;

    @CDIAutowired
    private WebSocketManager websocketManager;


    private CubeFace currentFace;

    @PostConstruct
    public void setup() {
        this.currentFace = this.cubeService.allCubeFaces().get((int) (Math.random()*12)) ;
    }

    public CubeFace getCurrentFace() {
        return this.currentFace;
    }

    public void cubeChange() {
        this.currentFace = this.cubeService.allCubeFaces().get(cubeService.getDummyCubeFace()) ;
        this.websocketManager.getCubeFaceChannel().send("cubeFaceChange");
    }
}
