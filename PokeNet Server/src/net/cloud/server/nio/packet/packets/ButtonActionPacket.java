package net.cloud.server.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.server.logging.Logger;
import net.cloud.server.nio.bufferable.Bufferable;
import net.cloud.server.entity.player.Player;
import net.cloud.server.game.action.ActionManager;
import net.cloud.server.game.action.ButtonActionID;
import net.cloud.server.nio.bufferable.BufferableException;
import net.cloud.server.nio.packet.Packet;
import net.cloud.server.nio.packet.PacketConstants;
import net.cloud.server.nio.packet.ReceiveOnlyPacket;

/**
 * This packet indicates that a button was pressed on a client. The packet tells us 
 * which button was pressed, as well as some optional context information. 
 * The action we take in regards to this packet is dynamically generated and cached
 */
public class ButtonActionPacket extends ReceiveOnlyPacket {
	
	/** The ID on the action, so the server knows which button was pressed */
	private ButtonActionID buttonID;
	
	/** Any number and type of various arguments that the button needs to provide for context */
	private Bufferable[] args;
	
	/** The data this packet got, saved so decoding can take place in the ButtonAction itself */
	private ByteBuf data;
	
	/** Prototype constructor */
	public ButtonActionPacket() {}

	@Override
	public short getOpcode()
	{
		return PacketConstants.BUTTON_ACTION;
	}

	@Override
	public Packet decode(ByteBuf data) throws BufferableException
	{
		ButtonActionPacket packet = new ButtonActionPacket();
		
		packet.buttonID = ButtonActionID.values()[data.readInt()];
		
		// How how many arguments are there?
		int numArgs = data.readInt();
		
		int argsLength = data.readInt();
		
		// Are there no arguments?
		if(numArgs <= 0)
		{
			// Since there are none, we just use the empty optional. Fun fact: In the JDK, it's singleton
			packet.args = null;
		}
		else {
			// There are, we'll initialize. We can't decode them, we don't know the type, ourselves.
			packet.args = new Bufferable[numArgs];
		}
		
		// The argument data, we don't need a memory copy, but we do need to keep it stored away
		packet.data = data.readSlice(argsLength);
		packet.data.retain();
		
		return packet;
	}

	/**
	 * Ships the action off to the ActionManager
	 */
	@Override
	public void handlePacket(Player player)
	{
		try {
			// Let the action manager take the reigns
			ActionManager.instance().handleButton(player, buttonID, args, data);
		} catch (Exception e) {
			// Something happened while handling - this doesn't need to be fatal, the client will just see it as nothing happening
			Logger.instance().logException("Exception while handling button " + buttonID.toString() + " for " + player.getUsername(), e);
		} finally {
			// We need to release the argument data that was stored away to avoid a memory leak
			data.release();
		}
	}

}
