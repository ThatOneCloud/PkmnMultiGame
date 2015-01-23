package net.cloud.gfx.sprites;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import net.cloud.client.file.FileRequestException;
import net.cloud.client.file.FileServer;
import net.cloud.client.file.address.FileAddress;
import net.cloud.client.file.address.FileAddressBuilder;
import net.cloud.client.file.cache.CachedFile;
import net.cloud.client.file.cache.CachedFileRegion;
import net.cloud.client.file.request.CachedFileRegionRequest;
import net.cloud.client.file.request.CachedFileRequest;

/**
 * A call which contains the code to handle loading sprites. Moreover exists to separate 
 * the complexity of doing this from the SpriteManager class. 
 */
public class SpriteLoader {

	/**
	 * Load an entire set of sprites and place them in the collection. 
	 * Note that this method is intended to be used as a startup operation, and will not 
	 * synchronize its modifications of the collection for efficiency reasons. 
	 * @param set The SpriteSet to load all of the sprites for
	 * @param collection The SpriteCollection to place all of the sprites into
	 * @throws FileRequestException If the set's data could not be retrieved
	 * @throws IOException If a sprite file could not be read into an image
	 */
	public void loadEntireSet(SpriteSet set, SpriteCollection collection) throws FileRequestException, IOException {
		// Get the address to both the cache and cache table. We'll need them both anyways. 
		FileAddressBuilder addrBuilder = FileAddressBuilder.newBuilder();
		FileAddress cacheAddr = addrBuilder.createSpriteCacheAddress(set.getCanonicalName());
		FileAddress tableAddr = addrBuilder.createSpriteCacheTableAddress(set.getCanonicalName());
		
		// We want the whole thing! So get a region for the whole thing. 
		CachedFileRegionRequest req = new CachedFileRegionRequest(0, collection.getSize()-1, tableAddr, cacheAddr);
		
		// Submit it through the file server. If it fails, exception is re-thrown
		CachedFileRegion region = FileServer.instance().submitAndWaitForDescriptor(req);
		
		// Each of the files in the region becomes a new sprite
		int spriteIndex = 0;
		Iterator<CachedFile> fileIt = region.getFileIterator();
		while(fileIt.hasNext())
		{
			// This throws IOException. Arguably could catch it and just report a single sprite missing. 
			BufferedImage sprite = ImageIO.read(fileIt.next().asInputStream());
			
			collection.putSprite(spriteIndex, sprite);
			spriteIndex++;
		}
	}
	
	/**
	 * Load an image that has not already been loaded. This method will load a sprite and place it 
	 * into its collection. It will then return the image. In order to load the image, the collection 
	 * will need to be locked - so this method may block until the needed lock(s) are acquired. 
	 * For some collections, it will also spur off background loading of a block of images. 
	 * Even if this happens, the requested image will be loaded and return as soon as possible. 
	 * @param set The sprite set the needed image is in
	 * @param collection The collection of images to place the sprite into
	 * @param spriteID The ID of the sprite to be loaded
	 * @return The image of the sprite once it has been loaded
	 * @throws InterruptedException If the thread was interrupted before loading could happen
	 * @throws FileRequestException If the FileServer could not get the image files
	 * @throws IOException If the sprite file data could not be read
	 */
	public BufferedImage loadSprite(
			SpriteSet set, 
			SpriteCollection collection, 
			int spriteID) throws InterruptedException, FileRequestException, IOException
	{
		// Common code for all sprite loading, regardless of collection type. 
		// First we'll need to lock the collection so we can avoid redundant loading
		collection.lock(spriteID);
		
		// So we've now acquired the lock. Check if the image has been loaded while we were waiting for the lock
		if(collection.isLoaded(spriteID))
		{
			// Since it was - thanks other thread - we'll just release the lock and return that image. All good.
			collection.unlock(spriteID);
			return collection.getSprite(spriteID);
		}
		
		// Was not already loaded. We'll have to do the lifting. First things first, for all collection types, a single load. 
		FileAddressBuilder addrBuilder = FileAddressBuilder.newBuilder();
		FileAddress cacheAddr = addrBuilder.createSpriteCacheAddress(set.getCanonicalName());
		FileAddress tableAddr = addrBuilder.createSpriteCacheTableAddress(set.getCanonicalName());
		CachedFileRequest singleReq = new CachedFileRequest(spriteID, tableAddr, cacheAddr);
		
		FileServer.instance().submit(singleReq);
		
		// Now, I could use a conditional statement since as of writing this there are only two collection types
		// However, I'm opting to somewhat complicate that hierarchy in favor of double dispatch to mitigate later complexity
		return collection.finishLoading(this, set, spriteID, singleReq);
	}
	
