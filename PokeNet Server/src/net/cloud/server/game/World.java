package net.cloud.server.game;

import net.cloud.server.ConfigConstants;
import net.cloud.server.entity.player.LoginHandler;
import net.cloud.server.entity.player.save.PlayerSaveTask;
import net.cloud.server.event.task.TaskEngine;
import net.cloud.server.event.task.voidtasks.CancellableVoidTask;

/**
 * Represents the entire Game World.  There can be only one! (singleton class)
 * Keeps track of global information, such as all of the players online.
 */
public class World {
	
	/** Singleton instance of the World */
	private static World instance;
	
	/** A global list of Players, mapped to by the Channel they're connected with */
	private WorldPlayerMap playerMap;
	
	/** A global list of Players that have disconnect abruptly from the game */
	private DisconnectedPlayerMap disconnectMap;
	
	/** The save task */
	private CancellableVoidTask saveTask;
	
	/**
	 * Instantiate the world. Creates the player maps and starts the save task
	 */
	private World()
	{
		// Initialize map of online players
		playerMap = new WorldPlayerMap();
		
		// Initialize the map of disconnected players
		disconnectMap = new DisconnectedPlayerMap();
		
		// Now that we have the list of players, can safely kick off the saving task
		saveTask = new PlayerSaveTask();
		TaskEngine.instance().scheduleImmediate(ConfigConstants.SAVE_INTERVAL, saveTask);
	}
	
	/**
	 * Get a reference to the World instance, holding global information about the game world
	 * @return A reference to the single World instance
	 */
	public static World instance()
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
	 * Stop the save task from running - it will not save any more players immediately after this is called. 
	 * (Although it will finish saving a single player if it is currently on one)
	 */
	public void cancelSaveTask()
	{
		saveTask.cancel();
	}
	
	/**
	 * Tell each player to log out (This will consequently remove them from the world and save their data). 
	 * This applies to logged in players and disconnected players (their reconnect will be moved right ahead to failed)
	 */
	public void kickAllPlayers()
	{
		playerMap.forAll((p) -> LoginHandler.doLogout(p));
		
		disconnectMap.forAll((p) -> LoginHandler.doReconnectFailed(p));
	}

	/**
	 * Obtain a means of accessing and interacting with all of the players in the world
	 * @return A map of all players in the world
	 */
	public WorldPlayerMap getPlayerMap()
	{
		return playerMap;
	}
	
	/**
	 * Obtain a means of accessing the collection of players that are disconnected, but may still be able to reconnect
	 * @return A map of all disconnected players
	 */
	public DisconnectedPlayerMap getDisconnectMap()
	{
		return disconnectMap;
	}

}
