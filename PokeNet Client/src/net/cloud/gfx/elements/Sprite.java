package net.cloud.gfx.elements;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import net.cloud.client.util.IteratorException;
import net.cloud.gfx.constants.Priority;
import net.cloud.gfx.sprites.SpriteManager;
import net.cloud.gfx.sprites.SpriteSet;

/**
 * A Sprite is an image being shown in the game. The properties of the image 
 * are defined by the image itself, such as transparency. But of course, a sprite 
 * can be moved around and changed. This is not for animations, however. 
 */
public class Sprite extends AbstractElement {
	
	/** Default priority of Sprite. Sorta low. */
	public static final int PRIORITY = Priority.MED_LOW;
	
	/** The image to draw */
	protected BufferedImage image;
	
	/**
	 * Create a new Sprite element for showing an image. The shown image will be from the default set 
	 * and have the default priority. 
	 * @param id The sprite ID
	 * @param x X position
	 * @param y Y position
	 */
	public Sprite(int id, int x, int y)
	{
		this(id, x, y, PRIORITY);
	}
	
	/**
	 * Create a new Sprite element for showing an image. The shown image will be from the given set 
	 * and have the default priority. 
	 * @param set The set the sprite is in
	 * @param id The sprite ID
	 * @param x X position
	 * @param y Y position
	 */
	public Sprite(SpriteSet set, int id, int x, int y)
	{
		this(set, id, x, y, PRIORITY);
	}
	
	/**
	 * Create a new Sprite element for showing an image. The shown image will be from the given set 
	 * and have the given priority. 
	 * @param set The set the sprite is in
	 * @param id The sprite ID
	 * @param x X position
	 * @param y Y position
	 * @param priority The z-priority
	 */
	public Sprite(SpriteSet set, int id, int x, int y, int priority)
	{
		super(null, priority, x, y);
		
		// Set sprite, width, and height
		changeImage(set, id);
	}
	
	/**
	 * Create a new Sprite element for showing an image. This constructor accepts an actual Image, 
	 * and so can be used with any image rather than a sprite from the sprite system. 
	 * @param img The image to use for the sprite
	 * @param x X position
	 * @param y Y position
	 * @param priority The z-priority
	 */
	public Sprite(BufferedImage img, int x, int y, int priority)
	{
		super(null, priority, x, y);
		
		// Set the sprite, width, and height
		changeImage(img);
	}
	
	/**
	 * A constructor for subclasses. Minimally responsible. Sets the given parameters, parent, and priority. 
	 * Parent is left null and the priority is left as the constant default. 
	 * Subclasses must set the image on their own.
	 * @param x X-position
	 * @param y Y-position 
	 * @param width Width of the element
	 * @param height Height of the element
	 */
	protected Sprite(int x, int y, int width, int height)
	{
		super(null, PRIORITY, x, y, width, height);
	}

	/**
	 * Draw the image to the provided graphics object. The image is drawn where the top 
	 * left corner is the coordinate to draw from. 
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException {
		// Draw the image
		g.drawImage(image, offsetX, offsetY, null);
	}
	
	/**
	 * Change the sprite being shown. This is done via the sprite system, and may be done 
	 * when there is no current sprite. This will also update the width and height to be the 
	 * same as the image's. 
	 * @param set The set the sprite is in
	 * @param id The ID of the sprite
	 */
	public void changeImage(SpriteSet set, int id)
	{
		// Grab the image from the SpriteManager
		changeImage(SpriteManager.instance().getSprite(set, id));
	}
	
	/**
	 * Change the image being shown. This is done directly via a supplied image. 
	 * This will also update the width and height to be the same as the image's.
	 * @param img The image that will be shown
	 */
	public void changeImage(BufferedImage img)
	{
		// Set the image and update dimensions
		this.image = img;
		
		super.setWidth(image.getWidth());
		super.setHeight(image.getHeight());
	}

}
