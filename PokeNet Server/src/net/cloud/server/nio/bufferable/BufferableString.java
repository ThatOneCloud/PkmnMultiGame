package net.cloud.server.nio.bufferable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.cloud.server.util.StringUtil;
import io.netty.buffer.ByteBuf;

/**
 * A String wrapper that implements Bufferable, useful for when a Bufferable object is expected but the calling 
 * code has a String to provide. You could also store this in place of a String, or whatever. 
 * Either way, it's just going to rely on the methods in {@link StringUtil}. 
 * Null is allowed, and do be wary that while String is immutable, this wrapper is not.
 */
@XStreamAlias("BufferableString")
public class BufferableString implements Bufferable {
	
	/** The wrapped string */
	private String string;
	
	/**
	 * Constructor for deserialization to use
	 */
	private BufferableString() {}
	
	/**
	 * Create a BufferableString which wraps around the given string
	 * @param string The wrapped string
	 */
	public BufferableString(String string)
	{
		this.string = string;
	}
	
	/**
	 * Create a new BufferableString by deserializing it from the given buffer
	 * @param buffer The buffer the data is in
	 * @return A new BufferableString
	 */
	public static BufferableString createFrom(ByteBuf buffer)
	{
		BufferableString newObj = new BufferableString();
		newObj.restore(buffer);
		return newObj;
	}

	/**
	 * If the string is null, this will do nothing. 
	 * Otherwise the string contents are written to the buffer
	 */
	@Override
	public void save(ByteBuf buffer)
	{
		// Make sure the string isn't null before proceeding
		if(string == null)
		{
			return;
		}

		// Delegate
		StringUtil.writeStringToBuffer(string, buffer);
	}

	/**
	 * Set the string by reading its contents from the buffer
	 */
	@Override
	public void restore(ByteBuf buffer)
	{
		// Read from the buffer
		set(StringUtil.getFromBuffer(buffer));
	}
	
	/**
	 * @return The wrapped string
	 */
	public String get()
	{
		return string;
	}
	
	/**
	 * @param string The new string to wrap around
	 */
	public void set(String string)
	{
		this.string = string;
	}

}
