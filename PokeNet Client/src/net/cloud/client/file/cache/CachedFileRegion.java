package net.cloud.client.file.cache;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A block of one or more CachedFile objects. In other words, a region of a cache file 
 * represented as each individual file. The region is contiguous, and so will contain 
 * all cached files from a starting index to an ending index (possibly the same for a single file) <br>
 * This object is obtained via a CachedFileRegionLoadRequest and allows access to the contained files. 
 */
public class CachedFileRegion {
	
	/** The index of the first file of the region */
	private final int startIndex;
	
	/** The index of the last file in the region */
	private final int endIndex;
	
	/** Each of the CachedFiles that make up this region */
	private final CachedFile[] files;
	
	/**
	 * Created a new CachedFileRegion which will contain the CachedFiles in the 
	 * designated block of the cache file. The block must be at least one CachedFile 
	 * (endIndex >= startIndex) and the cached files will need to be set into this region 
	 * as they are loaded. Until then they will be null. 
	 * @param startIndex First file in the region. 0 <= startIndex <= endIndex
	 * @param endIndex Last file in the region. startIndex <= endIndex < # files in region
	 * @throws IllegalArgumentException If the indices are invalid
	 */
	public CachedFileRegion(int startIndex, int endIndex)
	{
		// Validate the arguments, first
		if(startIndex < 0 || startIndex > endIndex)
		{
			throw new IllegalArgumentException("Start index may not be negative or greater than end index");
		}
		
		if(endIndex < startIndex)
		{
			throw new IllegalArgumentException("End index may not be less than the start index");
		}
		
		// Normal constructor stuff
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		
		this.files = new CachedFile[endIndex - startIndex + 1];
	}
	
	/**
	 * Place a file into the region once it has been loaded. (This object does not handle 
	 * loading the regions on its own)  The index must fit within the region
	 * @param index The absolute index of the file in the cache. <b>Not</b> relative to the region
	 * @param file The CachedFile which has been loaded and is now being assigned to the region
	 * @throws IllegalArgumentException If the index is out of this region's bounds
	 */
	public void placeFileAbs(int index, CachedFile file)
	{
		// Make sure the index is within the region
		if(index < startIndex || index > endIndex)
		{
			throw new IllegalArgumentException("Invalid file index for placing file: " + index);
		}
		
		files[index - startIndex] = file;
	}
	
	/**
	 * Place a file into the region once it has been loaded. (This object does not handle 
	 * loading the regions on its own)  The index is relative, so the first file in the region 
	 * has index 0. 
	 * @param index The relative index of the file within this region
	 * @param file The CachedFile which has been loaded and is now being assigned to the region
	 */
	public void placeFileRel(int index, CachedFile file)
	{
		// Arrays are bound checked.. so.. whatever.
		files[index] = file;
	}
	
	/**
	 * Obtain a file from this region. This uses the absolute index of the file within 
	 * the entire cache. Of course, the index must be in bounds. Fear the ArrayIndexOutOfBounds exception!
	 * @param index Index of the file within the entire cache
	 * @return The CachedFile at the index, or null if it was never loaded
	 */
	public CachedFile getFileAbs(int index)
	{
		return files[index - startIndex];
	}
	
	/**
	 * Obtain a file from this region. This uses the relative index of the file within 
	 * this particular region. Of course, the index must be in bounds. Fear the ArrayIndexOutOfBounds exception!
	 * @param index Index of the file relative to this region
	 * @return The CachedFile at the index, or null if it was never loaded
	 */
	public CachedFile getFileRel(int index)
	{
		return files[index];
	}
	
	/**
	 * Obtain an iterator which will iterate over all of the CachedFiles contained in this region
	 * @return An iterator which will iterate over all of the CachedFiles contained in this region
	 */
	public Iterator<CachedFile> getFileIterator()
	{
		return Arrays.stream(files).iterator();
	}

}
