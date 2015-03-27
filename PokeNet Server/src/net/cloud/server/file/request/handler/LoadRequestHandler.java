package net.cloud.server.file.request.handler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.thoughtworks.xstream.XStreamException;

import net.cloud.server.file.request.RandomAccessFileLoadRequest;
import net.cloud.server.file.cache.CacheTable;
import net.cloud.server.file.request.CachedFileRegionRequest;
import net.cloud.server.file.request.CachedFileRequest;
import net.cloud.server.file.request.XmlLoadRequest;
import net.cloud.server.file.FileRequestException;
import net.cloud.server.file.XStreamHandler;
import net.cloud.server.file.request.BufferedReaderRequest;
import net.cloud.server.util.IOUtil;

/**
 * A handler class which specifically deals with requests 
 * to load a file. (Subclasses of LoadRequest)
 * It exists as a means to break up what would otherwise be 
 * a very large handler class, and should also serve to align 
 * with the parallel class hierarchies of this subsystem in general.
 */
public class LoadRequestHandler {
	
	/** Default public constructor. Creates a new instance for use. */
	public LoadRequestHandler()
	{
	}
	
	/**
	 * Attempt to handle a BufferedReaderRequest.<br>
	 * That is, it will create a BufferedReader to the requested file and 
	 * notify the Request object that it is ready or that an exception occurred.
	 * @param req The request to fulfill
	 */
	public void handleRequest(BufferedReaderRequest req)
	{
		// Going to create a BufferedReader with lots of wrappers. Here goes
		try {
			BufferedReader br = IOUtil.streamToReader(new FileInputStream(req.address().getPathString()));
			
			// Now that we've got the reader, assign it to the request
			req.setFileDescriptor(br);
			
			// And notify the request it's ready
			req.notifyReady();
		} catch (FileNotFoundException e) {
			// Uh oh. The path wasn't right. Let the request know so the exception can propagate
			req.notifyHandleException(new FileRequestException("Requested file could not be found", e));
		}
	}
	
	/**
	 * Attempt to handle a RandomAccessFileLoadRequest.<br>
	 * That is, it will create a read-only RandomAccessFile to the requested file and 
	 * notify the Request object that it is ready or that an exception occurred.
	 * @param req The request to fulfill
	 */
	public void handleRequest(RandomAccessFileLoadRequest req)
	{
		try {
			// Obtain a RAF in read mode
			RandomAccessFile raf = new RandomAccessFile(req.address().getPathString(), "r");
			
			req.setFileDescriptor(raf);
			
			req.notifyReady();
		} catch (FileNotFoundException e) {
			req.notifyHandleException(new FileRequestException("Requested file could not be found", e));
		}
	}
	
	/**
	 * Attempt to handle a CachedFileRequest.<br>
	 * That is, create a CachedFile object which holds the data being requested. 
	 * The Request object will be notified when it is ready or if an exception occurred. 
	 * @param req The request to fulfill
	 */
	public void handleRequest(CachedFileRequest req) 
	{
		// Try-with-resources will close the RAFs on its own, and mask any closing exception
		try (

			RandomAccessFile table = new RandomAccessFile(req.getTableAddress().getPathString(), "r");
			RandomAccessFile cache = new RandomAccessFile(req.address().getPathString(), "r")
		) {
			// A CacheTable object handily can take care of details (and let's just do this in one swoop...)
			req.setFileDescriptor(new CacheTable(table, cache).getFile(req.getIndexInCache()));
			
			req.notifyReady();
		} catch (FileNotFoundException e) {
			// Uh oh. The path wasn't right. Let the request know so the exception can propagate
			req.notifyHandleException(new FileRequestException("Requested file(s) could not be found", e));
		} catch (IOException e) {
			// Something went wrong during reading of the files
			req.notifyHandleException(new FileRequestException("File could not be retrieved from cache", e));
		}
	}
	
	/**
	 * Attempt to handle a CachedFileRegionRequest.<br>
	 * That is, create a CachedFileRegion object which holds the data being requested. 
	 * The Request object will be notified when it is ready or if an exception occurred. 
	 * @param req The request to fulfill
	 */
	public void handleRequest(CachedFileRegionRequest req) 
	{
		// Try-with-resources will close the RAFs on its own, and mask any closing exception
		try (
			RandomAccessFile table = new RandomAccessFile(req.getTableAddress().getPathString(), "r");
			RandomAccessFile cache = new RandomAccessFile(req.address().getPathString(), "r")
		) {
			// A CacheTable object handily can take care of details (and let's just do this in one swoop...)
			req.setFileDescriptor(new CacheTable(table, cache).getFileRegion(req.getStartIndex(), req.getEndIndex()));
			
			req.notifyReady();
		} catch (FileNotFoundException e) {
			// Uh oh. The path wasn't right. Let the request know so the exception can propagate
			req.notifyHandleException(new FileRequestException("Requested file(s) could not be found", e));
		} catch (IOException e) {
			// Something went wrong during reading of the files
			req.notifyHandleException(new FileRequestException("File could not be retrieved from cache", e));
		}
	}

	/**
	 * Attempt to handle an XmlLoadRequest. <br>
	 * The file storing the XML information will be opened, and then be parsed to create an object. 
	 * That object will be set as the file descriptor, and the XML file will be closed. 
	 * @param req The request to fulfill
	 * @param <T> The type of the object to be loaded
	 */
	public <T> void handleRequest(XmlLoadRequest<T> req)
	{
		// Try-with-resources, use a FileInputStream since we'll just need to get the data quick
		try (InputStream input = new FileInputStream(req.address().getPathString()))
		{
			// Buffer it up, of course. 
			InputStream bufferedInput = new BufferedInputStream(input);
			
			// Delegate deserializing action off to the handler
			Object obj = XStreamHandler.instance().from(bufferedInput);
			
			// Attempt a type-cast on the object. If it doesn't match, that's where the exception comes in
			@SuppressWarnings("unchecked")
			T castedObj = (T) obj;
			
			// Now set the object as the file descriptor
			req.setFileDescriptor(castedObj);
			
			// Finally ready, the object should now be successfully deserialized
			req.notifyReady();
		} catch (FileNotFoundException e) {
			req.notifyHandleException(new FileRequestException("Requested XML file does not exist", e));
		} catch (IOException e) {
			req.notifyHandleException(new FileRequestException("IO Exception on XML file", e));
		} catch (XStreamException e) {
			req.notifyHandleException(new FileRequestException("Could not deserialize XML", e));
		} catch (ClassCastException e) {
			req.notifyHandleException(new FileRequestException("Deserialized object type did not match requested type", e));
		}
	}
	
}
