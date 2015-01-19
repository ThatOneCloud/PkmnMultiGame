package net.cloud.server.event.shutdown.hooks;

import java.io.PrintWriter;

import net.cloud.server.event.command.CommandServiceThread;
import net.cloud.server.event.shutdown.ShutdownException;
import net.cloud.server.event.shutdown.ShutdownHook;

/**
 * A ShutdownHook that will stop a CommandService. 
 * The hook requires the CommandServiceThread and Thread that are 
 * being used to run the service.
 */
public class CommandServiceShutdownHook implements ShutdownHook {
	
	/** The Runnable object doing the io loop */
	private CommandServiceThread cmdSvcThread;
	
	/** The Thread the service is being run on */
	private Thread serviceThread;
	
	/**
	 * Create a new ShutdownHook for a CommandService
	 * @param cmdSvcThread The Runnable object doing the io loop
	 * @param serviceThread The Thread the service is being run on
	 */
	public CommandServiceShutdownHook(CommandServiceThread cmdSvcThread, Thread serviceThread) {
		this.cmdSvcThread = cmdSvcThread;
		this.serviceThread = serviceThread;
	}

	/**
	 * Shut down the associated CommandService. Tells the loop to stop running 
	 * and interrupts the thread it is running on.
	 * @param out A PrintWriter to which status information will be output
	 * @throws ShutdownException If the service could not be stopped
	 */
	@Override
	public void shutdown(PrintWriter out) throws ShutdownException {
		out.println("Shutting down command service");
		out.flush();
		
		// Try to interrupt the thread
		try {
			cmdSvcThread.setRunning(false);
			serviceThread.interrupt();
		} catch (Exception e) {
			// Chain exceptions
			throw new ShutdownException("Could not interrupt command service thread", e);
		}
		
		out.println("Command service shut down");
		out.flush();
	}

}
