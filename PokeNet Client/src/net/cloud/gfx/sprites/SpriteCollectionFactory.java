package net.cloud.gfx.sprites;

import java.io.IOException;

import net.cloud.client.file.FileRequestException;
import net.cloud.client.file.address.FileAddressBuilder;
import net.cloud.client.file.request.RandomAccessFileLoadRequest;

/**
 * A simple factory class which will create SpriteCollection objects for a SpriteSet. 
 * Meant to decouple the creation of the collection from the SpriteSet enum. 
 */
public class SpriteCollectionFactory {
	
	/**
	 * Create a SpriteCollection based on the desired type. The constructor will surely be passed the size. 
	 * However, this is determined from the sprite cache file. 
	 * The constructor will also be passed the parameters defined in the set's object array. These are for the parameters 
	 * of the specific subclass. 
	 * @param set What set the collection is being created for
	 * @return A SpriteCollection to use for the given SpriteSet
	 * @throws FileRequestException The sprite set specifies a canonical name with no matching file space
	 * @throws IOException If the sprite set's files could not be read
	 */
	public static SpriteCollection createCollectionByType(SpriteSet set) throws FileRequestException, IOException
	{
		// The size needs to be determined regardless of type. It's the first 4 bytes in the cache. 
		RandomAccessFileLoadRequest req = new RandomAccessFileLoadRequest(FileAddressBuilder.newBuilder().createSpriteCacheAddress(set.getCanonicalName()));
		
		// Wait for the file to be opened
		req.waitForRequest();
		
		// Read the size from the file, then we can close it.
		int collectionSize = req.getFileDescriptor().readInt();
		req.getFileDescriptor().close();
		
		// Now based on the type of collection the set wants, we change our approach
		switch(set.getCollectionType())
		{
		default:
		case SIMPLE:
			// Only needs to know the size. So we're good
			return new SimpleSpriteCollection(collectionSize);
		case BLOCK:
			// Needs to know the block size. So the set should have that in the Object[] array
			return new BlockSpriteCollection(collectionSize, (Integer) set.getCollectionParams()[0]);
		}
	}

}
