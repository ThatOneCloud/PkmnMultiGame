package net.cloud.client;

import java.time.Clock;
import java.time.ZoneId;

import net.cloud.client.event.shutdown.ShutdownHandler;
import net.cloud.client.event.task.TaskEngine;
import net.cloud.client.file.FileServer;
import net.cloud.client.game.World;
import net.cloud.client.logging.Logger;
import net.cloud.client.nio.NettyClient;
import net.cloud.gfx.Mainframe;
import net.cloud.gfx.interfaces.LoginInterface;

/**
 * Entry point, to start the game client. 
 * Can also act as a central point for subsystems if need be.
 */
public class Client {
	
	/** A Clock for the game to standardize on. All timing operations can then rely on this clock */
	public static final Clock CLOCK = Clock.system(ZoneId.of("UTC-5"));
	
	/** The single instance of the Client class */
	private static Client instance;
	
	/** ShutdownHandler for all the services the main thread starts up */
	private ShutdownHandler shutdownHandler;
	
	/** Oddly, keep an instance around so we can connect later */
	private NettyClient nettyClient;

	/**
	 * Main method to start the client. Starting the client means 
	 * starting up the sub systems and then waiting for shutdown. 
	 * @param args Runtime arguments. Accepts none.
	 */
	public static void main(String[] args)
	{
		// Honestly this is here so that it kicks open the Eclipse console window
		System.err.println("Starting PokeNet Client!");
		
		// Kick off the client on the main thread
		Client.instance().init();
	}

	/** Private default constructor - does nothing */
	private Client()
	{
	}
	
	/**
	 * Obtain a reference to the Client object. This object acts as a linkage to 
	 * components of the client. Responsible for starting and stopping many services. 
	 * @return The singleton Client instance
	 */
	public static Client instance()
	{
		if(instance == null)
		{
			synchronized(Client.class)
			{
				if(instance == null)
				{
					instance = new Client();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Initialize the client by starting a few services and obtaining their shutdown hooks. 
	 * Then goes and waits until something tells the client to shut down.
	 */
	private void init()
	{
		// Get the World initialized (rather than lazy initialization)
		World.instance();
				
		// Initialize the shutdown handler
		shutdownHandler = new ShutdownHandler();
		
		// Start up the client, but do not connect yet
		nettyClient = new NettyClient();
		try {
			nettyClient.startup();
		} catch (InterruptedException e1) {
			Logger.instance().logException("Could not start up Netty Client", e1);
		}

		// Start up the various sub-systems and services in the server
		startServices();
		
		// It may seem odd to do this here rather than during construction of the Quasi-root, but show the login interface
		Mainframe.root().add(new LoginInterface());

		// Sit back, wait for someone to tell us it's shutdown time
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
	 * Start the sub-services the main thread is responsible for. 
	 * These include the Task Engine, file server, logger, etc.
	 */
	private void startServices()
	{
		// Grab the frame, which will kick off quite the series of events. (Including getting it showing)
		shutdownHandler.addHook(Mainframe.instance().gfx().getShutdownHook());
		
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
	 * Shut down the entire Client. (Hopefully gracefully...)
	 * The Client (main thread) will be waiting for some other thread to call this.
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
	
	/**
	 * @return The shutdown handler the client is using to stop global services
	 */
	public ShutdownHandler shutdownHandler()
	{
		return shutdownHandler;
	}
	
	/**
	 * @return The NettyClient object acting as an entry point to the network code
	 */
	public NettyClient nettyClient()
	{
		return nettyClient;
	}

}
