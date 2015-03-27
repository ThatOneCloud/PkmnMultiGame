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
	
	/** Login has - somewhere along the line - failed - trap state*/
	LOGIN_FAILED,
	
	/** In the process of logging out */
	LOGGING_OUT,
	
	/** After a successful login, the player has later successfully logged out */
	LOGGED_OUT,
	
	/** Disconnected abruptly after being logged in */
	DISCONNECTED,
	
	/** In the process of reconnecting after disconnecting */
	RECONNECTING,
	
	/** Did not reconnect after being disconnected - trap state */
	RECONNECT_FAILED;

}
