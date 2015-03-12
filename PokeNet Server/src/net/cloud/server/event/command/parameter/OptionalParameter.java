package net.cloud.server.event.command.parameter;

import net.cloud.server.event.command.CommandException;
import net.cloud.server.event.command.argument.CommandArgument;

/**
 * A parameter that a command does not required - it may or may not be provided. 
 * Has two names associated with it, one of which is needed. Also has a value. 
 *
 * @param <V> The type of the value
 */
public class OptionalParameter<V> extends CommandParameter<V> {
	
	/** A brief name that can be used to refer to the parameter */
	private String shortName;
	
	/** A longer, more descriptive name used to refer to the parameter */
	private String longName;
	
	/**
	 * Create a new [immutable] parameter with the given fields
	 * @param shortName The brief name associated with this parameter
	 * @param longName The full name associated with this parameter
	 * @param argument The value in this parameter
	 */
	public OptionalParameter(String shortName, String longName, CommandArgument<V> argument)
	{
		super(argument);
		this.shortName = shortName;
		this.longName = longName;
	}
	
	/**
	 * Create a new instance of this parameter from an existing instance. 
	 * The names will be the same, but the argument will be different
	 * @param valueString The new value of the argument
	 * @throws CommandException If the value could not be parsed
	 */
	@Override
	public OptionalParameter<V> newParsedInstance(String valueString) throws CommandException
	{
		// New Argument to use in the new Parameter
		CommandArgument<V> newArg = super.getArgument().newParsedInstance(valueString);
		
		// Weak reference to the name strings, and use the new argument
		return new OptionalParameter<V>(shortName, longName, newArg);
	}

	/**
	 * @return The full name associated with this parameter
	 */
	public String getLongName()
	{
		return longName;
	}

	/**
	 * @return The brief name associated with this parameter
	 */
	public String getShortName()
	{
		return shortName;
	}

}
