package net.cloud.server.game;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import net.cloud.server.entity.player.Player;

/**
 * Similar to the WorldPlayerMap, this serves as a temporary storage location for players that have disconnected 
 * and still have a chance to reconnect. 
 * Players are stored with their username as a key, instead of the Channel they connected with.
 */
public class DisconnectedPlayerMap {
	
	/** Initial capacity of the map. Low-ball how many players are expected in here */
	private static final int INITIAL_CAPACITY = 4;
	
	/** Density of the map. Higher for less overhead, but not too high */
	private static final float LOAD_FACTOR = (7.0f / 8.0f);
	
	/** Essentially how many threads can concurrently access map. Keep as low as possible. */
	private static final int MAP_SHARDS = 1;
	
	/** How many players should be online before bulk actions become parallel */
	private static final long PARALLELISM_THRESHOLD = Long.MAX_VALUE;
	
	/** Stores a mapping from the channel the player is connected with to the player itself */
	private ConcurrentHashMap<String, Player> players;
	
	/**
	 * Creates a new map ready to put players in. 
	 */
	public DisconnectedPlayerMap()
	{
		players = new ConcurrentHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, MAP_SHARDS);
	}
	
	/**
	 * Look for a player in the map
	 * @param username The player's username
	 * @return The player, or null if they were not in the map
	 */
	public Player get(String username)
	{
		return players.get(username.toLowerCase());
	}
	
	/**
	 * Put a player in the map. Their username is used as the key.
	 * @param player The player that has disconnected
	 */
	public void place(Player player)
	{
		players.put(player.getUsername().toLowerCase(), player);
	}
	
	/**
	 * Remove the player with the given username from the map
	 * @param username The username of the player
	 * @return The player that was removed, or null if the player was not in the map
	 */
	public Player remove(String username)
	{
		return players.remove(username.toLowerCase());
	}
	
	/**
	 * Do the given action for all players in the map. 
	 * @param action What to do with each Player
	 */
	public void forAll(Consumer<Player> action)
	{
		players.forEachValue(PARALLELISM_THRESHOLD, action);
	}

}
