package net.cloud.client.event.shutdown.hooks;

import java.io.PrintWriter;

import net.cloud.client.event.shutdown.ShutdownException;
import net.cloud.client.event.shutdown.ShutdownHook;
import net.cloud.client.file.FileServerThread;

/**
 * This hook will stop the file server.  This means that the 
 * file server will no longer accept requests, but an attempt 
 * will be made to serve pending requests (there may be some saving to do, right?)
 */
public class FileServerShutdownHook implements ShutdownHook {
	
	/** The thread executing the file server logic loop */
	private Thread thread;
	
	/** The object with the logic loop code */
	private FileServerThread fileServerThread;

	/**
	 * Create a shutdown hook for the File Server. The thread is interrupted and 
	 * the file server object has its flag set.
	 * @param thread The thread executing the file server logic loop
	 * @param fileServerThread The object with the logic loop code
	 */
	public FileServerShutdownHook(Thread thread, FileServerThread fileServerThread)
	{
		this.thread = thread;
		this.fileServerThread = fileServerThread;
	}

	/**
	 * Tell the file server it's time to stop. The file server will no longer 
	 * accept new requests but will attempt to serve requests that are currently 
	 * queued. The idea is that it might have some saving to finish up. <br>
	 * This shutdown method will return immediately.
	 */
	@Override
	public void shutdown(PrintWriter out) throws ShutdownException
	{
		out.println("Shutting down file server");
		out.flush();
		
		// Try to interrupt the thread
		try {
			fileServerThread.setRunning(false);
			thread.interrupt();
		} catch (Exception e) {
			// Chain exceptions
			throw new ShutdownException("Could not interrupt file server thread", e);
		}
		
		out.println("File server shut down");
		out.flush();
	}

}
