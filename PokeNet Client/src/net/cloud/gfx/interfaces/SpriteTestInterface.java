package net.cloud.gfx.interfaces;

import net.cloud.gfx.elements.Interface;
import net.cloud.gfx.elements.Sprite;
import net.cloud.gfx.sprites.SpriteSet;

/**
 * Interface to make sure sprites are loading. 
 * Shows test sprites in four corners and in middle. 
 */
public class SpriteTestInterface extends Interface {
	
	/**
	 * Create interface
	 * @param width
	 * @param height
	 */
	public SpriteTestInterface(int width, int height)
	{
		super(0, 0, width, height);
		
		// Add sprites to corners
		add(new Sprite(SpriteSet.TEST, 0, 0, 0));
		add(new Sprite(SpriteSet.TEST, 1, width-50, 0));
		add(new Sprite(SpriteSet.TEST, 2, 0, height-50));
		add(new Sprite(SpriteSet.TEST, 3, width-50, height-50));
		add(new Sprite(SpriteSet.TEST, 4, (width / 2) - 25, (height / 2) - 25));
	}

}
