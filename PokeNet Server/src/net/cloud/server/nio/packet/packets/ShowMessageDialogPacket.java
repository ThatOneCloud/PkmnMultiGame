package net.cloud.server.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.server.nio.bufferable.BufferableException;
import net.cloud.server.nio.packet.PacketConstants;
import net.cloud.server.nio.packet.SendOnlyPacket;
import net.cloud.server.util.StringUtil;

/**
 * Tell the client that it should show a modal popup message dialog
 */
public class ShowMessageDialogPacket extends SendOnlyPacket {
	
	/** The title of the message */
	private String title;
	
	/** The message */
	private String message;
	
	/** Prototype constructor */
	public ShowMessageDialogPacket() {}
	
	/**
	 * Create a packet which will have the given message shown
	 * @param title The title to place on the frame
	 * @param message The message to show
	 */
	public ShowMessageDialogPacket(String title, String message)
	{
		this.title = title;
		this.message = message;
	}

	@Override
	public short getOpcode()
	{
		return PacketConstants.SHOW_MSG_DIALOG;
	}

	@Override
	public void encode(ByteBuf buffer) throws BufferableException
	{
		// Place the title into the buffer
		StringUtil.writeStringToBuffer(title, buffer);
		
		// Place the message into the buffer
		StringUtil.writeStringToBuffer(message, buffer);
	}

}
