package net.cloud.mmo.event.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import net.cloud.mmo.event.shutdown.ShutdownHook;
import net.cloud.mmo.event.shutdown.ShutdownService;
import net.cloud.mmo.event.shutdown.hooks.CommandServiceShutdownHook;

public class CommandService implements ShutdownService {
	
	// TODO: Idea is take in some input stream. Create Scanner. Use that to read lines, parse commands.
	// commands can be tossed to CommandHandler. (Commands will be handled same whether in-game or 
	// from some external console.. probably with different privileges and availability.)
	// This should probably be a ShutdownService, since it *does* take control and block a thread.
	// As to how shutting it down will work.. have to think about that.
	
	
	
	private Thread serviceThread;
	
	private ShutdownHook shutdownHook;
	
	public CommandService(InputStream in, OutputStream out)
	{
		// Create and start thread to deal with io and kicking off commands
		CommandServiceThread cmdSvcThread = new CommandServiceThread(in, out);
		serviceThread = new Thread(cmdSvcThread);
		serviceThread.start();
		
		// Create the shutdown hook that'll stop this service (the thread)
		shutdownHook = new CommandServiceShutdownHook(cmdSvcThread, serviceThread);
	}
	
	@Override
	public ShutdownHook getShutdownHook() throws NullPointerException {
		return Optional.ofNullable(shutdownHook).orElseThrow(() -> new NullPointerException("Null ShutdownHook"));
	}

}
