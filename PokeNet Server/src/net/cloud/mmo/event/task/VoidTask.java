package net.cloud.mmo.event.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.cloud.mmo.util.function.TriFunction;

public interface VoidTask /*extends Task<Object>*/ {
	
	public void execute();
	
	public default Future<?> submitImmediate(TriFunction<Runnable, Long, TimeUnit, Future<?>> func)
	{
		System.out.println("in void task");
		return func.apply(this::execute, 0L, TimeUnit.MILLISECONDS);
	}
	
	/*default public Object execute()
	{
		execute2();
		return null;
	}*/

}
