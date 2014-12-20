package net.cloud.mmo.event.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import net.cloud.mmo.event.shutdown.ShutdownHook;
import net.cloud.mmo.event.shutdown.ShutdownService;
import net.cloud.mmo.event.shutdown.hooks.CommandServiceShutdownHook;

public class CommandService implements ShutdownService {	
	
	/**
	 * The thread the service will be running on
	 */
	private Thread serviceThread;
	
	/**
	 * A ShutdownHook that can be used to stop the command service
	 */
	private ShutdownHook shutdownHook;
	
	/**
	 * Create a new ComamndService. It will run on the provided streams. 
	 * The service will start immediately after creation - no start method needs to be called
	 * @param in A stream from which commands will be read from
	 * @param out A stream to which responses will be written
	 */
	public CommandService(InputStream in, OutputStream out)
	{
		// Create and start thread to deal with io and kicking off commands
		CommandServiceThread cmdSvcThread = new CommandServiceThread(in, out);
		serviceThread = new Thread(cmdSvcThread);
		serviceThread.start();
		
		// Create the shutdown hook that'll stop this service (the thread)
		shutdownHook = new CommandServiceShutdownHook(cmdSvcThread, serviceThread);
	}
	
	/**
	 * Obtain the ShutdownHook for this CommandService. It will stop the service, so 
	 * no more commands will be accepted. The hook is created during construction, 
	 * so a NPE should not be a concern. Still, it's a possibility.
	 * @return The ShutdownHook capable of stopping this CommandService
	 * @throw NullPointerException If the hook has not yet been created
	 */
	@Override
	public ShutdownHook getShutdownHook() throws NullPointerException {
		return Optional.ofNullable(shutdownHook).orElseThrow(() -> new NullPointerException("Null ShutdownHook"));
	}

}
