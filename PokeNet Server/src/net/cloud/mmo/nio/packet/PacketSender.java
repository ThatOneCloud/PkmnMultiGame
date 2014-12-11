package net.cloud.mmo.nio.packet;

import io.netty.channel.Channel;
import net.cloud.mmo.nio.packet.packets.*;
import net.cloud.mmo.nio.packet.packets.LoginPacket.LoginResponse;
import net.cloud.mmo.nio.packet.packets.LoginPacket.LoginResponsePacket;

/**
 * Each Player will have their own PacketSender.  It holds a reference to the connection 
 * with the client, and is responsible for beginning the process of writing 
 * a Packet out.
 */
public class PacketSender {
	
	/** The Channel representing the connection between client and server */
	private final Channel channel;
	
	/**
	 * Creates a PacketSender, where the connection is given by the Channel
	 * @param channel The Channel for the connection between client and server
	 */
	public PacketSender(Channel channel) {
		this.channel = channel;
	}
	
	/**
	 * Sends a TestPacket, which is just an integer of the given value
	 */
	public PacketSender sendTestPacket(int value) {
		channel.writeAndFlush(new TestPacket(value));
		
		return this;
	}
	
	/**
	 * Send a response to a client which requested to login.
	 * @param response The response (to the credentials they sent)
	 */
	public PacketSender sendLoginReponse(LoginResponse response)
	{
		channel.writeAndFlush(new LoginResponsePacket(response));
		
		return this;
	}

}
