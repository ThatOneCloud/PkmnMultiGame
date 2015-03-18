package net.cloud.server.game.action;

/**
 * Common interface for all scripted actions. 
 * They all need to be able to return an ID via an ActionEnum
 */
public interface Action {
	
	/**
	 * Obtain the enum constant that serves as an ID for this action. 
	 * This in turn provides a way to access the constant information related to the action.
	 * @return The ID of the action
	 */
	public ActionEnum getActionID();

}
