package net.cloud.client.entity.player;

/**
 * Describes which step of the login process a player is in
 */
public enum LoginState {
	
	/** Not even connected, the initial state */
	INITIAL,
	
	/** A new player - just connected */
	CONNECTED,
	
	/** They have sent their username and password */
	VERIFIED,
	
	/** They're logged into the game */
	LOGGED_IN,
	
	/** Login has - somewhere along the line - failed */
	FAILED;

}
