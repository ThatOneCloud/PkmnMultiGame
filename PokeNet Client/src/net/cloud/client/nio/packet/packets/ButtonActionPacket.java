package net.cloud.client.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.client.game.action.ButtonActionID;
import net.cloud.client.nio.bufferable.Bufferable;
import net.cloud.client.nio.bufferable.BufferableException;
import net.cloud.client.nio.packet.PacketConstants;
import net.cloud.client.nio.packet.SendOnlyPacket;

/**
 * A packet to tell the server about some button being acted on. Not all buttons need send this packet. 
 * This applies to buttons in general, either a plain button, check box, etc. 
 * It contains an integer ID and may contain other arguments as contextually needed. The ID comes from the list 
 * of constants in ButtonConstants.
 */
public class ButtonActionPacket extends SendOnlyPacket {
	
	/** The ID on the action, so the server knows which button was pressed */
	private ButtonActionID buttonID;
	
	/** Any number and type of various arguments that the button needs to provide for context */
	private Bufferable[] args;
	
	/** Prototype constructor */
	public ButtonActionPacket() {}
	
	/**
	 * Create a packet which tells the server the given button was acted on. 
	 * There will be no arguments
	 * @param buttonID The ID from ButtonConstants
	 */
	public ButtonActionPacket(ButtonActionID buttonID)
	{
		this.buttonID = buttonID;
		this.args = null;
	}
	
	/**
	 * Create a packet which tells the server the given button was acted on. 
	 * Arguments are not required
	 * @param buttonID The ID from ButtonConstants
	 * @param args Optionally provided arguments
	 */
	public ButtonActionPacket(ButtonActionID buttonID, Bufferable... args)
	{
		this.buttonID = buttonID;
		this.args = args;
	}

	@Override
	public short getOpcode()
	{
		return PacketConstants.BUTTON_ACTION;
	}

	@Override
	public void encode(ByteBuf buffer) throws BufferableException
	{
		// First and foremost, write the ID
		buffer.writeInt(buttonID.ordinal());

		// Now are there any arguments we need to worry about?
		if(args != null && args.length > 0)
		{
			// How many arguments are there? Write that number so we know how many to decode
			buffer.writeInt(args.length);
			
			// Keep track of where we're at before writing any arguments
			int idxBeforeArgs = buffer.writerIndex();
			
			// Write a placeholder integer - real length is soon to come
			buffer.writeInt(0);
			
			// Write each of the arguments in turn
			for(Bufferable arg : args)
			{
				arg.save(buffer);
			}
			
			// All arguments written, where are we at
			int idxAfterArgs = buffer.writerIndex();
			
			// Length of just the arguments (not the argument length)
			int argsLength = idxAfterArgs - idxBeforeArgs - 4;
			
			// Move back and put the actual argument length in place
			buffer.writerIndex(idxBeforeArgs);
			buffer.writeInt(argsLength);
			buffer.writerIndex(idxAfterArgs);
		}
		// There are no arguments
		else {
			// So we write a 0 for count and size of arguments
			buffer.writeInt(0);
			buffer.writeInt(0);
		}
	}

}
