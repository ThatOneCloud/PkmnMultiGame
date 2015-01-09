package net.cloud.mmo.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ReverseIteratorTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/** Test successful iteration of a few items */
	@Test
	public void testNormal() {
		// Create a list, put a few items in it
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		
		// Keep count of how many items we've gone through, and the concatenation of them
		int itemCount = 0;
		String itemConcat = "";
		
		// Iterate
		ReverseIterator<String> it = new ReverseIterator<String>(list.listIterator());
		while(it.hasNext())
		{
			itemCount++;
			itemConcat += it.next();
		}
		
		// Verify results
		assertTrue(itemCount == 3 && itemConcat.equals("321"));
	}
	
	/** Test iteration of an empty list */
	@Test
	public void testEmpty() {
		// Create a list, put a no items in it
		List<String> list = new ArrayList<String>();
		
		// Keep count of how many items we've gone through, and the concatenation of them
		int itemCount = 0;
		String itemConcat = "";
		
		// Iterate
		ReverseIterator<String> it = new ReverseIterator<String>(list.listIterator());
		while(it.hasNext())
		{
			itemCount++;
			itemConcat += it.next();
		}
		
		// Verify results
		assertTrue(itemCount == 0 && itemConcat.equals(""));
	}
	
	/** Test unsuccessful iteration (fail-fast behavior) */
	@Test()
	public void testComod() {
		// Create a list, put a few items in it
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		
		thrown.expect(ConcurrentModificationException.class);
		
		// Iterate
		ReverseIterator<String> it = new ReverseIterator<String>(list.listIterator());
		while(it.hasNext())
		{
			it.next();
			
			// Change it up. Cause concurrent modification
			list.add("13");
		}
		
		// Well, an exception should've come up
		fail("Concurrent modification exception expected - did not occur");
	}

}
