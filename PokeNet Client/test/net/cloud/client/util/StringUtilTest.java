package net.cloud.client.util;

import static org.junit.Assert.*;
import net.cloud.client.util.StringUtil;

import org.junit.Test;

/** Test methods for a few utility methods. Good place to start with JUnit test cases */
public class StringUtilTest {
	
	@Test
	public void testCleanDecimal()
	{
		double v1 = 15.4257;
		String r1 = "15.43";
		
		double v2 = 0.333;
		String r2 = "0.3";
		
		double v3 = 0.045;
		String r3 = "0.045";
		
		// Trim with rounding
		assertTrue(StringUtil.cleanDecimal(v1, 2).equals(r1));
		
		// Trim not rounded up
		assertTrue(StringUtil.cleanDecimal(v2, 1).equals(r2));
		
		// No minimum requirement
		assertTrue(StringUtil.cleanDecimal(v3, 6).equals(r3));
	}

	@Test
	public void testUpperFirst() {
		// Start off with some test cases and expected results
		String t1 = "a lower case sentence";
		String t2 = "underscores_become_spaces";
		String r1 = "A Lower Case Sentence";
		String r2 = "Underscores Become Spaces";
		
		// Two straight forward assertions - make sure the strings are equal
		assertTrue(StringUtil.upperFirst(t1).equals(r1));
		assertTrue(StringUtil.upperFirst(t2).equals(r2));
	}

	@Test
	public void testAllCaps() {
		// Start off with some test cases and expected results
		String t1 = "lower";
		String t2 = "spaces become underscores";
		String r1 = "LOWER";
		String r2 = "SPACES_BECOME_UNDERSCORES";

		// Two straight forward assertions - make sure the strings are equal
		assertTrue(StringUtil.allCaps(t1).equals(r1));
		assertTrue(StringUtil.allCaps(t2).equals(r2));
	}

}
