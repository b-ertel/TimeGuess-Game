package at.timeguess.backend.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Team;
import at.timeguess.backend.repositories.TeamRepository;

@Component
@Scope("application")
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
    public List<Team> getAvailableTeams() {
		// TODO
		// should be teamRepository.findByStatus(AVAILABLE);
        return teamRepository.findAll();
    }
    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }
}
