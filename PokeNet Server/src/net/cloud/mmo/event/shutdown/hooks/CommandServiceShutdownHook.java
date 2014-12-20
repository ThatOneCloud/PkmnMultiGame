package net.cloud.mmo.event.shutdown.hooks;

import net.cloud.mmo.event.command.CommandServiceThread;
import net.cloud.mmo.event.shutdown.ShutdownException;
import net.cloud.mmo.event.shutdown.ShutdownHook;

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
	 * @throws ShutdownException If the service could not be stopped
	 */
	@Override
	public void shutdown() throws ShutdownException {
		// Try to interrupt the thread
		try {
			cmdSvcThread.setRunning(false);
			serviceThread.interrupt();
		} catch (Exception e) {
			// Chain exceptions
			throw new ShutdownException("Could not interrupt command service thread", e);
		}
	}

}
