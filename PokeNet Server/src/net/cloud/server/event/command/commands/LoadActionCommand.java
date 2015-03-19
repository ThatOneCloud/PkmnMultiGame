package net.cloud.server.event.command.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Function;

import net.cloud.server.event.command.Command;
import net.cloud.server.event.command.argument.ArgumentPrototypes;
import net.cloud.server.event.command.argument.EnumArgument;
import net.cloud.server.event.command.parameter.OptionalParameter;
import net.cloud.server.event.command.parameter.RequiredParameter;
import net.cloud.server.game.action.ActionManager;
import net.cloud.server.game.action.ButtonActionID;
import net.cloud.server.groovy.GroovyObjectLoader;
import net.cloud.server.logging.Logger;
import net.cloud.server.util.function.ExceptionBiConsumer;
import net.cloud.server.util.function.ExceptionTriConsumer;
import net.cloud.server.util.function.Pair;

/**
 * A command to load or reload actions in the server. Why not, since they're dynamically loaded. 
 * Allows for all actions, all of a type of action, or a single action to be loaded. 
 * However, only one such load will be done, whichever would have the largest 'scope'. Beyond that, ties are decided in an arbitrary order. 
 * The moral is: Only choose one load per invocation of the command
 */
public class LoadActionCommand extends AbstractCommand {
	
	/** The optional parameters this command could possibly accept */
	private static OptionalParameter<?>[] allOptParams = 
	{
		new OptionalParameter<>("all", "all", ArgumentPrototypes.FLAG),
		new OptionalParameter<>("allbuttons", "allbuttons", ArgumentPrototypes.FLAG),
		new OptionalParameter<>("button", "button", new EnumArgument<ButtonActionID>(ButtonActionID.class)),
	};
	
	/** Okay, I'm proud of this one. Think of it as which argument will call which function. They'll be looked through in order. */
	private static LookupTableEntry[] pairs = new LookupTableEntry[]
	{
		new LookupTableEntry("all", LoadActionCommand::allActions),
		new LookupTableEntry("allbuttons", LoadActionCommand::allButtonActions),
		new LookupTableEntry("button", LoadActionCommand::buttonAction)
	};
	
	/** Create an empty LoadActionCommand */
	public LoadActionCommand()
	{
		super();
	}

	/** Create a new dynamically binded instance */
	@Override
	public Command newPrototypedInstance()
	{
		return new LoadActionCommand();
	}

	@Override
	public String doCommand()
	{
		// Look through all of the entries in the lookup table
		for(LookupTableEntry entry : pairs)
		{
			// Does the name of the argument in the table match an optional argument we've been provided?
			if(super.hasOptParam(entry.first))
			{
				// We have the argument, so we're going to call the associated function
				return entry.second.apply(this);
			}
		}
		
		// Fall-back, we didn't find a match in the lookup table
		return "Must specify which action(s) to load";
	}
	
	/** Optional parameters are in a way mutually exclusive, but we'll take care of that */
	@Override
	protected OptionalParameter<?>[] getAllOptionalParameters()
	{
		return allOptParams;
	}

	/** There are no required parameters */
	@Override
	protected RequiredParameter<?>[] getAllRequiredParameters()
	{
		return null;
	}
	
	/**
	 * Load all actions
	 * @return A string with a detailed report of the results
	 */
	private String allActions()
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter resultWriter = new PrintWriter(stringWriter);
		
		try {
			// Let the ActionManager do its thing, filling in result messages as it goes
			ActionManager.instance().loadAllActions(resultWriter);
		} catch (Exception e) {
			// Append exception messages to the end of the normal results
			resultWriter.println(e.getMessage());
		}
		
		return stringWriter.toString();
	}
	
	/**
	 * Load all button actions
	 * @return A string with a detailed report of the results
	 */
	private String allButtonActions()
	{
		return allActionsOfAKind(ActionManager.instance()::loadAllButtonActions);
	}
	
	/**
	 * Load a single button action, using the value of the "button" optional parameter. 
	 * This assumes the "button" parameter is present
	 * @return A string with a detailed report of the results
	 */
	private String buttonAction()
	{
		// Pull the ID from the argument we know exists.
		ButtonActionID id = (ButtonActionID) getOptParam("button").get().getArgValue();
		
		// Move off to template code
		return singleActionOfAKind(id, ActionManager.instance()::loadButtonAction);
	}
	
	/**
	 * Like a template for loading all actions of a certain kind. This way, each of those methods only needs one line. 
	 * This will return the results of the load, both its normal output and any exception message that is generated
	 * <br>Call as so: <code>return allActionsOfAKind(ActionManager.instance()::loadMethod);</code>
	 * @param loadFunc The function to call to actually load actions
	 * @return Results of the load
	 */
	private String allActionsOfAKind(ExceptionBiConsumer<PrintWriter, GroovyObjectLoader, Exception> loadFunc)
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter resultWriter = new PrintWriter(stringWriter);
		
		try(GroovyObjectLoader loader = new GroovyObjectLoader())
		{
			
			try {
				// Call the load function we've been given, it'll use our loader and fill in our result string
				loadFunc.accept(resultWriter, loader);
			} catch (Exception e) {
				// Append exception messages to the end of the normal results
				resultWriter.println(e.getMessage());
			}
			
		} catch (IOException e) {
			// Issue closing the object loader. Not fatal, not pertinent to command user. But show to server output
			Logger.instance().logException("Could not close GroovyObjectLoader while loading all button actions from command.", e);
		}
		
		return stringWriter.toString();
	}
	
	/**
	 * I'm a bit impressed that this worked out. Takes the ID of the particular action we want to load, and the method that 
	 * will do the loading for that specific action. Does all of the template work to get us a result string from doing the loading.
	 * @param id ID of the action to load
	 * @param loadFunc The function that's going to do the loading
	 * @return Results of the load
	 */
	private <T> String singleActionOfAKind(T id, ExceptionTriConsumer<PrintWriter, GroovyObjectLoader, T, Exception> loadFunc)
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter resultWriter = new PrintWriter(stringWriter);
		
		try(GroovyObjectLoader loader = new GroovyObjectLoader())
		{
			
			try {
				// Neat that it's a generic method that matches the type of the ID and infers the type
				loadFunc.accept(resultWriter, loader, id);
			} catch (Exception e) {
				// Append exception messages to the end of the normal results
				resultWriter.println(e.getMessage());
			}
			
		} catch (IOException e) {
			// Issue closing the object loader. Not fatal, not pertinent to command user. But show to server output
			Logger.instance().logException("Could not close GroovyObjectLoader while loading button action from command.", e);
		}
		
		return stringWriter.toString();
	}
	
	/**
	 * Because we can't have generic arrays and it would look a mess. 
	 * Think of this as a mapping from a condition (having the option name) to a function to call when that condition is met
	 */
	private static class LookupTableEntry extends Pair<String, Function<LoadActionCommand, String>> {

		/**
		 * Create a pairing from the option name to the function
		 * @param optionName Name of the optional argument
		 * @param function Function to call when the condition is met
		 */
		public LookupTableEntry(String optionName, Function<LoadActionCommand, String> function)
		{
			super(optionName, function);
		}
		
	}

}
