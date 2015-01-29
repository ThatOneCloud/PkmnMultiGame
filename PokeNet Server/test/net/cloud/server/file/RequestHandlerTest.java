package net.cloud.server.file;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.cloud.server.file.request.CachedFileRegionRequest;
import net.cloud.server.file.request.CachedFileRequest;
import net.cloud.server.file.FileRequestException;
import net.cloud.server.file.address.FileAddress;
import net.cloud.server.file.request.BufferedReaderRequest;
import net.cloud.server.file.request.PrintWriterRequest;
import net.cloud.server.file.request.handler.RequestHandler;

public class RequestHandlerTest {
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	/** The object under test */
	private static RequestHandler handler;
	
	/** Temporary file to write to */
	private static File tempFile = null;
	
	@BeforeClass
	public static void setUpBeforeClass()
	{
		handler = new RequestHandler();
	}
	
	@Before
	public void setUp() throws IOException
	{
		// Make sure the file is ready to use
		if(tempFile == null)
		{
			tempFile = tempFolder.newFile();
		}
	}
	
	@After
	public void tearDown()
	{
		tempFile = null;
	}
	
	@AfterClass
	public static void tearDownAfterClass()
	{
		handler = null;
	}

	@Test
	public void testBufferedReaderRequest() throws IOException, FileRequestException {
		// The line we'll read from a known test file
		String TEST_LINE = "This is a known text file useful for testing";

		// Use a BR as our request and a known test file
		BufferedReaderRequest req = new BufferedReaderRequest(new FileAddress("./data/test/test_file.txt"));
		
		// Go directly to the handler
		handler.handleRequest(req);
		
		// Read the test line
		String readLine = req.getFileDescriptor().readLine();
		
		req.getFileDescriptor().close();
		
		// Make sure it matches
		assertTrue(readLine.equals(TEST_LINE));
	}

	@Test
	public void testPrintWriterRequest() throws FileRequestException {
		String TEST_WRITE = "File created as part of JUnit testing";
		
		PrintWriterRequest req = new PrintWriterRequest(new FileAddress(tempFile.getAbsolutePath()));
		
		handler.handleRequest(req);
		
		// Write to the file. Just a line
		req.getFileDescriptor().println(TEST_WRITE);
		req.getFileDescriptor().flush();
		
		req.getFileDescriptor().close();
		
		// Make sure the file was at least created
		assertTrue(Files.exists(Paths.get(tempFile.getAbsolutePath())));
	}
	
	/** See if a test CachedFileRequest will work */
	@Test
	public void testCachedFileRequest() throws IOException, FileRequestException {
		// What we know the file should contain
		byte[] TEST_DATA = new byte[] {0, 0, 0, 2};

		CachedFileRequest req = new CachedFileRequest(1, new FileAddress("./data/test/testCache.dat"));
		
		// Go directly to the handler
		handler.handleRequest(req);
		
		// Read the bytes
		byte readData[] = req.getFileDescriptor().getData();
		
		// Make sure it matches
		assertTrue(Arrays.equals(readData, TEST_DATA));
	}
	
	/** See if a test CachedFileRegionRequest will work */
	@Test
	public void testCachedFileRegionRequest() throws IOException, FileRequestException {
		// What we know the file should contain
		byte[][] TEST_DATA = {
				new byte[] {0, 0, 0, 1},
				new byte[] {0, 0, 0, 2},
				new byte[] {0, 0, 0, 3}
		};

		CachedFileRegionRequest req = new CachedFileRegionRequest(0, 2, new FileAddress("./data/test/testCache.dat"));
		
		// Go directly to the handler
		handler.handleRequest(req);
		
		// Check all the data
		for(int i = 0; i < 3; ++i)
		{
			byte readData[] = req.getFileDescriptor().getFileRel(i).getData();
			
			assertTrue(Arrays.equals(readData, TEST_DATA[i]));
		}
	}
	
}
