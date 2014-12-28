package net.cloud.mmo.file.request;

import java.io.BufferedReader;

import net.cloud.mmo.file.RequestHandler;
import net.cloud.mmo.file.address.FileAddress;

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
