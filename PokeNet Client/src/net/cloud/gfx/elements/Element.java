package net.cloud.gfx.elements;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Optional;

import net.cloud.gfx.Mainframe;
import net.cloud.mmo.util.IteratorException;

/**
 * A graphic element. This is the root of the graphic element hierarchy. 
 * It's fair to compare it to Swing components, but [hopefully] lightweight 
 * and more custom-purposed. <br>
 * Each element will be responsible for drawing itself, and handling mouse and 
 * key events that occur on it. Drawing and mouse events are handled top-down 
 * from the root element, while key events mitigate bottom-up from the element 
 * that currently has focus. To clarify, the propagation of these events is primarily 
 * along the container hierarchy rather than by class. <br><br>
 * 
 * Element defines a coordinate (X,Y) 
 * and dimensions (width, height) although subclasses may call 
 * the width and height 0 if there is no need. <br>
 * This class also keeps track of whether or not it has focus. 
 * This class will mitigate key events to the parent element, 
 * if there is a parent element. Otherwise, no action will be done. <br>
 * When the element is clicked, the only action taken is to 
 * register the new focus through the controlling class.
 */
public abstract class Element {
	
	/** Consider it like a Z coordinate - i.e. how the element will stack. Higher is top. */
	private int priority;
	
	/** A rectangle defining the location and size of this element */
	protected Rectangle rectangle;
	
	/** True when the element has key focus */
	private boolean hasFocus;
	
	/** The element which contains this one, if any */
	private Optional<Element> parent;
	
	/**
	 * Default initialization for an AbstractElement
	 */
	public Element()
	{
		this(null, 0, 0, 0, 0, 0, false);
	}
	
	/**
	 * Initialize an AbstractElement so each of its fields are set to the given values. 
	 * The element will by default not have focus. The width and height will be 0. 
	 * @param parent The element containing this one, or null
	 * @param priority Essentially the Z coordinate. Higher is on top.
	 * @param x The X coordinate of this element relative to its parent
	 * @param y The Y coordinate of this element relative to its parent
	 */
	public Element(Element parent, int priority, int x, int y) {
		this(parent, priority, x, y, 0, 0, false);
	}
	
	/**
	 * Initialize an AbstractElement so each of its fields are set to the given values. 
	 * The element will by default not have focus. 
	 * @param parent The element containing this one, or null
	 * @param priority Essentially the Z coordinate. Higher is on top.
	 * @param x The X coordinate of this element relative to its parent
	 * @param y The Y coordinate of this element relative to its parent
	 * @param width The width of this element. May be 0.
	 * @param height The height of this element. May be 0.
	 */
	public Element(Element parent, int priority, int x, int y, int width, int height) {
		this(parent, priority, x, y, width, height, false);
	}
	
	/**
	 * Initialize an AbstractElement so each of its fields are set to the given values.
	 * @param parent The element containing this one, or null
	 * @param priority Essentially the Z coordinate. Higher is on top.
	 * @param x The X coordinate of this element relative to its parent
	 * @param y The Y coordinate of this element relative to its parent
	 * @param width The width of this element. May be 0.
	 * @param height The height of this element. May be 0.
	 * @param hasFocus Whether or not the element already has key focus
	 */
	public Element(
			Element parent,
			int priority,
			int x, 
			int y, 
			int width, 
			int height, 
			boolean hasFocus) {
		this.parent = Optional.ofNullable(parent);
		this.priority = priority;
		this.rectangle = new Rectangle(x, y, width, height);
		this.hasFocus = hasFocus;
	}
	
	/**
	 * Draw this element to the provided graphics object. The drawing is done 
	 * using the provided offsets, to account for the container hierarchy. Drawing coordinates 
	 * should add the offset coordinates with the element's coordinates to obtain the location 
	 * for this element to draw at. 
	 * @param g The graphics object to draw to
	 * @param offsetX How far off on the X axis this element is from the Graphic's origin
	 * @param offsetY How far off on the Y axis this element is from the Graphic's origin
	 * @throws IteratorException If there was an issue iterating child elements
	 */
	public abstract void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException;

	/** 
	 * Register that this element has focus, both with the 
	 * local variable and the controlling class. Aside from that, 
	 * no special action is taken. If a subclass does override this method, 
	 * it should certainly call <code>super.elementClicked(...)</code> or 
	 * perform the focus 'attenuation' itself. 
	 * @throws IteratorException If searching for the clicked element fails
	 */
	public void elementClicked(Point relPoint) throws IteratorException {
		setFocus(true);
		
		// Register focus with controller
		Mainframe.instance().gfx().rootPanel().getKeyEventHandler().registerFocus(this);
	}

	/** 
	 * Pass the event to the parent Element, if there is a parent. 
	 * If a subclass overrides this for interest in some keys but not all, 
	 * it can certainly call <code>super.keyTyped(key)</code> to allow the 
	 * other keys to propagate. (Chain of responsibility)
	 */
	public void keyTyped(char key) {
		// Pass the event to the parent, if we aren't an orphan :(
		parent.ifPresent((p) -> p.keyTyped(key));
	}

	/** @return The X coordinate of this element relative to its parent */
	public int getX() {
		return rectangle.x;
	}

	/** @param x The X coordinate of this element relative to its parent */
	public void setX(int x) {
		rectangle.x = x;
	}

	/** @return The Y coordinate of this element relative to its parent */
	public int getY() {
		return rectangle.y;
	}

	/** @param y The Y coordinate of this element relative to its parent */
	public void setY(int y) {
		rectangle.y = y;
	}
	
	/** @return Essentially the Z coordinate. Higher is on top. */
	public int getPriority() {
		return priority;
	}
	
	/** @param priority The new priority of the element. This will not have an effect unless re-added to the container. */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/** @return The width of this element. May be 0. */
	public int getWidth() {
		return rectangle.width;
	}

	/** @param width The width of this element. May be 0. */
	public void setWidth(int width) {
		rectangle.width = width;
	}

	/** @return The height of this element. May be 0. */
	public int getHeight() {
		return rectangle.height;
	}

	/** @param height The height of this element. May be 0. */
	public void setHeight(int height) {
		rectangle.height = height;
	}

	/** @return True if this element currently has key focus */
	public boolean hasFocus() {
		return hasFocus;
	}

	/** @param hasFocus Whether or not the element currently has key focus */
	public void setFocus(boolean hasFocus) {
		this.hasFocus = hasFocus;
	}
	
	/** @return An Optional which may contain the element containing this one */
	public Optional<Element> getParent()
	{
		return parent;
	}
	
	/** @param parent The element which contains this one */
	public void setParent(Element parent)
	{
		this.parent = Optional.ofNullable(parent);
	}
	
}
