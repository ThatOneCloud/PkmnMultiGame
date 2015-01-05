package net.cloud.mmo.file;

import static org.junit.Assert.*;
import net.cloud.mmo.file.address.FileAddressBuilder;

import org.junit.Test;

/** Test that the builder gives out a correctly formatted string */
public class AddressBuilderTest {

	/** A simple through and through test */
	@Test
	public void testBuilder() {
		String PATH = "./data/test/";
		String FILE = "test_file";
		String EXT = "txt";
		String FULL_ADDRESS = "./data/test/test_file.txt";
		
		String result = FileAddressBuilder.newBuilder().space(PATH).filename(FILE).extension(EXT).createAddress().getPath();
		
		// The result should be equal to the full address. Basically did it put it together right
		assertTrue(result.equals(FULL_ADDRESS));
	}
	
	/** Does it uphold the no exception policy? */
	@Test
	public void testBuilderNoException() {
		String PATH = "./data/test/";
		String EXT = "txt";
		
		// We left to filename unspecified. It should not fail, at the least.
		String result = FileAddressBuilder.newBuilder().space(PATH).extension(EXT).createAddress().getPath();
		
		// The result should at least be there
		assertTrue(result != null);
	}

}
