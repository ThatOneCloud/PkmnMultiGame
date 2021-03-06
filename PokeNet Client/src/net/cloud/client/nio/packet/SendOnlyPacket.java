package net.cloud.client.nio.packet;

import io.netty.buffer.ByteBuf;
import net.cloud.client.entity.player.Player;

/** 
 * A packet that is only intended to be sent by the client, reducing redundancy and useless code
 * by not requiring declaration of decoding methods.
 */
public abstract class SendOnlyPacket implements Packet {

	/**
	 * Unsupported by SendOnlyPacket.
	 */
	@Override
	public Packet decode(ByteBuf data) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("Packet " + getOpcode() + " is SendOnly. Does not support decoding");
	}

	/**
	 * Unsupported by SendOnlyPacket.
	 */
	@Override
	public void handlePacket(Player player) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("Packet " + getOpcode() + " is SendOnly. Does not support handling");
	}

}
