package net.cloud.mmo;

import java.util.concurrent.ExecutionException;

import net.cloud.mmo.event.command.CommandException;
import net.cloud.mmo.event.command.CommandHandler;
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
		
		try {
			System.out.println(CommandHandler.getInstance().handleCommand("::Test -p \" three four\"").get());
		} catch (InterruptedException | ExecutionException | CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
		
		
		// Kick-off the server on the main thread
		new Server();
		
		
		
		
		// TODO: remove
		TaskEngine.getInstance().submitImmediate(() -> System.out.println("A task!"));
	}
	
	private Server()
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
	
	public static Server getInstance()
	{
		// This is started from main(). Let's just.. not check it. It'll be our secret
		return instance;
	}
	
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

		// TODO: Command sub-system
	}
	
	/**
	 * Shut down the entire Server. (Hopefully gracefully...)
	 * The Server (main thread) will be waiting for some other thread to call this.
	 */
	public void shutdown()
	{
		// But hey, that's what a ShutdownHandler is for, right?
		try {
			shutdownHandler.shutdownAll();
		} catch (Exception e) {
			// Hey look. The end of an exception chain
			e.printStackTrace();
		}
	}

}
