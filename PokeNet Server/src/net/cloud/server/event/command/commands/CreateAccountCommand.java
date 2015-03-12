package net.cloud.server.event.command.commands;

import net.cloud.server.entity.player.AccountCreationResult;
import net.cloud.server.entity.player.PlayerFactory;
import net.cloud.server.event.command.Command;
import net.cloud.server.event.command.argument.ArgumentPrototypes;
import net.cloud.server.event.command.parameter.OptionalParameter;
import net.cloud.server.event.command.parameter.RequiredParameter;

/**
 * A command which can be used to create a new player account on the server side 
 * of things. Obeys the usual constraints, and will give back a status message on the 
 * results of the attempt.
 */
public class CreateAccountCommand extends AbstractCommand {
	
	/** The parameters this command must have provided to it */
	private static RequiredParameter<?>[] allReqParams =
	{
		new RequiredParameter<String>(ArgumentPrototypes.STRING),
		new RequiredParameter<String>(ArgumentPrototypes.STRING)
	};
	
	/** Create an empty prototype command */
	public CreateAccountCommand() 
	{
		super();
	}

	@Override
	public Command newPrototypedInstance() 
	{
		return new CreateAccountCommand();
	}

	/** @return A message on the success of account creation */
	@Override
	public String doCommand() 
	{
		String user = super.<String>getReqParam(0).getArgValue();
		String pass = super.<String>getReqParam(1).getArgValue();
		
		// Provide the arguments we've got and grab the results
		AccountCreationResult result = PlayerFactory.createNewAccount(user, pass);
		
		// The result message is sufficient
		return result.getMessage();
	}

	@Override
	protected OptionalParameter<?>[] getAllOptionalParameters() 
	{
		return null;
	}

	@Override
	protected RequiredParameter<?>[] getAllRequiredParameters() 
	{
		return allReqParams;
	}

}
