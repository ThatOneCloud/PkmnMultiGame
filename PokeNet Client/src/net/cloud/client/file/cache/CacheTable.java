package net.cloud.client.file.cache;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;

/**
 * Logical object to deal with a cache table file. This requires both the cache table 
 * and cache file. Then, it supports retrieving information from both files. 
 * Neither the table nor the cache are stored in memory but rather read on demand, 
 * and the file objects it has been provided are not closed by any actions here - 
 * that is the responsibility of the caller. 
 */
public class CacheTable {
	
	/** The size (in bytes) of each row in the table */
	private static final int ROW_LENGTH = 8;
	
	/** The cache table file we will use to get information on the cache itself */
	private RandomAccessFile table;
	
	/** The file holding the cached data */
	private RandomAccessFile cache;
	
	/** How many entries there are in the table */
	private int size;
	
	/**
	 * Create a new CacheTable object representing the given associated table and cache files. 
	 * These files should already be open and ready for reading, however the position does not matter. 
	 * The files may have their position changed, but this class will not close the files. 
	 * @param table The cacheTable.dat file in the pairing, giving info on cache
	 * @param cache The cache.dat file, whose info is contained in the table
	 * @throws IOException If the table size could not be determined
	 */
	public CacheTable(RandomAccessFile table, RandomAccessFile cache) throws IOException
	{
		this.table = table;
		this.cache = cache;
		
		// Determine the number of entries via the size of file and size of each row
		this.size = (int) (table.length() / ROW_LENGTH);
	}
	
	/**
	 * Obtain a single file from the cache. The file to retrieve is the one at the given 
	 * index. The index cannot be less than 0 or greater than the number of files in the cache, of course. 
	 * @param index Index of the file in the cache
	 * @return A single CachedFile representing the data from the cache 
	 * @throws IOException If the file could not be retrieved
	 * @throws IllegalArgumentException If the index is out of bounds
	 */
	public CachedFile getFile(int index) throws IOException
	{
		// Bounds checking
		if(index < 0 || index >= size)
		{
			throw new IllegalArgumentException("CacheTable index out of bounds: " + index);
		}
		
		// Now to get the file itself. Need the position and size of the file
		long filePos = getFilePosition(index);
		int fileSize = getFileSize(index, filePos);
		
		// We'll read directly from the file, a single file is likely small enough to make this faster than mapping
		byte[] fileData = new byte[fileSize];
		cache.seek(filePos);
		cache.read(fileData);
		
		// Finally wrap it in a CachedFile object and we're good to go
		return new CachedFile(fileData);
	}
	
	/**
	 * Obtain a block of files from the cache. These files are all contiguous and each of them is retrieved. 
	 * The returned CachedFileRegion will contain all of these files, and they will all be loaded and ready. 
	 * The block is defined by the given indices. The start index must be non-negative and less than the end index. 
	 * The end index must not be greater than the number of files in the cache. 
	 * @param startIndex Index of the first file to retrieve
	 * @param endIndex Index of the last file to retrieve
	 * @return A CachedFileRegion containing all of the requested files
	 * @throws IOException If the file(s) could not be retrieved
	 * @throws IllegalArgumentException If the indices are invalid
	 */
	public CachedFileRegion getFileRegion(int startIndex, int endIndex) throws IOException
	{
		// Bounds checking (we only check that the end index fits - the rest will happen thanks to the CachedFileRegion constructor)
		if(endIndex >= size)
		{
			throw new IllegalArgumentException("CacheTable index out of bounds: " + endIndex);
		}
		
		// So now create the region based on the indices (will re-throw IAE)
		CachedFileRegion region = new CachedFileRegion(startIndex, endIndex);
		
		// Do this a tad more efficiently than for a single file. We'll map the entire region and split it manually
		// So the start position and size are for the entire region
		long regionPos = getFilePosition(startIndex);
		
		// Fudge the size by starting at the first position and finding the end of the last file
		long regionSize = getFileSize(endIndex, regionPos);
		
		// The ByteBuffer is mapped for the entire region instead of a single file
		ByteBuffer regionBuffer = getMemoryMappedBuffer(regionPos, regionSize);
		
		// Now we do things different to do this efficiently. We don't need the position for the buffer, just the size of each file. 
		byte[][] regionData = getRegionByteArrays(startIndex, endIndex);
		
		// So each of the arrays is earmarked for a different file's data.
		for(int i = 0; i < regionData.length; ++i)
		{
			// Fill each array up. It's the right size so bulk get works
			regionBuffer.get(regionData[i]);
			
			// Now create and add a CachedFile to the region
			region.placeFileRel(i, new CachedFile(regionData[i]));
		}
		
		// Finally the region is all filled up
		return region;
	}
	
