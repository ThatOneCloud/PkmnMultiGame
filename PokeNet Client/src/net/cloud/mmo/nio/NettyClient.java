package net.cloud.mmo.nio;

import net.cloud.mmo.nio.packet.PacketSender;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

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
			ChannelFuture f = b.connect("localhost", 43594).sync();
			System.out.println("Connected to server.");

			// Create a PacketSender
			// TODO: Move so that the Player has their own
			PacketSender sender = new PacketSender(f.channel());
			sender.sendTestPacket(54);
			System.out.println("Sent test packet.");

			// Blocks until the connection to the server is closed
			f.channel().closeFuture().sync();
			System.out.println("Channel closed.");

		} finally {

			// When all is said and done, stop the I/O thread
			workerGroup.shutdownGracefully();

		}
	}

}
