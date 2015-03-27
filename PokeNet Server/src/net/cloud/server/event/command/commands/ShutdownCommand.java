package net.cloud.server.event.command.commands;

import net.cloud.server.Server;
import net.cloud.server.event.command.Command;
import net.cloud.server.game.World;

/**
 * A command that will shut down the server when called
 */
public class ShutdownCommand extends NoParameterCommand {
	
	/** Default constructor which calls super() */
	public ShutdownCommand()
	{
		super();
	}

	/**
	 * Get a ShutdownCommand instance from a prototype
	 * @return A ShutdownCommand instance to build up
	 */
	@Override
	public Command newPrototypedInstance()
	{
		return new ShutdownCommand();
	}

	/**
	 * Have the server do its shutdown procedure
	 * @return A message saying shutdown is starting
	 */
	@Override
	public String doCommand()
	{
		// Get all players out of the way, first
		World.instance().cancelSaveTask();
		World.instance().kickAllPlayers();
		
		// Tell the server it's time to shutdown
		Server.getInstance().shutdown();
		
		return "Shutdown command called";
	}

}
