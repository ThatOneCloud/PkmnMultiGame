package net.cloud.server.file.request.handler;

import net.cloud.server.file.request.*;

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
	 * See {@link LoadRequestHandler#handleRequest(XmlLoadRequest)}
	 */
	public <T> void handleRequest(XmlLoadRequest<T> req) {
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
	
	/**
	 * See {@link SaveRequestHandler#handleRequest(FileOutputStreamRequest)}
	 */
	public void handleRequest(FileOutputStreamRequest req) {
		// Delegate the call off to a more specific handler
		saveRequestHandler.handleRequest(req);
	}
	
	/**
	 * See {@link SaveRequestHandler#handleRequest(XmlSaveRequest)}
	 */
	public void handleRequest(XmlSaveRequest req) {
		// Delegate the call off to a more specific handler
		saveRequestHandler.handleRequest(req);
	}

}
