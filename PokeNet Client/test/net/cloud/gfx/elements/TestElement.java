package net.cloud.gfx.elements;

import java.awt.Graphics;

/**
 * Element useful for testing. Provide initialization information, 
 * but beyond that it does not draw anything or override anything.
 */
public class TestElement extends Element {
	
	/**
	 * Initialize an Element so each of its fields are set to the given values.
	 * @param priority Essentially the Z coordinate. Higher is on top.
	 * @param x The X coordinate of this element relative to its parent
	 * @param y The Y coordinate of this element relative to its parent
	 * @param width The width of this element. May be 0.
	 * @param height The height of this element. May be 0.
	 */
	public TestElement(int priority, int x, int y, int width, int height) {
		super(null, priority, x, y, width, height, false);
	}
	
	/**
	 * Initialize an Element so each of its fields are set to the given values.
	 * @param parent The element containing this one, or null
	 * @param priority Essentially the Z coordinate. Higher is on top.
	 * @param x The X coordinate of this element relative to its parent
	 * @param y The Y coordinate of this element relative to its parent
	 * @param width The width of this element. May be 0.
	 * @param height The height of this element. May be 0.
	 * @param hasFocus Whether or not the element already has key focus
	 */
	public TestElement(Element parent, int priority, int x, int y, int width,
			int height, boolean hasFocus) {
		super(parent, priority, x, y, width, height, hasFocus);
	}

	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) {
		// Meh.
	}

}
