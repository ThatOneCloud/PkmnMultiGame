package net.cloud.server.file.request;

import net.cloud.server.file.address.AddressConstants;
import net.cloud.server.file.address.FileAddress;
import net.cloud.server.file.cache.CachedFile;
import net.cloud.server.file.request.handler.RequestHandler;

/**
 * A request which asks for a CachedFile to be retrieved. The file is loaded 
 * from a cache file using the corresponding cache table, so that the file 
 * contains the bytes from the file. A CachedFile is effectively read-only 
 * and so this is a LoadRequest.
 */
public class CachedFileRequest extends LoadRequest<CachedFile> {
	
	/** A separate file address for the cache table */
	private FileAddress tableAddress;
	
	/** The index into the cache for the file we want */
	private int indexInCache;
	
	/**
	 * Create a request for a single cached file. This constructor only requires the address of the cache 
	 * file, and will create the address to the table on its own based on the cache address. The table's address 
	 * will be set to the same location, with "Table" added into the filename. 
	 * <br>Ex: ./cache.dat => ./cacheTable.dat<br>
	 * The provided index must be within bounds, or the request will encounter an exception. 
	 * @param indexInCache The index of the file within the cache
	 * @param cacheAddress The location of the cache file itself
	 */
	public CachedFileRequest(int indexInCache, FileAddress cacheAddress) {
		super(cacheAddress);
		
		this.indexInCache = indexInCache;
		
		// Mutate the address
		StringBuilder tableB = new StringBuilder(cacheAddress.getPath());
		tableB.insert(tableB.lastIndexOf(AddressConstants.EXT_CACHE) - 1, "Table");
		this.tableAddress = new FileAddress(tableB.toString());
	}

	/**
	 * Create a request for a single cached file. This will use both addresses, one separately for each file. 
	 * The super class will be used to hold the cache address, whilst this holds the table address. 
	 * The provided index must be within bounds, or the request will encounter an exception. 
	 * @param indexInCache The index of the file within the cache
	 * @param tableAddress The location of the cache table file
	 * @param cacheAddress The location of the cache file itself
	 */
	public CachedFileRequest(int indexInCache, FileAddress tableAddress, FileAddress cacheAddress) {
		super(cacheAddress);
		
		this.indexInCache = indexInCache;
		this.tableAddress = tableAddress;
	}

	@Override
	public void handle(RequestHandler handler) {
		// Double dispatch off to the handler
		handler.handleRequest(this);
	}
	
	/**
	 * @return The index of the file within the cache
	 */
	public int getIndexInCache() {
		return indexInCache;
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
