package net.cloud.mmo.event.command;

import java.util.concurrent.Future;

import net.cloud.mmo.event.task.TaskEngine;

/**
 * 
 * @author Blake
 *
 */
public class CommandHandler {
	
	private static CommandHandler instance;
	
	private CommandHandler()
	{
	}
	
	public static CommandHandler getInstance()
	{
		if(instance == null)
		{
			instance = new CommandHandler();
		}
		
		return instance;
	}
	
	public Future<String> handleCommand(String commandLine)
	{
		// TODO: create command, handle it, deal with exceptions
		
		
		// Parse the name of the command
		String commandName;
		commandName = parseCommandName();

		// And pull out the rest (which will be the parameters)
		String commandParams;
		commandParams = parseParamters(commandName);

		// Tricky part - creating a Command object for this info
		// TODO: Command creation
		Command command = null;

		// Finally, we have a command. Hand it off to the TaskEngine
		Future<String> result = TaskEngine.getInstance().submitImmediate(command::doCommand);
		
		// Return the result Future so the caller can wait for the command to complete if they'd like
		return result;
	}

}
