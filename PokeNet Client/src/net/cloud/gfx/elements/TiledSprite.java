package net.cloud.gfx.elements;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import net.cloud.gfx.sprites.SpriteManager;
import net.cloud.gfx.sprites.SpriteSet;

/**
 * A sprite which has its image tiled to match the width and height of the element. 
 * The image is stored and obtained just like with a regular Sprite element, but it will 
 * be a tiled variant and any change to the dimension of the element will re-tile the image.
 * 
 * @see Sprite
 */
public class TiledSprite extends Sprite {
	
	/**
	 * Create a new tiled sprite using the default sprite set. The sprite will be tiled and/or cropped to match 
	 * the given width and height.
	 * @param id The ID of the sprite
	 * @param x X location
	 * @param y Y location
	 * @param width Width of the element
	 * @param height Height of the element
	 */
	public TiledSprite(int id, int x, int y, int width, int height)
	{
		// Delegate using default set
		this(SpriteSet.DEFAULT, id, x, y, width, height);
	}
	
	/**
	 * Create a new tiled sprite using the given sprite set. The sprite will be tiled and/or cropped to match 
	 * the given width and height.
	 * @param set The sprite set to get the image from
	 * @param id The ID of the sprite
	 * @param x X location
	 * @param y Y location
	 * @param width Width of the element
	 * @param height Height of the element
	 */
	public TiledSprite(SpriteSet set, int id, int x, int y, int width, int height)
	{
		// Delegate with the loaded image
		this(SpriteManager.instance().getSprite(set, id), x, y, width, height);
	}
	
	/**
	 * Create a new tiled sprite using the provided image. The sprite will be tiled and/or cropped to match 
	 * the given width and height.
	 * @param img An image to use for this sprite. It will be tiled to form the sprite.
	 * @param x X location
	 * @param y Y location
	 * @param width Width of the element
	 * @param height Height of the element
	 */
	public TiledSprite(BufferedImage img, int x, int y, int width, int height)
	{
		// Will only set these, priority, and parent. Setting the image is left to us.
		super(x, y, width, height);
		
		// So we go through change image with the image we've been given
		changeImage(img);
	}
	
	/**
	 * Change the sprite being shown. This is done via the sprite system, and may be done 
	 * when there is no current sprite. This will not update the dimensions of the element, 
	 * as those are defined for tiling the sprite. 
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
	 * This will not change the dimensions of the element.
	 * @param img The image that will be shown
	 */
	public void changeImage(BufferedImage img)
	{
		// We have an image, but we need to tile it to our size to make it the one we display
		this.image = SpriteManager.instance().getTiledSprite(img, getWidth(), getHeight());
	}
	
	/**
	 * Also sets the rectangle defining location and dimensions, but updates the image to 
	 * once again be tiled to the new width and height.
	 */
	@Override
	public void setRectangle(Rectangle r)
	{
		super.setRectangle(r);
		
		changeImage(this.image);
	}
	
	/**
	 * Setting the width of a tiled sprite will re-tile the image so that it 
	 * once again is tiled to the dimensions of the element
	 */
	@Override
	public void setWidth(int width)
	{
		super.setWidth(width);
		
		// This should work just fine, it'll set the sprite anew
		changeImage(this.image);
	}
	
	/**
	 * Setting the height of a tiled sprite will re-tile the image so that it 
	 * once again is tiled to the dimensions of the element
	 */
	@Override
	public void setHeight(int height)
	{
		super.setHeight(height);
		
		// This should work just fine, it'll set the sprite anew
		changeImage(this.image);
	}

}
