package net.cloud.gfx.sprites;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.cloud.client.file.FileRequestException;
import net.cloud.client.file.request.CachedFileRequest;

/**
 * A SpriteCollection which leaves abstract class behavior as default. 
 * The lock and unlock methods will do nothing, each sprite is treated as independent 
 * and synchronization is not considered an issue for a simple collection. 
 */
public class SimpleSpriteCollection extends SpriteCollection {

	/**
	 * Create a collection of sprites where they are treated independently using the 
	 * behavior found in {@link SpriteCollection}
	 * @param size The number of sprites in the collection
	 */
	public SimpleSpriteCollection(int size)
	{
		super(size);
	}

	/**
	 * Does nothing. This collection does not treat synchronization as important 
	 * and so this method will return immediately. 
	 */
	@Override
	public void lock(int spriteIndex)
	{
	}

	/**
	 * Does nothing. Since you cannot acquire a lock, you cannot unlock.
	 */
	@Override
	public void unlock(int spriteIndex)
	{
	}

	@Override
	public BufferedImage finishLoading(
			SpriteLoader loader, 
			SpriteSet set, 
			int spriteID, 
			CachedFileRequest pendingRequest) throws FileRequestException, IOException
	{
		return loader.finishLoading(set, this, spriteID, pendingRequest);
	}

}
