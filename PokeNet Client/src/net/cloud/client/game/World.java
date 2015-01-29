package net.cloud.client.game;

import net.cloud.client.entity.player.Player;

/**
 * Client version of World.  Keeps track of the game world as seen by 
 * the Player using this Client. So hey, this World revolves around them.
 */
public class World {
	
	/** Singleton instance of the World */
	private static World instance;
	
	/** The Player using this client */
	private Player player;
	
	/** Default private constructor */
	private World()
	{
	}
	
	/**
	 * Get a reference to the World instance, holding relevant game world information
	 * @return A reference to the single World instance
	 */
	public static World getInstance()
	{
		if(instance == null)
		{
			synchronized(World.class)
			{
				if(instance == null)
				{
					instance = new World();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Get the Player this world revolves around
	 * @return The Player object that represents the player using this client
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Set a player as the user of this client. Until this is done, Player
	 * will be null. Typically a player is assigned right before trying to 
	 * login to the server.
	 * @param player The Player object that will represent the player using the client
	 */
	public void setPlayer(Player player)
	{
		this.player = player;
	}

}
