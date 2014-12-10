package net.cloud.mmo.game;

import net.cloud.mmo.entity.player.Player;

/**
 * Client version of World.  Keeps track of the game world as seen by 
 * the Player using this Client. So hey, this World revolves around them.
 */
public class World {
	
	/** Singleton instance of the World */
	private static World instance;
	
	/** The Player using this client */
	private Player player;
	
	private World()
	{
		// Initialize the player object - starts off pretty blank
		player = new Player();
	}
	
	/**
	 * Get a reference to the World instance, holding relevant game world information
	 * @return A reference to the single World instance
	 */
	public static World getInstance()
	{
		if(instance == null)
		{
			instance = new World();
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

}
