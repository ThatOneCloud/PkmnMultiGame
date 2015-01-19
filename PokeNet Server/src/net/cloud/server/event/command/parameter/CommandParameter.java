package net.cloud.server.event.command.parameter;

import net.cloud.server.event.command.CommandException;
import net.cloud.server.event.command.argument.CommandArgument;

/**
 * A parameter is what a Command expects to be provided as a variable of sorts. 
 * A parameter contains an argument, which holds the actual value. 
 *
 * @param <V> The type of the value held by the parameter
 */
public abstract class CommandParameter<V> {
	
	/** The argument holds the actual value */
	private CommandArgument<V> argument;
	
	/**
	 * Creates a new Parameter, with the specified argument. 
	 * This is intended to be a prototype argument, and parameters can later then 
	 * be added to a command via <code>newParsedInstance(String)</code><br>
	 * For example: <code>new RequiredParameter(ArgumentPrototyeps.STRING)</code>
	 * @param argument The argument - a Prototype - to specify what type the argument should be
	 */
	public CommandParameter(CommandArgument<V> argument)
	{
		this.argument = argument;
	}
	
	/**
	 * Create a new instance of the parameter.  This is useful for creating an instance 
	 * of the same type, without necessarily knowing the type (letting dynamic binding do the work)
	 * The new instance is not a clone, and the details are left to the subclass. 
	 * The valueString is used to create a new argument within the parameter
	 * @param valueString The string that will be parsed into the new value
	 * @return A new CommandParameter instance, of the same type, with the given new value
	 * @throws CommandException If parsing is not successful
	 */
	public abstract CommandParameter<V> newParsedInstance(String valueString) throws CommandException;

	/**
	 * @return The underlying argument - which holds the value
	 */
	public CommandArgument<V> getArgument() {
		return argument;
	}
	
	/**
	 * A convenience method to <code>getArgument().getValue()</code>
	 * @return The value held by the argument
	 */
	public V getArgValue() {
		return argument.getArgValue();
	}
	
}
