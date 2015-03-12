package net.cloud.server.event.command.commands;

import net.cloud.server.event.command.Command;
import net.cloud.server.event.command.argument.ArgumentPrototypes;
import net.cloud.server.event.command.parameter.OptionalParameter;
import net.cloud.server.event.command.parameter.RequiredParameter;

/**
 * A Command that I'll use for testing various things. 
 * It was the first command in the game. 
 * Who knows what it'll do at any given time!
 */
public class TestCommand extends AbstractCommand {
	
	// Define the optional parameters this command could possibly accept
	private static OptionalParameter<?>[] allOptParams =
	{
		new OptionalParameter<String>("p", "parameter", ArgumentPrototypes.STRING),
		new OptionalParameter<String>("a", "aaa", ArgumentPrototypes.STRING),
		new OptionalParameter<String>("b", "bbb", ArgumentPrototypes.STRING)
	};
	
	// Define the parameters this command must have provided to it
	private static RequiredParameter<?>[] allReqParams =
	{
		new RequiredParameter<String>(ArgumentPrototypes.STRING),
		new RequiredParameter<String>(ArgumentPrototypes.STRING)
	};
	
	/**
	 * Create a TestCommand - Must be provided parameters
	 */
	public TestCommand()
	{
		// Make sure to call super() so the lists are intialized
		super();
	}

	/**
	 * Take the action of the command. TestCommand does whatever it wants.
	 * @return A result message. Useful feedback for the user
	 */
	@Override
	public String doCommand()
	{
		return "do command";
	}
	
	/**
	 * Create a new instance of the TestCommand, ready to be 
	 * parsed and provided parameters.  Useful when called from a prototype, 
	 * so that dynamic binding can give the right type back.
	 * @return A TestCommand instance to build up
	 */
	@Override
	public Command newPrototypedInstance()
	{
		return new TestCommand();
	}

	@Override
	protected OptionalParameter<?>[] getAllOptionalParameters()
	{
		return allOptParams;
	}

	@Override
	protected RequiredParameter<?>[] getAllRequiredParameters()
	{
		return allReqParams;
	}

}
