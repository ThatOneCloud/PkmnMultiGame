package net.cloud.client.event.task;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A Future which is returned upon submission of a task when the task engine is shutdown. 
 * Usage is to clean up the output, since many tasks are not interested in a result anyways. 
 */
public class RejectedFuture implements Future<Object> {

	/** @return Always false */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning)
	{
		return false;
	}

	/** @return Always true */
	@Override
	public boolean isCancelled()
	{
		return true;
	}

	/** @return Always true */
	@Override
	public boolean isDone()
	{
		return true;
	}

	/** Always throws CancellationException */
	@Override
	public Object get() throws InterruptedException, ExecutionException
	{
		throw new CancellationException("Rejected Future: Your task was never submitted");
	}

	/** Always throws CancellationException */
	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
	{
		throw new CancellationException("Rejected Future: Your task was never submitted");
	}

}
