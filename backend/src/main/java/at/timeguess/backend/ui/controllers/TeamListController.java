package at.timeguess.backend.ui.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Team;
import at.timeguess.backend.services.TeamService;

@Component
@Scope("view")
public class TeamListController {
    @Autowired
    private TeamService teamService;

    public Collection<Team> getTeams() {
        return teamService.getAllTeams();
    }
}
