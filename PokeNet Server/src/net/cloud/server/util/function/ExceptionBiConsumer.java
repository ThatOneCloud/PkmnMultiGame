package net.cloud.server.util.function;

/**
 * A Consumer that has a throws clause on its accept method. Because of this, it is not a sub-interface of the regular Consumer. 
 * The type of the exception can also be generic, so that's neat.
 *
 * @param <T> Type of the first argument
 * @param <U> Type of the second argument
 * @param <E> Type of the exception
 */
@FunctionalInterface
public interface ExceptionBiConsumer<T, U, E extends Throwable> {
	
	/**
	 * Performs this operation on the given arguments, possibly throwing a checked exception
	 * @param t The first input argument
	 * @param u The second input argument
	 * @throws E Exception arising from the operation
	 */
	public void accept(T t, U u) throws E;
	
	/**
	 * Returns an ExceptionBiConsumer that will perform this operation followed by the after operation. 
	 * The exception type of after is declared a tad different, so it meets the throws clause of this operation. 
	 * If either throw an exception, it will be relayed. If it's the first that throws, the after operation won't happen.
	 * @param after The operation to perform after this one
	 * @return A Consumer that will do both operations when called.
	 */
	public default ExceptionBiConsumer<T, U, E> andThen(ExceptionBiConsumer<? super T, ? super U, ? extends E> after)
	{
		return (t, u) ->
		{
			this.accept(t, u);
			
			after.accept(t, u);
		};
	}

}
