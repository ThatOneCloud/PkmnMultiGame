package net.cloud.gfx.elements;

import java.awt.Graphics;

import net.cloud.client.util.IteratorException;
import net.cloud.client.util.StrongIterator;

/**
 * An Interface is an element which contains child elements and behaves just like 
 * a Container, except that it will actually draw all of the child elements as-is. 
 * An Interface also supports a background sprite, which will be drawn as.. well.. 
 * a background behind any children.
 */
public class Interface extends Container {
	
	/** Default priority of an Interface. Kinda high. */
	public static final int PRIORITY = 7;
	
	// TODO: background sprite
	
	/** 
	 * Default constructor. Creates an interface with all default parameters. 
	 * That is, at location 0,0 with size 0,0. No parent, no focus, no children, no background.
	 */
	public Interface()
	{
		super();
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
		// TODO: background image
		super(null, priority, x, y, width, height);
	}

	/**
	 * Draw the contents of this interface to the graphics object. 
	 * If there is a background image, it will be drawn below any children. 
	 * Modifying the list of children during drawing will interrupt the process with an exception
	 * @throws IteratorException If the Container's list of children was modified during drawing
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException {
		// TODO draw background sprite

		// Obtain an iterator and go through all of the children
		StrongIterator<Element> it = children.iterator();
		while(it.hasNext())
		{
			Element child = it.next();
			
			// Now the child will paint itself, knowing its own offset
			child.drawElement(g, offsetX + child.getX(), offsetY + child.getY());
		}
	}

}
