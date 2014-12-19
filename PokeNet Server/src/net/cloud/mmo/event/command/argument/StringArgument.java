package net.cloud.mmo.event.command.argument;

public class StringArgument extends CommandArgument<String> {
	
	protected StringArgument()
	{
		super();
	}
	
	public StringArgument(String value)
	{
		super(value);
	}

	@Override
	public StringArgument newParsedInstance(String valueString) {
		// Well, it's already a String...
		return new StringArgument(valueString);
	}

}
