package net.cloud.gfx.elements;

import static org.junit.Assert.*;
import net.cloud.mmo.util.IteratorException;
import net.cloud.mmo.util.StrongIterator;

import org.junit.Test;

/**
 * Test the ElementList data type. 
 * Note that the add() method is not tested because the iterate tests effectively rely on and test its behavior.
 */
public class ElementListTest {
	
	// Some elements we can use for these tests
	private static TestElement el1 = new TestElement(1, 0, 0, 0, 0);
	private static TestElement el2 = new TestElement(3, 0, 0, 0, 0);
	private static TestElement el3 = new TestElement(5, 0, 0, 0, 0);
	
	@Test
	public void testRemove() {
		// Create and fill a list to work on. Assumes add() works correctly
		ElementList list = new ElementList();
		list.add(el3);
		list.add(el2);
		list.add(el1);
		
		// First remove should be successful. After that, not so much. Already gone.
		assertTrue(list.remove(el2));
		assertFalse(list.remove(el2));
		
		// To test remove all, after it both of the remaining elements should be gone
		list.removeAll();
		assertFalse(list.remove(el1));
		assertFalse(list.remove(el3));
	}

	/** 
	 * Test straight forward iteration.
	 * @throws IteratorException Only if the test fails
	 */
	@Test
	public void testIterator() throws IteratorException {
		// Create and fill a list to work on. Assumes add() works correctly
		ElementList list = new ElementList();
		list.add(el3);
		list.add(el2);
		list.add(el1);
		
		// Iterate. Store the elements in an array for later checking
		Element[] els = new Element[3];
		int idx = 0;
		StrongIterator<Element> it = list.iterator();
		while(it.hasNext())
		{
			els[idx] = it.next();
			idx++;
		}
		
		// We should find the order of the elements particular. Not the same as they were added. (Ascending priority)
		assertTrue(els[0] == el1 && els[1] == el2 && els[2] == el3);
	}

	/** 
	 * Test reverse iteration 
	 * @throws IteratorException Only if the test fails
	 */
	@Test
	public void testReverseIterator() throws IteratorException {
		// Create and fill a list to work on. Assumes add() works correctly
		ElementList list = new ElementList();
		list.add(el1);
		list.add(el3);
		list.add(el2);
		
		// Iterate. Store the elements in an array for later checking
		Element[] els = new Element[3];
		int idx = 0;
		StrongIterator<Element> it = list.reverseIterator();
		while(it.hasNext())
		{
			els[idx] = it.next();
			idx++;
		}
		
		// We should find the order of the elements particular. Not the same as they were added. (Descending priority)
		assertTrue(els[0] == el3 && els[1] == el2 && els[2] == el1);
	}

}
