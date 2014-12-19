package net.cloud.mmo.event.command.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.cloud.mmo.event.command.Command;
import net.cloud.mmo.event.command.parameter.OptionalParameter;

public abstract class AbstractCommand implements Command {
	
	// TODO: lists for the actual provided arguments
	private List<OptionalParameter<?>> providedOptionalParameters;
	
	public AbstractCommand()
	{
		// Initialize arrays - subclasses should call super() constructor
		providedOptionalParameters = new ArrayList<>();
	}
	
	protected abstract OptionalParameter<?>[] getAllOptionalParameters();
	
	protected abstract void getAllRequiredParameters();
	
	@Override
	public void parseArguments(String argumentLine)
	{
		// TODO: Implement
	}
	
	public Iterator<OptionalParameter<?>> getProvidedOptionalParameters()
	{
		return providedOptionalParameters.iterator();
	}
	
	public void provideOptionalParameter(OptionalParameter<?> parameter)
	{
		providedOptionalParameters.add(parameter);
	}

}
