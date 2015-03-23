package net.cloud.client.file;

import java.io.InputStream;
import java.io.OutputStream;

import com.thoughtworks.xstream.XStream;

/**
 * Singleton access point to the XStream library. 
 * Provides the methods to serialize and deserialize an object to a file (but not to 
 * create or open the file.) 
 * Also takes care of aliasing - as much as I'd like to have it taken care of by the object classes. 
 */
public class XStreamHandler {
	
	/** Singleton instance */
	private static volatile XStreamHandler instance;
	
	/** The XStream object - a facade to the XStream library */
	private XStream xStream;
	
	/**
	 * Look through all the annotations, namely for aliasing. 
	 * Hits up super types, generic types, field member types. 
	 * Put this method up top for accessibility and noticeability. 
	 */
	private void processAnnotations()
	{
		xStream.processAnnotations(net.cloud.client.nio.bufferable.BufferableInteger.class);
		xStream.processAnnotations(net.cloud.client.nio.bufferable.BufferableString.class);
		xStream.processAnnotations(net.cloud.client.util.ConnectionInfo.class);
		xStream.processAnnotations(net.cloud.client.util.HashObj.class);
		xStream.processAnnotations(net.cloud.client.entity.player.Player.class);
		xStream.processAnnotations(net.cloud.client.util.SimpleDate.class);
		xStream.processAnnotations(net.cloud.client.util.SimpleDateTime.class);
		xStream.processAnnotations(net.cloud.client.util.SimpleTime.class);
	}
	
	/** Singleton constructor. */
	private XStreamHandler()
	{
		xStream = new XStream();
		
		// Process annotations for all classes that need it
		processAnnotations();
	}
	
	/**
	 * Obtain a singleton reference to the XStream Handler. 
	 * From it you can deal with XML serialization
	 * @return XStreamHandler reference
	 */
	public static XStreamHandler instance()
	{
		// Double checked locking
		if(instance == null)
		{
			synchronized(XStreamHandler.class)
			{
				if(instance == null)
				{
					instance = new XStreamHandler();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Serialize an object to an output stream. The stream should already be opened, 
	 * and will not be closed by this method. The stream is flushed, however. 
	 * @param object The object to serialize
	 * @param output An open output stream to write XML to
	 */
	public void to(Object object, OutputStream output)
	{
		xStream.toXML(object, output);
	}
	
	/**
	 * Deserialize an object from an input stream. The stream should already be opened, 
	 * and will not be closed by this method. The return type is Object - it is not casted. 
	 * @param input An open input stream to read XML from
	 * @return A deserialized object
	 * @throws XStreamException If the object cannot be deserialized
	 */
	public Object from(InputStream input)
	{
		return xStream.fromXML(input);
	}
	
}
