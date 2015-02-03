package net.cloud.server.file;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import net.cloud.server.file.address.FileAddress;
import net.cloud.server.file.request.XmlLoadRequest;
import net.cloud.server.file.request.XmlSaveRequest;
import net.cloud.server.file.request.handler.RequestHandler;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.thoughtworks.xstream.XStreamException;

/**
 * To test the XML load and save requests, and with it, the general XML functionality. 
 */
public class XmlRequestTest {
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Rule
	public ExpectedException expectedExc = ExpectedException.none();
	
	/** Skip straight to the request handler */
	private static RequestHandler handler;
	
	@BeforeClass
	public static void setUpBeforeClass()
	{
		handler = new RequestHandler();
	}

	/** 
	 * Test saving then loading an object 
	 * @throws IOException 
	 * @throws FileRequestException 
	 */
	@Test
	public void testSerialDeserial() throws IOException, FileRequestException 
	{
		// Get a temporary file to work with
		File tempFile = tempFolder.newFile();
		
		// Create basic object
		XmlTestObject saveObj = new XmlTestObject("saveObj", 33, new byte[] {5, 6, 7});
		
		// Create request to save object
		XmlSaveRequest saveReq = new XmlSaveRequest(new FileAddress(tempFile.getAbsolutePath()), saveObj);
		
		// Try to save the object
		handler.handleRequest(saveReq);
		
		// Create request to load the object back
		XmlLoadRequest<XmlTestObject> loadReq = new XmlLoadRequest<>(new FileAddress(tempFile.getAbsolutePath()));
		
		// Try to load the object
		handler.handleRequest(loadReq);
		XmlTestObject loadObj = loadReq.getObject();
		
		// Verify the data fields are correct
		assertTrue(loadObj.equals(saveObj));
	}
	
	/**
	 * Test that we do indeed get an exception when we request the wrong class type
	 * @throws IOException 
	 * @throws FileRequestException 
	 */
	@Test
	public void testClassCast() throws IOException, FileRequestException
	{
		// Get a temporary file to work with
		File tempFile = tempFolder.newFile();

		// Create basic object
		XmlTestObject saveObj = new XmlTestObject("saveObj", 33, new byte[] {5, 6, 7});

		// Create request to save object
		XmlSaveRequest saveReq = new XmlSaveRequest(new FileAddress(tempFile.getAbsolutePath()), saveObj);

		// Try to save the object
		handler.handleRequest(saveReq);

		// Create request to load the object back, with a blatantly wrong type
		XmlLoadRequest<Integer> loadReq = new XmlLoadRequest<>(new FileAddress(tempFile.getAbsolutePath()));
		
		// Try to load object and do something that'll actually cast it
		expectedExc.expect(ClassCastException.class);
		handler.handleRequest(loadReq);
		Integer notReallyAnInteger = loadReq.getObject();
		notReallyAnInteger.intValue();
	}
	
	/**
	 * Test trying to deserialize from a XML file with some invalid information
	 */
	@Test
	public void testBadXml()
	{
		// Create request to load known bad xml
		XmlLoadRequest<XmlTestObject> loadReq = new XmlLoadRequest<>(new FileAddress("./data/test/badXml.xml"));

		// Try to load the object
		try {
			handler.handleRequest(loadReq);
			loadReq.getObject();
			
			fail("Request exception not thrown from bad xml");
		} catch (FileRequestException e) {
			// We expect it to have been caused by some xstream exception
			assertTrue(e.getCause() instanceof XStreamException);
		}
	}
	
	/** Simple class with some simple yet common data types */
	private static class XmlTestObject {
		
		private String str;
		
		private int num;
		
		private byte[] array;

		public XmlTestObject(String str, int num, byte[] array) {
			this.str = str;
			this.num = num;
			this.array = array;
		}
		
		public boolean equals(XmlTestObject other) {
			return str.equals(other.str) && (num == other.num) && Arrays.equals(array, other.array);
		}
		
	}

}
