package net.cloud.mmo.event.command;

import java.util.HashMap;
import java.util.Map;

import net.cloud.mmo.event.command.commands.*;

public class CommandPrototypes {
	
	private static Map<String, Command> prototypes = new HashMap<>();
	
	static
	{
		prototypes.put("test", new TestCommand());
	}
	
	public static boolean exists(String commandName)
	{
		return prototypes.containsKey(commandName);
	}
	
	public static Command get(String commandName)
	{
		return prototypes.get(commandName);
	}

}
