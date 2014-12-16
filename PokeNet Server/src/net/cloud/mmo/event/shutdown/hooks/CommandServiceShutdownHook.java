package net.cloud.mmo.event.shutdown.hooks;

import net.cloud.mmo.event.command.CommandServiceThread;
import net.cloud.mmo.event.shutdown.ShutdownException;
import net.cloud.mmo.event.shutdown.ShutdownHook;

public class CommandServiceShutdownHook implements ShutdownHook {
	
	private CommandServiceThread cmdSvcThread;
	
	private Thread serviceThread;
	
	public CommandServiceShutdownHook(CommandServiceThread cmdSvcThread, Thread serviceThread) {
		this.cmdSvcThread = cmdSvcThread;
		this.serviceThread = serviceThread;
	}

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
