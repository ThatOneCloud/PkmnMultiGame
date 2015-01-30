package net.cloud.server.file.address;

/**
 * A class which contains constants that can be used for creation 
 * of a FileAddress.<br>
 * A SPACE_xxx constant essentially refers to the folder the file is in.<br>
 * An EXT_xxx constant is just for a file format. These are here... just because.
 */
public class AddressConstants {
	
	/** Location of command script files */
	public static final String SPACE_COMMAND_SCRIPTS = "./data/scripts/commands/";
	
	/** Location of log report files */
	public static final String SPACE_LOG_FILES = "./data/logs/";
	
	/** Location of player save files */
	public static final String SPACE_PLAYER_DATA = "./data/players/";
	
	
	/** Text file extension */
	public static final String EXT_TEXT = "txt";
	
	/** Extension used for cache files */
	public static final String EXT_CACHE = "dat";
	
	/** Extension use for player data files */
	public static final String EXT_P_DATA = "dat";

}
