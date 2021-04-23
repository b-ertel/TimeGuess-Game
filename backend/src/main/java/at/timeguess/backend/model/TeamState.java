package at.timeguess.backend.model;

public enum TeamState {
	AVAILABLE, // Team can join game
	IN_GAME, // Team is in a game
	OFFLINE, // No member is online, can not participate
	RETIRED // Team can no longer participate
}
