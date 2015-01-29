package net.cloud.client.file;

import net.cloud.client.event.shutdown.ShutdownHook;
import net.cloud.client.event.shutdown.ShutdownService;
import net.cloud.client.event.shutdown.hooks.FileServerShutdownHook;
import net.cloud.client.file.request.FileRequest;
import net.cloud.client.file.request.handler.RequestHandler;
import net.cloud.client.logging.Logger;

/**
 * The front of the file server module.  Deals with FileRequests. 
 * The file server accepts a FileRequest and will attempt to fulfill it. 
 * This occurs through a chain of classes and calls behind this facade of sorts. <br>
 * Create a FileRequest object and submit it here.  The request can then be waited upon 
 * or a listener can be attached to it which will be acted on when the request is fulfilled.<br>
 * An example of the usage to obtain a BufferedReader on a file:<br>
 * <code>
 * BufferedReaderRequest req = new BufferedReaderRequest(FileAddressBuilder.newBuilder().createCommandScriptAddress("echo"));<br>
 * req.waitForRequest();<br>
 * BufferedReader fileReader = req.getFileDescriptor();<br>
 * </code>
 * @see FileRequest
 * @see FileAddressBuilder
 */
public class FileServer implements ShutdownService {
	
	/** Singleton instance to the FileServer */
	private static FileServer instance;
	
	/** An instance of RequestHandler to delegate request to */
	private RequestHandler requestHandler;
	
	/** The thread that the logic loop is running on */
	private Thread logicThread;
	
	/** The Runnable object the thread is executing */
	private FileServerThread fileServerThread;
	
	/** The hook to stop the File Server */
	private ShutdownHook shutdownHook;

	/** Private singleton constructor */
	private FileServer()
	{
		// Initialize our own request handler
		this.requestHandler = new RequestHandler();
		
		// Start up a thread running the logic loop
		fileServerThread = new FileServerThread(requestHandler);
		logicThread = new Thread(fileServerThread);
		logicThread.start();
		
		// Create a shutdown hook to stop this process
		shutdownHook = new FileServerShutdownHook(logicThread, fileServerThread);
		
		Logger.writer().println("File Server now running");
		Logger.writer().flush();
	}
	
	/**
	 * Obtain a reference to the singleton instance of the FileServer, 
	 * to submit requests to.
	 * @return A reference to the FileServer
	 */
	public static FileServer instance()
	{
		if(instance == null)
		{
			synchronized(FileServer.class)
			{
				if(instance == null)
				{
					instance = new FileServer();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Submit a FileRequest to the server, so that it is eventually 
	 * handled and assigned a file descriptor. When it is handled, 
	 * if there is a listener attached, the listener will also be called.
	 * @param request The FileRequest to submit
	 * @throws FileRequestException If the request could not be submitted
	 */
	public void submit(FileRequest<?> request) throws FileRequestException
	{
		// Delegate the submission to the logic object
		fileServerThread.submit(request);
	}
	
	/**
	 * A convenience method. It will submit the request to the FileServer, 
	 * wait for the request to be ready, and then return the file descriptor 
	 * from the completed request.  This is equivalent to 3 method calls:<br>
	 * <code>
	 * FileServer.instance().submit(request);<br>
	 * request.waitForRequest();<br>
	 * request.getFileDescriptor();<br>
	 * </code>
	 * @param request The request to submit and wait on
	 * @param <T> The type of the file descriptor object. Should be inferred from the request.
	 * @return The file descriptor that is being requested
	 * @throws FileRequestException If any of these convenient steps fail
	 */
	public <T> T submitAndWaitForDescriptor(FileRequest<T> request) throws FileRequestException
	{
		submit(request);
		
		request.waitForRequest();
		
		return request.getFileDescriptor();
	}

	/**
	 * Obtain the ShutdownHook for the FileServer. It will stop the service, so 
	 * no more requests will be accepted. The hook is created during construction, 
	 * so a NPE should not be a concern. Still, it's a possibility.
	 * @return The ShutdownHook capable of stopping the FileServer
	 * @throws NullPointerException If the hook has not yet been created
	 */
	@Override
	public ShutdownHook getShutdownHook() throws NullPointerException {
		return shutdownHook;
	}

}
