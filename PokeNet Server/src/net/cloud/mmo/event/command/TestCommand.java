package net.cloud.mmo.event.command;

import net.cloud.mmo.event.command.parameter.OptionalParameter;

public class TestCommand extends AbstractCommand {
	
	// TODO: Static lists of all arguments for parsing usage
	private static OptionalParameter<?>[] allOptParams = {new OptionalParameter<String>("p", "parameter")};
	
	public TestCommand(String arg)
	{
		System.out.println("Test command created: " + arg);
	}

	@Override
	public String doCommand() {
		// TODO Auto-generated method stub
		return "do command";
	}

	@Override
	protected OptionalParameter<?>[] getAllOptionalParameters() {
		return allOptParams;
	}

	@Override
	protected void getAllRequiredParameters() {
		// TODO Auto-generated method stub
		
	}

}
