package net.cloud.mmo.file;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.cloud.mmo.file.address.FileAddress;
import net.cloud.mmo.file.request.BufferedReaderRequest;
import net.cloud.mmo.file.request.PrintWriterRequest;
import net.cloud.mmo.file.request.handler.RequestHandler;

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
	
}
