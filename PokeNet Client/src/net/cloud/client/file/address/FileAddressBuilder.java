package net.cloud.client.file.address;

import java.time.LocalDateTime;

/**
 * This class is useful for building a FileAddress. (As the name implies...)
 * To use it, obtain a new instance via the static factory method. 
 * Then, call methods on the builder instance to specify which file you're after. 
 * Some of the calls will overwrite previous calls, of course. <br><br>
 * Ex:<br>
 * <code>
 * FileAddressBuilder builder = FileAddressBuilder.newBuilder();<br>
 * builder.space(AddressConstants.SPACE_COMMAND_SCRIPTS);<br>
 * builder.filename("echoScript");<br>
 * builder.extension("txt");<br>
 * FileAddress address = builder.createAddress();<br>
 * </code><br>
 * As a convenience, and to reduce knowledge of file naming, this is equivalent:<br>
 * <code>
 * FileAddressBuilder builder = FileAddressBuilder.newBuilder();<br>
 * FileAddress address = builder.createCommandScriptAddress("echoScript");<br>
 * </code>
 */
public class FileAddressBuilder {
	
	/** The space - i.e. the folder the file is in */
	private String space;
	
	/** The name of the file (without extension) */
	private String name;
	
	/** The extension of the file. See. This is where it comes in */
	private String extension;
	
	/**
	 * Public default constructor. Same as <code>newBuilder()</code>
	 */
	public FileAddressBuilder()
	{
	}

	/**
	 * Obtain a new instance of a builder object, so you can obtain a FileAddress
	 * @return A new FileAddressBuilder with no current FileAddress information
	 */
	public static FileAddressBuilder newBuilder()
	{
		return new FileAddressBuilder();
	}
	
	/**
	 * Specify the address space (i.e. the folder) the file is in
	 * @param addressSpace The folder the file is in. Refer to {@link AddressConstants}
	 * @return This builder object
	 */
	public FileAddressBuilder space(String addressSpace)
	{
		this.space = addressSpace;
		
		return this;
	}
	
	/**
	 * Specify the name of the file. This is without extension or any indication of folder
	 * @param fileName The name of the file
	 * @return This builder object
	 */
	public FileAddressBuilder filename(String fileName)
	{
		this.name = fileName;
		
		return this;
	}
	
	/**
	 * Specify the file format. Do not include the dot ('.')
	 * @param fileExtension The extension of the file, without the dot
	 * @return This builder object
	 */
	public FileAddressBuilder extension(String fileExtension)
	{
		this.extension = fileExtension;
		
		return this;
	}
	
	/**
	 * Create and return a FileAddress which will lead to a file 
	 * to be used as a log report. This file will be created essentially 
	 * on a per-run basis where it can be located by time of run.
	 * @param logName The name to define the log, so there may be specific log files
	 * @return A FileAddress for creation of a log file
	 */
	public static FileAddress createLogFileAddress(String logName)
	{
		FileAddressBuilder b = newBuilder();
		
		// Name is the current time. The day is a folder, the file is the time
		b.space = AddressConstants.SPACE_LOG_FILES;
		LocalDateTime now = LocalDateTime.now();
		StringBuilder name = new StringBuilder();
		name.append(now.getMonthValue()).append('-')
			.append(now.getDayOfMonth()).append('-')
			.append(now.getYear()).append('/')
			.append(now.getHour()).append('-')
			.append(now.getMinute()).append('/')
			.append(logName);
		b.name = name.toString();
		b.extension = AddressConstants.EXT_TEXT;
		
		return b.createAddress();
	}
	
	/**
	 * Create a FileAddress which will lead to the cache file for a set of sprites. 
	 * This address can be passed as the only parameter to the constructor of the cache request, 
	 * or can be used in combination with <code>createSpriteCacheTableAddress</code>
	 * @param setCanonicalName The canonical name of the sprite set
	 * @return A FileAddress to request some file in a packed sprite set
	 */
	public static FileAddress createSpriteCacheAddress(String setCanonicalName)
	{
		FileAddressBuilder b = newBuilder();
		
		b.space = AddressConstants.SPACE_SPRITES;
		b.name = setCanonicalName + "/cache";
		b.extension = AddressConstants.EXT_CACHE;
		
		return b.createAddress();
	}
	
	/**
	 * Create a FileAddress which will lead to the cache table file for a set of sprites. 
	 * This address is to be used in combination with <code>createSpriteCacheAddress</code>
	 * @param setCanonicalName The canonical name of the sprite set
	 * @return A FileAddress to request some file in a packed sprite set
	 */
	public static FileAddress createSpriteCacheTableAddress(String setCanonicalName)
	{
		FileAddressBuilder b = newBuilder();
		
		b.space = AddressConstants.SPACE_SPRITES;
		b.name = setCanonicalName + "/cacheTable";
		b.extension = AddressConstants.EXT_CACHE;
		
		return b.createAddress();
	}
	
	/**
	 * Obtain a new FileAddress object which will refer to the file this builder 
	 * has been making a path to. No exceptions will occur if the builder 
	 * has not been fully specified.
	 * @return A FileAddress reflecting this builder's current state
	 */
	public FileAddress createAddress()
	{
		// For now, it's string concatenation. Something mutable would be nice, if the need arises
		return new FileAddress(space + name + "." + extension);
	}

}
