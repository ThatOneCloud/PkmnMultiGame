package net.cloud.server.game.action;

import java.io.PrintWriter;

import io.netty.buffer.ByteBuf;
import net.cloud.server.entity.player.Player;
import net.cloud.server.file.address.AddressConstants;
import net.cloud.server.file.address.FileAddressBuilder;
import net.cloud.server.groovy.GroovyObjectLoader;
import net.cloud.server.logging.Logger;
import net.cloud.server.nio.bufferable.Bufferable;
import net.cloud.server.util.NullWriter;

/**
 * An ActionHandler dedicated to ButtonActions. 
 * Knows how to load them, store them, and pass off handling calls to them.
 */
public class ButtonActionHandler implements ActionHandler<ButtonActionID> {
	
	/** Builder we'll consistently use for getting file paths */
	private FileAddressBuilder file;
	
	/** Array of all the button actions */
	private ButtonAction[] actions;
	
	/**
	 * Initializes actions, does not load them
	 */
	public ButtonActionHandler()
	{
		// This way, we only need to change the file name each time
		file = FileAddressBuilder.newBuilder();
		file.space(AddressConstants.SPACE_BUTTON_ACTIONS);
		file.extension(AddressConstants.EXT_GROOVY);
		
		// As many actions as there are enums
		this.actions = new ButtonAction[ButtonActionID.values().length];
	}

	/**
	 * Load all actions, one by one. This loads an action for each value in ButtonActionID. 
	 * Continues even if one or more actions fail to load, instead concatenating all of the messages 
	 * into the thrown exception.
	 * @throws Exception An exception if one or more button actions could not be loaded
	 */
	@Override
	public void loadAllActions(PrintWriter out, GroovyObjectLoader loader) throws Exception
	{
		Exception err = null;
		StringBuilder errMsg = null;
		boolean singleErr = false;
		
		int numLoaded = 0;
		
		// Run through all possible IDs, loading the action for each
		for(ButtonActionID id : ButtonActionID.values())
		{
			try {
				// Load a single action, giving it a writer so its individual output is ignored
				loadAction(NullWriter.NULL_PRINT_WRITER, loader, id);
				
				numLoaded++;
			} catch (Exception e) {
				// A single failure won't stop us. Was this the first?
				if(err == null)
				{
					// The first just becomes what it is
					err = new Exception("Could not load ButtonAction " + id.toString(), e);
					errMsg = new StringBuilder(err.getMessage());
					singleErr = true;
				}
				else {
					// Afterwards, we concatenate
					errMsg.append("Could not load ButtonAction " + id.toString() + System.lineSeparator()
							+ "Cause Message: " + e.getMessage() + System.lineSeparator());
					singleErr = false;
				}
				
				// We'll use this setting to pump out specific information on loading issues
				if(ActionManager.VERBOSE_EXCEPTIONS)
				{
					e.printStackTrace();
				}
				
				// Continue on our merry way
				continue;
			}
		}
		
		// Shout out how many actions were [successfully] loaded
		out.println("Loaded " + numLoaded + " Button Actions");
		out.flush();
		
		// Done loading, so do we need to throw just a single exception that arose?
		if(err != null && singleErr)
		{
			throw err;
		}
		// There was more than one exception
		else if(err != null && !singleErr)
		{
			throw new Exception(errMsg.toString());
		}
		// No exception, all good
	}

	/**
	 * Load a single button action, replacing any existing action with the same ID. 
	 * This is not thread safe... but if need ever be, just make the file name part synchronized
	 * @throws Exception If the action could not be loaded
	 */
	@Override
	public void loadAction(PrintWriter out, GroovyObjectLoader loader, ButtonActionID id) throws Exception
	{
		// What's the file we need to load from?
		file.filename(id.getCanonicalName());
		String fileName = file.createString();
		
		// Use the loader to grab just the single instance
		ButtonAction action = loader.createObject(fileName, id);
		
		// Place the new instance in the array
		actions[id.ordinal()] = action;
		
		// Shout out that the action is loaded
		out.println("Loaded button action " + id.toString());
		out.flush();
	}
	
	/**
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
	public void handle(Player player, ButtonActionID id, Bufferable[] args, ByteBuf data) throws Exception
	{
		ButtonAction action = actions[id.ordinal()];
		
		// We don't have the action loaded, do nothing
		if(action == null)
		{
			Logger.instance().logMessage("Button action not loaded, cannot handle: " + id.toString());
			return;
		}
		
		// First the action needs to decode if need be
		action.decodeArgs(args, data);
		
		// Then we tell the action it's time to perform its duties
		action.handle(player, args);
	}
	
}
