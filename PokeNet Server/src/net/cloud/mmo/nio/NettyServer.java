package net.cloud.mmo.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Initializes the server, built on the Netty framework. 
 * The class's task is to get the server up and running, 
 * ready to accept and deal with client connections. 
 */
public class NettyServer {
	
	/** The port the server will become bound to */
	public static final int PORT = 43594;

	/**
	 * Start the Netty server.  This is the part of the server that accepts and handles 
	 * incoming connections. So after this, the whole thing will be ready for network I/O
	 * @throws InterruptedException If a channel operation was interrupted
	 */
	public void start() throws InterruptedException
	{
		// I've followed the guide at http://netty.io/wiki/user-guide-for-4.x.html
		// so a large amount of code will be the same or similar to that.

		// First, EventLoopGroups are created - for handling tasks
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			// Bootstrap object handles a lot of start up for us
			ServerBootstrap bootStrap = new ServerBootstrap();

			// While this configures the server
			bootStrap.group(bossGroup, workerGroup);
			bootStrap.channel(NioServerSocketChannel.class);
			bootStrap.childHandler(new NettyServerChannelInitializer());
			bootStrap.option(ChannelOption.SO_BACKLOG, 128);
			bootStrap.childOption(ChannelOption.SO_KEEPALIVE, true);

			// This allows for incoming connections, waits until binding is done
			ChannelFuture future = bootStrap.bind(PORT).sync();

			System.out.println("PokeNet Server is now running on port " + PORT);

			// Only returns when the server socket is closed, allowing the finally clause to gracefully shutdown the server
			future.channel().closeFuture().sync();
		} finally {

			// Gracefully shuts down the server once the channel has been closed
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();

		}
	}



}
