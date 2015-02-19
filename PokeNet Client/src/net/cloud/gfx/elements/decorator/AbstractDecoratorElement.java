package net.cloud.gfx.elements.decorator;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Optional;

import net.cloud.client.util.IteratorException;
import net.cloud.gfx.elements.Element;
import net.cloud.gfx.elements.ParentElement;
import net.cloud.gfx.focus.FocusHandler;
import net.cloud.gfx.focus.Focusable;

/**
 * A base class that DecoratorElements may use, that already has all of the basic 
 * functionality implemented. This is so that subclasses only need to override 
 * the methods they are interested in. <br>
 * This class contains the wrapped element. All of the Element methods are implemented 
 * so that they delegate through to the wrapped element. Subclasses may override them to 
 * add functionality, still utilizing the wrapped element within them.  
 */
public abstract class AbstractDecoratorElement implements DecoratorElement {

	/** The element we're adding drag functionality to */
	private final Element wrapped;

	/**
	 * Constructor which will set the wrapped element. It may not be changed.
	 * @param wrapped The element to decorate
	 */
	public AbstractDecoratorElement(Element wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public Element getDecoratedElement() {
		return wrapped;
	}
	
	@Override
	public boolean equals(Object obj) {
		// We're comparing to another decorator. Our goal is to strip it down (the obj parameter) to the bottom element
		if(obj instanceof DecoratorElement)
		{
			return this.equals(((DecoratorElement) obj).getDecoratedElement());
		}
		// We're comparing ourselves to... not another decorator. So we strip ourself down
		else {
			return wrapped.equals(obj);
		}
	}

	/**
	 * Simply draw the original element as-is
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		wrapped.drawElement(g, offsetX, offsetY);
	}

	/**
	 * Delegate to the wrapped element. 
	 * Note: This is not called when a child element is clicked
	 */
	@Override
	public void clicked(Element clicked, Point relPoint, boolean isRightClick) {
		wrapped.clicked(clicked, relPoint, isRightClick);
	}

	/**
	 * Delegate to the wrapped element
	 * Note: This is not called when a child element is pressed
	 */
	@Override
	public void pressed(Element pressed, Point relPoint) {
		wrapped.pressed(pressed, relPoint);
	}

	/**
	 * Delegate to the wrapped element
	 * Note: This is not called when a child element is released
	 */
	@Override
	public void released(Element released, Point relPoint, boolean onElement) {
		wrapped.released(released, relPoint, onElement);
	}
	
	/**
	 * Delegate to the wrapped element
	 * Note: This is not called when a child element is dragged
	 */
	@Override
	public void dragged(Element dragged, Point start, Point withinStart, Point current) {
		wrapped.dragged(dragged, start, withinStart, current);
	}

	/**
	 * Delegate to the wrapped element
	 * Note: This is not called when a child element transfers responsibility
	 */
	@Override
	public void keyTyped(char key) {
		wrapped.keyTyped(key);
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void focusGained() {
		wrapped.focusGained();
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void focusLost() {
		wrapped.focusLost();
	}

	/**
	 * The element is still essentially the top. If the top element is not the one 
	 * that this decorator is wrapped around, then that element instance is returned. 
	 * If the top element is the one this element is wrapped around, then a reference 
	 * to this decorator is returned instead.
	 */
	@Override
	public Element topElementAtPoint(Point point) throws IteratorException {
		// Let the wrapped element keep going
		Element top = wrapped.topElementAtPoint(point);
		
		// But if the wrapped element is on top, substitute ourself in
		return (top == wrapped) ? this : top;
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public Rectangle getRectangle() {
		return wrapped.getRectangle();
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void setRectangle(Rectangle r) {
		wrapped.setRectangle(r);
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public int getX() {
		return wrapped.getX();
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void setX(int x) {
		wrapped.setX(x);
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public int getY() {
		return wrapped.getY();
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void setY(int y) {
		wrapped.setY(y);
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public int getPriority() {
		return wrapped.getPriority();
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void setPriority(int priority) {
		wrapped.setPriority(priority);
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public int getWidth() {
		return wrapped.getWidth();
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void setWidth(int width) {
		wrapped.setWidth(width);
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public int getHeight() {
		return wrapped.getHeight();
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void setHeight(int height) {
		wrapped.setHeight(height);
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public FocusHandler getFocusHandler() {
		return wrapped.getFocusHandler();
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void setFocusHandler(FocusHandler focusHandler) {
		wrapped.setFocusHandler(focusHandler);
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public boolean isPressedDown() {
		return wrapped.isPressedDown();
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void setPressedDown(boolean isPressedDown) {
		wrapped.setPressedDown(isPressedDown);
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public Optional<ParentElement> getParent() {
		return wrapped.getParent();
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void setParent(ParentElement parent) {
		wrapped.setParent(parent);
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void linkNextFocusable(Focusable current, Focusable next) {
		wrapped.linkNextFocusable(current, next);
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void linkPreviousFocusable(Focusable current, Focusable previous) {
		wrapped.linkPreviousFocusable(current, previous);
	}

	/**
	 * Delegate to the wrapped element
	 */
	@Override
	public void unlink(Focusable current) {
		wrapped.unlink(current);
	}

}