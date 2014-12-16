package net.cloud.mmo.event.command;

public interface Command {
	
	public void parseArguments(String argumentLine);
	
	public String doCommand();

}
