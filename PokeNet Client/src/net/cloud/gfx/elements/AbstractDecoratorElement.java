package net.cloud.gfx.elements;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Optional;

import net.cloud.client.util.IteratorException;
import net.cloud.gfx.focus.FocusHandler;
import net.cloud.gfx.focus.Focusable;

public abstract class AbstractDecoratorElement implements DecoratorElement {

	/** The element we're adding drag functionality to */
	private final Element wrapped;

	public AbstractDecoratorElement(Element wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public Element getDecoratedElement() {
		return wrapped;
	}

	/**
	 * Simply draw the original element as-is
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		wrapped.drawElement(g, offsetX, offsetY);
	}

	public void clicked(Point relPoint, boolean isRightClick) {
		wrapped.clicked(relPoint, isRightClick);
	}

	public void pressed(Point relPoint) {
		wrapped.pressed(relPoint);
	}

	public void released(Point relPoint, boolean onElement) {
		wrapped.released(relPoint, onElement);
	}

	@Override
	public void keyTyped(char key) {
		wrapped.keyTyped(key);
	}

	@Override
	public void focusGained() {
		wrapped.focusGained();
	}

	@Override
	public void focusLost() {
		wrapped.focusLost();
	}

	public Element topElementAtPoint(Point point) throws IteratorException {
		// Let the wrapped element keep going
		Element top = wrapped.topElementAtPoint(point);
		
		// But if the wrapped element is on top, substitute ourself in
		return (top == wrapped) ? this : top;
	}

	public Rectangle getRectangle() {
		return wrapped.getRectangle();
	}

	public void setRectangle(Rectangle r) {
		wrapped.setRectangle(r);
	}

	public int getX() {
		return wrapped.getX();
	}

	public void setX(int x) {
		wrapped.setX(x);
	}

	public int getY() {
		return wrapped.getY();
	}

	/** @param y The Y coordinate of this element relative to its parent */
	public void setY(int y) {
		wrapped.setY(y);
	}

	/** @return Essentially the Z coordinate. Higher is on top. */
	public int getPriority() {
		return wrapped.getPriority();
	}

	/** @param priority The new priority of the element. This will not have an effect unless re-added to the container. */
	public void setPriority(int priority) {
		wrapped.setPriority(priority);
	}

	/** @return The width of this element. May be 0. */
	public int getWidth() {
		return wrapped.getWidth();
	}

	/** @param width The width of this element. May be 0. */
	public void setWidth(int width) {
		wrapped.setWidth(width);
	}

	/** @return The height of this element. May be 0. */
	public int getHeight() {
		return wrapped.getHeight();
	}

	/** @param height The height of this element. May be 0. */
	public void setHeight(int height) {
		wrapped.setHeight(height);
	}

	public FocusHandler getFocusHandler() {
		return wrapped.getFocusHandler();
	}

	public void setFocusHandler(FocusHandler focusHandler) {
		wrapped.setFocusHandler(focusHandler);
	}

	public boolean isPressedDown() {
		return wrapped.isPressedDown();
	}

	public void setPressedDown(boolean isPressedDown) {
		wrapped.setPressedDown(isPressedDown);
	}

	public Optional<Container> getParent() {
		return wrapped.getParent();
	}

	public void setParent(Container parent) {
		wrapped.setParent(parent);
	}

	public void linkNextFocusable(Focusable next) {
		wrapped.linkNextFocusable(next);
	}

	public void linkPreviousFocusable(Focusable previous) {
		wrapped.linkPreviousFocusable(previous);
	}

	public void unlink() {
		wrapped.unlink();
	}

}