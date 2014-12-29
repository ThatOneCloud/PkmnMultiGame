package net.cloud.mmo;

import java.io.BufferedReader;
import java.io.IOException;

import net.cloud.mmo.event.command.CommandService;
import net.cloud.mmo.event.shutdown.ShutdownHandler;
import net.cloud.mmo.event.task.TaskEngine;
import net.cloud.mmo.file.FileRequestException;
import net.cloud.mmo.file.FileServer;
import net.cloud.mmo.file.address.FileAddressBuilder;
import net.cloud.mmo.file.request.BufferedReaderRequest;
import net.cloud.mmo.file.request.FileRequest;
import net.cloud.mmo.file.request.LoadRequest;
import net.cloud.mmo.nio.NettyServer;
import net.cloud.mmo.util.IOUtil;

/**
 * The entry point for the PokeNet server.
 * Will start the Netty Server and start other necessary initialization tasks.
 */
public class Server {
	
	/** The single instance of the Server class */
	private static Server instance;
	
	/** ShutdownHandler for all the services the main thread starts up */
	private ShutdownHandler shutdownHandler;
	
	/**
	 * Entry point! Start the server and all of its sub-services
	 */
	public static void main(String[] args) {
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
			shutdownHandler.waitForShutdown(IOUtil.SYS_OUT);
			
			System.out.println("Shutdown complete");
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
		CommandService consoleCommandService = new CommandService(IOUtil.SYS_IN, IOUtil.SYS_OUT);
		shutdownHandler.addHook(consoleCommandService.getShutdownHook());
		
		// Grab the TaskEngine, put its shutdown hook in here
		shutdownHandler.addHook(TaskEngine.getInstance().getShutdownHook());
		
		// The FileServer is another service we'll start here
		shutdownHandler.addHook(FileServer.instance().getShutdownHook());
		
		
		
		// TODO: remove
		BufferedReaderRequest req = new BufferedReaderRequest(FileAddressBuilder.newBuilder().createCommandScriptAddress("echo"));
		try {
//			FileServer.instance().submit(req);
//			
//			System.out.println("waiting");
//			
//			req.waitForRequest();
//			
//			System.out.println("done waiting");
//			
//			BufferedReader br = req.getFileDescriptor();
			
			BufferedReader br = FileServer.instance().submitAndWaitForDescriptor(req);
			
			System.out.println("reading");
			
			System.out.println(br.readLine());
			
			System.out.println("done reading");
		} catch (FileRequestException | IOException e) {
			e.printStackTrace();
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
			System.out.println("Starting shutdown");
			
			shutdownHandler.shutdownAll(IOUtil.SYS_OUT);
		} catch (Exception e) {
			// Hey look. The end of an exception chain
			e.printStackTrace();
		}
	}

}
