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
		
		
		// Figure out the name of the command
		String commandName;
		commandName = parseCommandName();

		// Pull out the rest of the line, which will be the parameters
		String commandParams;
		commandParams = parseParameters(commandName);

		// Tricky part - creating a Command object for this info
		// TODO: Command creation
		// If it fails - CommandException created
		// Have map of prototype commands, created on demand
		// prototype command gives access to list of [static] prototype parameters
		// those in turn have a prototype argument (via some constant - CommandArguments.STRING for example)
		// that prototype argument attempts to parse a value and return an actual Argument object to use
		// (CommandException on failure of this parsing)
		Command command = null;

		// Finally, we have a command. Hand it off to the TaskEngine
		Future<String> result = TaskEngine.getInstance().submitImmediate(command::doCommand);
		
		// Return the result Future so the caller can wait for the command to complete if they'd like
		return result;
	}

}
