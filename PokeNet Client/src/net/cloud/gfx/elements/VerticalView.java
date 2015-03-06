package net.cloud.gfx.elements;

import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import net.cloud.client.util.IteratorException;
import net.cloud.client.util.StrongIterator;
import net.cloud.gfx.constants.Priority;
import net.cloud.gfx.elements.decorator.AbstractDecoratorElement;
import net.cloud.gfx.focus.ContainerFocusHandler;
import net.cloud.gfx.focus.FocusController;
import net.cloud.gfx.focus.Focusable;

/**
 * A container that shows all of the elements within it. Unlike the interface, which respects the location of each 
 * of the elements, this will show all of the elements in a vertical column. The elements may have some padding between them 
 * and the next, as well as have some alignment. For convenience, a view-wide policy on alignment and padding may be enforced, 
 * so all elements will have the same alignment and padding regardless of individual options. 
 * Note that added elements will have their focus chain changed to be naturally ordered within this view, and their Y coordinates 
 * will be updated to reflect their position within this view.
 */
public class VerticalView extends AbstractElement implements Container<Element> {
	
	/** Default priority of an Interface. Kinda high. */
	public static final int PRIORITY = Priority.MED_HIGH;
	
	/** Constant to check if padding has not been set */
	public static final int PADDING_NOT_SET = -1;
	
	/** Constant for a universal policy of no padding */
	public static final int NO_PADDING = 0;
	
	/** Our list of elements. We don't order by priority, so ElementList is not suitable. */
	protected List<VerticalViewElement> children;
	
	/** Default alignment within the view. If it exists, it is used. Otherwise the individual element's settings will be used. */
	private Optional<Alignment> alignment;
	
	/** Default padding within the view. If set, it is used. Otherwise, the individual element's settings will be used */
	private int padding;
	
	/**
	 * Create a new vertical view which will have the default element attributes. 
	 */
	public VerticalView()
	{
		super();
		
		// Common initialization
		init();
	}
	
	/**
	 * Create a vertical view that will be at the given location, and have the given width. 
	 * The width is pretty important... needed to interact with view elements and align them. 
	 * The height will adjust on its own as needed.
	 * @param x X location
	 * @param y Y location
	 * @param width Width
	 */
	public VerticalView(int x, int y, int width)
	{
		super(PRIORITY, x, y, width, 1);
		
		// Common initialization
		init();
	}
	
	/**
	 * Create a new vertical view that will be at the given location and be the given width. 
	 * It will also use a global policy of using the given alignment and padding. 
	 * The height will adjust on its own as needed.
	 * @param x X location
	 * @param y Y location
	 * @param width Width
	 * @param alignment Global alignment setting, null for not present
	 * @param padding Global padding setting. VerticalView.PADDING_NOT_SET for not present, else non-negative
	 */
	public VerticalView(int x, int y, int width, Alignment alignment, int padding)
	{
		super(PRIORITY, x, y, width, 1);
		
		// Before proceeding, is the padding a legal value?
		if(padding != PADDING_NOT_SET && padding < 0)
		{
			throw new IllegalArgumentException("Padding must be PADDING_NOT_SET or non-negative");
		}
		
		// Use a different kind of FocusHandler, rather than the default one Element provides
		super.setFocusHandler(new ContainerFocusHandler());

		// Linked list to deal with the frequent adding to the end and traversal
		children = new LinkedList<>();
		
		this.alignment = Optional.ofNullable(alignment);
		this.padding = padding;
	}
	
	/**
	 * Common initialization.
	 * Sets the focus handler, children, alignment, and padding.
	 */
	private void init()
	{
		// Use a different kind of FocusHandler, rather than the default one Element provides
		super.setFocusHandler(new ContainerFocusHandler());

		// Linked list to deal with the frequent adding to the end and traversal
		children = new LinkedList<>();

		// No alignment or padding to start
		alignment = Optional.empty();
		padding = PADDING_NOT_SET;
	}
	
