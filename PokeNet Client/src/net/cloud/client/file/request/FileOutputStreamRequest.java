package net.cloud.client.file.request;

import java.io.FileOutputStream;

import net.cloud.client.file.address.FileAddress;
import net.cloud.client.file.request.handler.RequestHandler;

/**
 * A request which asks for a FileOutputStream to be created so that a file may have 
 * raw data written to it. The obtained FileOutputStream should be wrapped in a BufferedOutputStream, 
 * it will not come pre-wrapped. 
 */
public class FileOutputStreamRequest extends SaveRequest<FileOutputStream> {

	/**
	 * Simply calls the super constructor. See {@link LoadRequest#LoadRequest(FileAddress)}
	 * @param address The location of the file
	 */
	public FileOutputStreamRequest(FileAddress address) {
		super(address);
	}

	@Override
	public void handle(RequestHandler handler) {
		// Double dispatch off to the handler
		handler.handleRequest(this);
	}

}
