package net.cloud.mmo;

import net.cloud.mmo.event.shutdown.ShutdownException;
import net.cloud.mmo.event.shutdown.ShutdownHandler;
import net.cloud.mmo.event.task.TaskEngine;
import net.cloud.mmo.nio.NettyServer;

/**
 * The entry point for the PokeNet server.
 * Will start the Netty Server and start other necessary initialization tasks.
 */
public class Server {
	
	/** The single instance of the Server class */
	private static Server instance;
	
	/** ShutdownHandler for all the services the main thread starts up */
	private ShutdownHandler shutdownHandler;

	public static void main(String[] args) {
		// Kick-off the server on the main thread
		new Server();
		
		
		
		
		// TODO: remove
		TaskEngine.getInstance().submitImmediate(() -> System.out.println("A task!"));
	}
	
	private Server()
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
		
		// The last thing the main thread will become responsible for is console commands
		
	}
	
	public static Server getInstance()
	{
		// This is started from main(). Let's just.. not check it. It'll be our secret
		return instance;
	}
	
	/**
	 * Shut down the entire Server. (Hopefully gracefully...)
	 */
	public void shutdown()
	{
		// But hey, that's what a ShutdownHandler is for, right?
		try {
			shutdownHandler.shutdownAll();
		} catch (ShutdownException e) {
			// Hey look. The end of an exception chain
			e.printStackTrace();
		}
	}

}
