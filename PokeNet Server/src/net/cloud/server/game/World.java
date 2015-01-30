package net.cloud.server.game;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.cloud.server.entity.player.Player;
import net.cloud.server.tracking.StatTracker;

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
		
		// Report that the number of players online has changed.
		StatTracker.instance().updatePlayersOnline(players.size());
	}
	
	/**
	 * Obtain an iterator over every player in the world. This includes players that may have recently connected 
	 * and are not fully logged in yet, as well as players that are no longer connected but have not been removed yet. 
	 * @return An iterator over every player held in the World
	 */
//	public Iterator<Player> getAllPlayers()
//	{
		// TODO: Think this over. How to iterate in a fast way. Stamped lock?
		//       Also how to get it down to just players, maybe. 
		//       Also offer one that'll just give players that are logged in?
		//       And a way to remove players
//	}

}
