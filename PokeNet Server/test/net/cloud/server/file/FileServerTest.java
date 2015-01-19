package net.cloud.server.file;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;

import net.cloud.server.file.FileRequestException;
import net.cloud.server.file.FileServer;
import net.cloud.server.file.address.FileAddress;
import net.cloud.server.file.request.BufferedReaderRequest;

import org.junit.Test;

/** Tests to see if the logic flow as a whole works */
public class FileServerTest {

	/** Test submitting a request and reading the result */
	@Test
	public void testSuccessfulSubmit() {
		// The line we'll read from a known test file
		String TEST_LINE = "This is a known text file useful for testing";
		
		// Use a BR as our request and a known test file
		BufferedReaderRequest req = new BufferedReaderRequest(new FileAddress("./data/test/test_file.txt"));
		
		try {
			// Submit the request - it will now flow through the server and handlers
			FileServer.instance().submit(req);
			
			// Wait for it to complete before we try to read
			req.waitForRequest();
			
			// Obtain the BR we asked for
			BufferedReader br = req.getFileDescriptor();
			
			// We know what the first line should be
			String readLine = br.readLine();
			
			br.close();
			
			// Make sure what we read matches what we expected
			assertTrue(readLine.equals(TEST_LINE));
		} catch (FileRequestException | IOException e) {
			// Not expected. This is a failure.
			fail("Exception not expected from testSuccessfulSubmit()");
		}
	}
	
	/** Test submitting a request for a missing file */
	@Test
	public void testMissingFileSubmit() {
		// Use a BR as our request and a nonexistant file
		BufferedReaderRequest req = new BufferedReaderRequest(new FileAddress("./data/test/nonexistant.txt"));
		
		try {
			// Submit the request - it will now flow through the server and handlers
			FileServer.instance().submit(req);
			
			// Wait for it to complete before we try to read
			req.waitForRequest();
			
			// An exception should have occurred
			fail("Exception expected in testMissingFileSubmit()");
		} catch (FileRequestException e) {
			// Goody. An exception is just what we wanted!
			assertTrue(true);
		}
	}

}
