package net.cloud.server.nio.packet;

import java.util.function.Consumer;

import net.cloud.server.entity.player.LoginResponse;
import net.cloud.server.entity.player.Player;
import io.netty.channel.socket.SocketChannel;

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
	private final SocketChannel channel;
	
	/** The PacketFactory being used to create Packet objects */
	private PacketFactory packetFactory;
	
	/**
	 * Creates a PacketSender, where the connection is given by the Channel
	 * @param channel The Channel for the connection between client and server
	 */
	public PacketSender(SocketChannel channel)
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
	/**
	 * Writes, but does not send, a packet. <br>See {@link PacketFactory#createTestPacket(int)}
	 * @param value Test value
	 * @return The packet
	 */
	public PacketSender writeTestPacket(int value)
	{
		write(createTestPacket(value));
		
		return this;
	}
	/**
	 * Writes and sends a packet. <br>See {@link PacketFactory#createTestPacket(int)}
	 * @param value Test value
	 */
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
	/**
	 * Writes, but does not send a packet. For a description of the packet, see<br>
	 * {@link PacketFactory#createCompositePacket(Packet, Packet...)}
	 * @param first The first packet that the composite will consist of
	 * @param others Any other packets the new packet will consist of
	 * @return The packet
	 */
	public PacketSender writeCompositePacket(Packet first, Packet... others)
	{
		write(createCompositePacket(first, others));
		
		return this;
	}
	/**
	 * Writes and sends a packet. For a description of the packet, see<br>
	 * {@link PacketFactory#createCompositePacket(Packet, Packet...)}
	 * @param first The first packet that the composite will consist of
	 * @param others Any other packets the new packet will consist of
	 */
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
	/**
	 * Writes, but does not send a packet. For a description of the packet, see <br>
	 * {@link PacketFactory#createLoginResponsePacket(LoginResponse)}
	 * @param response The response to encode in the packet
	 * @return The packet
	 */
	public PacketSender writeLoginResponse(LoginResponse response)
	{
		write(createLoginResponse(response));
		
		return this;
	}
	/**
	 * Writes and sends a packet. For a description of the packet, see <br>
	 * {@link PacketFactory#createLoginResponsePacket(LoginResponse)}
	 * @param response The response to encode in the packet
	 */
	public void sendLoginResponse(LoginResponse response)
	{
		this.writeLoginResponse(response).send();
	}
	
	/**
	 * Only creates and returns a packet. For a description of the packet, see <br>
	 * {@link PacketFactory#createLoginDataResponsePacket(Player)}
	 * @param player The player that is about to login
	 * @return The packet
	 */
	public Packet createLoginDataResponse(Player player)
	{
		return packetFactory.createLoginDataResponsePacket(player);
	}
	/**
	 * Writes, but does not send a packet. For a description of the packet, see <br>
	 * {@link PacketFactory#createLoginDataResponsePacket(Player)}
	 * @param player The player that is about to login
	 * @return The packet
	 */
	public PacketSender writeLoginDataResponse(Player player)
	{
		write(createLoginDataResponse(player));
		
		return this;
	}
	/**
	 * Writes and sends a packet. For a description of the packet, see <br>
	 * {@link PacketFactory#createLoginDataResponsePacket(Player)}
	 * @param player The player that is about to login
	 */
	public void sendLoginDataResponse(Player player)
	{
		this.writeLoginDataResponse(player).send();
	}
	
	/**
	 * Only creates and returns a packet. For a description of the packet, see <br>
	 * {@link PacketFactory#createShowMessageDialogPacket(String, String)}
	 * @param title The title to put on the dialog's frame
	 * @param message The message to show
	 * @return The packet
	 */
	public Packet createShowMessageDialog(String title, String message)
	{
		return packetFactory.createShowMessageDialogPacket(title, message);
	}
	/**
	 * Writes, but does not send a packet. For a description of the packet, see <br>
	 * {@link PacketFactory#createShowMessageDialogPacket(String, String)}
	 * @param title The title to put on the dialog's frame
	 * @param message The message to show
	 * @return The packet
	 */
	public PacketSender writeShowMessageDialog(String title, String message)
	{
		write(createShowMessageDialog(title, message));
		
		return this;
	}
	/**
	 * Writes and sends a packet. For a description of the packet, see <br>
	 * {@link PacketFactory#createShowMessageDialogPacket(String, String)}
	 * @param title The title to put on the dialog's frame
	 * @param message The message to show
	 */
	public void sendShowMessageDialog(String title, String message)
	{
		this.writeShowMessageDialog(title, message).send();
	}
	
	/**
	 * Writes and sends a logout packet. When this operation completes, the onSend function will be called
	 * @param player The player to make log out
	 * @param onSend A method to call when the packet is written
	 */
	public void sendLogout(Player player, Consumer<Player> onSend)
	{
		// Normally would use write().send(), but we need something other than a VoidPromise
		channel.writeAndFlush(packetFactory.createLogoutPacket()).addListener((f) -> onSend.accept(player));
	}
	
	
//	TEMPLATE FOR NEW PACKETS
//	/**
//	 * Only creates and returns a Packet. For a description of the packet, see<br>
//	 * {@link PacketFactory#create()}
//	 */
//	public Packet create()
//	{
//		return packetFactory.create();
//	}
//	/** Writes, but does not send a packet. <br>See {@link PacketFactory#create()} */
//	public PacketSender write()
//	{
//		write(create());
//		
//		return this;
//	}
//	/** Writes and sends a packet.  <br>See {@link PacketFactory#create()} */
//	public void send()
//	{
//		this.write().send();
//	}
//	TEMPLATE FOR NEW PACKETS
	
	
	/**
	 * @return The channel this PacketSender is working on (the one the player is connected with)
	 */
	public SocketChannel channel()
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
	 * @param packet The packet to write
	 */
	private void write(Packet packet)
	{
		channel.write(packet, channel.voidPromise());
	}

}
