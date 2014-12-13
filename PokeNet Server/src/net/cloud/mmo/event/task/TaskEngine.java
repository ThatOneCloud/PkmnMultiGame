package net.cloud.mmo.event.task;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TaskEngine {
	
	public static final int THREAD_POOL_SIZE = 4;
	
	private static TaskEngine instance;
	
	private ScheduledExecutorService taskExecutor = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
	
	private TaskEngine()
	{
		
	}
	
	public static TaskEngine getInstance()
	{
		if(instance == null)
		{
			instance = new TaskEngine();
		}
		
		return instance;
	}
	
	public <V> Future<V> submitImmediate(Task<V> task)
	{
		return taskExecutor.schedule(task::execute, 0, TimeUnit.MILLISECONDS);
	}
	public Future<?> submitImmediate(VoidTask task)
	{
//		return taskExecutor.schedule(task::execute, 0, TimeUnit.MILLISECONDS);
		System.out.println("in task engine");
		return task.submitImmediate(taskExecutor::schedule);
	}
	public Future<?> submitImmediate(AbstractVoidTask task)
	{
		task.setFuture(taskExecutor.schedule(task::execute, 0, TimeUnit.MILLISECONDS));
		return task.getFuture();
	}
	
	public <V> Future<V> submitDelayed(Task<V> task, long delay)
	{
		return taskExecutor.schedule(task::execute, delay, TimeUnit.MILLISECONDS);
	}
	
	public Future<?> scheduleImmediate(Task<?> task, long period)
	{
		return taskExecutor.scheduleAtFixedRate(task::execute, 0, period, TimeUnit.MILLISECONDS);
	}
	
	public Future<?> scheduleDelayed(Task<?> task, long delay, long period)
	{
		return taskExecutor.scheduleAtFixedRate(task::execute, delay, period, TimeUnit.MILLISECONDS);
	}
	
	private void deleteme()
	{
		
		Future<?> future = submitImmediate(() -> {System.out.println("done"); return null;});
		future.cancel(false);
		
		
		AbstractVoidTask avt = new AbstractVoidTask() {
			boolean can = false;
			@Override
			public void doTask() {
				System.out.println("do task");
				can = true;
			}
			@Override
			public boolean shouldCancel() {
				return can;
			}
		};
		submitImmediate(avt);
	}

}
