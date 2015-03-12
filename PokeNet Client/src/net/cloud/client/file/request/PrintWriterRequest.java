package net.cloud.client.file.request;

import java.io.PrintWriter;

import net.cloud.client.file.address.FileAddress;
import net.cloud.client.file.request.handler.RequestHandler;

/**
 * A request which asks for a PrintWriter to be created so that 
 * a file may be written to line by line.
 */
public class PrintWriterRequest extends SaveRequest<PrintWriter> {

	/**
	 * Simply calls the super constructor. See {@link LoadRequest#LoadRequest(FileAddress)}
	 * @param address The location of the file we want to obtain a BufferedReader for
	 */
	public PrintWriterRequest(FileAddress address)
	{
		super(address);
	}

	@Override
	public void handle(RequestHandler handler)
	{
		// Double dispatch off to the handler
		handler.handleRequest(this);
	}

}
