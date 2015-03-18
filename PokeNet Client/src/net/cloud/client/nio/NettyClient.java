package net.cloud.client.nio;

import java.util.Optional;

import net.cloud.client.Client;
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
	
	/** The channel future obtained from connecting to the server */
	private ChannelFuture connectFuture;
	
	/** Flag to indicate whether we are connected to the server or not */
	private volatile boolean connected;
	
	/**
	 * Initialize to an unconnected state
	 */
	public NettyClient()
	{
		this.connected = false;
	}
	
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
	 * @return False if the connection was not successful
	 */
	public boolean connectToServer()
	{
		// Have we already connected?
		if(connectFuture != null && connectFuture.isSuccess() && World.instance().getPlayer() != null)
		{
			return true;
		}
		
		try {
			// Try to connect to the server
			connectFuture = bootstrap.connect(ADDRESS, PORT).sync();
			Logger.writer().println("Connected to server.");
			Logger.writer().flush();

			// Create the ShutdownHook now
			if(shutdownHook != null)
			{
				Client.instance().shutdownHandler().removeHook(shutdownHook);
			}
			shutdownHook = new NettyShutdownHook(connectFuture, bootstrap.group());
			Client.instance().shutdownHandler().addHook(shutdownHook);
			
			// A PacketSender is made based on the channel that was returned
			PacketSender packetSender = new PacketSender(connectFuture.channel());

			// A new Player object is created and given the PacketSender
			World.instance().setPlayer(PlayerFactory.createOnNewConnection(packetSender));
			
			this.connected = true;
			return true;
		} catch(Exception e) {
			Logger.instance().logException("Could not connect to server.", e);
			
			return false;
		}
	}
	
	/**
	 * Disconnect from the server. This will also destroy the current player object, 
	 * since it was initially created by connecting. (Reconnecting will re-create). 
	 * This will not shutdown the netty threads.
	 * @return True if the disconnect was successful. Not like we can do much if it isn't.
	 */
	public boolean disconnect()
	{
		// Destroy the player
		World.instance().setPlayer(null);
		
		// Close the channel
		try {
			connectFuture.channel().close().sync();
			
			connectFuture = null;
		} catch (InterruptedException e) {
			Logger.instance().logException("[FATAL] Could not disconnect from server. Unpredictable network behavior will follow.", e);
			
			return false;
		}
		
		this.connected = false;
		return true;
	}
	
	/**
	 * @return True if the client is currently connected to the server
	 */
	public boolean isConnected()
	{
		return connected;
	}
	
	/** 
	 * Note that this hook is not created until connection is attempted. 
	 * In other words, it should not be added to a ShutdownHandler right away.
	 */
	@Override
	public ShutdownHook getShutdownHook() throws NullPointerException
	{
		// Return the hook or throw NPE if it is null
		return Optional.ofNullable(shutdownHook)
				.orElseThrow(() -> new NullPointerException("NettyServer has not created a ShutdownHook"));
	}

}
