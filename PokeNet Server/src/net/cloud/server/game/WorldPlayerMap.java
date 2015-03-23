package net.cloud.server.game;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.cloud.server.entity.player.LoginState;
import net.cloud.server.entity.player.Player;
import net.cloud.server.tracking.StatTracker;
import net.cloud.server.util.function.ConsumerFilter;

/**
 * An object designed to store all of the players that are currently connected to the server. 
 * Maintains a mapping from the Channel to the Player object, and provides facilities for 
 * adding and removing (analogous to logging in and out) as well as obtaining a Player and 
 * performing some action on all or a specific subset of Players. 
 */
public class WorldPlayerMap {
	
	/** Initial capacity of the map. Low-ball how many players are expected online */
	private static final int INITIAL_CAPACITY = 8;
	
	/** Density of the map. Higher for less overhead, but not too high */
	private static final float LOAD_FACTOR = (7.0f / 8.0f);
	
	/** Essentially how many threads can concurrently access map. Keep as low as possible. */
	private static final int MAP_SHARDS = 2;
	
	/** How many players should be online before bulk actions become parallel */
	private static final long PARALLELISM_THRESHOLD = Long.MAX_VALUE;
	
	/** Stores a mapping from the channel the player is connected with to the player itself */
	private ConcurrentHashMap<Channel, Player> players;
	
	/**
	 * Creates a new map ready to put players in. 
	 */
	public WorldPlayerMap()
	{
		players = new ConcurrentHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, MAP_SHARDS);
	}
	
	/**
	 * Find a player in the world, given the Netty Channel they are connected to the server with. 
	 * @param channel Describes the connection between the Player and the server
	 * @return The Player connected with the provided Channel
	 */
	public Player get(Channel channel)
	{
		// I did my research. The Channel deep down has its own hash function, great for map use
		return players.get(channel);
	}
	
	/**
	 * Search for a player in this map based on the given predicate condition. This will look through all 
	 * of the players in the world until a match is found. If no match is found, null is returned. If a match 
	 * is found, that matching player is returned.
	 * @param condition The condition with which to match players
	 * @return The first Player matching the condition, or null if none were found
	 */
	public Player search(Predicate<Player> condition)
	{
		// Search through all players
		return players.searchValues(PARALLELISM_THRESHOLD, (p) ->
		{
			// Checking to see if a player matches the condition
			return condition.test(p) ? p : null;
		});
	}
	
	/**
	 * Check to see if the condition matches any player in the world. This will become true and return as soon 
	 * as the first match is found.
	 * @param condition The condition with which to match players
	 * @return True if a player in the world matches the condition, false if not
	 */
	public boolean hasMatchingPlayer(Predicate<Player> condition)
	{
		// null means no match was found during the search
		return null != search(condition);
	}
	
	/**
	 * Place a Player into the World. (So they are in the global list of players) 
	 * This needs to be done as part of logging in, early on
	 * @param channel The Channel linking the player and server
	 * @param player The new Player that just connected
	 */
	public void place(Channel channel, Player player)
	{
		players.put(channel, player);
		
		// Report that the number of players online has changed.
		StatTracker.instance().updatePlayersOnline(+1);
	}
	
	/**
	 * Remove a player from the global list. If you don't directly have the channel, 
	 * recall the Player object has a PacketSender which has the Channel. 
	 * This should be done on log out and to clean up inactive connections. 
	 * @param channel The Channel the player is connected with
	 */
	public void remove(Channel channel)
	{
		players.remove(channel);
		
		// Report that the number of players online has changed.
		StatTracker.instance().updatePlayersOnline(-1);
	}
	
	/**
	 * Perform the given action on all players, regardless of whether they are 
	 * still logging in, logged in, or disconnected but still in the map. Null values 
	 * should not be encountered, so null checks should not be needed. If an exception is 
	 * thrown, the iteration will abruptly stop. 
	 * @param action What to do with each Player
	 */
	public void forAll(Consumer<Player> action)
	{
		players.forEachValue(PARALLELISM_THRESHOLD, action);
	}
	
	/**
	 * Perform the given action only for players that are currently logged into the game. 
	 * @param action What to do with each logged in Player
	 */
	public void forAllLoggedIn(Consumer<Player> action)
	{
		players.forEachValue(PARALLELISM_THRESHOLD, new LoggedInFilter(action));
	}
	
	/**
	 * Specialized consumer which will only pass on the accept call if the player 
	 * is logged in. 
	 */
	private static class LoggedInFilter extends ConsumerFilter<Player> {

		/**
		 * Create a new filter that will call the given consumer's accept method only if 
		 * the player is logged in. 
		 * @param consumer The composed consumer
		 */
		public LoggedInFilter(Consumer<Player> consumer)
		{
			super(consumer);
		}

		/**
		 * Allows the method call through only if the given player object is logged in
		 * @param p The player to apply the accept method to
		 */
		@Override
		public void accept(Player p)
		{
			if(p.getLoginState() == LoginState.LOGGED_IN)
			{
				passFilter(p);
			}
		}
		
	}

}
