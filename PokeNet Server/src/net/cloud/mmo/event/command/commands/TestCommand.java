package net.cloud.mmo.event.command.commands;

import net.cloud.mmo.event.command.argument.ArgumentPrototypes;
import net.cloud.mmo.event.command.parameter.OptionalParameter;

public class TestCommand extends AbstractCommand {
	
	// TODO: Static lists of all arguments for parsing usage
	private static OptionalParameter<?>[] allOptParams = {new OptionalParameter<String>("p", "parameter", ArgumentPrototypes.STRING)};
	
	public TestCommand()
	{
		super();
		System.out.println("Test command created");
	}

	@Override
	public String doCommand() {
		// TODO Auto-generated method stub
		return "do command";
	}
	
	@Override
	public AbstractCommand newPrototypedInstance()
	{
		return new TestCommand();
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
