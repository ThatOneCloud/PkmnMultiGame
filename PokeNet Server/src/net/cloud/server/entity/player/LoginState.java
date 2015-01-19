package net.cloud.server.entity.player;

/**
 * Describes which step of the login process a player is in
 */
public enum LoginState {
	
	/** A new player - just connected */
	CONNECTED,
	
	/** They have sent their username and password */
	VERIFIED,
	
	/** They're logged into the game */
	LOGGED_IN

}
