package net.cloud.client.file.cache;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.junit.Test;

/** Test the ability of the class to obtain file(s) */
public class CacheTableTest {

	/** Test grabbing a single file. Includes boundaries. */
	@Test
	public void testGetFile() throws IOException {
		// Try-with-resources. Open the files. 
		try (
				RandomAccessFile table = new RandomAccessFile("./data/test/testCacheTable.dat", "r"); 
				RandomAccessFile cache = new RandomAccessFile("./data/test/testCache.dat", "r")
		) {
			// Create the table object
			CacheTable ct = new CacheTable(table, cache);
			
			// We know there are 5 files total. Grab the boundaries.
			CachedFile f0 = ct.getFile(0);
			CachedFile f2 = ct.getFile(2);
			CachedFile f4 = ct.getFile(4);
			
			// They should all contain some deterministic data (integers 1-5)
			assertTrue(Arrays.equals(f0.getData(), new byte[] {0, 0, 0, 1}));
			assertTrue(Arrays.equals(f2.getData(), new byte[] {0, 0, 0, 3}));
			assertTrue(Arrays.equals(f4.getData(), new byte[] {0, 0, 0, 5}));
		}
	}
	
	/** Test grabbing a block of files. */
	@Test
	public void testGetRegion() throws IOException {
		// Try-with-resources. Open the files. 
		try (
				RandomAccessFile table = new RandomAccessFile("./data/test/testCacheTable.dat", "r"); 
				RandomAccessFile cache = new RandomAccessFile("./data/test/testCache.dat", "r")
		) {
			// Create the table object
			CacheTable ct = new CacheTable(table, cache);
			
			// Get the region, say 1-3 (the 3 middle files)
			CachedFileRegion r = ct.getFileRegion(1, 3);
			
			// Each file should contain some deterministic data (integers 2-4)
			assertTrue(Arrays.equals(r.getFileRel(0).getData(), new byte[] {0, 0, 0, 2}));
			assertTrue(Arrays.equals(r.getFileRel(1).getData(), new byte[] {0, 0, 0, 3}));
			assertTrue(Arrays.equals(r.getFileAbs(3).getData(), new byte[] {0, 0, 0, 4}));
		}
	}

}
