package net.cloud.mmo.event.command.parameter;

public class OptionalParameter<V> extends CommandParameter<V> {
	
	private String shortName;
	
	private String longName;
	
	public OptionalParameter(String shortName, String longName) {
		super();
		this.shortName = shortName;
		this.longName = longName;
	}

	public String getLongName() {
		return longName;
	}

	public String getShortName() {
		return shortName;
	}

}
