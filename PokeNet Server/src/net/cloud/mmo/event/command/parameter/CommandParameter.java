package net.cloud.mmo.event.command.parameter;

import net.cloud.mmo.event.command.argument.CommandArgument;

public abstract class CommandParameter<V> {
	
	private CommandArgument<V> argument;

	public CommandArgument<V> getArgument() {
		return argument;
	}
	
}
