package net.cloud.client.file.request;

import java.io.RandomAccessFile;

import net.cloud.client.file.address.FileAddress;
import net.cloud.client.file.request.handler.RequestHandler;

/**
 * Request that a read-only random access file be created and opened.
 */
public class RandomAccessFileLoadRequest extends LoadRequest<RandomAccessFile> {

	/**
	 * Create a new request for a file at the given address. It will be a read-only RAF
	 * @param address The location of the file
	 */
	public RandomAccessFileLoadRequest(FileAddress address)
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
