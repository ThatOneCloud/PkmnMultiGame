package net.cloud.client.file.request;

import net.cloud.client.file.address.FileAddress;

/**
 * A FileRequest which in particular is requesting that a file 
 * be loaded and prepared for writing to. Some SaveRequests may 
 * also return a file object which can be read from, and in these 
 * situations nothing will prevent it.  (Consider it a READ/WRITE mode)
 * 
 * @param <T> The type of the object used to represent the file
 */
public abstract class SaveRequest<T> extends FileRequest<T> {

	/**
	 * Simply calls the super constructor. See {@link FileRequest#FileRequest(FileAddress)}
	 * @param address The location of the file we want to request
	 */
	public SaveRequest(FileAddress address) {
		super(address);
	}

}
