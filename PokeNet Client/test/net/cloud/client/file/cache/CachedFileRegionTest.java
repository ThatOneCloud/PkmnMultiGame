package net.cloud.client.file.cache;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/** Mostly tests for the exceptional values */
public class CachedFileRegionTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/** constructor with bad args */
	@Test
	public void testCachedFileRegion_NegStart() {
		thrown.expect(IllegalArgumentException.class);
		
		new CachedFileRegion(-1, 2);
	}
	
	/** constructor with bad args */
	@Test
	public void testCachedFileRegion_GreaterStart() {
		thrown.expect(IllegalArgumentException.class);
		
		new CachedFileRegion(4, 2);
	}
	
	/** constructor with bad args */
	@Test
	public void testCachedFileRegion_LesserEnd() {
		thrown.expect(IllegalArgumentException.class);
		
		new CachedFileRegion(1, 0);
	}

	/** Placing out of bounds */
	@Test
	public void testPlaceFileAbs_LesserIdx() {
		thrown.expect(IllegalArgumentException.class);
		
		CachedFileRegion r = new CachedFileRegion(2, 5);
		
		// Place with an idx less than start
		r.placeFileAbs(1, null);
	}
	
	/** Placing out of bounds */
	@Test
	public void testPlaceFileAbs_GreaterIdx() {
		thrown.expect(IllegalArgumentException.class);
		
		CachedFileRegion r = new CachedFileRegion(2, 5);
		
		// Place with an idx greater than end
		r.placeFileAbs(7, null);
	}
	
	/** Placing in bounds - also tests get */
	@Test
	public void testPlaceFileAbs() {
		CachedFileRegion r = new CachedFileRegion(2, 5);
		byte[] data = new byte[] {1, 2, 3, 4};
		CachedFile f = new CachedFile(data);
		
		// Place (valid idx)
		r.placeFileAbs(4, f);
		
		// Make sure it's there
		assertTrue(data == r.getFileRel(2).getData());
	}

}
