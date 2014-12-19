package net.cloud.mmo.event.command;

public interface Command {
	
	public Command newPrototypedInstance();
	
	public void parseArguments(String argumentLine) throws CommandException;
	
	public String doCommand();

}