	/**
	 * Finds the top child element at the point. The point is still translated to the child's 
	 * relative coordinate space. 
	 * @return The top child, or this container itself
	 */
	@Override
	public Element topElementAtPoint(Point point) throws IteratorException
	{
		// Look through the children (in a read-only way!)
		StrongIterator<VerticalViewElement> it = new StrongIterator<>(children.iterator());
		while(it.hasNext())
		{
			// Pull the child out. If it fails, exception will be re-thrown
			VerticalViewElement child = it.next();

			// Check if it and the point intersect. We need to use the view's x coordinate for the child
			int viewX = xPosFor(child);
			if(viewX <= point.x && point.x <= viewX + child.getWidth() && child.getY() <= point.y && point.y <= child.getY() + child.getHeight())
			{
				// It does. First one is on top, because reverse order. Adjust the point.
				point.translate(-child.getX(), -child.getY());

				// Now continue the search to the bottom
				return child.topElementAtPoint(point);
			}
		}

		// No child was found, so it must be this container
		return this;
	}

	/**
	 * Draw each of the child elements down vertically, taking padding into account. The children will have their Y coordinates 
	 * adjusted as this proceeds to reflect their current position.
	 * The X position takes only one thing into account, depending on which is present. In order: View alignment, child alignment, child X position
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		// Keep track of where we're going to draw next
		int drawPosY = 0;
		
		// Look through the children (in a read-only way!)
		StrongIterator<VerticalViewElement> it = new StrongIterator<>(children.iterator());
		while(it.hasNext())
		{
			VerticalViewElement child = it.next();
			
			// Figure out which x coordinate to use
			int drawPosX = xPosFor(child);
			
			// Draw the child wherever it is that it needs to go
			child.setY(drawPosY);
			child.drawElement(g, offsetX + drawPosX, offsetY + drawPosY);
			
			// The element's height is probably most current, now. Bump up the draw position by its height, at least.
			drawPosY += child.getHeight();
			
			// We may also need to apply padding, if there will be another element
			if(it.hasNext())
			{
				drawPosY += paddingFor(child);
			}
		}
		
		// Update our own height
		super.setHeight(drawPosY);
	}
	
	/**
	 * Add the given child to this view. The child will not have any alignment, and will not have any padding.
	 * Its focus chain will be linked to this view's natural ordering
	 */
	@Override
	public void add(Element newChild) {
		add(newChild, null, PADDING_NOT_SET);
	}
	
	/**
	 * Add the given child to this view, giving it the provided alignment but not padding
	 * Its focus chain will be linked to this view's natural ordering
	 * @param newChild The element to add to the view
	 * @param alignment The alignment this child will use if there is no view-wide setting
	 */
	public void add(Element newChild, Alignment alignment)
	{
		add(newChild, alignment, PADDING_NOT_SET);
	}
	
	/**
	 * Add the given child to this view, giving it the provided padding but no alignment. 
	 * Its focus chain will be linked to this view's natural ordering
	 * @param newChild The element to add to this view
	 * @param padding The padding this child will use if there is no view-wide setting
	 */
	public void add(Element newChild, int padding)
	{		
		add(newChild, null, padding);
	}
	
	/**
	 * Add the given child to this view, giving it the provided alignment and padding
	 * Its focus chain will be linked to this view's natural ordering
	 * @param newChild The element to add to this view
	 * @param alignment The alignment this child will use if there is no view-wide setting
	 * @param padding The padding this child will use if there is no view-wide setting
	 */
	public void add(Element newChild, Alignment alignment, int padding)
	{
		// Before proceeding, is the padding a legal value?
		if(padding != PADDING_NOT_SET && padding < 0)
		{
			throw new IllegalArgumentException("Padding must be PADDING_NOT_SET or non-negative");
		}
		
		// Wrap it in our decorator
		VerticalViewElement decChild = new VerticalViewElement(newChild, alignment, padding);
		
		// Then add that to our list of children
		children.add(decChild);
		
		// Establish the parent-child relationship
		decChild.setParent(new ParentElement(this, this::add, this::remove));
		
		// Establish the focus chain linkage
		linkAddedChild(decChild);
	}

	@Override
	public boolean remove(Element child) {
		// Attempt to remove the child. If removal was successful, we were the parent - nullify that relationship
		if(children.remove(child))
		{
			child.setParent(null);
			
			// We also adjust the focus chain to account for the removed child
			removeLink(child);
			
			return true;
		}

		// Was not successful, so return false.
		return false;
	}

