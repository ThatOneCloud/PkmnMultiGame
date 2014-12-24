package net.cloud.mmo.util.function;

/**
 * A function that accepts four parameters and returns a result
 *
 * @param <T> Type of the first argument
 * @param <U> Type of the second argument
 * @param <V> Type of the third argument
 * @param <W> Type of the fourth argument
 * @param <R> Type of the return value
 */
@FunctionalInterface
public interface QuadFunction<T, U, V, W, R> {
	
	/**
	 * Execute the function defined by this interface
	 * @param arg1 Uh.. the first argument
	 * @param arg2 ... and the second ...
	 * @param arg3 ... and the third ...
	 * @param arg4 ... and the fourth ...
	 * @return The result of the function
	 */
	public R apply(T arg1, U arg2, V arg3, W arg4);

}
