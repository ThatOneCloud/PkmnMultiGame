package net.cloud.gfx.sprites;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import net.cloud.client.file.cache.CachedFile;
import net.cloud.client.file.cache.CachedFileRegion;
import net.cloud.client.file.request.listener.FileRequestListener;
import net.cloud.client.logging.Logger;

public class BlockLoadRequestHandler implements FileRequestListener<CachedFileRegion> {
	
	/** The collection of sprites to put the sprites in */
	private SpriteCollection collection;
	
	/** The ID of the first sprite in the block that's been loaded */
	private int firstSpriteID;
	
	/**
	 * Create a new request listener which will finish loading a block of sprites when the request is 
	 * ready. The handler will release the lock on the collection, so it should already be acquired. 
	 * The thread that has the lock when this handler is called does not matter. 
	 * @param collection The collection to put the sprites in 
	 * @param firstSpriteID The index of the first sprite this handler will be loading
	 */
	public BlockLoadRequestHandler(SpriteCollection collection, int firstSpriteID) {
		this.collection = collection;
		this.firstSpriteID = firstSpriteID;
	}

	/**
	 * Called when the file data for a block of images is ready to be loaded. 
	 * Creates a BufferedImage for each and places the image into the collection. 
	 * When all of the images have been placed into the collection, the block's lock is released. 
	 */
	@Override
	public void requestReady(CachedFileRegion region) {
		// Create and place an image for all of the loaded files
		Iterator<CachedFile> fileIt = region.getFileIterator();
		int spriteIndex = firstSpriteID;
		
		while(fileIt.hasNext())
		{
			try {
				BufferedImage sprite = ImageIO.read(fileIt.next().asInputStream());
			
				collection.putSprite(spriteIndex, sprite);
				spriteIndex++;
			} catch (IOException e) {
				// There's no callback built in, this is an asynchronous call basically. Just shout something happened.
				Logger.instance().logException("Could not load sprite #" + spriteIndex, e);
				
				// I'm opting out of skimming past bad sprites, to enforce fixing the issue. So no continue.
			} finally {
				// No matter what happens, we want to release the lock that should still be held
				collection.unlock(firstSpriteID);
			}
		}
	}

}
