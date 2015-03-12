package net.cloud.client.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * An iterator (although it does not implement Iterator) that rather than being 
 * weakly-consistent or just a normal iterator, will instead throw a checked exception 
 * when concurrent modification occurs. This is so that when iterating a list which 
 * is not entirely synchronized, if something happens an exception must be caught 
 * and can be re-thrown until it has propagated high enough to take appropriate action. 
 * (Such as retrying the iteration.)
 *
 * @param <T> The type of elements in the list being iterated
 */
public class StrongIterator<T> {
	
	/** Iterator this one is composed of. Should be fail-fast */
	private Iterator<T> iterator;
	
	/**
	 * Create a new StrongIterator by wrapping it around an existing Iterator
	 * @param iterator A fail-fast iterator to make throw a checked exception
	 */
	public StrongIterator(Iterator<T> iterator)
	{
		this.iterator = iterator;
	}

	/**
	 * Check to see if there is another element to retrieve
	 * @return True if the underlying iterator's hasNext would return true
	 */
	public boolean hasNext()
	{
		return iterator.hasNext();
	}

	/**
	 * Obtain the next element from the list, as a call to next() normally would. 
	 * This will throw an IteratorException if concurrent modification was detected 
	 * by the underlying iterator. This is a checked exception. 
	 * @return The next element in the list
	 * @throws IteratorException If the list was modified and behavior is no longer guarantee-able
	 */
	public T next() throws IteratorException
	{
		try {
			return iterator.next();
		} catch (ConcurrentModificationException cme) {
			throw new IteratorException(cme);
		}
	}

}
