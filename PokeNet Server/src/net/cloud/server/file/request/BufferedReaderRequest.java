package net.cloud.server.file.request;

import java.io.BufferedReader;

import net.cloud.server.file.address.FileAddress;
import net.cloud.server.file.request.handler.RequestHandler;

/**
 * A request which asks for a BufferedReader to be created so that 
 * a file may be read from line by line.
 */
public class BufferedReaderRequest extends LoadRequest<BufferedReader> {

	/**
	 * Simply calls the super constructor. See {@link LoadRequest#LoadRequest(FileAddress)}
	 * @param address The location of the file we want to obtain a BufferedReader for
	 */
	public BufferedReaderRequest(FileAddress address) {
		super(address);
	}

	@Override
	public void handle(RequestHandler handler) {
		// Double dispatch off to the handler
		handler.handleRequest(this);
	}

}
