package net.cloud.mmo.event.command.argument;

import net.cloud.mmo.event.command.CommandException;

public abstract class CommandArgument<V> {
	
	private V argValue;
	
	protected CommandArgument()
	{
		this.argValue = null;
	}
	
	public CommandArgument(V value)
	{
		this.argValue = value;
	}
	
	public abstract CommandArgument<V> newParsedInstance(String valueString) throws CommandException;

	public V getArgValue() {
		return argValue;
	}

}
