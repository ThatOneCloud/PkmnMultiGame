package net.cloud.server.util;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.cloud.server.logging.Logger;
import net.cloud.server.nio.bufferable.Bufferable;

/**
 * A lack of creativity with the name... 
 * An object which stores the results of the hash function on 
 * some data. Only the hash is kept, not the original data. <br>
 * This object is immutable.
 */
@XStreamAlias("HashObj")
public final class HashObj implements Bufferable {
	
	/** Hash results as a byte array */
	private byte[] hash;
	
	/**
	 * Constructor for deserialization to use
	 */
	private HashObj() {}
	
	/**
	 * Create a HashObj based on a string. The hash function will use the bytes from the string. 
	 * @param data The data in String format (Like a password, maybe...)
	 */
	public HashObj(String data)
	{
		// Create a MessageDigest which will take care of the algorithm for us
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			
			// One shot, get the results from the data
			hash = digest.digest(data.getBytes());
		} catch (NoSuchAlgorithmException e) {
			// This shouldn't happen for sha-1 but just in case
			Logger.instance().logException("SHA-1 not supported by this JVM. Server will not function properly.", e);
			
			// Initialize it to something (sha-1 hashes will be 20 bytes, this will not match any)
			hash = new byte[] {-1};
		}
	}
	
	/**
	 * Create a new HashObj by deserializing it from the given buffer
	 * @param buffer The buffer the data is in
	 * @return A new HashObj
	 */
	public static HashObj createFrom(ByteBuf buffer)
	{
		HashObj newObj = new HashObj();
		newObj.restore(buffer);
		return newObj;
	}
	
	/**
	 * Create a new HashObj by deserializing it from the given file directly
	 * @param raf The file the data is in
	 * @return A new HashObj
	 */
	public static HashObj createFrom(RandomAccessFile raf) throws IOException
	{
		HashObj newObj = new HashObj();
		newObj.restoreFromRAF(raf);
		return newObj;
	}
	
	/**
	 * Convenience call for <code>equivalentTo(new HashObj(data))</code>
	 * @param data The un-hashed data to compare to
	 * @return True if the given data matches the data in this hash
	 */
	public boolean equivalentTo(String data)
	{
		return equivalentTo(new HashObj(data));
	}
	
	/**
	 * Check to see if this hash is equivalent to another hash. 
	 * @param other The other hash function
	 * @return True if the hashes are the same. Which implies the original data was likely the same!
	 */
	public boolean equivalentTo(HashObj other)
	{
		return Arrays.equals(hash, other.hash);
	}
	
	/**
	 * Saves this hash's data to the buffer. Will not throw BufferableException
	 */
	@Override
	public void save(ByteBuf buffer)
	{
		// Only have the bytes to store, need to know how many we write out
		buffer.writeInt(hash.length);
		buffer.writeBytes(hash);
	}

	/**
	 * Restore some hash data from the buffer. 
	 * Will not throw BufferableException
	 */
	@Override
	public void restore(ByteBuf buffer)
	{
		// Read how long the array is (although... this is constant for SHA-1 isn't it?)
		int size = buffer.readInt();
		hash = new byte[size];
		buffer.readBytes(hash);
	}
	
	/**
	 * Restored hash data from the file
	 * @param raf The file to load from
	 * @throws IOException If the file could not be read
	 */
	public void restoreFromRAF(RandomAccessFile raf) throws IOException
	{
		// Read how long the array is (although... this is constant for SHA-1 isn't it?)
		int size = raf.readInt();
		hash = new byte[size];
		raf.read(hash);
	}

}
