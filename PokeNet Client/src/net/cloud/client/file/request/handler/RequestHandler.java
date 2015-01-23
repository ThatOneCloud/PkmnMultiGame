package net.cloud.client.file.request.handler;

import net.cloud.client.file.request.BufferedReaderRequest;
import net.cloud.client.file.request.CachedFileRegionRequest;
import net.cloud.client.file.request.CachedFileRequest;
import net.cloud.client.file.request.PrintWriterRequest;
import net.cloud.client.file.request.RandomAccessFileLoadRequest;

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
	 */
	public void handleRequest(BufferedReaderRequest req) {
		// Delegate the call off to a more specific handler
		loadRequestHandler.handleRequest(req);
	}
	
	/**
	 * See {@link LoadRequestHandler#handleRequest(RandomAccessFileLoadRequest)}
	 */
	public void handleRequest(RandomAccessFileLoadRequest req) {
		// Delegate the call off to a more specific handler
		loadRequestHandler.handleRequest(req);
	}
	
	/**
	 * See {@link LoadRequestHandler#handleRequest(CachedFileRequest)}
	 */
	public void handleRequest(CachedFileRequest req) {
		// Delegate the call off to a more specific handler
		loadRequestHandler.handleRequest(req);
	}
	
	/**
	 * See {@link LoadRequestHandler#handleRequest(CachedFileRegionRequest)}
	 */
	public void handleRequest(CachedFileRegionRequest req) {
		// Delegate the call off to a more specific handler
		loadRequestHandler.handleRequest(req);
	}
	
	/**
	 * See {@link SaveRequestHandler#handleRequest(PrintWriterRequest)}
	 */
	public void handleRequest(PrintWriterRequest req) {
		// Delegate the call off to a more specific handler
		saveRequestHandler.handleRequest(req);
	}

}
