package net.cloud.server.event.shutdown.hooks;

import java.io.PrintWriter;

import net.cloud.server.event.shutdown.ShutdownException;
import net.cloud.server.event.shutdown.ShutdownHook;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;

public class NettyShutdownHook implements ShutdownHook {
	
	/** The ChannelFuture given as a result from binding the server */
	private ChannelFuture channelFuture;
	
	/** The EventLoopGroup responsible for accepting connections */
	private EventLoopGroup bossGroup;
	
	/** The EventLoopGroup responsible for handling established connections */
	private EventLoopGroup workerGroup;

	public NettyShutdownHook(ChannelFuture future, EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
		this.channelFuture = future;
		this.bossGroup = bossGroup;
		this.workerGroup = workerGroup;
	}

	/**
	 * Attempt to stop the Netty Server. 
	 * Blocks until the attempt is finished. The attempt will enforce a quiet period 
	 * where I/O must be silent for some time in order to assure a graceful shutdown.
	 */
	@Override
	public void shutdown(PrintWriter out) throws ShutdownException {
		out.println("Shutting down Netty Server");
		out.flush();
		
		// First we need to close the channel
		try {
			channelFuture.channel().close().sync();
		} catch (InterruptedException e) {
			// Re-throw
			throw new ShutdownException("Could not close Netty channel.", e);
		}

		// And now the EventLoopGroups can be shutdown in turn
		try {
			workerGroup.shutdownGracefully().sync();
			bossGroup.shutdownGracefully().sync();
		} catch (InterruptedException e) {
			// Re-throw
			throw new ShutdownException("Failed to gracefully shutdown EventLoopGroups.", e);
		}
		
		out.println("Netty Server shut down.");
		out.flush();
	}

}
