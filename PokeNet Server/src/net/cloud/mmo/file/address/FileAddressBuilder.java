package net.cloud.mmo.file.address;

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
	 * Create and return a FileAddress object in one go. In particular, 
	 * the address will lead to a file for executing a command script.
	 * @param scriptName The name of the script
	 * @return A FileAddress which can be used to request the file
	 */
	public FileAddress createCommandScriptAddress(String scriptName)
	{
		this.space = AddressConstants.SPACE_COMMAND_SCRIPTS;
		this.name = scriptName;
		this.extension = AddressConstants.EXT_TEXT;
		
		return createAddress();
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
