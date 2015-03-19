package net.cloud.server.util.function;

/**
 * A simple pair object which contains two objects. 
 * Fields are public, for more convenient access.
 *
 * @param <U> Type of the first object
 * @param <V> Type of the second object
 */
public class Pair<U, V> {
	
	/** The first object... */
	public U first;
	
	/** The second object... */
	public V second;

	/**
	 * Create a new pair of objects
	 * @param first The first object...
	 * @param second The second object...
	 */
	public Pair(U first, V second) 
	{
		this.first = first;
		this.second = second;
	}

}
