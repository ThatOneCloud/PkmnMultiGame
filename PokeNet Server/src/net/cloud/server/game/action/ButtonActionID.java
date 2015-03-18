package net.cloud.server.game.action;

/**
 * Defines the various action buttons that we can handle. 
 * Each has a canonical name leading us to the file that contains the code for handling the action.
 * This enum should match the order of the enum in the client's ButtonActionID.java
 */
public enum ButtonActionID implements ActionEnum {
	
	/** The logout button, telling us the player wants to log out */
	LOGOUT("Logout");
	
	/** File name */
	private final String canonicalName;
	
	/**
	 * Constructor
	 * @param canonicalName File name
	 */
	private ButtonActionID(String canonicalName)
	{
		this.canonicalName = canonicalName;
	}

	@Override
	public String getCanonicalName()
	{
		return canonicalName;
	}

}
