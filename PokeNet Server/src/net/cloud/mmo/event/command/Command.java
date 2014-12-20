package net.cloud.mmo.event.command;

/**
 * A Command, which is meant to perform some action dynamically. 
 * It can be provided parameters.  A command is submitted and created 
 * through the CommandHandler, from a string containing information about the command.
 */
public interface Command {
	
	/**
	 * Create a new instance of a command, which will be the same type as the concrete 
	 * class implementing this interface. This is so a prototype can be used.
	 * @return A new instance of a concrete command type
	 */
	public Command newPrototypedInstance();
	
	/**
	 * Take a line of text which is used to construct parameters that are handed to the command
	 * @param argumentLine The parameters this command is given
	 * @throws CommandException If the parameters cannot be parsed
	 */
	public void parseArguments(String argumentLine) throws CommandException;
	
	/**
	 * Take the action of the command.  The result is returned as a user friendly string
	 * @return A user-friendly result message
	 */
	public String doCommand();

}
