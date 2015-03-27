package net.cloud.server.file.request;

import net.cloud.server.file.FileRequestException;
import net.cloud.server.file.address.FileAddress;
import net.cloud.server.file.request.handler.RequestHandler;

/**
 * A request to deserialize from an XML file. Rather than requesting a file descriptor type object, 
 * the parameter type of this request is actually the type of the object being deserialized. 
 * 
 * @param <T> Type of the object being deserialized
 */
public class XmlLoadRequest<T> extends LoadRequest<T> {

	/**
	 * Simply calls the super constructor. See {@link LoadRequest#LoadRequest(FileAddress)}
	 * @param address The location of the file
	 */
	public XmlLoadRequest(FileAddress address)
	{
		super(address);
	}

	@Override
	public void handle(RequestHandler handler)
	{
		// Double dispatch off to the handler
		handler.handleRequest(this);
	}
	
	/**
	 * Obtain the deserialized object. Equivalent to {@link LoadRequest#getFileDescriptor()} but renamed 
	 * for more clarity. Same caveats. 
	 * @return The object that was deserialized from XML
	 * @throws FileRequestException If the object has not been created yet
	 */
	public T getObject() throws FileRequestException
	{
		return super.getFileDescriptor();
	}

}
