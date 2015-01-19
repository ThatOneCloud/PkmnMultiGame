package net.cloud.server.event.command;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Optional;

import net.cloud.server.event.shutdown.ShutdownHook;
import net.cloud.server.event.shutdown.ShutdownService;
import net.cloud.server.event.shutdown.hooks.CommandServiceShutdownHook;

/**
 * A CommandService is a service running on its own thread 
 * that will read commands from the provided input, parse them, 
 * have them executed via the task engine, and return the result 
 * of the command through the provided output.
 */
public class CommandService implements ShutdownService {
	
	/** Reasonable default for polling time on the input */
	private static final int DEFAULT_POLL_DELAY = 200;
	
	/** The thread the service will be running on */
	private Thread serviceThread;
	
	/** A ShutdownHook that can be used to stop the command service */
	private ShutdownHook shutdownHook;
	
	/**
	 * Create a new ComamndService. It will run on the provided streams. 
	 * The service will start immediately after creation - no start method needs to be called. 
	 * The delay between checks for readable data will be set to a reasonable default.
	 * @param in A stream from which commands will be read from
	 * @param out A stream to which responses will be written
	 */
	public CommandService(BufferedReader in, PrintWriter out)
	{
		this(DEFAULT_POLL_DELAY, in, out);
	}
	
	/**
	 * Create a new ComamndService. It will run on the provided streams. 
	 * The service will start immediately after creation - no start method needs to be called
	 * @param pollDelay The amount of time between checks for readable data on <code>in</code>
	 * @param in A stream from which commands will be read from
	 * @param out A stream to which responses will be written
	 */
	public CommandService(int pollDelay, BufferedReader in, PrintWriter out)
	{
		// Create and start thread to deal with io and kicking off commands
		CommandServiceThread cmdSvcThread = new CommandServiceThread(pollDelay, in, out);
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
	 * @throws NullPointerException If the hook has not yet been created
	 */
	@Override
	public ShutdownHook getShutdownHook() throws NullPointerException {
		return Optional.ofNullable(shutdownHook).orElseThrow(() -> new NullPointerException("Null ShutdownHook"));
	}

}
