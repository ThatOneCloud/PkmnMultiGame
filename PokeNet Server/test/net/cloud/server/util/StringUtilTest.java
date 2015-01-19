package net.cloud.server.util;

import static org.junit.Assert.*;
import net.cloud.server.util.StringUtil;

import org.junit.Test;

/** Test methods for a few utility methods. Good place to start with JUnit test cases */
public class StringUtilTest {

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

	@Test
	public void testExtractCommandToken() {
		// Start off with some test cases and expected results
		String t1 = "tokens by spaces";
		String t2 = "  leading spaces";
		String t3 = "\"quote delimited\"";
		String r1 = "tokens";
		String r2 = "leading";
		String r3 = "quote delimited";

		// Two straight forward assertions - make sure the strings are equal
		try {
			assertTrue(StringUtil.extractCommandToken(new StringBuilder(t1)).equals(r1));
			assertTrue(StringUtil.extractCommandToken(new StringBuilder(t2)).equals(r2));
			assertTrue(StringUtil.extractCommandToken(new StringBuilder(t3)).equals(r3));
		} catch (Exception e) {
			fail("No exceptions expected");
		}
	}

}
