package net.cloud.server.event.command.commands;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import net.cloud.server.event.command.Command;
import net.cloud.server.event.command.CommandException;
import net.cloud.server.event.command.parameter.OptionalParameter;
import net.cloud.server.event.command.parameter.RequiredParameter;
import net.cloud.server.util.StringUtil;

/**
 * An AbstractCommand implements the Command interface,  and imposes some new methods.
 * Most importantly, an AbstractCommand has code to handle parsing the parameters.
 */
public abstract class AbstractCommand implements Command {
	
	/** List of optional parameters that were actually given to this command */
	protected List<OptionalParameter<?>> providedOptionalParameters;
	
	/** List of required parameters that were actually given to this command */
	protected List<RequiredParameter<?>> providedRequiredParameters;
	
	/**
	 * Initializes the parameter lists
	 */
	public AbstractCommand()
	{
		// Initialize lists - subclasses should call super() constructor
		providedOptionalParameters = new ArrayList<>();
		providedRequiredParameters = new ArrayList<>();
	}
	
	/**
	 * @return An array with all of the optional parameters the command can possibly accept
	 */
	protected abstract OptionalParameter<?>[] getAllOptionalParameters();
	
	/**
	 * @return An array with all of the parameters the command must have
	 */
	protected abstract RequiredParameter<?>[] getAllRequiredParameters();
	
	/**
	 * Takes a string containing the arguments provided to the command, 
	 * and parses it into individual parameters - where are added to the lists.
	 * @param argumentLine The parameters passed to the command
	 * @throws CommandException If for some reason parsing is unsuccessful - it will have a useful message
	 */
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

		// No parameters, but none provided. So that's okay
		if(getAllRequiredParameters() == null && reqParamIdx == 0)
		{
			// Erm, No-op. This condition is here more for clarity...
		}
		// No parameters... somehow one was provided?
		else if(getAllRequiredParameters() == null && reqParamIdx != 0)
		{
			throw new CommandException("This command does not require parameters");
		}
		// Not every required parameter was provided
		else if(reqParamIdx != getAllRequiredParameters().length)
		{
			throw new CommandException("Invalid number of required parameters");
		}
	}
	
	/**
	 * Take care of parsing an optional parameter. When done, it is added to the list.
	 * @param argBuilder The StringBuilder with the remaining parameter line
	 * @throws CommandException If the parameter could not be parsed
	 */
	private void parseOptParam(StringBuilder argBuilder) throws CommandException
	{
		// Check if there are no optional parameters - we've been provided one, so fail before even looking
		if(getAllOptionalParameters() == null)
		{
			throw new CommandException("This command does not accept any optional parameters");
		}
		
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

	/**
	 * Determine which of the optional parameters - if any - match the one provided. 
	 * When done, the parameter is added to the list
	 * @param longForm True if the name is the long name
	 * @param paramName The name of the optional parameter
	 * @param paramValue The value to be given to the parameter
	 * @throws CommandException If the parameter could not be added
	 */
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
	
	/**
	 * Take care of parsing a required parameter. When done, it is added to the list.
	 * @param argBuilder The StringBuilder with the remaining parameter line
	 * @param reqParamIdx The index of the RequiredParameter we're currently looking at
	 * @throws CommandException If the parameter could not be parsed
	 */
	private void parseReqParam(StringBuilder argBuilder, int reqParamIdx) throws CommandException
	{
		// Check if no required parameters. We've been provided one, so fail before even proceeding
		if(getAllRequiredParameters() == null)
		{
			throw new CommandException("This command does not require any parameters");
		}
		
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
	
	/**
	 * @return The optional parameters *actually* given to the command after parsing
	 */
	public Iterator<OptionalParameter<?>> getProvidedOptionalParameters()
	{
		return providedOptionalParameters.iterator();
	}
	
	/**
	 * Obtain an OptionalParameter by name. If the parameter has been provided, 
	 * the returned Optional will contain the parameter.  If it has not been, the 
	 * returned Optional will be empty.  It can be checked for the presence of a value, 
	 * to safely decide on an action. Optional-ception!<br>
	 * Usage note: Prefer storing the optional. Each call of this method will search 
	 * again and create a new Optional.
	 * @param name The name of the parameter. Short or long form. Case sensitive.
	 * @param <T> The type of the value within the parameter
	 * @return An Optional wrapping the parameter if present, or an empty optional if not.
	 */
	@SuppressWarnings("unchecked")
	public <T> Optional<OptionalParameter<T>> getOptParam(String name)
	{
		Iterator<OptionalParameter<?>> optParamIt = getProvidedOptionalParameters();
		
		// Look for a provided optional parameter matching either name
		while(optParamIt.hasNext())
		{
			OptionalParameter<?> optParam = optParamIt.next();
			
			if(optParam.getShortName().equals(name) || optParam.getLongName().equals(name))
			{
				// Found one. Wrap in an Optional and return (Optional-ception!)
				return Optional.of((OptionalParameter<T>) optParam);
			}
		}
		
		// No match. Which is fine, its existence is optional. However, there will be no value.
		return Optional.empty();
	}
	
	/**
	 * Tell this command that the optional parameter is being passed to it
	 * @param parameter The parameter to provide to the command
	 */
	public void provideOptionalParameter(OptionalParameter<?> parameter)
	{
		providedOptionalParameters.add(parameter);
	}
	
	/**
	 * @return The required parameters *actually* given to the command after parsing 
	 */
	public Iterator<RequiredParameter<?>> getProvidedRequiredParameters()
	{
		return providedRequiredParameters.iterator();
	}
	
	/**
	 * Short convenience method to get a required parameter. 
	 * The returned value is cast to an inferred type. This must be the correct type, 
	 * and it is the responsibility of the caller to ensure this.
	 * @param index Index of the parameter. Store in declared order.
	 * @param <T> The type of the value within the parameter
	 * @return The RequiredParameter at the given index
	 * @throws IndexOutOfBoundsException If the index is out of bounds
	 */
	@SuppressWarnings("unchecked")
	public <T> RequiredParameter<T> getReqParam(int index) throws IndexOutOfBoundsException
	{
		return (RequiredParameter<T>) providedRequiredParameters.get(index);
	}
	
	/**
	 * Tell this command that the required parameter is being passed to it
	 * @param parameter The parameter to provide to the command
	 */
	public void provideRequiredParameter(RequiredParameter<?> parameter)
	{
		providedRequiredParameters.add(parameter);
	}
	
}
