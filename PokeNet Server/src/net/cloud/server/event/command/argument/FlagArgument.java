package net.cloud.server.event.command.argument;

import net.cloud.server.event.command.CommandException;

/**
 * An argument that exists as a flag - an indicator - with no actual value
 */
public class FlagArgument extends CommandArgument<Void> {
	
	/**
	 * Create a new FlagArgument with a null value
	 */
	public FlagArgument()
	{
		super();
	}

	/**
	 * Creates a new FlagArgument. The value remains null.
	 * @param valueString Ignored
	 */
	@Override
	public CommandArgument<Void> newParsedInstance(String valueString) throws CommandException
	{
		// Void is pretty much just always null so that's neat, we don't need a special constructor even
		return new FlagArgument();
	}

}
