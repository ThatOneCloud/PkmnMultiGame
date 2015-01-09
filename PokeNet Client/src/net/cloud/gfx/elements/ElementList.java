package net.cloud.gfx.elements;

import java.util.LinkedList;
import java.util.ListIterator;

import net.cloud.mmo.util.ReverseIterator;
import net.cloud.mmo.util.StrongIterator;

/**
 * A list of Element objects. A specialized list rather than using simply an 
 * existing list class.  The methods and actions are specialized and restricted 
 * to externalize and ease dealing with elements. <br>
 * Read-only iteration over the list can happen whenever, it is not synchronized 
 * and the iterator will define some fail behavior in case the list is modified. 
 * Modifications to the list are, however, synchronized, so that two writes will 
 * not conflict.
 */
public class ElementList {
	
	/** Underlying list is a plain old linked list */
	private LinkedList<Element> list;
	
	/** Create a new, empty, ElementList */
	public ElementList()
	{
		list = new LinkedList<>();
	}
	
	/**
	 * Add an element to this list. This method modifies the list and so 
	 * is synchronized - multiple writes can not take place at the same time. 
	 * The element will be added so that all elements with a higher priority 
	 * come after the new element, with no promise as to what happens if the 
	 * priorities are the same. (But between you and I, the new element will 
	 * be placed in front of an element with a matching priority.)
	 * @param e The new element to add to the list
	 */
	public synchronized void add(Element e)
	{
		// Obtain an iterator. I guess I'm choosing it over a for loop
		ListIterator<Element> it = list.listIterator();
		
		// Go until next would return an element that should be after what we're adding
		while(it.hasNext())
		{
			Element next = it.next();
			
			// See if next in line stays next in line
			if(next.getPriority() >= e.getPriority())
			{
				// Since it does, back-track one element so the 'cursor' is in the right place
				it.previous();
				
				// Break from the loop, we're ready to add
				break;
			}
		}
		
		// The iterator's cursor should now be in the right place. Add the new element.
		it.add(e);
	}
	
	/**
	 * Obtain an iterator over the elements in the list. 
	 * Consider it a read-only iterator. This is not synchronized, so multiple 
	 * reads can occur at once but a write will make the iteration fail. 
	 * The iterator will throw a checked exception if this list is modified while iteration is happening. 
	 * @return An iterator over the elements in the list which will throw a checked exception
	 */
	public StrongIterator<Element> iterator()
	{
		return new StrongIterator<Element>(list.iterator());
	}
	
	public StrongIterator<Element> reverseIterator()
	{
		// Composition! ListIterator wrapped in a ReverseIterator wrapped in a StrongIterator
		return new StrongIterator<Element>(new ReverseIterator<Element>(list.listIterator()));
	}

}
