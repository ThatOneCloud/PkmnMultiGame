package net.cloud.gfx.elements;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Optional;

import net.cloud.client.util.IteratorException;
import net.cloud.client.util.StrongIterator;
import net.cloud.gfx.constants.Priority;
import net.cloud.gfx.sprites.SpriteManager;
import net.cloud.gfx.sprites.SpriteSet;

/**
 * An Interface is an element which contains child elements and behaves just like 
 * a Container, except that it will actually draw all of the child elements as-is. 
 * An Interface also supports a background sprite, which will be drawn as.. well.. 
 * a background behind any children.
 */
public class Interface extends Container {
	
	/** Default priority of an Interface. Kinda high. */
	public static final int PRIORITY = Priority.MED_HIGH;
	
	/** Optional background image */
	private Optional<Image> background;
	
	/** 
	 * Default constructor. Creates an interface with all default parameters. 
	 * That is, at location 0,0 with size 0,0. No parent, no focus, no children, no background.
	 */
	public Interface()
	{
		super();
		
		this.background = Optional.empty();
	}
	
	/**
	 * Create an Interface that will be at the given position and be of the given size. 
	 * The priority will be default and there will initially be no parent nor background.
	 * @param x The X-axis location of this interface
	 * @param y The Y-axis location of this interface
	 * @param width The width along the x-axis
	 * @param height The height along the y-axis
	 */
	public Interface(int x, int y, int width, int height)
	{
		super(null, PRIORITY, x, y, width, height);
		
		this.background = Optional.empty();
	}
	
	/**
	 * Create an Interface that will be at the given position and be of the given size. 
	 * It will have the given priority and use the specified background image, but have no parent.
	 * @param priority The z-priority of the interface. Defines what other elements it will appear over. 
	 * @param x The X-axis location of this interface
	 * @param y The Y-axis location of this interface
	 * @param width The width along the x-axis
	 * @param height The height along the y-axis
	 */
	public Interface(int priority, int x, int y, int width, int height)
	{
		super(null, priority, x, y, width, height);
		
		this.background = Optional.empty();
	}

	/**
	 * Draw the contents of this interface to the graphics object. 
	 * If there is a background image, it will be drawn below any children. 
	 * Modifying the list of children during drawing will interrupt the process with an exception
	 * @throws IteratorException If the Container's list of children was modified during drawing
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException {
		// Draw background sprite
		background.ifPresent((img) -> g.drawImage(img, offsetX, offsetY, null));

		// Obtain an iterator and go through all of the children
		StrongIterator<Element> it = children.iterator();
		while(it.hasNext())
		{
			Element child = it.next();
			
			// Now the child will paint itself, knowing its own offset
			child.drawElement(g, offsetX + child.getX(), offsetY + child.getY());
		}
	}
	
	/**
	 * Set a background image via a request from the sprite manager. 
	 * It will appear behind all of the child elements. 
	 * To remove a background, call <code>setBackground(null)</code>
	 * @param set The set of sprites the background is from
	 * @param spriteID The ID of the sprite
	 */
	public void setBackground(SpriteSet set, int spriteID)
	{
		setBackground(SpriteManager.instance().getSprite(set, spriteID));
	}
	
	/**
	 * Set a background image to this interface. It will appear behind all of the 
	 * child elements. Set to null to remove. 
	 * @param background The image to use as a background
	 */
	public void setBackground(Image background)
	{
		this.background = Optional.ofNullable(background);
	}

}
