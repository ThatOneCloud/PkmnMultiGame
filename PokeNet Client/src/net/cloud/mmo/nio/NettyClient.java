package net.cloud.mmo.nio;

import net.cloud.mmo.entity.player.Player;
import net.cloud.mmo.game.World;
import net.cloud.mmo.nio.packet.PacketSender;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
	
	public static final String ADDRESS = "localhost";
	
	public static final int PORT = 43594;

	/**
	 * Startup procedure for the client's network communication.
	 * Sets up Netty and attempts to connect to the server.
	 * @throws InterruptedException If a channel operation was interrupted
	 */
	public void startup() throws InterruptedException
	{
		// Create an executor for the client's I/O events
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {

			// Start up a boostrapper for the client
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new NettyClientChannelInitializer());

			// Immediately tries to connect to the server
			ChannelFuture f = b.connect(ADDRESS, PORT).sync();
			System.out.println("Connected to server.");

//			// Create a PacketSender
//			PacketSender sender = new PacketSender(f.channel());
//			sender.sendTestPacket(54);
//			System.out.println("Sent test packet.");
			
			// A PacketSender is made based on the channel that was returned
			PacketSender packetSender = new PacketSender(f.channel());
			
			// A new Player object is created and given the PacketSender
			World.getInstance().setPlayer(new Player(packetSender));
			
			// That player is now going to try to login
			World.getInstance().getPlayer().getPacketSender().sendLogin();

			// Blocks until the connection to the server is closed
			f.channel().closeFuture().sync();
			System.out.println("Channel closed.");

		} finally {

			// When all is said and done, stop the I/O thread
			workerGroup.shutdownGracefully();

		}
	}

}
