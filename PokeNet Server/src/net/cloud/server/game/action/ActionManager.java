package net.cloud.server.game.action;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.PrintWriter;

import net.cloud.server.entity.player.Player;
import net.cloud.server.groovy.GroovyObjectLoader;
import net.cloud.server.logging.Logger;
import net.cloud.server.nio.bufferable.Bufferable;

/**
 * Entry point to all of the action sub-system stuff. 
 * Gives us access to loading and handling actions, for all the various types of actions.
 */
public class ActionManager {
	
	/** 
	 * Flag indicating whether or not the action sub-system will print all exception stack traces, 
	 * since some would otherwise be masked by more tersely defined exceptions
	 */
	public static final boolean VERBOSE_EXCEPTIONS = true;
	
	/** Singleton instance */
	private static ActionManager instance;
	
	/** Handler for button actions */
	private ButtonActionHandler buttonActionHandler;
	
	/**
	 *  Private singleton constructor. 
	 *  Initializes all of the handlers, but does not start any loading. 
	 */
	private ActionManager()
	{
		buttonActionHandler = new ButtonActionHandler();
	}
	
	/**
	 * Obtain the ActionManager, the entrance point to the Action sub-system
	 * @return Singleton reference to the ActionManager
	 */
	public static ActionManager instance()
	{
		if(instance == null)
		{
			synchronized(ActionManager.class)
			{
				if(instance == null)
				{
					instance = new ActionManager();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Load all actions, for all the various types of actions. If loading any of them goes wrong, loading will continue. Replaces existing actions.
	 * However, an exception will be thrown at the end if one or more actions failed to load. 
	 * This exception will have a concatenated message stating everything that went wrong, but lacking stack trace information.
	 * @param out A PrintWriter to write output progress messages to
	 * @throws Exception If one or more actions failed to load
	 */
	public void loadAllActions(PrintWriter out) throws Exception
	{
		// Keep track of all the exception messages. Alternatively could return a list of exceptions but this works. 
		// We could always keep concatenating the messages, and actually log the full exception when it happens if we wanted to see details.
		StringBuilder errMsg = new StringBuilder();

		// The object loader can be closed to finalize resources it's used
		try(GroovyObjectLoader loader = new GroovyObjectLoader())
		{
			
			// Load all of the button actions, keep track of any errors that come up
			try {
				buttonActionHandler.loadAllActions(out, loader);
			} catch (Exception e) {
				errMsg.append("Error(s) while loading button actions: " + System.lineSeparator() + e.getMessage() + System.lineSeparator());
			}
			
		} catch (IOException e) {
			// There was an issue closing the object loader. It's not fatal and we're more interested in exceptions from loading, 
			// so let's avoid masking those with this exception. Log the closing exception, but slide through to loading exceptions.
			Logger.instance().logException("Could not close GroovyObjectLoader while loading all actions.", e);
		}
		
		// Are there exception messages we need to report?
		if(errMsg.length() > 0)
		{
			// Yeah, so throw an exception with the given message.
			throw new Exception(errMsg.toString());
		}
	}
	
	/**
	 * Load all button actions. If loading any single action goes wrong, loading will continue. Replaces existing actions.
	 * However, an exception will be thrown at the end if one or more actions failed to load. 
	 * This exception will have a concatenated message stating everything that went wrong, but lacking stack trace information.
	 * @param out A PrintWriter to write output progress messages to
	 * @param loader A loader to work with. It will not be closed, so make sure to do that.
	 * @throws Exception If one or more actions failed to load
	 */
	public void loadAllButtonActions(PrintWriter out, GroovyObjectLoader loader) throws Exception
	{
		// Almost identical to loadAllActions(), but throws rather than appends, and no line separator at end
		try {
			buttonActionHandler.loadAllActions(out, loader);
		} catch (Exception e) {
			throw new Exception("Error(s) while loading button actions: " + System.lineSeparator() + e.getMessage());
		}
	}
	
	/**
	 * Attempt to load a single action. It will replace any currently loaded action. 
	 * @param out A PrintWriter to write output progress messages to
	 * @param loader A loader to work with. It will not be closed, so make sure to do that.
	 * @param id Identifies the action to load
	 * @throws Exception If the action failed to load
	 */
	public void loadButtonAction(PrintWriter out, GroovyObjectLoader loader, ButtonActionID id) throws Exception
	{
		buttonActionHandler.loadAction(out, loader, id);
	}
	
	/**
	 * <b>Copied from: {@link ButtonActionHandler#handle(Player, ButtonActionID, Bufferable[], ByteBuf)}</b><br>
	 * <br>
	 * Handle a button action by passing the call off to the ButtonAction object. 
	 * If for some reason the action has not been loaded, then this call will terminate and do nothing. 
	 * Normally, the action is asked to decode the provided data into the provided args array. 
	 * Then, the action's handle method is called with the player and argument information.
	 * @param player The player performing this action
	 * @param id The ID of the action
	 * @param args Initialized array of arguments (may be null if not needed)
	 * @param data Data that will be decoded into the arguments
	 * @throws Exception If something went wrong while handling the action
	 */
	public void handleButton(Player player, ButtonActionID id, Bufferable[] args, ByteBuf data) throws Exception
	{
		buttonActionHandler.handle(player, id, args, data);
	}

}