	@Override
	public void removeAllChildren() {
		// For each child, attempt to remove them. If removal was successful, nullify their parent.
		children.forEach((child) -> {
			if(children.remove(child))
			{
				child.setParent(null);
			}
		});
		
		// After removing everyone, clean up the focus chain in one go
		linkAfterRemoveAll();
	}
	
	/**
	 * @return True if the view has a view-wide alignment setting
	 */
	public boolean hasAlignment()
	{
		return alignment.isPresent();
	}
	
	/**
	 * @return The view-wide alignment setting
	 */
	public Alignment getAlignment() {
		return alignment.get();
	}

	/**
	 * @param alignment The alignment to set, or null to remove the setting
	 */
	public void setAlignment(Alignment alignment) {
		this.alignment = Optional.ofNullable(alignment);
	}

	/**
	 * @return The view-wide padding setting
	 */
	public int getPadding() {
		return padding;
	}

	/**
	 * @param padding The padding to set. Either VerticalView.PADDING_NOT_SET or non-negative
	 */
	public void setPadding(int padding) {
		// Before proceeding, is the padding a legal value?
		if(padding != PADDING_NOT_SET && padding < 0)
		{
			throw new IllegalArgumentException("Padding must be PADDING_NOT_SET or non-negative");
		}
		
		this.padding = padding;
	}

	/**
	 * Figure out where to draw this child along the X axis by taking into account the 
	 * different factors that may affect it
	 * @param child The child about to be drawn
	 * @return The x position to draw the child at
	 */
	private int xPosFor(VerticalViewElement child)
	{
		// First, is there a global alignment to obey?
		if(alignment.isPresent())
		{
			return xCoordForAlignment(alignment.get(), getWidth(), child.getWidth());
		}
		// What about some alignment on the element itself?
		else if(child.hasAlignment())
		{
			return xCoordForAlignment(child.getAlignment(), getWidth(), child.getWidth());
		}
		// If nothing else, the x coordinate of the child
		else {
			return child.getX();
		}
	}
	
	/**
	 * Find the x coordinate drawing should take place from given an alignment factor. 
	 * @param alignment The alignment in use
	 * @param availableWidth The parent width
	 * @param elementWidth The child width
	 * @return The x coordinate to draw at
	 */
	private int xCoordForAlignment(Alignment alignment, int availableWidth, int elementWidth)
	{
		switch(alignment)
		{
		
		// Well this is easy. We just put it all the way left.
		case LEFT:
			return 0;
			
		// Align the center lines of each
		case CENTER:
			return (int) ((availableWidth / 2.0) - (elementWidth / 2.0));
			
		// Put it as far right as the widths will allow
		case RIGHT:
			return availableWidth - elementWidth;
			
		// Because why not? Heh
		default:
			throw new AssertionError("VerticalView.xCoordForAlignment() reached default case. Unhandled enum value: " + alignment.toString());
		}
	}
	
	/**
	 * Figure out how much padding to use for the given child element. 
	 * This is the padding between this child and the next. 
	 * Takes into consideration which padding is present and has priority
	 * @param child The element being drawn
	 * @return The padding between the child and its next sibling
	 */
	private int paddingFor(VerticalViewElement child)
	{
		// First, is there a global padding setting to obey?
		if(padding != PADDING_NOT_SET)
		{
			return padding;
		}
		// What about some padding setting on the element itself?
		else if(child.getPadding() != PADDING_NOT_SET)
		{
			return child.getPadding();
		}
		// If nothing else, there is no padding
		else {
			return NO_PADDING;
		}
	}
	
	/**
	 * Link a recently added child into this view. The assumption is that the child was added 
	 * to the end of the view. The links will be created between this child and either the previous 
	 * child or the view itself, and the child and the focusable after this view if there is one
	 * @param child
	 */
	private void linkAddedChild(VerticalViewElement child)
	{
		ContainerFocusHandler handler = (ContainerFocusHandler) super.getFocusHandler();
		
		// First, the child and previous. Is there a sibling behind it?
		if(children.get(0).equals(child))
		{
			// The child was the first, so there is no sibling behind it. Link to the view itself
			FocusController.link(this, child);
			FocusController.link(child, handler.getNext());
			
			// Tell the handler what the first (and last) child is
			handler.setFirst(child);
			handler.setLast(child);
		}
		else {
			// The child was not first, so link to the child behind it (2nd to last)
			Focusable previousSibling = handler.getLast();
			
			// Is that sibling linked forward to something? Whatever it is, is what we're going to link to
			FocusController.link(child, previousSibling.getFocusHandler().getNext());
			FocusController.link(previousSibling, child);
			
			// Tell the handler what the last child is, now
			handler.setLast(child);
		}
	}
	
