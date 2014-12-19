package net.cloud.mmo.event.command.parameter;

import net.cloud.mmo.event.command.CommandException;
import net.cloud.mmo.event.command.argument.CommandArgument;

public class RequiredParameter<V> extends CommandParameter<V> {
	
	public RequiredParameter(CommandArgument<V> argument) {
		super(argument);
	}
	
	@Override
	public RequiredParameter<V> newParsedInstance(String valueString) throws CommandException
	{
		// New Argument to use in the new Parameter
		CommandArgument<V> newArg = super.getArgument().newParsedInstance(valueString);
		
		// Weak reference to the name strings, and use the new argument
		return new RequiredParameter<V>(newArg);
	}

}
