package net.cloud.gfx.elements;

import java.awt.Point;

import net.cloud.client.util.IteratorException;
import net.cloud.client.util.StrongIterator;
import net.cloud.gfx.focus.ContainerFocusHandler;
import net.cloud.gfx.focus.FocusController;
import net.cloud.gfx.focus.Focusable;

/**
 * This abstract class is an element which contains other elements. 
 * The contained elements will not be drawn by this element - in fact this element 
 * does not define any drawing code.  When a mouse click is received, the click 
 * is passed down to the top element which intersects the click's position. If there is 
 * no other element, the call will instead propagate up the class hierarchy. 
 * (Primarily through container, secondarily through the class hierarchy)
 */
public abstract class Container extends Element {
	
	/** A list of elements contained in this element */
	protected ElementList children;
	
	/** Initialize the container to be empty, using default constructor options */
	public Container()
	{
		super();
		
		// Use a different kind of FocusHandler, rather than the default one Element provides
		super.setFocusHandler(new ContainerFocusHandler());
		
		children = new ElementList();
	}
	
	/**
	 * Initialize a Container so each of its fields are set to the given values. 
	 * The element will by default not have focus. The width and height will be 0. 
	 * @param parent The element containing this one, or null
	 * @param priority Essentially the Z coordinate. Higher is on top.
	 * @param x The X coordinate of this element relative to its parent
	 * @param y The Y coordinate of this element relative to its parent
	 */
	public Container(Element parent, int priority, int x, int y) {
		super(parent, priority, x, y, 0, 0, false);
		
		// Use a different kind of FocusHandler, rather than the default one Element provides
		super.setFocusHandler(new ContainerFocusHandler());
		
		children = new ElementList();
	}
	
	/**
	 * Initialize a Container so each of its fields are set to the given values. 
	 * The element will by default not have focus. 
	 * @param parent The element containing this one, or null
	 * @param priority Essentially the Z coordinate. Higher is on top.
	 * @param x The X coordinate of this element relative to its parent
	 * @param y The Y coordinate of this element relative to its parent
	 * @param width The width of this element. May be 0.
	 * @param height The height of this element. May be 0.
	 */
	public Container(Element parent, int priority, int x, int y, int width, int height) {
		super(parent, priority, x, y, width, height, false);
		
		// Use a different kind of FocusHandler, rather than the default one Element provides
		super.setFocusHandler(new ContainerFocusHandler());
		
		children = new ElementList();
	}
	
	/**
	 * Initialize a Container so each of its fields are set to the given values.
	 * @param parent The element containing this one, or null
	 * @param priority Essentially the Z coordinate. Higher is on top.
	 * @param x The X coordinate of this element relative to its parent
	 * @param y The Y coordinate of this element relative to its parent
	 * @param width The width of this element. May be 0.
	 * @param height The height of this element. May be 0.
	 * @param hasFocus Whether or not the element already has key focus
	 */
	public Container(
			Element parent,
			int priority,
			int x, 
			int y, 
			int width, 
			int height, 
			boolean hasFocus) {
		super(parent, priority, x, y, width, height, hasFocus);
		
		// Use a different kind of FocusHandler, rather than the default one Element provides
		super.setFocusHandler(new ContainerFocusHandler());
		
		children = new ElementList();
	}
	
	/** 
	 * Propagates the click to the top contained element that intersects the location 
	 * of the click. The coordinates passed to the contained element become relative to 
	 * that element, not this container.  If there is no such element, then the focus 
	 * will be set to this container element.
	 * @throws IteratorException If while looking for an intersecting child, iteration fails
	 */
	@Override
	public void elementClicked(Point relPoint) throws IteratorException {
		// Look through the children (in a read-only way!)
		StrongIterator<Element> it = children.reverseIterator();
		while(it.hasNext())
		{
			// Pull the child out. If it fails, exception will be re-thrown
			Element child = it.next();
			
			// Check if it and the click intersect
			if(child.rectangle.contains(relPoint))
			{
				// It does. First one is on top, because reverse order. Adjust the point.
				relPoint.translate(-getX(), -getY());
				
				// Now tell the child its been clicked
				child.elementClicked(relPoint);
				
				// Return so that focus doesn't end up given to this container
				return;
			}
		}
		
		// No child was found, so this will give this Container key focus
		super.elementClicked(relPoint);
	}
	
	/**
	 * Add an element to this container. Thread-safe. 
	 * Assigns the parent of the element to be this container. 
	 * @param newChild The element to add to this container
	 */
	public void add(Element newChild)
	{
		children.add(newChild);
		
		// Establish the parent-child relationship. ElementList itself will not take care of this.
		newChild.setParent(this);
	}
	
	/**
	 * Remove the given element from this container. 
	 * The parent of the element is reset to null, regardless of result. 
	 * @param child The element to remove from this Container
	 * @return True if the element was indeed removed
	 */
	public boolean remove(Element child)
	{
		// Disown the child
		child.setParent(null);
		
		return children.remove(child);
	}
	
	/**
	 * Remove all of the children from this Container. 
	 * Each of the children has its parent set to null. 
	 */
	public void removeAllChildren()
	{
		// Disown all of the children before going and removing them
		children.forEach((child) -> child.setParent(null));
		
		children.removeAll();
	}
	
	/**
	 * Tell this container that the given element (which should be a child of this container) 
	 * is the one which will receive focus first, upon traversing forward. This should be done 
	 * before linking this container to another Focusable. 
	 * @param firstChild The child to traverse to first
	 */
	public void setFirstFocusTraversalChild(Element firstChild)
	{
		((ContainerFocusHandler) super.getFocusHandler()).setFirst(firstChild);
	}
	
	/**
	 * Tell this container that the given element (which should be a child of this container) 
	 * is the one which will receive focus last, upon traversing forward. This should be done 
	 * before linking this container to another Focusable. 
	 * @param lastChild The child to traverse to last
	 */
	public void setLastFocusTraversalChild(Element lastChild)
	{
		((ContainerFocusHandler) super.getFocusHandler()).setLast(lastChild);
	}
	
	/**
	 * Link this container to another Focusable so that after focus has traversed through 
	 * this container's children, it will then go onto the provided Focusable. 
	 * If there is no child to traverse to, then the link will instead be from this container itself 
	 * to the provided Focusable to preserve the traversal chain. 
	 */
	@Override
	public void linkNextFocusable(Focusable next)
	{
		ContainerFocusHandler handler = (ContainerFocusHandler) super.getFocusHandler();
		
		// Set the next one up in the handler, it can be used to skip forward when there aren't children
		handler.setNext(next);
		
		// Link the last child and next focusable
		if(handler.getLast() != null)
		{
			FocusController.link(handler.getLast(), next);
		}
		// There's no last child, so link the container and next instead to preserve chain
		else {
			FocusController.link(this, next);
		}
	}
	
	/**
	 * Link this container to another Focusable so that the given Focusable will lead 
	 * to this container. Rather than leading directly to the first child, the link will be made between 
	 * the previous and this container. The container will then link to the first child, so it's sort 
	 * of like a two-step connection. If there are no children, then it won't make the second link 
	 * and the traversal will just be one step. 
	 */
	@Override
	public void linkPreviousFocusable(Focusable previous)
	{
		ContainerFocusHandler handler = (ContainerFocusHandler) super.getFocusHandler();
		
		// Set the previous so that it's there
		handler.setPrevious(previous);
		
		// We link from the container previous Focusable to the container 
		FocusController.link(previous, this);
		
		// and then from the container to the first child
		if(handler.getFirst() != null)
		{
			FocusController.link(this, handler.getFirst());
		}
	}

}
