package at.timeguess.backend.model.utils;

import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.springframework.beans.factory.annotation.Autowired;

import at.timeguess.backend.model.Team;
import at.timeguess.backend.services.TeamService;

@ManagedBean
@RequestScoped
@FacesConverter("teamConverter")
public class TeamConverter implements Converter<Team> {

    @Autowired
    private TeamService teamService;

    @Override
    public Team getAsObject(FacesContext context, UIComponent component, String value) {
        Long id = Long.parseLong(value);
        Optional<Team> db = teamService.findById(id);
        if (db.isEmpty()) return null;
        return db.get();
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Team value) {
        return Long.toString(value.getId());

    }
}
