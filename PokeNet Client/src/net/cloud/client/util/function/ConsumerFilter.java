package net.cloud.client.util.function;

import java.util.function.Consumer;

/**
 * A Consumer that can be used for filtering the function applications. 
 * The accept() method must still be specified, and should call <code>passFilter()</code> 
 * if the call should be forwarded to the composed Consumer. 
 *
 * @param <T> The type of the accept method parameter
 */
public abstract class ConsumerFilter<T> implements Consumer<T> {
	
	/** A composed Consumer whose accept method is called if the filter is passed */
	private final Consumer<? super T> consumer;
	
	/**
	 * Create a new filter composed with the given consumer. The consumer's 
	 * accept method is called if the filter is passed. 
	 * @param consumer A Consumer whose accept method is called when the filter is passed
	 */
	public ConsumerFilter(Consumer<? super T> consumer)
	{
		this.consumer = consumer;
	}

	/**
	 * Called if the filter condition is met. Will call the accept method of the 
	 * composed Consumer. 
	 * @param t The object to apply the function to
	 */
	public void passFilter(T t)
	{
		consumer.accept(t);
	}

}
