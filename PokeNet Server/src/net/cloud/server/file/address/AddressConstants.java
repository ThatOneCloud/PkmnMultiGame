package net.cloud.server.file.address;

/**
 * A class which contains constants that can be used for creation 
 * of a FileAddress.<br>
 * A SPACE_xxx constant essentially refers to the folder the file is in.<br>
 * An EXT_xxx constant is just for a file format. These are here... just because.
 */
public class AddressConstants {
	
	/** Location of button action script files */
	public static final String SPACE_BUTTON_ACTIONS = "./data/scripts/actions/buttons/";
	
	/** Location of command script files */
	public static final String SPACE_COMMAND_SCRIPTS = "./data/scripts/commands/";
	
	/** Location of log report files */
	public static final String SPACE_LOG_FILES = "./data/logs/";
	
	/** Location of player save files */
	public static final String SPACE_PLAYER_DATA = "./data/players/";
	
	/** Location of XML data files */
	public static final String SPACE_XML_DATA = "./data/resources/xml/";
	
	/** Extension used for cache files */
	public static final String EXT_CACHE = "dat";
	
	/** Extension used for Groovy files */
	public static final String EXT_GROOVY = "groovy";
	
	/** Extension use for player data files */
	public static final String EXT_P_DATA = "dat";
	
	/** Text file extension */
	public static final String EXT_TEXT = "txt";
	
	/** Extension for XML files. Go figure. */
	public static final String EXT_XML = "xml";

}