	/**
	 * Picks up where <code>loadImage()</code> left off. However, it finishes loading in a manner suitable for a simple sprite collection. <br>
	 * This method exists for double dispatch reasons, rather than using a conditional statement. 
	 * @param set The sprite set the needed image is in
	 * @param collection The collection of images to place the sprite into
	 * @param spriteID The ID of the sprite to be loaded
	 * @param pendingRequest The file request grabbing the sprite's data
	 * @return The image of the sprite once it has been loaded
	 * @throws FileRequestException If the FileServer could not get the image files
	 * @throws IOException If the sprite file data could not be read
	 */
	public BufferedImage finishLoading(
			SpriteSet set, 
			SimpleSpriteCollection collection, 
			int spriteID, 
			CachedFileRequest pendingRequest) throws FileRequestException, IOException
	{
		// The pending request has been submitted. So now we wait until it's done
		pendingRequest.waitForRequest();
		
		// Have a BufferedImage created and placed into the collection based on the file data we now have
		BufferedImage sprite = ImageIO.read(pendingRequest.getFileDescriptor().asInputStream());
		collection.putSprite(spriteID, sprite);
		
		// Don't forget to unlock the collection before we're done
		collection.unlock(spriteID);
		
		return sprite;
	}
	
	/**
	 * Picks up where <code>loadImage()</code> left off. However, it finishes loading in a manner suitable for a block sprite collection. 
	 * This is unique in that it will prioritize loading and returning the requested image, but then will start background loading 
	 * a block of other sprites. The lock on the collection will not be released until the block loading is done. <br>
	 * This method exists for double dispatch reasons, rather than using a conditional statement. 
	 * @param set The sprite set the needed image is in
	 * @param collection The collection of images to place the sprite into
	 * @param spriteID The ID of the sprite to be loaded
	 * @param pendingRequest The file request grabbing the sprite's data
	 * @return The image of the sprite once it has been loaded
	 * @throws FileRequestException If the FileServer could not get the image files
	 * @throws IOException If the sprite file data could not be read
	 */
	public BufferedImage finishLoading(
			SpriteSet set, 
			BlockSpriteCollection collection, 
			int spriteID, 
			CachedFileRequest pendingRequest) throws FileRequestException, IOException
	{
		// The single request has been submitted, but we also want a block of sprites
		CachedFileRegionRequest blockReq = new CachedFileRegionRequest(
				collection.firstSpriteInBlock(collection.blockIndexWithSprite(spriteID)), 
				collection.lastSpriteInBlock(collection.blockIndexWithSprite(spriteID)), 
				pendingRequest.address());
		
		// Now, that request will be let to run on the file server we need a listener to act when its ready
		BlockLoadRequestHandler handler = new BlockLoadRequestHandler(collection, collection.firstSpriteInBlock(collection.blockIndexWithSprite(spriteID)));
		blockReq.attachListener(handler);
		
		// Submit the block request. It'll happen when it happens. Hopefully pretty soon.
		FileServer.instance().submit(blockReq);
		
		// The pending request has been submitted. So now we wait until it's done
		pendingRequest.waitForRequest();
		
		// A bit redundant, the block handler will likely do this again. But for the here and now...
		BufferedImage sprite = ImageIO.read(pendingRequest.getFileDescriptor().asInputStream());
		collection.putSprite(spriteID, sprite);
		
		return sprite;
	}

}
