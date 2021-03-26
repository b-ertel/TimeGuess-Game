package at.timeguess.backend.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Team {
	
	@Id
	private Long id;
	
	@Column
	private String name;
	
	@ManyToMany(mappedBy = "teams")
	private Set<Game> games;
	
	@ManyToMany (mappedBy = "teams")
	private Set<User> teamMembers; 
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Set<Game> getGames() {
		return games;
	}

	public void setGames(Set<Game> games) {
		this.games = games;
	}

	
}
