package net.cloud.mmo;

import net.cloud.mmo.event.shutdown.ShutdownHandler;
import net.cloud.mmo.event.task.TaskEngine;
import net.cloud.mmo.nio.NettyServer;

/**
 * The entry point for the PokeNet server.
 * Will start the Netty Server and start other necessary initialization tasks.
 */
public class Server {
	
	/** ShutdownHandler for all the services the main thread starts up */
	private ShutdownHandler shutdownHandler;

	public static void main(String[] args) {
		new Server();
		
		
		
		
		// TODO: remove
		TaskEngine.getInstance().submitImmediate(() -> System.out.println("A task!"));
	}
	
	public Server()
	{
		// Initialize the shutdown handler
		shutdownHandler = new ShutdownHandler();
		
		// Start the Netty server
		NettyServer nettyServer = null;
		try {
			nettyServer = new NettyServer();
			nettyServer.start();
			
			// Add the hook from the Netty server
			shutdownHandler.addHook(nettyServer.getShutdownHook());
		} catch (InterruptedException e) {
			System.err.println("Could not start server. Shutting down.");
			e.printStackTrace();
			System.exit(1);
		}
		
		
	}

}
