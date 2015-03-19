package net.cloud.client.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.client.entity.player.Player;
import net.cloud.client.nio.bufferable.BufferableException;
import net.cloud.client.nio.packet.Packet;
import net.cloud.client.nio.packet.PacketConstants;
import net.cloud.client.nio.packet.ReceiveOnlyPacket;
import net.cloud.client.util.StringUtil;
import net.cloud.gfx.elements.modal.ModalManager;

/**
 * A packet that asks us to show a message dialog
 */
public class ShowMessageDialogPacket extends ReceiveOnlyPacket {
	
	/** The title of the message */
	private String title;
	
	/** The message to show */
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
	public Packet decode(ByteBuf data) throws BufferableException
	{
		return new ShowMessageDialogPacket(StringUtil.getFromBuffer(data), StringUtil.getFromBuffer(data));
	}

	/**
	 * Open a modal dialog, although since there is no interest in what it returns, we will not block waiting for the dialog to close. 
	 * Certainly we don't want to block the I/O thread, anyways.
	 */
	@Override
	public void handlePacket(Player player)
	{
		// Display a message, this way the call will not block
		ModalManager.instance().displayMessage(title, message);
	}

}
