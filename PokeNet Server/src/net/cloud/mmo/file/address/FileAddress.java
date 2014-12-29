package net.cloud.mmo.file.address;

/**
 * The location of a file behind the File Server.  
 * The idea is that this address is relative to the location of the file 
 * server, and so other sections of the server need not have any real 
 * knowledge of the underlying file system.<br>
 * A FileAddress can possibly be constructed directly, however it is recommended 
 * to use the FileAddressBuilder which will construct a FileAddress dynamically 
 * based on the needs of the request - separating concerns and knowledge.<br>
 */
public class FileAddress {
	
	/** The path to the file. A fully qualified name */
	private String path;
	
	/**
	 * Create a FileAddress where the file is given by the provided path
	 * @param path The fully qualified path to the file
	 */
	public FileAddress(String path)
	{
		this.path = path;
	}
	
	/**
	 * Get the address - the path - to the path
	 * @return A fully qualified path and filename
	 */
	public String getPath()
	{
		return path;
	}

}