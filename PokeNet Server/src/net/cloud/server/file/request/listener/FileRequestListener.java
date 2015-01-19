package net.cloud.server.file.request.listener;

import net.cloud.server.file.FileRequestException;
import net.cloud.server.logging.Logger;

/**
 * Listener to take action when a FileRequest has been served and is ready. 
 * The listener is provided the file descriptor object from the request. 
 * Useful for asynchronously requesting a file and then dealing with that file.
 *
 * @param <T> The type of the object acting as a file descriptor
 */
@FunctionalInterface
public interface FileRequestListener<T> {
	
	/**
	 * Called when the request has been fulfilled. 
	 * This may take some custom action on the file.
	 * @param file The object representing the file
	 */
	public void requestReady(T file);
	
	/**
	 * Called when the handling of the request resulting in some 
	 * exception.  This method is provided an exception which may 
	 * have a cause wrapped into it.  The purpose of this method is to 
	 * allow for the exception to be detected and handled gracefully. <br>
	 * This is a default method so that a listener may remain a functional 
	 * interface if desired.
	 * The default behavior is to simply log the exception.
	 * @param ex The exception resulting from the handling of the request this listener is attached to
	 */
	public default void requestException(FileRequestException ex)
	{
		Logger.instance().logException("FileRequestListener: requestException called", ex);
	}

}
