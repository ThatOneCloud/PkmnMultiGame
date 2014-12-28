package net.cloud.mmo.file.request;

/**
 * Listener to take action when a FileRequest has been served and is ready. 
 * The listener is provided the file descriptor object from the request. 
 * Useful for asynchronously requesting a file and then dealing with that file.
 *
 * @param <T> The type of the object acting as a file descriptor
 */
public interface FileRequestListener<T> {
	
	/**
	 * Called when the request has been fulfilled. 
	 * This may take some custom action on the file.
	 * @param file The object representing the file
	 */
	public void requestReady(T file);

}
