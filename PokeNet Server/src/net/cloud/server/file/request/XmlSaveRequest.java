package net.cloud.server.file.request;

import net.cloud.server.file.address.FileAddress;
import net.cloud.server.file.request.handler.RequestHandler;

public class XmlSaveRequest extends SaveRequest<Object> {
	
	/** The object we want to serialize to XML */
	private final Object saveObject;

	/**
	 * Calls the super constructor. See {@link LoadRequest#LoadRequest(FileAddress)}
	 * @param address The save location
	 * @param saveObject The object to serialize to XML
	 */
	public XmlSaveRequest(FileAddress address, Object saveObject)
	{
		super(address);
		
		this.saveObject = saveObject;
	}

	@Override
	public void handle(RequestHandler handler)
	{
		// Double dispatch off to the handler
		handler.handleRequest(this);
	}
	
	/** @return The object to serialize to XML */
	public Object getSaveObject()
	{
		return saveObject;
	}

}
