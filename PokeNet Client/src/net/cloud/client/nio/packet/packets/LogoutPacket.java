package net.cloud.client.nio.packet.packets;

import io.netty.buffer.ByteBuf;
import net.cloud.client.entity.player.LoginHandler;
import net.cloud.client.entity.player.Player;
import net.cloud.client.nio.bufferable.BufferableException;
import net.cloud.client.nio.packet.Packet;
import net.cloud.client.nio.packet.PacketConstants;
import net.cloud.client.nio.packet.ReceiveOnlyPacket;

/**
 * The server is telling us to log out when we receive this packet
 */
public class LogoutPacket extends ReceiveOnlyPacket {

	@Override
	public short getOpcode()
	{
		return PacketConstants.LOGOUT;
	}

	@Override
	public Packet decode(ByteBuf data) throws BufferableException
	{
		// Read the dummy int
		data.readInt();
		
		return new LogoutPacket();
	}

	/**
	 * Starts the logout procedure on the client side
	 */
	@Override
	public void handlePacket(Player player)
	{
		// Move the call off to the LoginHandler, because yes, log out code is there, too
		LoginHandler.doLogout();
	}

}
