package net.cloud.client.file.cache;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

/** Simple tests. It's just a wrapper class. */
public class CachedFileTest {

	/** Constructor test */
	@Test
	public void testCachedFile() {
		CachedFile f = new CachedFile(new byte[] {0, 1, 2, 3});
		
		assertTrue(f.getData().length == 4);
	}

	/** 
	 * Convenience method test 
	 * @throws IOException test fails
	 */
	@Test
	public void testAsInputStream() throws IOException {
		CachedFile f = new CachedFile(new byte[] {0, 1, 2, 3});
		
		// Make sure it reads the right stuff
		InputStream is = f.asInputStream();
		byte[] bytes = new byte[4];
		is.read(bytes);
		
		assertTrue(bytes[0] == 0 && bytes[3] == 3);
	}

}
