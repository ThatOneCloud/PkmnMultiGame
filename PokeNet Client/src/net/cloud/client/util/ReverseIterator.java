package net.cloud.client.util;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * An iterator which will return elements in reverse order of a given list iterator, 
 * starting from the end of that given iterator. A call to hasNext() will return 
 * as expected if next() has an element to return. 
 *
 * @param <T> The type of elements being iterated
 */
public class ReverseIterator<T> implements Iterator<T>, Iterable<T> {
	
	/** The iterator we're using to compose behavior */
	private ListIterator<T> iterator;
	
	/**
	 * Create a new ReverseIterator, by composing it with an existing 
	 * ListIterator. This iterator will move the given ListIterator to the 
	 * end of the list.
	 * @param listIterator The ListIterator to modify behavior of
	 */
	public ReverseIterator(ListIterator<T> listIterator)
	{
		this.iterator = listIterator;
		
		// Jump to the end of the given iterator, so previous has something to return
		while(iterator.hasNext())
		{
			iterator.next();
		}
	}

	/**
	 * Obtain an iterator which can be used in a for-each statement
	 * @return An iterator in reverse order
	 */
	@Override
	public Iterator<T> iterator()
	{
		return this;
	}

	@Override
	public boolean hasNext()
	{
		return iterator.hasPrevious();
	}

	@Override
	public T next()
	{
		return iterator.previous();
	}

}
