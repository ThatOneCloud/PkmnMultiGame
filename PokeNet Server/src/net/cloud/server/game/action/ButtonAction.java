package net.cloud.server.game.action;

import io.netty.buffer.ByteBuf;
import net.cloud.server.entity.player.Player;
import net.cloud.server.nio.bufferable.Bufferable;
import net.cloud.server.nio.bufferable.BufferableException;

/**
 * A ButtonAction is the abstraction for an action the server takes when a button was pressed by a client. 
 * Tied to a ButtonActionID. 
 * A ButtonAction knows how to decode the buffered arguments sent to it, as there may or may not be any, 
 * and their format is up to the action to decide. (non-uniform)
 * A ButtonAction also knows how to handle itself, of course.
 */
public abstract class ButtonAction implements Action {
	
	/** The ID that uniquely identifies this action and provides constant information */
	private ButtonActionID id;
	
	/**
	 * Assigns the ID
	 * @param id The identifier for this action
	 */
	public ButtonAction(ButtonActionID id)
	{
		this.id = id;
	}

	/**
	 * Obtain a ButtonActionID, which allows access to button-related information. 
	 * Apparently, I can restrict the return type and still obey the interface. So that's cool.
	 */
	@Override
	public ButtonActionID getActionID()
	{
		return id;
	}
	
	/**
	 * Decode arguments that the button may need. The array should be initialized, and the needed data should be in the 
	 * buffer starting at the current reader index. This will then read the data and place the decoded information in the array 
	 * so that handle(...) may use it later. 
	 * The default implementation of this method does nothing.
	 * @param args Initialized array of context arguments
	 * @param data Buffer containing raw data which will become the decoded arguments
	 * @throws BufferableException If one arose during decoding
	 */
	public void decodeArgs(Bufferable[] args, ByteBuf data) throws BufferableException
	{
		// Do nothing
	}
	
	/**
	 * Handle the button action. 
	 * Take care of whatever action the button needs to, using the provided Player and arguments. 
	 * The arguments will have been decoded by decodeArgs(...) prior to this method being called.
	 * @param player The player that pressed a button, starting this whole chain of events
	 * @param args Arguments that the client wanted to send to the action
	 * @throws Exception If something went wrong while handling the action
	 */
	public abstract void handle(Player player, Bufferable[] args) throws Exception;

}
