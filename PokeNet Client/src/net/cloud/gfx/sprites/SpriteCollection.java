package net.cloud.gfx.sprites;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.cloud.client.file.FileRequestException;
import net.cloud.client.file.request.CachedFileRequest;

/**
 * A collection of sprite images. Holds the images and provides methods to obtain them. 
 * Also provides methods to manipulate the collection. Optionally provides functionality to 
 * lock on all or part of a collection to help synchronize modifications. 
 */
public abstract class SpriteCollection {
	
	/** Store the images as an array of BufferedImage objects */
	private BufferedImage[] sprites;
	
	/**
	 * Initialize a new SpriteCollection. It will be set to hold the given number of images. 
	 * This capacity cannot be changed during runtime. The images will not be initialized, however. 
	 * Image initialization must be done else-ways. 
	 * @param size The number of sprites in the collection
	 */
	public SpriteCollection(int size)
	{
		this.sprites = new BufferedImage[size];
	}
	
	/**
	 * Optional operation. Should attempt to obtain a lock on the given sprite. The lock may extend to 
	 * a group of sprites, a single sprite, or all sprites. Blocks until the lock is acquired. 
	 * @param spriteIndex The index of the sprite that will be modified
	 * @throws InterruptedException If the lock could not be obtained due to a thread interrupt
	 */
	public abstract void lock(int spriteIndex) throws InterruptedException;
	
	/**
	 * Optional operation. Release a lock that has previously been acquired via <code>lock(int)</code>
	 * @param spriteIndex The index of the sprite that is done being modified
	 */
	public abstract void unlock(int spriteIndex);
	
	/**
	 * Double dispatch method. It should simply call <code>return loader.finishLoading(set, this, spriteID, pendingRequest);</code>
	 * @param loader The SpriteLoader currently doing the loading work, needing some help finishing it. 
	 * @param set The SpriteSet the image is being loaded from
	 * @param spriteID The ID of the sprite being loaded
	 * @param pendingRequest The file request grabbing the sprite's data
	 * @return The requested image, once it is loaded and ready
	 * @throws IOException If the FileServer could not get the image files
	 * @throws FileRequestException If the sprite file data could not be read
	 */
	public abstract BufferedImage finishLoading(
			SpriteLoader loader, 
			SpriteSet set, 
			int spriteID, 
			CachedFileRequest pendingRequest) throws FileRequestException, IOException;
	
	/**
	 * Obtain a sprite from this collection. The returned image may be null if it has not been loaded yet. 
	 * @param index The index into the collection the sprite is at. (The ID of the sprite)
	 * @return A BufferedImage for the sprite, or null if it has not been loaded
	 */
	public BufferedImage getSprite(int index)
	{
		return sprites[index];
	}
	
	/**
	 * Check to see if a sprite has been loaded yet. 
	 * @param index The index into the collection the sprite is at. (The ID of the sprite)
	 * @return True if the sprite has been loaded and placed into the collection
	 */
	public boolean isLoaded(int index)
	{
		return sprites[index] != null;
	}
	
	/**
	 * Place the given sprite into this collection at the given index. Will overwrite a current sprite. 
	 * @param index The index into the collection the sprite is at. (The ID of the sprite)
	 * @param sprite The image for the sprite
	 */
	public void putSprite(int index, BufferedImage sprite)
	{
		sprites[index] = sprite;
	}
	
	/**
	 * @return The number of sprites in the collection
	 */
	public int getSize()
	{
		return sprites.length;
	}
	
}
