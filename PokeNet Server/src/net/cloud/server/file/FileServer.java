package net.cloud.server.file;

import java.nio.file.Files;

import net.cloud.server.event.shutdown.ShutdownHook;
import net.cloud.server.event.shutdown.ShutdownService;
import net.cloud.server.event.shutdown.hooks.FileServerShutdownHook;
import net.cloud.server.file.address.FileAddress;
import net.cloud.server.file.address.FileAddressBuilder;
import net.cloud.server.file.request.FileRequest;
import net.cloud.server.file.request.handler.RequestHandler;

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
	private static volatile FileServer instance;
	
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
	 * Slightly different that submitting a request and waiting for it, this convenience 
	 * method will actually use the calling thread to fulfill the request rather than 
	 * relying on the file server thread. This should only be faster, since the calling thread 
	 * would just be waiting anyways and the file server thread may be busy.
	 * @param request The request to submit and wait on
	 * @param <T> The type of the file descriptor object. Should be inferred from the request.
	 * @return The file descriptor that is being requested
	 * @throws FileRequestException If any of these convenient steps fail
	 */
	public <T> T submitAndWaitForDescriptor(FileRequest<T> request) throws FileRequestException
	{
		// Throw it straight to the handler
		request.handle(requestHandler);
		
		// Wait just in case. Most likely will be no wait.
		request.waitForRequest();
		
		return request.getFileDescriptor();
	}
	
	/**
	 * Check to see if a file exists. This is not a typical request and is not handled asynchronously. 
	 * Rather, the method is here to maintain the division of responsibility.
	 * @param address The location of the file
	 * @return True if the file already exists
	 */
	public boolean fileExists(FileAddress address)
	{
		return Files.exists(address.getPath());
	}

	/**
	 * Obtain the ShutdownHook for the FileServer. It will stop the service, so 
	 * no more requests will be accepted. The hook is created during construction, 
	 * so a NPE should not be a concern. Still, it's a possibility.
	 * @return The ShutdownHook capable of stopping the FileServer
	 * @throws NullPointerException If the hook has not yet been created
	 */
	@Override
	public ShutdownHook getShutdownHook() throws NullPointerException
	{
		return shutdownHook;
	}

}
