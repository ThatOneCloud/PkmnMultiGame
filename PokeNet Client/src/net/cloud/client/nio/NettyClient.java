package net.cloud.client.nio;

import java.util.Optional;

import net.cloud.client.entity.player.PlayerFactory;
import net.cloud.client.event.shutdown.ShutdownHook;
import net.cloud.client.event.shutdown.ShutdownService;
import net.cloud.client.event.shutdown.hooks.NettyShutdownHook;
import net.cloud.client.game.World;
import net.cloud.client.logging.Logger;
import net.cloud.client.nio.packet.PacketSender;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * The front of the network I/O code. 
 * Deals with creating a network connection and initializing 
 * the network channels.
 */
public class NettyClient implements ShutdownService {
	
	/** Network address of the server */
	public static final String ADDRESS = "localhost";
	
	/** Port to connect to the server on */
	public static final int PORT = 43594;
	
	/** The ShutdownHook that'll take care of shutting down the connection */
	private NettyShutdownHook shutdownHook;
	
	/** The Bootstrap object Netty was started on */
	private Bootstrap bootstrap;

	/**
	 * Startup procedure for the client's network communication.
	 * Sets up Netty, but will not attempt connecting yet
	 * @throws InterruptedException If a channel operation was interrupted
	 */
	public void startup() throws InterruptedException
	{
		Logger.writer().println("Starting Netty service...");
		Logger.writer().flush();
		
		// Create an executor for the client's I/O events
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		// Start up a boostrapper for the client
		bootstrap = new Bootstrap();
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.handler(new NettyClientChannelInitializer());
		
		Logger.writer().println("Netty service started");
		Logger.writer().flush();
	}
	
	/**
	 * Attempts to connect to the server. When a connection 
	 * is made, then this client's player object is created.
	 * @throws InterruptedException If the client could not connect
	 */
	public void connectToServer() throws InterruptedException
	{
		// Try to connect to the server
		ChannelFuture f = bootstrap.connect(ADDRESS, PORT).sync();
		Logger.writer().println("Connected to server.");
		Logger.writer().flush();
		
		// Create the ShutdownHook now
		shutdownHook = new NettyShutdownHook(f, bootstrap.group());

		// A PacketSender is made based on the channel that was returned
		PacketSender packetSender = new PacketSender(f.channel());

		// A new Player object is created and given the PacketSender
		World.getInstance().setPlayer(PlayerFactory.createOnNewConnection(packetSender));

		// That player is now going to try to login
		World.getInstance().getPlayer().getPacketSender().sendLogin();
	}
	
	/** 
	 * Note that this hook is not created until connection is attempted. 
	 * In other words, it should not be added to a ShutdownHandler right away.
	 */
	@Override
	public ShutdownHook getShutdownHook() throws NullPointerException {
		// Return the hook or throw NPE if it is null
		return Optional.ofNullable(shutdownHook)
				.orElseThrow(() -> new NullPointerException("NettyServer has not created a ShutdownHook"));
	}

}
