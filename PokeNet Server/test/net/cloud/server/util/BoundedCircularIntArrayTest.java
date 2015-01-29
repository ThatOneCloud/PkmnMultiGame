package net.cloud.server.util;

import static org.junit.Assert.*;

import org.junit.Test;

/** Test the BoundedCircularIntArray utility class */
public class BoundedCircularIntArrayTest {

	/** 
	 * Make sure clone behavior is as expected 
	 * @throws CloneNotSupportedException Test fails.
	 */
	@Test
	public void testClone() throws CloneNotSupportedException {
		// Create an object to work with
		BoundedCircularIntArray obj = new BoundedCircularIntArray(10, 6);
		
		// Get a clone and modify it
		BoundedCircularIntArray clone = obj.clone();
		clone.pushValue(16);
		clone.pushValue(26);
		
		// Expect the average of the first to remain the same (an independent clone)
		assertTrue(obj.average() == 6);
		assertTrue(clone.average() == 9);
	}

	/** Make sure adding values and finding the average work properly */
	@Test
	public void testPushValue() {
		// Create an object to work with
		BoundedCircularIntArray obj = new BoundedCircularIntArray(10, 6);
		
		// First average will be all 6 (initialization should fill the array)
		assertTrue(obj.average() == 6);
		
		// Now add some values, which will modify the average (to 90 total - subtract the '6' being pushed out)
		obj.pushValue(16);
		obj.pushValue(26);
		assertTrue(obj.average() == 9);
	}

}
