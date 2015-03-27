package net.cloud.server.event.command;

import java.util.concurrent.Future;

import net.cloud.server.event.task.TaskEngine;

/**
 * An entrance point to the command subsystem. 
 * Commands can be submitted to the CommandHandler, where they are then 
 * constructed and passed on for execution.
 */
public class CommandHandler {
	
	/** Singleton instance */
	private static CommandHandler instance;
	
	private CommandHandler()
	{
	}
	
	/**
	 * @return A reference to the singleton instance
	 */
	public static CommandHandler instance()
	{
		if(instance == null)
		{
			synchronized(CommandHandler.class)
			{
				if(instance == null)
				{
					instance = new CommandHandler();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Execute a command. Takes in the entire line for the command, including the :: indicating 
	 * a command.  A Future is returned, which will only have a value once the command has finished 
	 * executing.  The value in the Future is a result message from the command.
	 * @param commandLine The string calling the command, in entirety
	 * @return A Future bearing the pending results of the command's execution
	 * @throws CommandException If the command could not be executed. The message is user-friendly and meaningful
	 */
	public Future<String> handleCommand(String commandLine) throws CommandException
	{
		// Use a StringBuilder to manipulate the command line as we go
		StringBuilder commandBuilder = new StringBuilder(commandLine);
		
		// Figure out the name of the command
		String commandName = parseCommandName(commandBuilder);

		// Pull out the rest of the line, which will be the parameters
		String commandParams = parseParameters(commandBuilder, commandName);
		
		// Obtain a prototype instance, which we then...
		Command prototype = getPrototypeCommand(commandName);
		
		// ... use to create another instance to have the real parameters
		Command command = prototype.newPrototypedInstance();
		
		// The real instance is responsible for parsing its own parameters
		command.parseArguments(commandParams);

		// Finally, we have a command. Hand it off to the TaskEngine
		Future<String> result = TaskEngine.instance().submitImmediate(command::doCommand);
		
		// Return the result Future so the caller can wait for the command to complete if they'd like
		return result;
	}
	
	/**
	 * Determine the name of a command, from the entire command line
	 * @param commandLine The command line in entirety
	 * @return The name of the command being called
	 * @throws CommandException If the name could not be determined
	 */
	private String parseCommandName(StringBuilder commandLine) throws CommandException
	{
		// A command needs to be at least three characters long
		if(commandLine.length() < 3)
		{
			throw new CommandException("Commands start with '::' followed by the name of the command");
		}
		
		// A command starts with "::" and the name comes immediately afterwards
		if(commandLine.charAt(0) != ':' || commandLine.charAt(1) != ':')
		{
			// For some reason it didn't start with "::".. not okay
			throw new CommandException("Commands start with '::'");
		}
		
		// The name is immediately after :: until the first space or end of the string
		int nameBeginIndex = 2;
		int nameEndIndex = commandLine.indexOf(" ");
		nameEndIndex = ((nameEndIndex == -1) ? commandLine.length() : nameEndIndex);
		
		// Now that we have where the name starts and ends, the name is the substring
		String name = commandLine.substring(nameBeginIndex, nameEndIndex);
		
		// If the name is empty, that's not okay
		if(name.length() == 0)
		{
			throw new CommandException("Command has no name");
		}
		
		// Good to go - return the name
		return name;
	}
	
	/**
	 * Simply determine the string that describes the parameters being passed to the command
	 * @param commandLine The text that is calling the command
	 * @param commandName The name of the command being called
	 * @return The text string representing the parameters being passed to the command
	 * @throws CommandException If the parameter string could not be pulled out
	 */
	private String parseParameters(StringBuilder commandLine, String commandName) throws CommandException
	{
		// Start index is after the name
		int startIndex = commandLine.indexOf(commandName) + commandName.length();
		
		// Maybe that's the end of the string (no params)
		if(startIndex >= commandLine.length())
		{
			// So an empty string will work
			return "";
		}
		
		// Otherwise we expect a space to follow the name
		if(commandLine.charAt(startIndex) != ' ')
		{
			throw new CommandException("Command parameters are separated by a space");
		}
		
		// Space is there. Move forward past it, and the parameters are the rest of the string
		return commandLine.substring(startIndex + 1);
	}
	
	/**
	 * Obtain a reference to the prototype command, from its name. 
	 * Then it can be used to create a new instance of right command type
	 * @param commandName The name of the command being created
	 * @return A reference to the command's prototype object
	 * @throws CommandException If there is no matching command
	 */
	private Command getPrototypeCommand(String commandName) throws CommandException
	{
		// Check if a prototype has been declared
		if(!CommandPrototypes.exists(commandName.toLowerCase()))
		{
			// Nope, so an exception stating such is issued
			throw new CommandException("No such command: " + commandName);
		}
		
		// At this point, an entry is in the map or an exception has been thrown
		return CommandPrototypes.get(commandName.toLowerCase());
	}

}
