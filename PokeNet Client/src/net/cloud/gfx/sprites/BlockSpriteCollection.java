package net.cloud.gfx.sprites;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import net.cloud.client.file.FileRequestException;
import net.cloud.client.file.request.CachedFileRequest;

/**
 * A sprite collection which logically groups its sprites into blocks. These blocks are treated as 
 * synchronization units, so when a lock is obtained it is for that block. This is so that the entire 
 * block can safely be loaded all at once, without repeating a load. 
 */
public class BlockSpriteCollection extends SpriteCollection {
	
	/** The number of sprites that are grouped into each block */
	private int blockSize;
	
	/** Use [unfair] binary semaphores for locking */
	private Semaphore[] locks;

	/**
	 * Create a collection where the sprites will be grouped into blocks of the given size
	 * @param size The number of sprites in the collection
	 * @param blockSize The number of sprites that are grouped into each block
	 */
	public BlockSpriteCollection(int size, int blockSize) {
		super(size);
		
		this.blockSize = blockSize;
		
		initLocks();
	}
	
	/**
	 * Initialize the locks array so there are enough binary semaphores for each block
	 */
	private void initLocks()
	{
		// Just enough so that each block has a semaphore
		locks = new Semaphore[(int) Math.ceil(super.getSize() / ((double) blockSize))];
		
		// Initialize them all to binary semaphores
		for(int i = 0; i < locks.length; ++i)
		{
			locks[i] = new Semaphore(1, false);
		}
	}

	/**
	 * Acquire a lock for the logical block the sprite is a member of. 
	 * The lock will not be re-entrant, so a thread cannot obtain it twice. 
	 * @throws InterruptedException If the thread was interrupted so the lock could not be acquired
	 */
	@Override
	public void lock(int spriteIndex) throws InterruptedException {
		// Need to acquire the lock for the block
		locks[blockIndexWithSprite(spriteIndex)].acquire();
	}

	/**
	 * Release a lock which has been obtained for the block the specified sprite is a member of
	 */
	@Override
	public void unlock(int spriteIndex) {
		// Release the lock. This could actually be done by a non-owning thread... weird right
		locks[blockIndexWithSprite(spriteIndex)].release();
	}
	
	@Override
	public BufferedImage finishLoading(
			SpriteLoader loader, 
			SpriteSet set, 
			int spriteID, 
			CachedFileRequest pendingRequest) throws FileRequestException, IOException {
		return loader.finishLoading(set, this, spriteID, pendingRequest);
	}
	
	/**
	 * Determine which logical block the given sprite belongs to 
	 * @param spriteIndex The index of the sprite
	 * @return The index of the logical block
	 */
	public int blockIndexWithSprite(int spriteIndex)
	{
		return (int) Math.floor(spriteIndex / ((double) blockSize));
	}
	
	/**
	 * Obtain the index of the first sprite in a logical block. 
	 * @param blockIndex The index of the logical block
	 * @return The sprite ID of the first sprite in the given logical block
	 * @throws IllegalArgumentException If the block index is out of bounds
	 */
	public int firstSpriteInBlock(int blockIndex)
	{
		// Bound check
		if(blockIndex < 0 || blockIndex >= locks.length)
		{
			throw new IllegalArgumentException("Block index out of bounds: " + blockIndex);
		}
		
		return blockIndex * blockSize;
	}
	
	/**
	 * Obtain the index of the last sprite in a logical block
	 * @param blockIndex The index of the logical block
	 * @return The sprite ID of the last sprite in the given logical block
	 * @throws IllegalArgumentException If the block index is out of bounds
	 */
	public int lastSpriteInBlock(int blockIndex)
	{
		// Bound check
		if(blockIndex < 0 || blockIndex >= locks.length)
		{
			throw new IllegalArgumentException("Block index out of bounds: " + blockIndex);
		}
		
		int lastIdx = ((blockIndex + 1) * blockSize) - 1;
		
		// Possible the last index from above extends beyond size. Prevent that.
		return (lastIdx >= super.getSize()) ? (super.getSize() - 1) : (lastIdx);
	}

}
