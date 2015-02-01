package net.cloud.server.entity.player;

/**
 * An enumerable of the various return values possible from account creation. 
 * Because enum values are a bit tidier than integer constants
 */
public enum AccountCreationResult {
	
	/** Invalid username */
	INVALID_USER("Usernames may only be alphanumeric with underscores"),
	
	/** Invalid password */
	INVALID_PASS("Passwords may only contain alphanumeric characters, underscores, and number row special characters"),
	
	/** Account already exists */
	EXISTS("Username is already taken"),
	
	/** File saving issue */
	SAVE_ERROR("Could not save player account data"),
	
	/** Success (pending file write but yes) */
	SUCCESS("Account created successfully");
	
	/** Simple user friendly message */
	private final String message;
	
	/**
	 * @param message Simple user friendly message
	 */
	private AccountCreationResult(String message)
	{
		this.message = message;
	}
	
	/**
	 * A simple message that can be shown to the user
	 * @return A friendly message about the result
	 */
	public String getMessage()
	{
		return message;
	}

}
