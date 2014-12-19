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
	
	public Future<String> handleCommand(String commandLine) throws CommandException
	{
		// Use a StringBuilder to manipulate the command line as we go
		StringBuilder commandBuilder = new StringBuilder(commandLine);
		
		// Figure out the name of the command
		String commandName = parseCommandName(commandBuilder);

		// Pull out the rest of the line, which will be the parameters
		String commandParams = parseParameters(commandBuilder, commandName);
		
		
		
		System.out.println("Command name: " + commandName);
		System.out.println("Command params: " + commandParams);
		
		
		
		
		// Obtain a prototype instance, which we then...
		Command prototype = getPrototypeCommand(commandName);
		
		// ... use to create another instance to have the real parameters
		Command command = prototype.newPrototypedInstance();
		
		// The real instance is responsible for parsing its own parameters
		command.parseArguments(commandParams);

		// Finally, we have a command. Hand it off to the TaskEngine
		Future<String> result = TaskEngine.getInstance().submitImmediate(command::doCommand);
		
		// Return the result Future so the caller can wait for the command to complete if they'd like
		return result;
	}
	
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
