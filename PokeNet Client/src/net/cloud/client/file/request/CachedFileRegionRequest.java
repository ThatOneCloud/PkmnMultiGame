package net.cloud.client.file.request;

import net.cloud.client.file.address.AddressConstants;
import net.cloud.client.file.address.FileAddress;
import net.cloud.client.file.cache.CachedFileRegion;
import net.cloud.client.file.request.handler.RequestHandler;

/**
 * A request which asks for a CachedFileRegion to be retrieved. The file is loaded 
 * from a cache file using the corresponding cache table, so that the region object contains 
 * all of the files that were requested. The region will load all of the requested file 
 * in a contiguous block. 
 */
public class CachedFileRegionRequest extends LoadRequest<CachedFileRegion> {
	
	/** A separate file address for the cache table */
	private FileAddress tableAddress;
	
	/** The index of the first file in the region we want */
	private int startIndex;
	
	/** The index of the last file we want */
	private int endIndex;
	
	/**
	 * Create a request for a block of cached files. This constructor only requires the address of the cache 
	 * file, and will create the address to the table on its own based on the cache address. The table's address 
	 * will be set to the same location, with "Table" added into the filename. 
	 * <br>Ex: ./cache.dat => ./cacheTable.dat<br>
	 * The provided indices must be within bounds, or the request will encounter an exception. 
	 * @param startIndex The index of the first file in the region we want 
	 * @param endIndex The index of the last file we want
	 * @param cacheAddress The location of the cache file itself
	 */
	public CachedFileRegionRequest(int startIndex, int endIndex, FileAddress cacheAddress) {
		super(cacheAddress);
		
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		
		// Mutate the address
		StringBuilder tableB = new StringBuilder(cacheAddress.getPathString());
		tableB.insert(tableB.lastIndexOf(AddressConstants.EXT_CACHE) - 1, "Table");
		this.tableAddress = new FileAddress(tableB.toString());
	}

	/**
	 * Create a request for a block of cached files. This will use both addresses, one separately for each file. 
	 * The super class will be used to hold the cache address, whilst this holds the table address. 
	 * The provided indices must be within bounds, or the request will encounter an exception. 
	 * @param startIndex The index of the first file in the region we want 
	 * @param endIndex The index of the last file we want
	 * @param tableAddress The location of the cache table file
	 * @param cacheAddress The location of the cache file itself
	 */
	public CachedFileRegionRequest(int startIndex, int endIndex, FileAddress tableAddress, FileAddress cacheAddress) {
		super(cacheAddress);
		
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.tableAddress = tableAddress;
	}

	@Override
	public void handle(RequestHandler handler) {
		// Double dispatch off to the handler
		handler.handleRequest(this);
	}
	
	/**
	 * @return The index of the first file we want
	 */
	public int getStartIndex() {
		return startIndex;
	}
	
	/**
	 * @return The index of the last file we want
	 */
	public int getEndIndex() {
		return endIndex;
	}

	/**
	 * Obtain the address for the cache table with information of the file this request is for. 
	 * To obtain the address of the cache file itself, use {@link #address()}
	 * @return The address to the cache table file
	 */
	public FileAddress getTableAddress() {
		return tableAddress;
	}

}
