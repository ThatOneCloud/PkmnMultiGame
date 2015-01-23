package net.cloud.client.file.cache;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * A wrapper around a byte array. Contains the byte data of a file that was stored in a 
 * cache file and has been retrieved from that cache file. Supports methods to obtain 
 * an IO-type object to operate on the file data. Do note that from this application, these files 
 * are effectively read-only. (Since they must be packed a bit differently)<br>
 * To obtain a CachedFile, submit a request to the FileServer via a CachedFileRegionLoadRequest 
 * which is capable of returning either a single CachedFile or block of contiguous files from 
 * within the cache as an efficiency operation. 
 */
public class CachedFile {
	
	/** The data from the file */
	private final byte[] data;
	
	/**
	 * Create a new CachedFile object that will contain the given bytes as data
	 * @param data The raw data in the file
	 */
	public CachedFile(byte[] data)
	{
		this.data = data;
	}
	
	/**
	 * Obtain the data in the file. Whilst nothing is stopping anyone from modifying this data, 
	 * it's not like it'd do much good. Maybe.. whatever. 
	 * @return The raw data in the file
	 */
	public byte[] getData()
	{
		return data;
	}
	
	/**
	 * Obtain an InputStream which can be used to read bytes from this file. 
	 * Note that the reads are occurring on the data as read from the file, not the file itself. 
	 * @return An InputStream to read bytes from this object
	 */
	public InputStream asInputStream()
	{
		return new ByteArrayInputStream(data);
	}

}