	/**
	 * Get the starting position of a file within the cache, via its index in the table. 
	 * Index is assumed to be in bounds. 
	 * @param index Index of the file in the table
	 * @return The position of the file in the cache
	 * @throws IOException If the position could not be read
	 */
	private long getFilePosition(int index) throws IOException
	{
		// Seek to the right row in the table
		table.seek(index * ROW_LENGTH);
		
		// And hey it's just the entry in the table.
		return table.readLong();
	}
	
	/**
	 * Determine the size of a file in the cache. In other words, how many bytes it occupies 
	 * in the cache file. Index is assumed to be in bounds. 
	 * @param index Index of the file in the table
	 * @param filePosition The location of the file within the cache. See <code>getFilePosition(int)</code>
	 * @return The size, in bytes, of the file in the cache
	 * @throws IOException If the size could not be determined, due to a file error
	 */
	private int getFileSize(int index, long filePosition) throws IOException
	{
		// The last row is a special case. Cannot read the position of the next entry
		if((index + 1) == size)
		{
			// Cache file size minus position of the last file
			return (int) (cache.length() - filePosition);
		}
		// Else it's all the same. Difference in positions.
		else {
			// Next position minus current position
			return (int) (getFilePosition(index + 1) - filePosition);
		}
	}
	
	/**
	 * Obtain a ByteBuffer which contains the data for the cache, from the given position for the given length
	 * @param filePos Position in the cache file to start at
	 * @param fileSize How many bytes in the cache to map
	 * @return A ByteBuffer linked to the given part of the cache
	 * @throws IOException Some IO exception occurs
	 */
	private ByteBuffer getMemoryMappedBuffer(long filePos, long fileSize) throws IOException
	{
		return cache.getChannel().map(MapMode.READ_ONLY, filePos, fileSize);
	}
	
	/**
	 * Obtain a byte array for each of the files in the specified range. The byte arrays are simply 
	 * initialized, they are not filled with the actual file data.
	 * @param startIndex The first file index
	 * @param endIndex The last file index
	 * @return A 2D byte array where each requested file has an array sized to the file size
	 * @throws IOException If an IO error occurs
	 */
	private byte[][] getRegionByteArrays(int startIndex, int endIndex) throws IOException
	{
		byte[][] regionData = new byte[endIndex - startIndex + 1][];
		int arrayIndex = 0;
		long startPosition;
		long endPosition;
		
		// Seek to the first table entry
		table.seek(startIndex * ROW_LENGTH);
		
		// Read the position of the first file, becomes the start position
		startPosition = table.readLong();
		
		// For all but the last file in the region (since it's only one that may be last in file)...
		for(int i = startIndex; i < endIndex; ++i)
		{
			// Read the position of the next file, becomes end position
			endPosition = table.readLong();
		
			// The difference is the size of this file
			int fileSize = (int) (endPosition - startPosition);
			
			// Have the size, create a byte array for the file. 
			regionData[arrayIndex] = new byte[fileSize];
			arrayIndex++;
		
			// And now the start position is moved up in preparation for the next loop
			startPosition = endPosition;
		}
		
		// Now the last in the region needs a special case check. 
		if((endIndex + 1) == size)
		{
			// So since the last in the region is last in the file, it's the end of the file for end position
			regionData[arrayIndex] = new byte[(int) (cache.length() - startPosition)];
		}
		else {
			// Nope, same as before. End position is the start of the next file.
			regionData[arrayIndex] = new byte[(int) (table.readLong() - startPosition)];
		}
		
		return regionData;
	}
	
}
