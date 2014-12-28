package net.cloud.mmo.file;

import net.cloud.mmo.file.request.FileRequest;

public class FileServer {
	// TODO: Turn into a service
	
	/** Singleton instance to the FileServer */
	private static FileServer instance;
	
	/** An instance of RequestHandler to delegate request to */
	private RequestHandler requestHandler;

	/** Private singleton constructor */
	private FileServer()
	{
		this.requestHandler = new RequestHandler();
	}
	
	/**
	 * Obtain a reference to the singleton instance of the FileServer, 
	 * to submit requests to.
	 * @return A reference to the FileServer
	 */
	public static FileServer instance()
	{
		if(instance == null)
		{
			instance = new FileServer();
		}
		
		return instance;
	}
	
	public void submit(FileRequest<?> request)
	{
		// TODO: actually queue the request
		request.handle(requestHandler);
	}

}
