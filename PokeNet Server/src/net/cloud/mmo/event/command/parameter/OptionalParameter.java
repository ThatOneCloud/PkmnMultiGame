package net.cloud.mmo.event.command.parameter;

import net.cloud.mmo.event.command.argument.CommandArgument;

public class OptionalParameter<V> extends CommandParameter<V> {
	
	private String shortName;
	
	private String longName;
	
	public OptionalParameter(String shortName, String longName, CommandArgument<V> argument) {
		super(argument);
		this.shortName = shortName;
		this.longName = longName;
	}
	
	@Override
	public OptionalParameter<V> newParsedInstance(String valueString)
	{
		// New Argument to use in the new Parameter
		CommandArgument<V> newArg = super.getArgument().newParsedInstance(valueString);
		
		// Weak reference to the name strings, and use the new argument
		return new OptionalParameter<V>(shortName, longName, newArg);
	}

	public String getLongName() {
		return longName;
	}

	public String getShortName() {
		return shortName;
	}

}
