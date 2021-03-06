package net.cloud.server.entity.player.save;

import net.cloud.server.entity.player.Player;

/**
 * Custom exception for when a player's data could not be loaded from data
 */
public class PlayerLoadException extends Exception {
	
	/** Eclipse generated serial ID */
	private static final long serialVersionUID = 6282872956075261430L;
	
	/** The player whose data could not be saved */
	private final Player player;
	
	/**
	 * Create a new exception for when a load was not successful. 
	 * @param player The player whose data could not be loaded
	 * @param message What went wrong
	 */
	public PlayerLoadException(Player player, String message)
	{
		super(message);
		
		this.player = player;
	}
	
	/**
	 * Create a new exception for when a load was not successful. 
	 * @param player The player whose data could not be loaded
	 * @param message What went wrong
	 * @param cause A cause for exception chaining
	 */
	public PlayerLoadException(Player player, String message, Throwable cause)
	{
		super(message, cause);
		
		this.player = player;
	}
	
	/**
	 * @return A message with the player's name and cause message
	 */
	@Override
	public String getMessage()
	{
		return "Could not load data for " + player.getUsername() + ": " + super.getMessage();
	}
	
	/**
	 * @return A message with the player's name and cause message
	 */
	@Override
	public String toString()
	{
		return "Could not load data for " + player.getUsername() + ": " + super.getMessage();
	}

}
