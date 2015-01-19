package net.cloud.client.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import net.cloud.client.logging.Logger;

/**
 * A lack of creativity with the name... 
 * An object which stores the results of the hash function on 
 * some data. Only the hash is kept, not the original data. <br>
 * This object is immutable.
 */
public final class HashObj {
	
	/** Hash results as a byte array */
	private byte[] hash;
	
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
	 * Check to see if this hash is equivalent to another hash. 
	 * @param other The other hash function
	 * @return True if the hashes are the same. Which implies the original data was likely the same!
	 */
	public boolean equivalentTo(HashObj other)
	{
		return Arrays.equals(hash, other.hash);
	}

}
