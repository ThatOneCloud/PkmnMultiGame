package net.cloud.mmo.event.command.commands;

import net.cloud.mmo.event.command.argument.ArgumentPrototypes;
import net.cloud.mmo.event.command.parameter.OptionalParameter;
import net.cloud.mmo.event.command.parameter.RequiredParameter;

public class TestCommand extends AbstractCommand {
	
	private static OptionalParameter<?>[] allOptParams = {
		new OptionalParameter<String>("p", "parameter", ArgumentPrototypes.STRING),
		new OptionalParameter<String>("a", "aaa", ArgumentPrototypes.STRING),
		new OptionalParameter<String>("b", "bbb", ArgumentPrototypes.STRING)
	};
	
	private static RequiredParameter<?>[] allReqParams = {
		new RequiredParameter<String>(ArgumentPrototypes.STRING),
		new RequiredParameter<String>(ArgumentPrototypes.STRING)
	};
	
	public TestCommand()
	{
		super();
	}

	@Override
	public String doCommand() {
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
	protected RequiredParameter<?>[] getAllRequiredParameters() {
		return allReqParams;
	}

}
