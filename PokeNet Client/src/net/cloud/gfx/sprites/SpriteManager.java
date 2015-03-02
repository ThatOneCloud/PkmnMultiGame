package net.cloud.gfx.sprites;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
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
	 * Obtain a scaled sprite in the form of a BufferedImage. This relies on normal sprite retrieval. 
	 * Then if the requested size already matches the original sprite, that original is returned. 
	 * Otherwise, a scaled image is created (with some existing render hints) and returned. Leaving width 
	 * or height -1 will maintain the width or height of the original. Leaving both -1 will result in 
	 * an IllegalArgumentException. 
	 * @param set The set the sprite belongs to
	 * @param spriteID The index of the sprite within the set
	 * @param width The desire width of the scaled image. -1 maintains ratio with height
	 * @param height The desired height of the scaled image -1 maintains ratio with width
	 * @return A BufferedImage that may be the original sprite, or a new image. Right size either way.
	 * @throws IllegalArgumentException If both width and height are -1
	 */
	public BufferedImage getScaledSprite(SpriteSet set, int spriteID, int width, int height)
	{
		// Get the original
		BufferedImage original = getSprite(set, spriteID);
		
		// Get an image of the requested size
		return getScaledSprite(original, width, height);
	}
	
	/**
	 * Obtain a scaled sprite in the form of a BufferedImage. This utilizes an existing image.
	 * Then if the requested size already matches the original sprite, that original is returned. 
	 * Otherwise, a scaled image is created (with some existing render hints) and returned. Leaving width 
	 * or height -1 will maintain the width or height of the original. Leaving both -1 will result in 
	 * an IllegalArgumentException. 
	 * @param original The image to scale
	 * @param width The desire width of the scaled image. -1 maintains ratio with height
	 * @param height The desired height of the scaled image -1 maintains ratio with width
	 * @return A BufferedImage that may be the original sprite, or a new image. Right size either way.
	 * @throws IllegalArgumentException If both width and height are -1
	 */
	public BufferedImage getScaledSprite(BufferedImage original, int width, int height)
	{
		// If it's already the right size, don't even bother
		if(original.getWidth() == width && original.getHeight() == height)
		{
			return original;
		}

		if(width == -1 && height == -1)
		{
			throw new IllegalArgumentException("Width and height may not both be -1");
		}
		// Maintain same width
		else if(width == -1) {
			width = original.getWidth();
		}
		// Maintain same height
		else if(height == -1) {
			height = original.getHeight();
		}

		// Create a new BufferedImage which we'll draw the scaled one into
		BufferedImage scaledImg = new BufferedImage(width, height, original.getType());

		// Utilize Graphics2D to create a quick scaled image
		Graphics2D g2d = scaledImg.createGraphics();

		// Make sure we dispose of the graphics, in case anything happens. 
		try {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

			g2d.drawImage(original, 0, 0, width, height, null);
		} finally {
			g2d.dispose();
		}

		// Return the scaled image, which has now been drawn to
		return scaledImg;
	}
	
	/**
	 * Obtain an image which is tiled rather than scaled to the given size. If the size is smaller 
	 * than the image normally would be, it will be cropped. If the desired size is larger, the returned 
	 * image will be the sprite laid out end-to-end with cropping on the edges to fit the size.
	 * @param set The set the desired sprite is in
	 * @param spriteID The ID of the desired sprite
	 * @param width The width of the tiled image
	 * @param height The height of the tiled image
	 * @return An image of the desired size with the sprite tiled and/or cropped
	 */
	public BufferedImage getTiledSprite(SpriteSet set, int spriteID, int width, int height)
	{
		// Get the original
		BufferedImage original = getSprite(set, spriteID);

		// Tile the original
		return getTiledSprite(original, width, height);
	}
	
	/**
	 * Obtain an image which is tiled rather than scaled to the given size. If the size is smaller 
	 * than the image already is, it will be cropped. If the desired size is larger, the returned 
	 * image will be the original laid out end-to-end with cropping on the edges to fit the size.
	 * -1 May be used for a single dimension to maintain the original 
	 * @param original The image to tile
	 * @param width The width of the tiled image
	 * @param height The height of the tiled image
	 * @return An image of the desired size with the original image tiled and/or cropped
	 * @throws IllegalArgumentException If both width and height are -1
	 */
	public BufferedImage getTiledSprite(BufferedImage original, int width, int height)
	{
		// If it's already the right size, don't even bother
		if(original.getWidth() == width && original.getHeight() == height)
		{
			return original;
		}
		
		if(width == -1 && height == -1)
		{
			throw new IllegalArgumentException("Width and height may not both be -1");
		}
		// Maintain same width
		else if(width == -1) {
			width = original.getWidth();
		}
		// Maintain same height
		else if(height == -1) {
			height = original.getHeight();
		}
		
		// Create a new BufferedImage which we'll draw the tiled one into
		BufferedImage tiledImg = new BufferedImage(width, height, original.getType());

		// Utilize Graphics2D to draw the 'tiles'
		Graphics2D g2d = tiledImg.createGraphics();
		
		// Well this is convenient for cropping
		g2d.setClip(0, 0, width, height);
		
		// Figure out how many columns and rows there will end up being
		int numCols = (int) Math.ceil(height / ((double) original.getHeight()));
		int numRows = (int) Math.ceil(width / ((double) original.getWidth()));
		
		// Draw the original in rows then columns (left-to-right, then top-to-bottom)
		try {
			for(int i = 0; i < numCols; ++i)
			{
				for(int j = 0; j < numRows; j++)
				{
					g2d.drawImage(original, j * original.getWidth(), i * original.getHeight(), null);
				}
			}
		}
		// Get rid of the graphics object when we're done drawing to free up resources sooner rather than later
		finally {
			g2d.dispose();
		}
		
		return tiledImg;
	}
	
	/**
	 * Obtain a rotated sprite. The rotation is in degrees. This will always give back a new image, 
	 * rather than the original. The rotation is around the center point of the sprite. 
	 * @param set The set the original sprite is in
	 * @param spriteID The ID of the original sprite
	 * @param rotation The amount of rotation, in degrees
	 * @return A sprite rotated the given amount
	 */
	public BufferedImage getRotatedSprite(SpriteSet set, int spriteID, int rotation)
	{
		// Get the original
		BufferedImage original = getSprite(set, spriteID);

		// Rotate the original
		return getRotatedSprite(original, rotation);
	}
	
	/**
	 * Obtain a rotated sprite. The rotation is in degrees. This will always give back a new image, 
	 * rather than the original. The rotation is around the center point of the sprite. 
	 * @param original The image to rotate
	 * @param rotation The amount of rotation, in degrees
	 * @return An image rotated the given amount
	 */
	public BufferedImage getRotatedSprite(BufferedImage original, int rotation)
	{
		// Bound the rotation within 0-359 inclusive
		rotation = Math.abs(rotation) % 360;
		
		// Use a transformation for rotation. Will rotate around the center point.
		AffineTransform rotTx = AffineTransform.getRotateInstance(Math.toRadians(rotation), original.getWidth() / 2.0, original.getHeight() / 2.0);
		
		// Need to figure out what the dimensions of the new image will need to be
		Shape rotShape = rotTx.createTransformedShape(new Rectangle(original.getWidth(), original.getHeight()));
		Rectangle rotRect = rotShape.getBounds();
		
		// Translate back from the origin
		rotTx.translate(rotRect.x, -rotRect.y);
		
		// Create a new BufferedImage which we'll draw the rotated one into
		BufferedImage rotImg = new BufferedImage(rotRect.width, rotRect.height, original.getType());

		// Utilize Graphics2D to draw the 'tiles'
		Graphics2D g2d = rotImg.createGraphics();
		
		// Make sure we dispose of the graphics, in case anything happens. 
		try {
			// Draw using the transformation
			g2d.drawImage(original, rotTx, null);
		} finally {
			g2d.dispose();
		}
		
		return rotImg;
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
