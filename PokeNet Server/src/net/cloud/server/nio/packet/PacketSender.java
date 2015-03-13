package net.cloud.server.nio.packet;

import net.cloud.server.entity.player.LoginResponse;
import io.netty.channel.Channel;

/**
 * Each Player will have their own PacketSender.  It holds a reference to the connection 
 * with the client, and is responsible for beginning the process of writing 
 * a Packet out.  Each packet can be controlled via three methods: <br><br>
 * <code>createPacket(...)</code> which creates a packet, and returns that packet<br>
 * <code>writePacket(...)</code> which creates and writes a packet, but does not send it.
 * The PacketSender is returned so that these calls may be chained.<br>
 * <code>sendPacket(...)</code> which creates and sends a packet. It is a convenience for
 * <code>writePacket(...).send()</code>
 */
public class PacketSender {
	
	/** The Channel representing the connection between client and server */
	private final Channel channel;
	
	/** The PacketFactory being used to create Packet objects */
	private PacketFactory packetFactory;
	
	/**
	 * Creates a PacketSender, where the connection is given by the Channel
	 * @param channel The Channel for the connection between client and server
	 */
	public PacketSender(Channel channel)
	{
		this.channel = channel;
		this.packetFactory = new PacketFactory();
	}
	
	/**
	 * Only creates and returns a Packet. For a description of the packet, see<br>
	 * {@link PacketFactory#createTestPacket(int)}
	 * @param value The test value
	 * @return The packet
	 */
	public Packet createTestPacket(int value)
	{
		return packetFactory.createTestPacket(value);
	}
	/** Writes, but does not send a packet. <br>See {@link PacketFactory#createTestPacket(int)} */
	public PacketSender writeTestPacket(int value)
	{
		write(createTestPacket(value));
		
		return this;
	}
	/** Writes and sends a packet.  <br>See {@link PacketFactory#createTestPacket(int)} */
	public void sendTestPacket(int value)
	{
		this.writeTestPacket(value).send();
	}
	
	/**
	 * Only creates and returns a Packet. For a description of the packet, see<br>
	 * {@link PacketFactory#createCompositePacket(Packet, Packet...)}
	 * @param first The first packet that the composite will consist of
	 * @param others Any other packets the new packet will consist of
	 * @return The packet
	 */
	public Packet createCompositePacket(Packet first, Packet... others)
	{
		return packetFactory.createCompositePacket(first, others);
	}
	/** Writes, but does not send a packet. <br>See {@link PacketFactory#createCompositePacket(Packet, Packet...)} */
	public PacketSender writeCompositePacket(Packet first, Packet... others)
	{
		write(createCompositePacket(first, others));
		
		return this;
	}
	/** Writes and sends a packet.  <br>See {@link PacketFactory#createCompositePacket(Packet, Packet...)} */
	public void sendCompositePacket(Packet first, Packet... others)
	{
		this.writeCompositePacket(first, others).send();
	}
	
	/**
	 * Only creates and returns a packet. For a description of the packet, see <br>
	 * {@link PacketFactory#createLoginResponsePacket(LoginResponse)}
	 * @param response The response to encode in the packet
	 * @return The packet
	 */
	public Packet createLoginResponse(LoginResponse response)
	{
		return packetFactory.createLoginResponsePacket(response);
	}
	/** Writes, but does not send a packet. <br>See {@link PacketFactory#createLoginResponse(LoginResponse)} */
	public PacketSender writeLoginResponse(LoginResponse response)
	{
		write(createLoginResponse(response));
		
		return this;
	}
	/** Writes and sends a packet.  <br>See {@link PacketFactory#createLoginResponse(LoginResponse)} */
	public void sendLoginResponse(LoginResponse response)
	{
		this.writeLoginResponse(response).send();
	}
	
	/**
	 * @return The channel this PacketSender is working on (the one the player is connected with)
	 */
	public Channel channel()
	{
		return channel;
	}
	
	/**
	 * Send all packets that have been created but not yet sent 
	 */
	public void send()
	{
		channel.flush();
	}
	
	/**
	 * Writes a packet but does not flush the channel. Uses a VoidPromise to reduce object creation
	 */
	private void write(Packet packet)
	{
		channel.write(packet, channel.voidPromise());
	}

}
