package net.cloud.mmo.file.request.handler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import net.cloud.mmo.file.FileRequestException;
import net.cloud.mmo.file.request.BufferedReaderRequest;

/**
 * A handler class which specifically deals with requests 
 * to load a file. (Subclasses of LoadRequest)
 * It exists as a means to break up what would otherwise be 
 * a very large handler class, and should also serve to align 
 * with the parallel class hierarchies of this subsystem in general.
 */
public class LoadRequestHandler {
	
	/** Default public constructor. Creates a new instance for use. */
	public LoadRequestHandler()
	{
	}
	
	/**
	 * Attempt to handle a BufferedReaderRequest.<br>
	 * That is, it will create a BufferedReader to the requested file and 
	 * notify the Request object that it is ready or that an exception occured.
	 * @param req The request to fulfill
	 */
	public void handleRequest(BufferedReaderRequest req) {
		// Going to create a BufferedReader with lots of wrappers. Here goes
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(req.address().getPath())));
			
			// Now that we've got the reader, assign it to the request
			req.setFileDescriptor(br);
			
			// And notify the request it's ready
			req.notifyReady();
		} catch (FileNotFoundException e) {
			// Uh oh. The path wasn't right. Let the request know so the exception can propagate
			req.notifyHandleException(new FileRequestException("Requested file could not be found", e));
		}
	}

}
