package net.cloud.mmo.event.command.argument;

public abstract class CommandArgument<V> {
	
	private V argValue;
	
	protected CommandArgument()
	{
		this.argValue = null;
	}
	
	public CommandArgument(V value)
	{
		this.argValue = value;
	}
	
	public abstract CommandArgument<V> newParsedInstance(String valueString);

	public V getArgValue() {
		return argValue;
	}

}
