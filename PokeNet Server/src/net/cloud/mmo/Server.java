package net.cloud.mmo;

import net.cloud.mmo.event.command.CommandService;
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
		Server.getInstance().init();
	}
	
	private Server()
	{
	}
	
	public static Server getInstance()
	{
		if(instance == null)
		{
			instance = new Server();
		}
		
		// This is started from main(). Let's just.. not check it. It'll be our secret
		return instance;
	}
	
	/**
	 * Initialize the server. 
	 * Involves starting all of the services, and then waiting until they are shutdown.
	 */
	private void init()
	{
		// Initialize the shutdown handler
		shutdownHandler = new ShutdownHandler();

		// Start up the various sub-systems and services in the server
		startServices();

		// Sit back, wait for someone to tell us it's shutdown time
		try {
			shutdownHandler.waitForShutdown();
		} catch (Exception e) {
			// Something went wrong with the shutdown process.. not much we can do.
			// Just won't be a graceful shutdown.
			e.printStackTrace();
		}
	}
	
	/**
	 * Start the sub-services the main thread is responsible for. 
	 * These include the Netty Server, a CommandService listening on the console, 
	 * and the TaskEngine.
	 */
	private void startServices()
	{
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

		// Start a CommandService on the standard in and out
		CommandService consoleCommandService = new CommandService(System.in, System.out);
		shutdownHandler.addHook(consoleCommandService.getShutdownHook());
		
		// Grab the TaskEngine, put its shutdown hook in here
		shutdownHandler.addHook(TaskEngine.getInstance().getShutdownHook());
	}
	
	/**
	 * Shut down the entire Server. (Hopefully gracefully...)
	 * The Server (main thread) will be waiting for some other thread to call this.
	 */
	public void shutdown()
	{
		// But hey, that's what a ShutdownHandler is for, right?
		try {
			System.out.println("Starting shutdown");
			
			shutdownHandler.shutdownAll();
			
			System.out.println("Done shutting down");
		} catch (Exception e) {
			// Hey look. The end of an exception chain
			e.printStackTrace();
		}
	}

}
