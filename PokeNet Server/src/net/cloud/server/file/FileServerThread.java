package net.cloud.server.file;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.cloud.server.file.request.FileRequest;
import net.cloud.server.file.request.handler.RequestHandler;
import net.cloud.server.logging.Logger;

/**
 * Contains the logic loop for the File Server.  Takes care of pulling 
 * requests out in turn and handing them off for handling.
 */
public class FileServerThread implements Runnable {
	
	/** Flag to determine if the logic loop should be running */
	private volatile boolean running;
	
	/** The object we use to handle requests */
	private RequestHandler requestHandler;
	
	/** We'll use a blocking queue to handle request storage for us */
	private BlockingQueue<FileRequest<?>> requestQueue;

	public FileServerThread(RequestHandler requestHandler)
	{
		// Start with the flag true
		this.running = true;
		
		this.requestHandler = requestHandler;
		
		// Using a LinkedBlockingQueue. It's unbounded and will block when empty
		requestQueue = new LinkedBlockingQueue<>();
	}

	@Override
	public void run()
	{
		// Only go while the running flag is set
		while(running && !Thread.currentThread().isInterrupted())
		{
			try {
				// Thankfully the blocking queue takes care of waiting for us
				FileRequest<?> nextRequest = requestQueue.take();
				
				// So now we'll utilize double dispatch to handle the request dynamically
				nextRequest.handle(requestHandler);
			} catch (InterruptedException e) {
				// It's even so kind as to throw an InterruptedException (ruddy BufferedReader...)
				Logger.instance().logException("FileServerThread interrupted", e);
			}
		}

	}
	
	/**
	 * Submit a request to eventually be handled by the logic loop. 
	 * @param request The request to eventually handle
	 * @throws FileRequestException If the request could not be accepted
	 */
	public void submit(FileRequest<?> request) throws FileRequestException
	{
		// Only accept submissions while we're running
		if(!running)
		{
			throw new FileRequestException("File server is not running");
		}
		
		// Submit it to the queue.
		try {
			requestQueue.add(request);
		} catch (Exception e) {
			// It could not be added. Re-throw with the cause
			throw new FileRequestException("Could not add request", e);
		}
	}
	
	/**
	 * Set the running flag. Once this is false, it cannot be undone. 
	 * The logic loop will be stopped.
	 * @param runningFlag What to set the flag to.
	 */
	public void setRunning(boolean runningFlag)
	{
		this.running = runningFlag;
	}

}
