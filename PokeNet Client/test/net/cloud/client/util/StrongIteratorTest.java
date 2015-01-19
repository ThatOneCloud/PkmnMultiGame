package net.cloud.client.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.cloud.client.util.IteratorException;
import net.cloud.client.util.StrongIterator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StrongIteratorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/** 
	 * Test successful iteration of a few items 
	 * @throws IteratorException Only if the test fails
	 */
	@Test
	public void testNormal() throws IteratorException {
		// Create a list, put a few items in it
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		
		// Keep count of how many items we've gone through, and the concatenation of them
		int itemCount = 0;
		String itemConcat = "";
		
		// Iterate
		StrongIterator<String> it = new StrongIterator<String>(list.iterator());
		while(it.hasNext())
		{
			itemCount++;
			itemConcat += it.next();
		}
		
		// Verify results
		assertTrue(itemCount == 3 && itemConcat.equals("123"));
	}
	
	/** 
	 * Test iteration of an empty list 
	 * @throws IteratorException Only if the test fails
	 */
	@Test
	public void testEmpty() throws IteratorException {
		// Create a list, put a no items in it
		List<String> list = new ArrayList<String>();
		
		// Keep count of how many items we've gone through, and the concatenation of them
		int itemCount = 0;
		String itemConcat = "";
		
		// Iterate
		StrongIterator<String> it = new StrongIterator<String>(list.listIterator());
		while(it.hasNext())
		{
			itemCount++;
			itemConcat += it.next();
		}
		
		// Verify results
		assertTrue(itemCount == 0 && itemConcat.equals(""));
	}
	
	/** 
	 * Test unsuccessful iteration (fail-fast behavior) 
	 * @throws IteratorException Actually expected this time
	 */
	@Test()
	public void testComod() throws IteratorException {
		// Create a list, put a few items in it
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		
		thrown.expect(IteratorException.class);
		
		// Iterate
		StrongIterator<String> it = new StrongIterator<String>(list.iterator());
		while(it.hasNext())
		{
			it.next();
			
			// Change it up. Cause concurrent modification
			list.add("13");
		}
		
		// Well, an exception should've come up
		fail("Concurrent modification exception expected - did not throw");
	}

}
