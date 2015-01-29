package net.cloud.gfx.sprites;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;

import net.cloud.client.file.FileRequestException;
import net.cloud.client.logging.Logger;

/**
 * The front to the sprite operation. This class has links to the storage of sprites, and 
 * is where to go if you want to get a sprite. It internally handles loading and storage of 
 * sprites. 
 */
public class SpriteManager {
	
	/** An image to use when all else fails and a sprite cannot be obtained */
	private static final BufferedImage DEFAULT_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	
	/** Singleton instance of the sprite manager */
	private static SpriteManager instance;
	
	/** Map from the type of set to the collection of sprites */
	private EnumMap<SpriteSet, SpriteCollection> spriteMap;
	
	/** We'll create an instance of SpriteLoader, then just use that object to take care of loading intricacies */
	private SpriteLoader spriteLoader;
	
	/**
	 * Default constructor for singleton pattern. 
	 * Initializes the map of sprites and will also initiate the loading of any sprite sets that are set 
	 * to load on startup. 
	 * @throws IOException If a sprite file could not be opened
	 * @throws FileRequestException If a sprite file could not be read
	 */
	private SpriteManager() throws FileRequestException, IOException
	{
		// Put entries in map
		initMap();
		
		// Create a SpriteLoader to take care of loading complexity
		spriteLoader = new SpriteLoader();
		
		// Load sprites that are to be loaded on initialization
		loadStartupSprites();
	}
	
	/**
	 * Obtain the singleton instance of the sprite manager. If this is the first call to this method, 
	 * expect some delays as the SpriteManager loads the sprites that are set to be loaded on startup. 
	 * @return
	 */
	public static SpriteManager instance()
	{
		if(instance == null)
		{
			synchronized(SpriteManager.class)
			{
				if(instance == null)
				{
					try {
						instance = new SpriteManager();
					} catch (FileRequestException | IOException e) {
						// If we can't create this object, it's fatal to the application. 
						Logger.instance().logException("[FATAL] Could not create SpriteManager", e);
					}
				}
			}
			
		}
		
		return instance;
	}
	
	/**
	 * Convenience method for retrieving a sprite from the DEFAULT set. 
	 * If the sprite has not yet been loaded, then loading will take place - there may be a small delay in the 
	 * retrieval while this happens. 
	 * @param spriteID The index of the sprite within the set
	 * @return A BufferedImage representing the requested sprite
	 */
	public BufferedImage getSprite(int spriteID)
	{
		return getSprite(SpriteSet.DEFAULT, spriteID);
	}
	
	/**
	 * Obtain a sprite (in the form of a BufferedImage). The sprite is retrieved from the given set. 
	 * If the sprite has not yet been loaded, then loading will take place - there may be a small delay in the 
	 * retrieval while this happens. 
	 * @param set The set the sprite belongs to 
	 * @param spriteID The index of the sprite within the set
	 * @return A BufferedImage representing the requested sprite
	 */
	public BufferedImage getSprite(SpriteSet set, int spriteID)
	{
		// Check if the sprite has already been loaded
		if(spriteMap.get(set).isLoaded(spriteID))
		{
			// Since it has, we can just return it. Yay!
			return spriteMap.get(set).getSprite(spriteID);
		} else {
			// Nope. We'll need to load it before we can return it. The SpriteLoader will take care of this bundle of work. 
			try {
				return spriteLoader.loadSprite(set, spriteMap.get(set), spriteID);
			} catch (InterruptedException | FileRequestException | IOException e) {
				// Couldn't load it, either. Fall back to last resort - a default blank image
				return DEFAULT_IMAGE;
			}
		}
	}
	
	/**
	 * Initialize the sprite map. Places entries for each enumerable. 
	 * @throws FileRequestException If a sprite file could not be opened
	 * @throws IOException If a sprite file could not be read
	 */
	private void initMap() throws FileRequestException, IOException
	{
		spriteMap = new EnumMap<>(SpriteSet.class);
		
		// Place entries for each enumerable, so we don't have to check for presence
		for(SpriteSet set : SpriteSet.values())
		{
			spriteMap.put(set, SpriteCollectionFactory.createCollectionByType(set));
		}
	}
	
	/**
	 * Load all of the sprite sets that request they be loaded on application startup. 
	 * This will attempt to load every sprite all such sets, not just a subset. 
	 * @throws IOException If a sprite file could not be read into a sprite
	 * @throws FileRequestException If a set's data could not be retrieved
	 */
	private void loadStartupSprites() throws FileRequestException, IOException
	{
		// Go through all of the sets, looking for any that request to be loaded on startup
		for(SpriteSet set : SpriteSet.values())
		{
			if(set.loadOnStartup())
			{
				spriteLoader.loadEntireSet(set, spriteMap.get(set));
			}
		}
		
		Logger.writer().println("Startup-loading Sprite Sets are now loaded");
		Logger.writer().flush();
		
		// Because I wanted to write it using streams. Just because. Oh, and I didn't feel like deleting it. 
//		Arrays.stream(SpriteSet.values()).filter((set) -> set.loadOnStartup()).forEach((set) -> spriteLoader.loadEntireSet(set));
	}
	
}
