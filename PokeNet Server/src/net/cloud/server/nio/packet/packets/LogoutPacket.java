package net.cloud.server.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.server.nio.bufferable.BufferableException;
import net.cloud.server.nio.packet.PacketConstants;
import net.cloud.server.nio.packet.SendOnlyPacket;

/**
 * This is like a command telling the receiving client they need to log out
 */
public class LogoutPacket extends SendOnlyPacket {
	
	/** Default constructor leaves all data fields default or null */
	public LogoutPacket() {}

	@Override
	public short getOpcode()
	{
		return PacketConstants.LOGOUT;
	}

	@Override
	public void encode(ByteBuf buffer) throws BufferableException
	{
		// Write a dummy value, don't currently have anything I need to send
		buffer.writeInt(0xD3AD);
	}

}
