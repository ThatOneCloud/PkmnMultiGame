package net.cloud.mmo.event.command.argument;

/**
 * An argument that is just a plain String
 */
public class StringArgument extends CommandArgument<String> {
	
	/**
	 * Create a new StringArgument with a null value
	 */
	protected StringArgument()
	{
		super();
	}
	
	/**
	 * Create a new StringArgument with the given value
	 * @param value The String in the StringArgument
	 */
	public StringArgument(String value)
	{
		super(value);
	}

	/**
	 * Creates a new StringArgument. The value is simply the given string
	 * @param valueString The value - used directly
	 */
	@Override
	public StringArgument newParsedInstance(String valueString) {
		// Well, it's already a String...
		return new StringArgument(valueString.toString());
	}

}
