package net.cloud.client.nio.bufferable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import io.netty.buffer.ByteBuf;

/**
 * A wrapper around a primitive integer, that implements Bufferable. Useful for when a Bufferable is expected but the calling 
 * code has an int to provide. You could also store this in place of an int, but why when a primitive is less work? 
 */
@XStreamAlias("BufferableInteger")
public class BufferableInteger implements Bufferable {
	
	/** The wrapped integer */
	private int value;
	
	/**
	 * Constructor for deserialization to use
	 */
	private BufferableInteger() {}
	
	/**
	 * Create a BufferableInteger which wraps around the given integer
	 * @param value The wrapped integer
	 */
	public BufferableInteger(int value)
	{
		this.value = value;
	}
	
	/**
	 * Create a new BufferableInteger by deserializing it from the given buffer
	 * @param buffer The buffer the data is in
	 * @return A new BufferableInteger
	 */
	public static BufferableInteger createFrom(ByteBuf buffer)
	{
		BufferableInteger newObj = new BufferableInteger();
		newObj.restore(buffer);
		return newObj;
	}

	/**
	 * An integer is written to the buffer
	 */
	@Override
	public void save(ByteBuf buffer)
	{
		// Yep.
		buffer.writeInt(value);
	}

	/**
	 * Set the value by reading it from the buffer
	 */
	@Override
	public void restore(ByteBuf buffer)
	{
		// Read from the buffer
		set(buffer.readInt());
	}
	
	/**
	 * @return The wrapped integer
	 */
	public int get()
	{
		return value;
	}
	
	/**
	 * @param value The new integer to wrap around
	 */
	public void set(int value)
	{
		this.value = value;
	}

}
