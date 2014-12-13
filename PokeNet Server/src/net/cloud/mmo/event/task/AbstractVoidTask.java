package net.cloud.mmo.event.task;

import java.util.concurrent.Future;

public abstract class AbstractVoidTask implements VoidTask {

	/*@Override
	public void execute() {
		// TODO Auto-generated method stub

	}*/
	
	protected Future<?> ourFuture;
	
	public void setFuture(Future<?> future)
	{
		ourFuture = future;
	}
	
	
	
	public Future<?> getFuture() {
		return ourFuture;
	}



	public void execute()
	{
		if(shouldCancel())
		{
			ourFuture.cancel(false);
			return;
		}
		
		doTask();
	}
	
	public abstract void doTask();
	
	public abstract boolean shouldCancel();

}
