package net.cloud.client.file.request.handler;

import net.cloud.client.file.request.BufferedReaderRequest;
import net.cloud.client.file.request.CachedFileRegionRequest;
import net.cloud.client.file.request.CachedFileRequest;
import net.cloud.client.file.request.PrintWriterRequest;
import net.cloud.client.file.request.RandomAccessFileLoadRequest;
import net.cloud.client.file.request.FileOutputStreamRequest;
import net.cloud.client.file.request.XmlLoadRequest;
import net.cloud.client.file.request.XmlSaveRequest;
import net.cloud.client.file.request.handler.LoadRequestHandler;
import net.cloud.client.file.request.handler.SaveRequestHandler;

/**
 * A class which serves as a facade to other, more specific, handlers. 
 * These handler classes are purposed with taking whatever action is needed 
 * to fulfill the request and get it ready for action to be taken upon it.
 */
public class RequestHandler {
	
	/** A handler which will deal with subclasses of LoadRequest */
	private LoadRequestHandler loadRequestHandler;
	
	/** A handler which will deal with subclasses of SaveRequest */
	private SaveRequestHandler saveRequestHandler;
	
	/**
	 * Create a new RequestHandler which can be used to, well, handle requests. 
	 */
	public RequestHandler()
	{
		this.loadRequestHandler = new LoadRequestHandler();
		this.saveRequestHandler = new SaveRequestHandler();
	}

	/**
	 * See {@link LoadRequestHandler#handleRequest(BufferedReaderRequest)}
	 * @param req The request
	 */
	public void handleRequest(BufferedReaderRequest req)
	{
		// Delegate the call off to a more specific handler
		loadRequestHandler.handleRequest(req);
	}
	
	/**
	 * See {@link LoadRequestHandler#handleRequest(RandomAccessFileLoadRequest)}
	 * @param req The request
	 */
	public void handleRequest(RandomAccessFileLoadRequest req)
	{
		// Delegate the call off to a more specific handler
		loadRequestHandler.handleRequest(req);
	}
	
	/**
	 * See {@link LoadRequestHandler#handleRequest(CachedFileRequest)}
	 * @param req The request
	 */
	public void handleRequest(CachedFileRequest req)
	{
		// Delegate the call off to a more specific handler
		loadRequestHandler.handleRequest(req);
	}
	
	/**
	 * See {@link LoadRequestHandler#handleRequest(CachedFileRegionRequest)}
	 * @param req The request
	 */
	public void handleRequest(CachedFileRegionRequest req)
	{
		// Delegate the call off to a more specific handler
		loadRequestHandler.handleRequest(req);
	}
	
	/**
	 * See {@link LoadRequestHandler#handleRequest(XmlLoadRequest)}
	 * @param req The request
	 * @param <T> The type of the object to load from XML
	 */
	public <T> void handleRequest(XmlLoadRequest<T> req)
	{
		// Delegate the call off to a more specific handler
		loadRequestHandler.handleRequest(req);
	}
	
	/**
	 * See {@link SaveRequestHandler#handleRequest(PrintWriterRequest)}
	 * @param req The request
	 */
	public void handleRequest(PrintWriterRequest req)
	{
		// Delegate the call off to a more specific handler
		saveRequestHandler.handleRequest(req);
	}
	
	/**
	 * See {@link SaveRequestHandler#handleRequest(FileOutputStreamRequest)}
	 * @param req The request
	 */
	public void handleRequest(FileOutputStreamRequest req)
	{
		// Delegate the call off to a more specific handler
		saveRequestHandler.handleRequest(req);
	}
	
	/**
	 * See {@link SaveRequestHandler#handleRequest(XmlSaveRequest)}
	 * @param req The request
	 */
	public void handleRequest(XmlSaveRequest req)
	{
		// Delegate the call off to a more specific handler
		saveRequestHandler.handleRequest(req);
	}

}
