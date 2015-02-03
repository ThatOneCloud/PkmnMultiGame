package net.cloud.client.file.address;

/**
 * A class which contains constants that can be used for creation 
 * of a FileAddress.<br>
 * A SPACE_xxx constant essentially refers to the folder the file is in.<br>
 * An EXT_xxx constant is just for a file format. These are here... just because.
 */
public class AddressConstants {
	
	/** Location of log report files */
	public static final String SPACE_LOG_FILES = "./data/logs/";
	
	/** Location of sprites in general */
	public static final String SPACE_SPRITES = "./data/resources/sprites/";
	
	/** Location of XML data files */
	public static final String SPACE_XML_DATA = "./data/resources/xml/";
	
	
	/** Text file extension */
	public static final String EXT_TEXT = "txt";
	
	/** Extension used for cache files */
	public static final String EXT_CACHE = "dat";
	
	/** Extension for XML files. Go figure. */
	public static final String EXT_XML = "xml";

}
