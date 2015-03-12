package net.cloud.server.event.command.commands;

import net.cloud.server.event.command.Command;
import net.cloud.server.tracking.StatTracker;

public class ToggleStatTrackingCommand extends NoParameterCommand {
	
	/** Create an empty stat tracking command */
	public ToggleStatTrackingCommand()
	{
		super();
	}

	/** Create a new dynamically binded instance */
	@Override
	public Command newPrototypedInstance()
	{
		return new ToggleStatTrackingCommand();
	}

	/**
	 * Toggles the stat tracking mode. If the server is only temporarily tracking stats, 
	 * then it will be turned on or off. Of course, if the setting is ALWAYS_ON then it's 
	 * simply always on. 
	 * @return A message saying if tracking is now on or off (No details about the mode though)
	 */
	@Override
	public String doCommand()
	{
		// Have the stat tracker toggle. It'll do nothing if it's always on. 
		StatTracker.instance().toggleTracking();
		
		// Now base our message off whether or not its tracking. Not the mode per-say. 
		if(StatTracker.instance().tracking())
		{
			return "Stats are being tracked.";
		} else {
			return "Stats are not being tracked.";
		}
	}

}
