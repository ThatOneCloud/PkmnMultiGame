package net.cloud.server.nio.packet;

import io.netty.buffer.ByteBuf;

/**
 * A packet which is only received by the server, and never sent. 
 * Reduces redundant pointless code by not requiring declaration of the encode method. 
 */
public abstract class ReceiveOnlyPacket implements Packet {

	/**
	 * Not supported by ReceiveOnlyPacket.
	 */
	@Override
	public void encode(ByteBuf buffer) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("Packet " + getOpcode() + " is ReceiveOnly. Does not support encoding");
	}

}
