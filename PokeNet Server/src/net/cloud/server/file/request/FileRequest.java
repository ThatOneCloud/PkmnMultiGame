package net.cloud.server.file.request;

import java.util.Optional;

import net.cloud.server.file.FileRequestException;
import net.cloud.server.file.address.FileAddress;
import net.cloud.server.file.request.handler.RequestHandler;
import net.cloud.server.file.request.listener.FileRequestListener;

/**
 * Base class for all file request.  Deals with the shared behavior 
 * of allowing a thread to block until the request has been served 
 * and is ready.<br>
 * A FileRequest object is what is submitted to the FileServer 
 * to ask for a file related object to be created and prepared. It will 
 * contain the way of accessing the file's information.
 * 
 * @param <T> The type of the object used to represent the file
 */
public abstract class FileRequest<T> {
	
	/** True only once the request has been served and is ready to be acted on */
	private volatile boolean readyFlag = false;
	
	/** Refers to the file this request is seeking */
	private final FileAddress address;
	
	/** The object representing the file this request is for */
	private T fileDescriptor;
	
	/** The listener (if any) attached to this request */
	private FileRequestListener<T> listener;
	
	/** If an exception happened during handling the request, this is it */
	private FileRequestException handleException;
	
	/**
	 * Create a new request which is not ready but will request the file 
	 * at the given location.
	 * @param address The location of the file we want to request
	 */
	public FileRequest(FileAddress address)
	{
		this.readyFlag = false;
		this.address = address;
	}
	
	/**
	 * Have the request ship itself off for handling. 
	 * This takes advantage of dynamic binding. More specifically, double dispatch. 
	 * Simply override it and call <code>handler.handleRequest(this)</code>
	 * @param handler An instance of RequestHandler to take care of this request
	 */
	public abstract void handle(RequestHandler handler);
	
	/**
	 * Wait until the request has been served and is ready to be acted on. 
	 * That is, whatever underlying objects that may exist are initialized 
	 * and can be utilized.<br>
	 * If an exception is thrown, it is not safe to access the file descriptor. No promise 
	 * is made as to whether it will be valid or not, even if the wait was just interrupted.
	 * @throws FileRequestException If the wait was interrupted, or the request could not be handled
	 */
	public void waitForRequest() throws FileRequestException
	{
		// Typical wait loop
		if((!readyFlag || fileDescriptor == null) && (handleException == null))
		{
			synchronized(this)
			{
				while((!readyFlag || fileDescriptor == null) && (handleException == null))
				{
					try {
						this.wait();
					} catch (InterruptedException e) {
						// The waiting thread was interrupted. Who knows what it'll do next.
						// Notify it that the request may not be ready, anyways
						throw new FileRequestException("Wait was interrupted. File may not be ready.");
					}
				}
			}
		}
		
		// Being here means this request is ready or an exception happened during handling
		if(handleException != null)
		{
			// Unfortunately handling the request resulted in an exception. Re-throw it.
			throw handleException;
		}
		
		// Getting here implies all is good. The wait is over!
	}
	
	/**
	 * Notify all threads waiting on this request to complete that it 
	 * is ready and can be acted on. <br>
	 * If there is a listener attached to this request, it will also be called.
	 */
	public void notifyReady()
	{
		synchronized(this)
		{
			// Update the ready flag
			this.readyFlag = true;
			
			// Notify anything waiting on this request that it's ready
			this.notifyAll();
		}

		// Call the listener's method if we need to
		this.notifyListenerThatRequestIsReady();
	}
	
	/**
	 * Notify all threads waiting on this request to complete that it 
	 * encountered an exception and could not properly be handled.<br>
	 * If there is a listener attached to this request, it will also be notified of this.
	 * @param ex The exception caused by handling the request
	 */
	public void notifyHandleException(FileRequestException ex)
	{
		synchronized(this)
		{
			// Set the exception, acts as a flag that it happened
			this.handleException = ex;
		
			// Notify anyone waiting that they should stop
			this.notifyAll();
		}
		
		// Call the listener's exception method
		this.notifyListenerOfException();
	}
	
	/**
	 * Obtain the address which refers to the location of the file this request is seeking
	 * @return The FileAddress to the file this request is for
	 */
	public FileAddress address()
	{
		return address;
	}
	
	/**
	 * Obtain the object representing the file this request was for. 
	 * This object will not be assigned until the request has been successfully 
	 * served. If this method is called before that, it will throw an exception.
	 * @return The file this request was for
	 * @throws FileRequestException If the descriptor is for some reason not available
	 */
	public T getFileDescriptor() throws FileRequestException
	{
		String msg = "File descriptor not available";
		return Optional.ofNullable(fileDescriptor).orElseThrow(() -> new FileRequestException(msg));
	}
	
	/**
	 * Set the object representing the file this request is for. 
	 * It really isn't unlike a file descriptor. But it's probably not just an integer. 
	 * Unix is kinda neat.
	 * @param fileDescriptor The file this request was for
	 */
	public void setFileDescriptor(T fileDescriptor)
	{
		this.fileDescriptor = fileDescriptor;
	}
	
	/**
	 * Attach a listener to this request so that whenever it has been served and is 
	 * ready, the listener is called to take whatever action it specifies. 
	 * This will overwrite any existing listener.
	 * @param listener The listener specifying action to take when this request is ready
	 */
	public void attachListener(FileRequestListener<T> listener)
	{
		this.listener = listener;
	}
	
	/**
	 * IF a listener is attached to this request, call its requestReady() method 
	 * to take the listener's specified action
	 */
	public void notifyListenerThatRequestIsReady()
	{
		Optional.ofNullable(listener).ifPresent((l) -> l.requestReady(fileDescriptor));
	}
	
	/**
	 * IF a listener is attached to this request, and an exception occured 
	 * from handling the request, this method will call the listener's requestException() method.
	 */
	public void notifyListenerOfException()
	{
		Optional.ofNullable(listener).ifPresent((l) -> l.requestException(handleException));
	}
	
}
