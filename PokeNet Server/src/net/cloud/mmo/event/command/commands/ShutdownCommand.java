package net.cloud.mmo.event.command.commands;

import net.cloud.mmo.Server;
import net.cloud.mmo.event.command.Command;

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
	public Command newPrototypedInstance() {
		return new ShutdownCommand();
	}

	/**
	 * Have the server do its shutdown procedure
	 * @return A message saying shutdown is starting
	 */
	@Override
	public String doCommand() {
		// Tell the server it's time to shutdown
		Server.getInstance().shutdown();
		
		return "Beginning shutdown";
	}

}
