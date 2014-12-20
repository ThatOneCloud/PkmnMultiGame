package net.cloud.mmo.event.command.parameter;

import net.cloud.mmo.event.command.CommandException;
import net.cloud.mmo.event.command.argument.CommandArgument;

/**
 * A Parameter that a Command expects to be provided. 
 * Immutable, but can create new instances
 *
 * @param <V> The type of the argument's value
 */
public class RequiredParameter<V> extends CommandParameter<V> {
	
	/**
	 * Create a new Parameter that will contain the given argument
	 * @param argument The argument, complete with value, in this parameter
	 */
	public RequiredParameter(CommandArgument<V> argument) {
		super(argument);
	}
	
	/**
	 * Create a new instance from an existing instance. 
	 * The argument will be different - this time equivalent to the value of the passed in String
	 * @param valueString The value that the argument of this specific parameter will parse
	 * @throws CommandException If the argument could not be parsed
	 */
	@Override
	public RequiredParameter<V> newParsedInstance(String valueString) throws CommandException
	{
		// New Argument to use in the new Parameter
		CommandArgument<V> newArg = super.getArgument().newParsedInstance(valueString);
		
		// Weak reference to the name strings, and use the new argument
		return new RequiredParameter<V>(newArg);
	}

}
