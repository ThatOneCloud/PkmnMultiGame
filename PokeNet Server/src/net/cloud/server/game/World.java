package net.cloud.server.game;

/**
 * Represents the entire Game World.  There can be only one! (singleton class)
 * Keeps track of global information, such as all of the players online.
 */
public class World {
	
	/** Singleton instance of the World */
	private static World instance;
	
	/** A global list of Players, mapped to by the Channel they're connected with */
	private WorldPlayerMap playerMap;
	
	private World()
	{
		// Initialize map of online players
		playerMap = new WorldPlayerMap();
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
	 * Obtain a means of accessing and interacting with all of the players in the world
	 * @return A map of all players in the world
	 */
	public WorldPlayerMap getPlayerMap() {
		return playerMap;
	}

}
