package net.cloud.server;

import java.time.Clock;
import java.time.ZoneId;

import net.cloud.server.event.command.CommandService;
import net.cloud.server.event.shutdown.ShutdownHandler;
import net.cloud.server.event.task.TaskEngine;
import net.cloud.server.file.FileServer;
import net.cloud.server.game.action.ActionManager;
import net.cloud.server.logging.Logger;
import net.cloud.server.nio.NettyServer;
import net.cloud.server.util.IOUtil;

/**
 * The entry point for the PokeNet server.
 * Will start the Netty Server and start other necessary initialization tasks.
 */
public class Server {
	
	/** A Clock for the server to standardize on. All timing operations can then rely on this clock */
	public static final Clock CLOCK = Clock.system(ZoneId.of("UTC-5"));
	
	/** The single instance of the Server class */
	private static Server instance;
	
	/** ShutdownHandler for all the services the main thread starts up */
	private ShutdownHandler shutdownHandler;
	
	/**
	 * Entry point! Start the server and all of its sub-services
	 * @param args None taken
	 */
	public static void main(String[] args)
	{
		// Kick-off the server on the main thread
		Server.getInstance().init();
	}
	
	/** Private default constructor for singleton pattern - does nothing */
	private Server()
	{
	}
	
	/**
	 * Obtain a reference to the Server object. This object acts as a linkage to 
	 * components of the server. Responsible for starting and stopping many services. 
	 * @return The singleton Server instance
	 */
	public static Server getInstance()
	{
		if(instance == null)
		{
			synchronized(Server.class)
			{
				if(instance == null)
				{
					instance = new Server();
				}
			}
		}
		
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
		
		// Get all of the actions loaded up-front
		loadActions();

		// Start up the various sub-systems and services in the server
		startServices();

		// Sit back, wait for someone to tell us it's shutdown time
		awaitShutdown();
	}
	
	/**
	 * Load all of the actions via the ActionManager
	 */
	private void loadActions()
	{
		try {
			// ActionManager is the facade of sorts we communicate through
			ActionManager.instance().loadAllActions(Logger.writer());
		} catch (Exception e) {
			// One or more things failed to load, it gives us an exception with a nice message
			Logger.instance().logException("Exception loading Actions on startup", e);
		}
	}

	/**
	 * Start the sub-services the main thread is responsible for. 
	 * These include the Netty Server, a CommandService listening on the console, 
	 * the Task Engine, the file server, and the logging system.
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
			Logger.instance().logException("Could not start server. Shutting down.", e);
			System.exit(1);
		}

		// Start a CommandService on the standard in and out
		CommandService consoleCommandService = new CommandService(IOUtil.SYS_IN, Logger.writer());
		shutdownHandler.addHook(consoleCommandService.getShutdownHook());
		Logger.writer().println("Commands may now be entered via the console");
		Logger.writer().flush();
		
		// Grab the TaskEngine, put its shutdown hook in here
		shutdownHandler.addHook(TaskEngine.instance().getShutdownHook());
		
		// The FileServer is another service we'll start here
		shutdownHandler.addHook(FileServer.instance().getShutdownHook());
		
		// Ideally having the Logging service last means it'll shutdown last
		if(ConfigConstants.LOGGING_ENABLED)
		{
			shutdownHandler.addHook(Logger.instance().getShutdownHook());
		}
	}
	
	/**
	 * Waits until the shutdown handler has its shutdown method called. 
	 * Blocks the main thread until it's time to shut the server down
	 */
	private void awaitShutdown() {
		try {
			shutdownHandler.waitForShutdown(Logger.writer());
			
			Logger.writer().println("Shutdown complete");
			Logger.writer().flush();
		} catch (Exception e) {
			// Something went wrong with the shutdown process.. not much we can do.
			// Just won't be a graceful shutdown.
			Logger.instance().logException("Could not gracefully shutdown", e);
		}
	}
	
	/**
	 * Shut down the entire Server. (Hopefully gracefully...)
	 * The Server (main thread) will be waiting for some other thread to call this.
	 */
	public void shutdown()
	{
		// But hey, that's what a ShutdownHandler is for, right?
		try {
			Logger.writer().println("Starting shutdown");
			Logger.writer().flush();
		
			shutdownHandler.shutdownAll(Logger.writer());
		} catch (Exception e) {
			// Hey look. The end of an exception chain
			Logger.instance().logException("Shutdown resulted in an exception", e);
		}
	}

}
