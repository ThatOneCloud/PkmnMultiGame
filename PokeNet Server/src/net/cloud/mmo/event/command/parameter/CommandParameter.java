package net.cloud.mmo.event.command.parameter;

import net.cloud.mmo.event.command.argument.CommandArgument;

public abstract class CommandParameter<V> {
	
	private CommandArgument<V> argument;
	
	public CommandParameter(CommandArgument<V> argument)
	{
		this.argument = argument;
	}
	
	public abstract CommandParameter<V> newParsedInstance(String valueString);

	public CommandArgument<V> getArgument() {
		return argument;
	}
	
}
