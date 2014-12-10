package net.cloud.mmo.nio.packet;

import io.netty.channel.Channel;
import net.cloud.mmo.nio.packet.packets.*;

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

}