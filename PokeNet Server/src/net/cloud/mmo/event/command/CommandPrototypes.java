package net.cloud.mmo.event.command;

import java.util.HashMap;
import java.util.Map;

import net.cloud.mmo.event.command.commands.*;

/**
 * Contains a mapping of command names to prototype instances. 
 * These prototype instances can be used to create another instance 
 * of the same type.
 */
public class CommandPrototypes {
	
	/** A map from command name to prototype command */
	private static Map<String, Command> prototypes = new HashMap<>();
	
	// Static initializer - map will get populated the first time this class is referenced
	static
	{
		prototypes.put("test", new TestCommand());
		prototypes.put("echo", new EchoCommand());
		prototypes.put("shutdown", new ShutdownCommand());
	}
	
	/**
	 * Check to see if a command prototype exists
	 * @param commandName The name of the command. Lowercase by convention.
	 * @return True if a prototype exists for the command
	 */
	public static boolean exists(String commandName)
	{
		return prototypes.containsKey(commandName);
	}
	
	/**
	 * Obtain a reference to a prototype command
	 * @param commandName The name of the command. Lowercase by convention.
	 * @return The mapped reference, or null if one does not exist
	 */
	public static Command get(String commandName)
	{
		return prototypes.get(commandName);
	}

}
