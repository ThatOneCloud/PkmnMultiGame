package net.cloud.mmo.file.request.handler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import net.cloud.mmo.file.FileRequestException;
import net.cloud.mmo.file.request.PrintWriterRequest;
import net.cloud.mmo.util.IOUtil;

/**
 * A handler class which specifically deals with requests 
 * to write to a file. (Subclasses of SaveRequest)
 * It exists as a means to break up what would otherwise be 
 * a very large handler class, and should also serve to align 
 * with the parallel class hierarchies of this subsystem in general.
 */
public class SaveRequestHandler {
	
	/** Default public constructor. Creates a new instance for use. */
	public SaveRequestHandler()
	{
	}
	
	/**
	 * Attempt to handle a PrintWriterRequest.<br>
	 * That is, it will create a PrintWriter to the requested file and 
	 * notify the Request object that it is ready or that an exception occurred.
	 * @param req The request to fulfill
	 */
	public void handleRequest(PrintWriterRequest req)
	{
		// Going to create a PrintWriter with lots of wrappers. Here goes
		try {
			PrintWriter pw = IOUtil.streamToWriter(new FileOutputStream(req.address().getPath()));

			// Now that we've got the writer, assign it to the request
			req.setFileDescriptor(pw);

			// And notify the request it's ready
			req.notifyReady();
		} catch (FileNotFoundException e) {
			// Uh oh. The file couldn't be opened. Let the request know so the exception can propagate
			req.notifyHandleException(new FileRequestException("Requested file could not be opened", e));
		}
	}

}
