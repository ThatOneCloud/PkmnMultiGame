package net.cloud.mmo.file.request.handler;

import net.cloud.mmo.file.request.BufferedReaderRequest;

/**
 * A class which serves as a facade to other, more specific, handlers. 
 * These handler classes are purposed with taking whatever action is needed 
 * to fulfill the request and get it ready for action to be taken upon it.
 */
public class RequestHandler {
	
	/** A handler which will deal with subclasses of LoadRequest */
	private LoadRequestHandler loadRequestHandler;
	
	/**
	 * Create a new RequestHandler which can be used to, well, handle requests. 
	 */
	public RequestHandler()
	{
		this.loadRequestHandler = new LoadRequestHandler();
	}

	/**
	 * See {@link LoadRequestHandler#handleRequest(BufferedReaderRequest)}
	 */
	public void handleRequest(BufferedReaderRequest req) {
		// Delegate the call off to a more specific handler
		loadRequestHandler.handleRequest(req);
	}

}