	/**
	 * Maintain the focus link for the child being removed. 
	 * This will maintain the first and last child attributes of the focus handler, 
	 * as well as maintaining the links by merging the child's previous and next focusables.
	 * @param child The child being removed
	 */
	private void removeLink(Element child)
	{
		ContainerFocusHandler handler = (ContainerFocusHandler) super.getFocusHandler();
		
		// To maintain first and last child attributes, are we either of those? (Having a child to remove implies a first and last exist)
		if(handler.getFirst().equals(child))
		{
			handler.setFirst(child.getFocusHandler().getNext());
		}
		
		if(handler.getLast().equals(child))
		{
			handler.setLast(child.getFocusHandler().getPrevious());
		}
		
		// Now maintain the links. Fortunately the FocusController code takes care of this
		FocusController.unlink(child.getFocusHandler().getPrevious(), child, child.getFocusHandler().getNext());
	}
	
	/**
	 * Maintain the focus handler's links after removing all children
	 */
	private void linkAfterRemoveAll()
	{
		ContainerFocusHandler handler = (ContainerFocusHandler) super.getFocusHandler();
		
		// We're still linked from our own previous to us, but our last child was connect to our next. 
		// We want to take over that, being ourselves linked to our next
		FocusController.link(this, handler.getLast().getFocusHandler().getNext());
		
		// And, well, we no longer have children to have a first or last
		handler.setFirst(null);
		handler.setLast(null);
	}
	
	/**
	 * Link this container to another Focusable so that after focus has traversed through 
	 * this container's children, it will then go onto the provided Focusable. 
	 * If there is no child to traverse to, then the link will instead be from this container itself 
	 * to the provided Focusable to preserve the traversal chain. 
	 */
	@Override
	public void linkNextFocusable(Focusable current, Focusable next)
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
			FocusController.link(current, next);
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
	public void linkPreviousFocusable(Focusable current, Focusable previous)
	{
		ContainerFocusHandler handler = (ContainerFocusHandler) super.getFocusHandler();
		
		// Set the previous so that it's there
		handler.setPrevious(previous);
		
		// We link from the container previous Focusable to the container 
		FocusController.link(previous, current);
		
		// and then from the container to the first child
		if(handler.getFirst() != null)
		{
			FocusController.link(this, handler.getFirst());
		}
	}
	
	/**
	 * Decorator that adds two fields to an element that's been added to the view. 
	 * It adds alignment and padding. These may or may not be set.
	 */
	private static class VerticalViewElement extends AbstractDecoratorElement {
		
		/** The alignment, which may or may not be set */
		private Optional<Alignment> alignment;
		
		/** The padding, which may or may not be set (using VerticalView.PADDING_NOT_SET) */
		private int padding;

		/**
		 * Create a new decorated element which will have neither alignment nor padding set
		 * @param wrapped The element being decorated
		 */
		public VerticalViewElement(Element wrapped)
		{
			super(wrapped);
			
			this.alignment = Optional.empty();
			this.padding = PADDING_NOT_SET;
		}
		
		/**
		 * Create a new custom decorator which will have the given parameters
		 * @param wrapped The element being decorated
		 * @param alignment The alignment. May be null
		 * @param padding The padding. May be VerticalView.PADDING_NOT_SET
		 */
		public VerticalViewElement(Element wrapped, Alignment alignment, int padding)
		{
			super(wrapped);
			
			this.alignment = Optional.ofNullable(alignment);
			this.padding = padding;
		}
		
		/**
		 * @return True if alignment has been defined for this element
		 */
		public boolean hasAlignment()
		{
			return alignment.isPresent();
		}
		
		/**
		 * Get the alignment. Will throw an exception if it has not been set.
		 * @return The alignment that has been set for this element. 
		 */
		public Alignment getAlignment()
		{
			return alignment.get();
		}
		
		/**
		 * @return The padding setting for this child
		 */
		public int getPadding()
		{
			return padding;
		}
		
	}

}
