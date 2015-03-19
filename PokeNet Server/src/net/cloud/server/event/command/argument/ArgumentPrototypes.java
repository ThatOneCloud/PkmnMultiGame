package net.cloud.server.event.command.argument;

/**
 * A utility class which contains references to CommandArguments 
 * to be used when creating new CommandParameters. 
 */
public class ArgumentPrototypes {
	
	/** A StringArgument prototype */
	public static final StringArgument STRING = new StringArgument();
	
	/** Prototype for a FlagArgument */
	public static final FlagArgument FLAG = new FlagArgument();

}
