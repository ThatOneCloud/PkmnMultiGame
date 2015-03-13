package net.cloud.client.entity.player;

/**
 * An enumerable for the possible responses from a login request, throughout 
 * any stage of the login process.
 */
public enum LoginResponse {
	
	/** Username and/or password are wrong */
	INVALID_CREDENTIALS,
	
	/** The account being logged into is already logged in */
	ALREADY_LOGGED_IN,
	
	/** So far so good, okay to proceed */
	OKAY;

}
