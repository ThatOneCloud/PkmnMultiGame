package net.cloud.gfx.elements;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Optional;

import net.cloud.client.util.IteratorException;
import net.cloud.gfx.constants.KeyConstants;
import net.cloud.gfx.focus.FocusController;
import net.cloud.gfx.focus.FocusHandler;
import net.cloud.gfx.focus.SingleFocusHandler;

/**
 * A functional and stand-alone implementation of the Element interface. 
 * Contains most of the private fields and methods to support a basic graphic element. 
 * The only thing that needs further specification is the draw method. Other methods 
 * may be overridden to specify special behavior, with some of these methods expecting 
 * a super call to them. This is noted in the documentation. All of the methods perform 
 * as documented in Element.
 * 
 * @see Element
 * @see FocusHandler
 */
public abstract class AbstractElement implements Element {
	
	/** Consider it like a Z coordinate - i.e. how the element will stack. Higher is top. */
	private int priority;
	
	/** A rectangle defining the location and size of this element */
	private Rectangle rectangle;
	
	/** Handles focus actions on this element */
	private FocusHandler focusHandler;
	
	/** The element which contains this one, if any */
	private Optional<ParentElement> parent;
	
	/** Flag for whether or not the element is pressed down by the mouse */
	private boolean isPressedDown;
	
	/**
	 * Default initialization for an AbstractElement
	 */
	public AbstractElement()
	{
		this(null, 0, 0, 0, 0, 0);
	}
	
	/**
	 * Initialize an AbstractElement so each of its fields are set to the given values. 
	 * The element will by default not have focus. The width and height will be 0. 
	 * @param parent The element containing this one, or null
	 * @param priority Essentially the Z coordinate. Higher is on top.
	 * @param x The X coordinate of this element relative to its parent
	 * @param y The Y coordinate of this element relative to its parent
	 */
	public AbstractElement(ParentElement parent, int priority, int x, int y) 
	{
		this(parent, priority, x, y, 0, 0);
	}
	
	/**
	 * Initialize an AbstractElement so each of its fields are set to the given values.
	 * @param parent The element containing this one, or null
	 * @param priority Essentially the Z coordinate. Higher is on top.
	 * @param x The X coordinate of this element relative to its parent
	 * @param y The Y coordinate of this element relative to its parent
	 * @param width The width of this element. May be 0.
	 * @param height The height of this element. May be 0.
	 */
	public AbstractElement(
			ParentElement parent,
			int priority,
			int x, 
			int y, 
			int width, 
			int height) {
		this.parent = Optional.ofNullable(parent);
		this.priority = priority;
		this.rectangle = new Rectangle(x, y, width, height);
		this.focusHandler = new SingleFocusHandler();
		this.isPressedDown = false;
	}
	
	/**
	 * Uses strict memory equivalence when dealing with another abstract element. 
	 * Otherwise, flips the responsibility and will check <code>obj.equals(this)</code> to have the 
	 * other object check for equivalence, instead.
	 */
	@Override
	public boolean equals(Object obj)
	{
		// When comparing to another abstract element, use strict equivalence
		if(obj instanceof AbstractElement)
		{
			return this == obj;
		}
		// Otherwise we're not sure, and thinking we're the center of everything, we make the other type determine
		else {
			return obj.equals(this);
		}
	}

	@Override
	public void clicked(Element clicked, Point relPoint, boolean isRightClick) 
	{
		// Register focus with controller
		FocusController.instance().register(clicked);
	}
	
	@Override
	public void pressed(Element pressed, Point relPoint)
	{
		this.isPressedDown = true;
	}
	
	@Override
	public void released(Element released, Point relPoint, boolean onElement)
	{
		// All we do is clear the flag, regardless of where the release happened
		this.isPressedDown = false;
	}
	
	@Override
	public void dragged(Element dragged, Point start, Point withinStart, Point current) {}
	
	@Override
	public void keyTyped(char key) 
	{
		// Take care of tab'ing over to the next focusable here
		if(key == KeyConstants.CHANGE_FOCUS_NEXT)
		{
			focusHandler.traverseNext();
			return;
		}
		// And then there's a special character for tabbing backwards
		else if(key == KeyConstants.CHANGE_FOCUS_PREVIOUS)
		{
			focusHandler.traversePrevious();
			return;
		}
		
		// Pass the event to the parent, if we aren't an orphan :(
		parent.ifPresent((p) -> p.getParent().keyTyped(key));
	}
	
	@Override
	public void focusGained()
	{
		focusHandler.focusGained();
	}

	@Override
	public void focusLost()
	{
		focusHandler.focusLost();
	}
	
	@Override
	public Element topElementAtPoint(Point point) throws IteratorException
	{
		return this;
	}
	
	@Override
	public Rectangle getRectangle() {
		return rectangle;
	}
	
	@Override
	public void setRectangle(Rectangle r) {
		this.rectangle = r;
	}

	@Override
	public int getX() {
		return rectangle.x;
	}

	@Override
	public void setX(int x) {
		rectangle.x = x;
	}

	@Override
	public int getY() {
		return rectangle.y;
	}

	@Override
	public void setY(int y) {
		rectangle.y = y;
	}
	
	@Override
	public int getPriority() {
		return priority;
	}
	
	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public int getWidth() {
		return rectangle.width;
	}

	@Override
	public void setWidth(int width) {
		rectangle.width = width;
	}

	@Override
	public int getHeight() {
		return rectangle.height;
	}

	@Override
	public void setHeight(int height) {
		rectangle.height = height;
	}

	@Override
	public FocusHandler getFocusHandler() {
		return focusHandler;
	}
	
	@Override
	public void setFocusHandler(FocusHandler focusHandler) {
		this.focusHandler = focusHandler;
	}
	
	@Override
	public Optional<ParentElement> getParent()
	{
		return parent;
	}
	
	@Override
	public void setParent(ParentElement parent)
	{
		this.parent = Optional.ofNullable(parent);
	}
	
	@Override
	public boolean isPressedDown()
	{
		return isPressedDown;
	}
	
	@Override
	public void setPressedDown(boolean isPressedDown)
	{
		this.isPressedDown = isPressedDown;
	}
	
}
