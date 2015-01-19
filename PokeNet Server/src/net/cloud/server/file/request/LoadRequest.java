package net.cloud.server.file.request;

import net.cloud.server.file.address.FileAddress;

/**
 * A FileRequest which in particular is requesting that a file 
 * be loaded and prepared for reading from. While there is no 
 * outright guarantee that subclasses will enforce that the returned 
 * file is read-only, this is the suggested purpose.
 * 
 * @param <T> The type of the object used to represent the file
 */
public abstract class LoadRequest<T> extends FileRequest<T> {
	
	/**
	 * Simply calls the super constructor. See {@link FileRequest#FileRequest(FileAddress)}
	 * @param address The location of the file we want to request
	 */
	public LoadRequest(FileAddress address)
	{
		super(address);
	}

}
