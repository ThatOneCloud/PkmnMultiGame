package net.cloud.server.file.request.handler;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.thoughtworks.xstream.XStreamException;

import net.cloud.server.file.FileRequestException;
import net.cloud.server.file.XStreamHandler;
import net.cloud.server.file.request.FileOutputStreamRequest;
import net.cloud.server.file.request.PrintWriterRequest;
import net.cloud.server.file.request.XmlSaveRequest;
import net.cloud.server.util.IOUtil;

/**
 * A handler class which specifically deals with requests 
 * to write to a file. (Subclasses of SaveRequest)
 * It exists as a means to break up what would otherwise be 
 * a very large handler class, and should also serve to align 
 * with the parallel class hierarchies of this subsystem in general.
 */
public class SaveRequestHandler {
	
	/** Default public constructor. Creates a new instance for use. */
	public SaveRequestHandler()
	{
	}
	
	/**
	 * Attempt to handle a PrintWriterRequest.<br>
	 * That is, it will create a PrintWriter to the requested file and 
	 * notify the Request object that it is ready or that an exception occurred.
	 * @param req The request to fulfill
	 */
	public void handleRequest(PrintWriterRequest req)
	{
		// Going to create a PrintWriter with lots of wrappers. Here goes
		try {
			String address = req.address().getPathString();
			Path path = Paths.get(address);
			
			// Create any directories leading up to the path that don't already exist
			// I'm a little concerned this may fail in the future, if something requests a bad path
			Files.createDirectories(path.getParent());
			
			// Create a PrintWriter to the given address
			PrintWriter pw = IOUtil.streamToWriter(new FileOutputStream(address));
			
			// Now that we've got the writer, assign it to the request
			req.setFileDescriptor(pw);

			// And notify the request it's ready
			req.notifyReady();
		} catch (IOException e) {
			// Uh oh. The file couldn't be opened. Let the request know so the exception can propagate
			req.notifyHandleException(new FileRequestException("Requested file could not be opened", e));
		}
	}
	
	/**
	 * Attempt to handle a FileOutputStreamRequest.<br>
	 * That is, it will create a FileOutputStream to the requested file and 
	 * notify the Request object that it is ready or that an exception occurred.
	 * @param req The request to fulfill
	 */
	public void handleRequest(FileOutputStreamRequest req)
	{
		// Going to create a FileOutputStream right to the file - not buffered
		try {
			String address = req.address().getPathString();
			Path path = Paths.get(address);
			
			// Create any directories leading up to the path that don't already exist
			// I'm a little concerned this may fail in the future, if something requests a bad path
			Files.createDirectories(path.getParent());
			
			// Create a FileOutputStream to the given address
			FileOutputStream out = new FileOutputStream(address);
			
			// Now that we've got the writer, assign it to the request
			req.setFileDescriptor(out);

			// And notify the request it's ready
			req.notifyReady();
		} catch (IOException e) {
			// Uh oh. The file couldn't be opened. Let the request know so the exception can propagate
			req.notifyHandleException(new FileRequestException("Requested file could not be opened", e));
		}
	}
	
	/**
	 * Attempt to handle a XmlSaveRequest.<br>
	 * A file stream will be opened for the request, and the object saved in the request will be 
	 * serialized to an XML file. The stream is then closed. There is no usable file descriptor. 
	 * @param req The request to fulfill
	 */
	public void handleRequest(XmlSaveRequest req)
	{
		String address = req.address().getPathString();
		Path path = Paths.get(address);
		
		// Again up front make sure the directory exists. Before trying to open the file.
		try {
			Files.createDirectories(path.getParent());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Try-with-resources. Just a quick one-off usage of an output stream
		try (OutputStream output = new FileOutputStream(address))
		{
			// Buffer it. Can you imagine all the tiny writes?
			OutputStream bufferedOutput = new BufferedOutputStream(output);
			
			// Have serialization taken care of
			XStreamHandler.instance().to(req.getSaveObject(), bufferedOutput);
			
			// Sort of a dummy FD - necessary to unblock if waiting
			req.setFileDescriptor(new Object());
			req.notifyReady();
		} catch (FileNotFoundException e) {
			req.notifyHandleException(new FileRequestException("Could not open or create XML file", e));
		} catch (IOException e) {
			req.notifyHandleException(new FileRequestException("IO Exception on XML file", e));
		} catch (XStreamException e) {
			req.notifyHandleException(new FileRequestException("Could not serialize XML", e));
		}
	}

}
