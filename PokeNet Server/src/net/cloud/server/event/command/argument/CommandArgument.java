package net.cloud.server.event.command.argument;

import net.cloud.server.event.command.CommandException;

/**
 * A CommandArgument holds a value passed to a Command
 *
 * @param <V> The type of the value
 */
public abstract class CommandArgument<V> {
	
	/** The value we're holding onto */
	private V argValue;
	
	/**
	 * Protected default constructor so that it cannot be created outside of the package. 
	 * The default constructor initializes the value to null.
	 */
	protected CommandArgument()
	{
		this.argValue = null;
	}
	
	/**
	 * Create a new Argument holding the given value
	 * @param value The value the Argument will have
	 */
	public CommandArgument(V value)
	{
		this.argValue = value;
	}
	
	/**
	 * Create a new CommandArgument from an existing one.  The new object will be of the same 
	 * type. (Thanks, dynamic binding!) but will have a new value
	 * @param valueString The string which will be parsed to give the value
	 * @return The new CommandArgument instance
	 * @throws CommandException If the value could not be parsed
	 */
	public abstract CommandArgument<V> newParsedInstance(String valueString) throws CommandException;

	/**
	 * @return The value of this argument
	 */
	public V getArgValue() {
		return argValue;
	}

}
