package net.cloud.server.groovy;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import groovy.lang.GroovyClassLoader;

/**
 * Takes care of creating an object from a class defined in Groovy. 
 * While creating objects, classes are cached so that they are not recompiled needlessly. 
 * However, once this loader is closed, the cache is cleared - more objects will not be able to be created, 
 * and previously created classes will not be accessible via this loader.
 */
public class GroovyObjectLoader implements Closeable, AutoCloseable {
	
	/** This will give us a java class from a groovy file */
	private GroovyClassLoader loader;
	
	/**
	 * Create a new object loader. Don't forget to close it when you're done.
	 */
	public GroovyObjectLoader()
	{
		loader = new GroovyClassLoader();
	}
	
	/**
	 * Closes the underlying GroovyClassLoader, and clears the cache of loaded classes
	 */
	@Override
	public void close() throws IOException
	{
		loader.close();
		
		loader.clearCache();
	}
	
	/**
	 * Create a Java object from a Groovy class file. The loaded class is then cached until this loader is closed, 
	 * so that recurring instances can be created quickly. 
	 * @param fileName The groovy class file
	 * @param args Arguments to the constructor
	 * @param <T> Type of the object being created
	 * @return An object instance created from the class file
	 * @throws Exception If one of so many things goes wrong
	 */
	public <T> T createObject(String fileName, Object... args) throws Exception
	{
		@SuppressWarnings("unchecked")
		Class<? extends T> clazz = loader.parseClass(new File(fileName));
		
		// There are no arguments, use the default constructor
		if(args == null || args.length == 0)
		{
			return clazz.newInstance();
		}
		// There are some arguments, we'll have to get the right constructor and pass the arguments
		else {
			return clazz.getConstructor(Arrays.stream(args).map((o) -> o.getClass()).toArray(Class[]::new)).newInstance(args);
		}
	}

}
