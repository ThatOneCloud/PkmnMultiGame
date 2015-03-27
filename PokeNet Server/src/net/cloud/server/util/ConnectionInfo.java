package net.cloud.server.util;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import io.netty.buffer.ByteBuf;
import net.cloud.server.nio.bufferable.Bufferable;

/**
 * A simple class that keeps track of connection information. 
 * This includes a timestamp and the IP address the connection was made from. 
 * This class is immutable, since well... once the connection happens, it stays happened
 */
@XStreamAlias("ConnectionInfo")
public class ConnectionInfo implements Bufferable {
	
	/** The date and time of login */
	private SimpleDateTime timestamp;
	
	/** The IP address the connection is from */
	private String address;
	
	/** 
	 * Dummy constructor for createFrom
	 * @param dummy Does not matter
	 */
	private ConnectionInfo(boolean dummy) {}
	
	/**
	 * Creates a login info using the given address and current time
	 * @param address The address the connection is on
	 */
	public ConnectionInfo(String address)
	{
		this(new SimpleDateTime(), address);
	}
	
	/**
	 * Creates a login info using the given address and time
	 * @param timestamp The time the connection was established
	 * @param address The address the connection is on
	 */
	public ConnectionInfo(SimpleDateTime timestamp, String address)
	{
		this.timestamp = timestamp;
		this.address = address;
	}
	
	/**
	 * Create a new ConnectionInfo object from data in the buffer
	 * @param buffer Holds data
	 * @return ConnectionInfo created from data
	 */
	public static ConnectionInfo createFrom(ByteBuf buffer)
	{
		ConnectionInfo info = new ConnectionInfo(true);
		
		info.restore(buffer);
		
		return info;
	}
	
	/** The formatted date and time, with the ip address in square brackets */
	@Override
	public String toString()
	{
		return timestamp.toString() + " [" + address + "]";
	}

	/**
	 * @return The timestamp
	 */
	public SimpleDateTime getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @return The address
	 */
	public String getAddress()
	{
		return address;
	}

	@Override
	public void save(ByteBuf buffer)
	{
		timestamp.save(buffer);
		StringUtil.writeStringToBuffer(address, buffer);
	}

	@Override
	public void restore(ByteBuf buffer)
	{
		this.timestamp = SimpleDateTime.createFrom(buffer);
		this.address = StringUtil.getFromBuffer(buffer);
	}
	
}
