package net.cloud.mmo.game;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

import net.cloud.mmo.entity.player.Player;

/**
 * Represents the entire Game World.  There can be only one! (singleton class)
 * Keeps track of global information, such as all of the players online.
 */
public class World {
	
	/** Singleton instance of the World */
	private static World instance;
	
	/** A global list of Players, mapped to by the Channel they're connected with */
	private Map<Channel, Player> players;
	
	private World()
	{
		// Initialize map of online players
		players = new HashMap<>();
	}
	
	/**
	 * Get a reference to the World instance, holding global information about the game world
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
	 * Find a player in the world, given the Netty Channel they are connected to the server with. 
	 * @param channel Describes the connection between the Player and the server
	 * @return The Player connected with the provided Channel
	 */
	public Player getPlayer(Channel channel)
	{
		// I did my research. The Channel deep down has its own hash function, great for map use
		return players.get(channel);
	}

	/**
	 * Place a Player into the World. (So they are in the global list of players)
	 * @param channel The Channel linking the player and server
	 * @param player The new Player that just connected
	 */
	public void placePlayer(Channel channel, Player player) {
		players.put(channel, player);
	}

}
