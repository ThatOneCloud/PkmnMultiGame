package net.cloud.gfx.elements;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Optional;

import net.cloud.client.util.IteratorException;
import net.cloud.gfx.constants.KeyConstants;
import net.cloud.gfx.focus.FocusController;
import net.cloud.gfx.focus.FocusHandler;
import net.cloud.gfx.focus.Focusable;
import net.cloud.gfx.focus.SingleFocusHandler;

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
public abstract class Element implements Focusable {
	
	/** Consider it like a Z coordinate - i.e. how the element will stack. Higher is top. */
	private int priority;
	
	/** A rectangle defining the location and size of this element */
	protected Rectangle rectangle;
	
	/** Handles focus actions on this element */
	private FocusHandler focusHandler;
	
	/** The element which contains this one, if any */
	private Optional<Container> parent;
	
	/** Flag for whether or not the element is pressed down by the mouse */
	private boolean isPressedDown;
	
	/**
	 * Default initialization for an AbstractElement
	 */
	public Element()
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
	public Element(Container parent, int priority, int x, int y) 
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
	public Element(
			Container parent,
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
	 * @param relPoint The point of click, relative to this element
	 * @param isRightClick True if the right mouse button was clicked
	 */
	public void clicked(Point relPoint, boolean isRightClick) 
	{
		// Register focus with controller
		FocusController.instance().register(this);
	}
	
	/**
	 * The element had a mouse press happen on it. This will set a flag, so subclasses 
	 * may override this method but should call either <code>super.pressed(...)</code> or 
	 * <code>super.setPressed(true)</code>
	 * @param relPoint The point of click, relative to this element
	 */
	public void pressed(Point relPoint)
	{
		this.isPressedDown = true;
	}
	
	/**
	 * This is called when either of two things happens: The mouse button is released over the 
	 * element, or the element was the element that was currently pressed down and is being 
	 * notified that it should no longer be down. In the case that both are true, the method is only 
	 * called once - for the release on top of the element. <br>
	 * May be overridden, but should call <code>super.released</code> or <code>super.setPressed(false)</code>
	 * @param relPoint The point of release, relative to this element. Invalid and null if the release is not on the element.
	 * @param onElement True when the mouse release is over this element
	 */
	public void released(Point relPoint, boolean onElement)
	{
		// All we do is clear the flag, regardless of where the release happened
		this.isPressedDown = false;
	}
	
	/**
	 * Called when this element is being dragged. (The mouse is pressed down on the element and moving) 
	 * The provided points are relative to the space the element is in, unlike the other mouse event methods, 
	 * which are relative to <i>within</i> the element. <br>
	 * The default behavior is that an Element discards the event.
	 * @param start Where the dragging started. I.e. where the mouse was first pressed down
	 * @param withinStart The point within the element dragging started. Like with the other mouse events.
	 * @param current Where the mouse is currently at. Can be used to find a movement delta or used directly.
	 */
	public void dragged(Point start, Point withinStart, Point current) {}
	
	/** 
	 * Does two things. First, will check to see if the event is telling us 
	 * that we should change focus over to the next thing in line. This behavior can 
	 * be stopped by intercepting the CHANGE_FOCUS_NEXT event. <br>
	 * Otherwise, the event will be passed to the parent Element, if there is a parent. 
	 * If a subclass overrides this for interest in some keys but not all, 
	 * it should certainly call <code>super.keyTyped(key)</code> to allow the 
	 * other keys to propagate. (Chain of responsibility)
	 */
	@Override
	public void keyTyped(char key) 
	{
		// Take care of tab'ing over to the next focusable here
		if(key == KeyConstants.CHANGE_FOCUS_NEXT)
		{
			focusHandler.traverseNext();
		}
		// And then there's a special character for tabbing backwards
		else if(key == KeyConstants.CHANGE_FOCUS_PREVIOUS)
		{
			focusHandler.traversePrevious();
		}
		
		// Pass the event to the parent, if we aren't an orphan :(
		parent.ifPresent((p) -> p.keyTyped(key));
	}
	
	/**
	 * Informs this element and its focus handler that focus was gained. 
	 * Elements overriding this for custom behavior should call <code>super.focusGained()</code>
	 */
	@Override
	public void focusGained()
	{
		focusHandler.focusGained();
	}
	
	/**
	 * Informs this element and its focus handler that focus was lost
	 * Elements overriding this for custom behavior should call <code>super.focusGained()</code>
	 */
	@Override
	public void focusLost()
	{
		focusHandler.focusLost();
	}
	
	/**
	 * Finds the element which is showing on the point. Mostly useful for containers, which 
	 * will traverse down the child hierarchy. However, elements in general may still be composed 
	 * of multiple elements and may want to preserve their behavior independently. <br>
	 * The point is translated to the relative space for the found element, so it may be passed 
	 * to event methods that require a relative point
	 * @param point The point relative to this element
	 * @return The element showing on top. By default, returns this instance
	 * @throws IteratorException If there was an issue iterating child elements
	 */
	public Element topElementAtPoint(Point point) throws IteratorException
	{
		return this;
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

	/** @return The handler taking care of focus actions for this element */
	public FocusHandler getFocusHandler() {
		return focusHandler;
	}
	
	/** @param focusHandler The handler that will take care of focus actions for this elements */
	public void setFocusHandler(FocusHandler focusHandler) {
		this.focusHandler = focusHandler;
	}
	
	/** @return An Optional which may contain the element containing this one */
	public Optional<Container> getParent()
	{
		return parent;
	}
	
	/** @param parent The element which contains this one */
	public void setParent(Container parent)
	{
		this.parent = Optional.ofNullable(parent);
	}
	
	/** @return True if the element is currently pressed down */
	public boolean isPressedDown()
	{
		return isPressedDown;
	}
	
	/** @param isPressedDown Whether or not the element is now pressed down */
	public void isPressedDown(boolean isPressedDown)
	{
		this.isPressedDown = isPressedDown;
	}
	
}
