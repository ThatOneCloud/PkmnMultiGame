package net.cloud.mmo.event.command;

import net.cloud.mmo.event.command.parameter.OptionalParameter;

public abstract class AbstractCommand implements Command {
	
	// TODO: lists for the actual provided arguments
	
	protected abstract OptionalParameter<?>[] getAllOptionalParameters();
	
	protected abstract void getAllRequiredParameters();
	
	@Override
	public void parseArguments(String argumentLine)
	{
		// TODO: Implement
	}

}
