package net.cloud.mmo.event.command.commands;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.cloud.mmo.event.command.Command;
import net.cloud.mmo.event.command.CommandException;
import net.cloud.mmo.event.command.parameter.OptionalParameter;
import net.cloud.mmo.event.command.parameter.RequiredParameter;
import net.cloud.mmo.util.StringUtil;

public abstract class AbstractCommand implements Command {
	
	private List<OptionalParameter<?>> providedOptionalParameters;
	private List<RequiredParameter<?>> providedRequiredParameters;
	
	public AbstractCommand()
	{
		// Initialize lists - subclasses should call super() constructor
		providedOptionalParameters = new ArrayList<>();
		providedRequiredParameters = new ArrayList<>();
	}
	
	protected abstract OptionalParameter<?>[] getAllOptionalParameters();
	
	protected abstract RequiredParameter<?>[] getAllRequiredParameters();
	
	@Override
	public void parseArguments(String argumentLine) throws CommandException
	{
		// Keep track of which required parameter we're at
		int reqParamIdx = 0;
		
		// Give ourselves a StringBuilder to work with - so we can move through it easier
		StringBuilder argBuilder = new StringBuilder(argumentLine.trim());
		
		// Step through each parameter in the argumentLine
		while(argBuilder.length() > 0)
		{
		
			// Decide if it's an optional or required parameter
			if(argBuilder.charAt(0) == '-')
			{
				// Dealing with what should be an optional parameter
				parseOptParam(argBuilder);
			} else {
				// Dealing with what should be a required parameter
				parseReqParam(argBuilder, reqParamIdx);
				
				// After a successful addition, move onto the next req param in the list
				reqParamIdx++;
			}
		}
		
		// Before we're done, have to check that every required parameter was provided
		if(reqParamIdx != getAllRequiredParameters().length)
		{
			throw new CommandException("Invalid number of required parameters");
		}
	}
	
	private void parseOptParam(StringBuilder argBuilder) throws CommandException
	{
		// Optional parameters have a short and long name, - and -- respectively
		boolean longForm = argBuilder.length() >= 2 && argBuilder.charAt(1) == '-';
		
		// The name follows the dashes
		String paramName;
		try {
			paramName = longForm ? (StringUtil.extractCommandToken(argBuilder.delete(0, 2))) :
								   (StringUtil.extractCommandToken(argBuilder.delete(0, 1)));
		} catch (ParseException e) {
			// Couldn't extract a token. Re-throw as CommandException
			throw new CommandException("Invalid command formatting", e);
		}
		
		// The name was removed during extractCommandToken. Now let it find the value
		String paramValue;
		try {
			paramValue = StringUtil.extractCommandToken(argBuilder);
		} catch (ParseException e) {
			throw new CommandException("Optional argument has no value", e);
		}
		
		// Finds a matching parameter, attempts to parse, and then add to the list of parameters
		findAndAddOptParam(longForm, paramName, paramValue);
	}

	private void findAndAddOptParam(boolean longForm, String paramName, String paramValue) throws CommandException {
		// Some variables we'll need now
		OptionalParameter<?> newParam = null;
		boolean foundMatch = false;

		// Optional parameters have no enforced order - look through them all
		OptionalParameter<?>[] allOptParams = getAllOptionalParameters();
		for(int i = 0; i < allOptParams.length; ++i)
		{
			// Short names are awesome sometimes.. the current parameter we're looking at
			OptionalParameter<?> p = allOptParams[i];
			
			// See if the name matches
			if((longForm && p.getLongName().equalsIgnoreCase(paramName)) || 
			   (!longForm && p.getShortName().equalsIgnoreCase(paramName)))
			{
				foundMatch = true;
				
				// Found one that matches - let it try to parse itself
				// If it fails, it will throw a CommandException - which we just let get re-thrown
				newParam = p.newParsedInstance(paramValue);
				
				// No need to continue looking
				break;
			}
		}
		
		// foundMatch flag is used so we know if we found an optional parameter with a matching name
		if(!foundMatch || newParam == null)
		{
			// Never found a match. Command is improperly formatted
			throw new CommandException("Unknown optional parameter: " + paramName);
		}
		
		// So now we have a parameter object parsed and ready to add
		this.provideOptionalParameter(newParam);
	}
	
	private void parseReqParam(StringBuilder argBuilder, int reqParamIdx) throws CommandException
	{
		// The value is the only thing
		String paramValue;
		try {
			paramValue = StringUtil.extractCommandToken(argBuilder);
		} catch (ParseException e) {
			throw new CommandException("Required argument has no value", e);
		}
		
		// Make sure there are still req params left (ie that not too many were provided)
		if(reqParamIdx >= getAllRequiredParameters().length)
		{
			throw new CommandException("Too many parameters provided");
		}
		
		// Required parameters come in the order listed
		RequiredParameter<?> p = getAllRequiredParameters()[reqParamIdx];

		// Have the next one in line try to parse itself
		// If it fails, it will throw a CommandException - which we just let get re-thrown
		RequiredParameter<?> newParam = p.newParsedInstance(paramValue);
		
		// Add the parameter
		this.provideRequiredParameter(newParam);
	}
	
	public Iterator<OptionalParameter<?>> getProvidedOptionalParameters()
	{
		return providedOptionalParameters.iterator();
	}
	
	public void provideOptionalParameter(OptionalParameter<?> parameter)
	{
		providedOptionalParameters.add(parameter);
	}
	
	public Iterator<RequiredParameter<?>> getProvidedRequiredParameters()
	{
		return providedRequiredParameters.iterator();
	}
	
	public void provideRequiredParameter(RequiredParameter<?> parameter)
	{
		providedRequiredParameters.add(parameter);
	}

}
