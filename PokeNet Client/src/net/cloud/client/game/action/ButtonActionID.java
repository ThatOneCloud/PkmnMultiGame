package net.cloud.client.game.action;

/**
 * The various buttons in the client that are known as action buttons.
 * In other words, they are going to inform the server that they were acted on. 
 * This enum should match the order of the ButtonActionID enum in the server, although we do not need more information. 
 */
public enum ButtonActionID {
	
	/** The logout button, telling us the player wants to log out */
	LOGOUT;

}
