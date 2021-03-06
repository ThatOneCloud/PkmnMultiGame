package net.cloud.server.event.command.commands;

import java.util.Optional;

import net.cloud.server.event.command.Command;
import net.cloud.server.event.command.argument.ArgumentPrototypes;
import net.cloud.server.event.command.parameter.OptionalParameter;
import net.cloud.server.event.command.parameter.RequiredParameter;

/**
 * A simple command that accepts one required and one optional string parameter. 
 * The command returns the required parameter, concatenated with the optional parameter 
 * if it was provided.
 */
public class EchoCommand extends AbstractCommand {
	
	/** The optional parameters this command could possibly accept */
	private static OptionalParameter<?>[] allOptParams = 
	{
		new OptionalParameter<String>("c", "concat", ArgumentPrototypes.STRING)
	};

	/** The parameters this command must have provided to it */
	private static RequiredParameter<?>[] allReqParams =
	{
		new RequiredParameter<String>(ArgumentPrototypes.STRING)
	};
	
	/** Create an empty EchoCommand */
	public EchoCommand()
	{
		super();
	}

	/** Create a new dynamically binded instance */
	@Override
	public Command newPrototypedInstance()
	{
		return new EchoCommand();
	}

	/** @return An echo of the parameters - the optional parameter is concatenated at the end */
	@Override
	public String doCommand()
	{
		// Start with the required parameter
		String echo = super.<String>getReqParam(0).getArgValue();

		// Concatenate if there's an optional parameter
		Optional<OptionalParameter<String>> concatParam = super.getOptParam("concat");
		if(concatParam.isPresent())
		{
			echo += concatParam.get().getArgValue();
		}
		
		return echo;
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
