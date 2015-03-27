package net.cloud.gfx.elements;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	
	/** Flag that indicates whether or not the child elements will have their height changed dynamically */
	private boolean optimizedDrawingEnabled;
	
	/** Queue of elements that need to be added, if optimization is enabled */
	private Queue<VerticalViewElement> addQueue;
	
	/** Queue of elements that need to be added, if optimization is enabled */
	private Queue<Element> removeQueue;
	
	/** The clip rectangle drawing will use, when optimized drawing is enabled */
	private Rectangle clipRect;
	
	/** The index that was last used as an optimized starting point */
	private int prevFirstDrawIdx;
	
	/** Flag, set to true once drawing starts, cannot be unset */
	private boolean drawingStarted;
	
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
		children = new ArrayList<>();
		
		this.alignment = Optional.ofNullable(alignment);
		this.padding = padding;
		
		optimizedDrawingEnabled = false;
		addQueue = null;
		removeQueue = null;
		clipRect = null;
		prevFirstDrawIdx = -1;
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
		children = new ArrayList<>();

		// No alignment or padding to start
		alignment = Optional.empty();
		padding = PADDING_NOT_SET;
		
		// Optimization is not enabled by default
		optimizedDrawingEnabled = false;
		addQueue = null;
		removeQueue = null;
		clipRect = null;
		prevFirstDrawIdx = -1;
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
		// Well, drawing has started. No harm in setting again if it's already been done
		drawingStarted = true;
		
		// Which drawing algorithm are we going to use?
		if(optimizedDrawingEnabled)
		{
			optimizedDraw(g, offsetX, offsetY);
		}
		else {
			simpleDraw(g, offsetX, offsetY);
		}
	}
	
	/**
	 * Simply draw all of the children in the view
	 * @param g The graphics object
	 * @param offsetX Offset X
	 * @param offsetY Offset Y
	 * @throws IteratorException If iteration fails
	 */
	private void simpleDraw(Graphics g, int offsetX, int offsetY) throws IteratorException
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
	 * Draw only the children that are currently in view. 
	 * This means optimized drawing must be enabled.  It also relies on the graphics clip, so if that 
	 * is not set, then this will revert back to the simple algorithm.
	 * @param g The graphics object
	 * @param offsetX Offset X
	 * @param offsetY Offset Y
	 * @throws IteratorException If the simple draw has failed due to an IteratorException
	 */
	private void optimizedDraw(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		// Reset the rectangle, and then have it become the bounds
		clipRect.setBounds(0, 0, 0, 0);
		g.getClipBounds(clipRect);
		
		// If there was no graphics clip, then optimization won't really work. Go back to the simple approach.
		if(clipRect.isEmpty())
		{
			simpleDraw(g, offsetX, offsetY);
			return;
		}
		
		// Start out by drawing the children we have. But where do we start?
		int topViewableY = clipRect.y - offsetY;
		int bottomViewableY = topViewableY + clipRect.height;
		
		// Now which child needs to be drawn first?
		int indexOfFirstChildToDraw = findFirstDrawIndex(topViewableY);
			
		// We guess that next time around, the same index is gonna be pretty close
		prevFirstDrawIdx = indexOfFirstChildToDraw;
		
		// Draw all of our children that will show
		drawChildrenWithinView(g, offsetX, offsetY, bottomViewableY, indexOfFirstChildToDraw);
		
		// Now that a drawing cycle is done, update our children with new changes - removals first
		reconcileRemovals();
		
		// Next up is taking care of all new additions
		reconcileAdditions(g);
	}

	/**
	 * Find which child index drawing should start at, so that all children in view are shown. 
	 * Only useful during optimized drawing.
	 * @param topViewableY The top Y coordinate, relative to us, drawing will happen at
	 * @return The index of the first child to draw
	 */
	private int findFirstDrawIndex(int topViewableY)
	{
		// Do we not have any clues to work with?
		if(prevFirstDrawIdx < 0 || children.size() <= prevFirstDrawIdx)
		{
			// Then we'll just look through everything. (I suppose this could become a binary search if need be)
			return findFirstDrawIndexIteratively(topViewableY);
		}
		// Oh cool, we can use the previous index to take a guess
		else {
			return findFirstDrawIndexUsingPrevious(topViewableY);
		}
	}

	/**
	 * Iteratively looks through all children to find which index to start drawing at.
	 * Only useful during optimized drawing.
	 * @param topViewableY The top Y coordinate, relative to us, drawing will happen at
	 * @return The index of the first child to draw
	 */
	private int findFirstDrawIndexIteratively(int topViewableY)
	{
		int indexOfFirstChildToDraw = -1;
		
		// We're going to iterate all children
		Iterator<VerticalViewElement> it = children.iterator();
		while(it.hasNext())
		{
			VerticalViewElement child = it.next();
			int childHeightSum = child.getHeightSum();

			// Is this child right at the top of view?
			if(childHeightSum == topViewableY)
			{
				// Well then the current location in the iterator is good to use
				indexOfFirstChildToDraw++;
				break;
			}
			// Is the child in view, but not right at the top?
			else if(childHeightSum > topViewableY)
			{
				// So this means we grab this one and the one before it. Is there one before it, though?
				if(indexOfFirstChildToDraw == -1)
				{
					// Nope, the first one to draw is the very first child
					indexOfFirstChildToDraw = 0;
				}
				else {
					// Yes, so the first to draw is whatever came before the current child (no change to index)
				}
				break;
			}
			// The child must not even be in view. Continue forward
			else {
				indexOfFirstChildToDraw++;
			}
		}
		
		// By the time the above logic is done, this is the value to use
		return indexOfFirstChildToDraw;
	}

	/**
	 * Find which child drawing should begin with, using an optimized algorithm that starts looking 
	 * at the index that drawing previously started at. (Based on the assumption that the clip and offset 
	 * aren't usually going to change.)
	 * Only useful during optimized drawing. 
	 * @param topViewableY The top Y coordinate, relative to us, drawing will happen at
	 * @return The index of the first child to draw
	 */
	private int findFirstDrawIndexUsingPrevious(int topViewableY)
	{
		int idx = prevFirstDrawIdx;
		VerticalViewElement curChild = children.get(idx);
		
		// Should we look forward or backward?
		if(curChild.getHeightSum() == topViewableY)
		{
			// We hit on the first try!
			return idx;
		}
		// Okay, this will be forward
		else if(curChild.getHeightSum() < topViewableY)
		{
			// Look forward through the rest of the children
			return lookForwardFrom(idx, topViewableY);
		}
		// This will be backward
		else {
			// Look backward through the rest of the children
			return lookBackwardFrom(idx, topViewableY);
		}
	}

	/**
	 * Find which child drawing should begin with, looking forward from the given index until a child 
	 * will need to be drawn to show given the top viewable Y coordinate
	 * @param idx Index to start from (actually only look ahead of this index)
	 * @param topViewableY The top Y coordinate, relative to us, drawing will happen at
	 * @return The index of the first child to draw
	 */
	private int lookForwardFrom(int idx, int topViewableY)
	{
		int indexOfFirstChildToDraw = -1;
		
		// Start one ahead of the given index
		for(int i = idx+1; i < children.size(); ++i)
		{
			// This child will draw at the line
			if(children.get(i).getHeightSum() == topViewableY)
			{
				// So use its index
				indexOfFirstChildToDraw = i;
				break;
			}
			// This child is one too far
			else if(children.get(i).getHeightSum() > topViewableY)
			{
				// So use the index before it (We know this is an okay index - we started one ahead)
				indexOfFirstChildToDraw = i-1;
				break;
			}
			// We need to keep looking
			else {
				indexOfFirstChildToDraw = i;
			}
		}
		
		// Use whatever index we're at
		return indexOfFirstChildToDraw;
	}

	/**
	 * Find which child drawing should begin with, looking backward from the given index until a child 
	 * will need to be drawn to show given the top viewable Y coordinate
	 * @param idx Index to start from (actually only look behind this index)
	 * @param topViewableY The top Y coordinate, relative to us, drawing will happen at
	 * @return The index of the first child to draw
	 */
	private int lookBackwardFrom(int idx, int topViewableY)
	{
		int indexOfFirstChildToDraw = -1;
		
		// Start one behind the given index
		for(int i = idx-1; i >= 0; --i)
		{
			// This child will draw at the line
			if(children.get(i).getHeightSum() == topViewableY)
			{
				// So use its index
				indexOfFirstChildToDraw = i;
				break;
			}
			// This child is one too far
			else if(children.get(i).getHeightSum() < topViewableY)
			{
				// So use the index before it
				indexOfFirstChildToDraw = i;
				break;
			}
			// We need to keep looking
			else {
				indexOfFirstChildToDraw = i;
			}
		}
		
		// Use whatever index we're at
		return indexOfFirstChildToDraw;
	}
	
	/**
	 * Draw children that will appear within view, starting at the given index and going until drawing 
	 * would happen beyond the bottom coordinate. Along the way this sets the Y coordinate of the elements 
	 * that have been drawn.
	 * @param g The graphics object
	 * @param offsetX Offset X
	 * @param offsetY Offset Y
	 * @param bottomViewableY Lowest Y coordinate that will show
	 * @param indexOfFirstChildToDraw Which child to start drawing with
	 * @throws IteratorException If thrown while drawing a child element
	 */
	private void drawChildrenWithinView(Graphics g, int offsetX, int offsetY,
			int bottomViewableY, int indexOfFirstChildToDraw)
			throws IteratorException
	{
		// Just make sure the index hasn't remained -1 up to this point
		if(indexOfFirstChildToDraw < 0)
		{
			return;
		}

		// We don't quite start at the top - instead wherever the first child would start
		int drawPosY = children.get(indexOfFirstChildToDraw).getHeightSum();
		
		// We can actually obtain an iterator starting at the start index
		Iterator<VerticalViewElement> drawIt = children.listIterator(indexOfFirstChildToDraw);
		while (drawIt.hasNext())
		{
			VerticalViewElement child = drawIt.next();

			// Figure out which x coordinate to use
			int drawPosX = xPosFor(child);

			// Draw the child wherever it is that it needs to go
			child.setY(drawPosY);
			child.drawElement(g, offsetX + drawPosX, offsetY + drawPosY);

			// The element's height is probably most current, now. Bump up the draw position by its height, at least.
			drawPosY += child.getHeight();

			// We may also need to apply padding, if there will be another element
			if (drawIt.hasNext())
			{
				drawPosY += paddingFor(child);
			}

			// Has the drawing position exceeded the viewing area yet?
			if (drawPosY > bottomViewableY)
			{
				// Since it has, no point in drawing further
				break;
			}
		}

	}
	
	/**
	 * Take care of pending remove requests in the remove queue. 
	 * This will remove them from the children list, update the partial height sum of its siblings, as well as the overall 
	 * height of this view. 
	 * This does not synchronize, so it cannot happen while iterating the children elsewhere
	 */
	private void reconcileRemovals()
	{
		// Continue while there are still remove requests
		while(!removeQueue.isEmpty())
		{
			Element toRemove = removeQueue.poll();
			
			// We want to remove a child. Iterate until we find it
			Iterator<VerticalViewElement> childIt = children.iterator();
			while(childIt.hasNext())
			{
				VerticalViewElement curChild = childIt.next();
				
				// Have we found it?
				if(curChild.equals(toRemove))
				{
					// Since we have, keep track of its height (how much to shift the rest by) and remove it
					childIt.remove();
					curChild.setParent(null);
					removeLink(curChild);
					
					// We adjust the height of the view
					if(children.get(children.size() - 1).equals(toRemove))
					{
						// Removing the last child, we don't need to account for its padding
						super.setHeight(super.getHeight() - curChild.getHeight());
					}
					else {
						// Removing some other child, its padding comes into play
						super.setHeight(super.getHeight() - (curChild.getHeight() + paddingFor(curChild)));
					}
					
					// We don't need this loop anymore, but we'll pick up where we left off for shifting
					break;
				}
			}
			
			// So now our iteration is on the remaining children
			while(childIt.hasNext())
			{
				VerticalViewElement curChild = childIt.next();
				
				// We want to shift this remaining child back up to fill the gap, basically
				curChild.changeHeightSum(-(curChild.getHeight() + paddingFor(curChild)));
			}
		}
	}
	
	/**
	 * Process pending requests to add a child to the view.  This adds the element to this view, 
	 * and then updates the overall height of the view.  This child is drawn far offscreen, so that 
	 * its height will be validated
	 * @param g A graphics object drawing is happening on
	 * @throws IteratorException If it occurs while drawing the element offscreen
	 */
	private void reconcileAdditions(Graphics g) throws IteratorException
	{
		// Continue while there are still elements to add
		while(!addQueue.isEmpty())
		{
			VerticalViewElement toAdd = addQueue.poll();
			
			// Draw it way offscreen somewhere, so that its height is valid
			toAdd.drawElement(g, 9999, 9999);
			
			// Set the partial sum value and update our height, depending on whether this is the first child or not
			if (children.size() == 0)
			{
				toAdd.setHeightSum(0);
				super.setHeight(super.getHeight() + toAdd.getHeight());
			}
			else {
				toAdd.setHeightSum(
						toAdd.getHeight()
						+ paddingFor(children.get(children.size() - 1))
						+ children.get(children.size() - 1).getHeightSum());
				super.setHeight(super.getHeight() + toAdd.getHeight() + paddingFor(children.get(children.size() - 1)));
			}
			
			// And add the new element to our children
			children.add(toAdd);
			toAdd.setParent(new ParentElement(this, this::add, this::remove));
			linkAddedChild(toAdd);
		}
	}
	
	/**
	 * Add the given child to this view. The child will not have any alignment, and will not have any padding.
	 * Its focus chain will be linked to this view's natural ordering
	 */
	@Override
	public void add(Element newChild)
	{
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
		
		// This is a vertical view, so horizontal-specific alignments are no good
		if(alignment != null && alignment != Alignment.LEFT && alignment != Alignment.CENTER && alignment != Alignment.RIGHT)
		{
			throw new IllegalArgumentException("Meaningless alignment value: " + alignment.toString());
		}
		
		// Wrap it in our decorator
		VerticalViewElement decChild = new VerticalViewElement(newChild, alignment, padding);
		
		if(optimizedDrawingEnabled)
		{
			addQueue.add(decChild);
		}
		else {
			// Then add that to our list of children
			children.add(decChild);

			// Establish the parent-child relationship
			decChild.setParent(new ParentElement(this, this::add, this::remove));

			// Establish the focus chain linkage
			linkAddedChild(decChild);
		}
	}

	/**
	 * Removes the child from this view, if it is a child of the view.  
	 * The view will have its height updated, and the child will have its parent nullified and be removed from the focus chain
	 */
	@Override
	public boolean remove(Element child)
	{
		if(optimizedDrawingEnabled)
		{
			removeQueue.add(child);
		}
		else {
			// Attempt to remove the child. If removal was successful, we were the parent - nullify that relationship
			if (children.remove(child)) {
				child.setParent(null);

				// We also adjust the focus chain to account for the removed child
				removeLink(child);

				return true;
			}
		}
		
		// Was not successful, so return false.
		return false;
	}

	/**
	 * Removes all of the children from the view. 
	 * For all of them, their parent link is nullified and the focus links are set back 
	 * to as if there are no children. (Because, well, there aren't.)
	 */
	@Override
	public void removeAllChildren()
	{
		if(optimizedDrawingEnabled)
		{
			// We can't just do it in one swoop the same way, we have to queue all existing children
			removeQueue.addAll(children);
		}
		else {
			// For each child, attempt to remove them. If removal was successful, nullify their parent.
			children.forEach((child) ->
			{
				if(children.remove(child))
				{
					child.setParent(null);
				}
			});

			// After removing everyone, clean up the focus chain in one go
			linkAfterRemoveAll();
		}
	}
	
	/**
	 * @return True if this view is assuming no changes to height of child elements
	 */
	public boolean hasOptimizationEnabled()
	{
		return optimizedDrawingEnabled;
	}
	
	/**
	 * Turn on or off optimized drawing. This will reduce the amount of drawing done from all 
	 * elements in the view, to only those that would show within the current graphics clip. 
	 * To do this, all of the elements in the view must never have their height changed after 
	 * they have been added to this view. (So for example, a TextArea may not have its text changed) 
	 * Will do nothing if it is being set on but already on, or being set off and already off
	 * @param enabled True if optimization should be turned on
	 */
	public void setOptimizedDrawing(boolean enabled)
	{
		// Once drawing has started, this is off the table, anyways.
		if(drawingStarted)
		{
			// We'll be noisy about it, this would indicate improper UI design
			throw new UnsupportedOperationException("Cannot change optimization once drawing has started");
		}
		
		// Turning on or off?
		if(enabled)
		{
			// Is it already on?
			if(optimizedDrawingEnabled)
			{
				// Well then we're already there. Do nothing
				return;
			}
			
			enableOptimization();
		}
		else {
			// Is it already off?
			if(!optimizedDrawingEnabled)
			{
				// Again already there
				return;
			}
			
			disableOptimization();
		}
	}
	
	/**
	 * Assumes it's okay to do this. Initialize what is necessary to turn it on
	 */
	private void enableOptimization()
	{
		addQueue = new ConcurrentLinkedQueue<>();
		removeQueue = new ConcurrentLinkedQueue<>();
		clipRect = new Rectangle();
		optimizedDrawingEnabled = true;
		prevFirstDrawIdx = -1;
	}
	
	/**
	 * Assumes it's okay to do this. De-initialize what is necessary to turn it off
	 */
	private void disableOptimization()
	{
		addQueue = null;
		removeQueue = null;
		clipRect = null;
		optimizedDrawingEnabled = false;
		prevFirstDrawIdx = -1;
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
	public Alignment getAlignment()
	{
		return alignment.get();
	}

	/**
	 * @param alignment The alignment to set, or null to remove the setting
	 * @throws IllegalArgumentException If the alignment is not vertically meaningful
	 */
	public void setAlignment(Alignment alignment)
	{
		// This is a vertical view, so horizontal-specific alignments are no good
		if(alignment != null && alignment != Alignment.LEFT && alignment != Alignment.CENTER && alignment != Alignment.RIGHT)
		{
			throw new IllegalArgumentException("Meaningless alignment value: " + alignment.toString());
		}
				
		this.alignment = Optional.ofNullable(alignment);
	}

	/**
	 * @return The view-wide padding setting
	 */
	public int getPadding()
	{
		return padding;
	}

	/**
	 * @param padding The padding to set. Either VerticalView.PADDING_NOT_SET or non-negative
	 */
	public void setPadding(int padding)
	{
		// Before proceeding, is the padding a legal value?
		if(padding != PADDING_NOT_SET && padding < 0)
		{
			throw new IllegalArgumentException("Padding must be PADDING_NOT_SET or non-negative");
		}
		
		// We also restrict changing this if optimization has been enabled and drawing has started...
		// it would be a pain to update the figures involved
		if(optimizedDrawingEnabled && drawingStarted)
		{
			throw new UnsupportedOperationException("Cannot set padding once optimized drawing has begun");
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
	 * @param child The recently added child
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
		
		/** A partial sum tracking the total Y location of this element. Only used when optimized */
		private int heightSum;

		/**
		 * Create a new decorated element which will have neither alignment nor padding set
		 * @param wrapped The element being decorated
		 */
		public VerticalViewElement(Element wrapped)
		{
			super(wrapped);
			
			this.alignment = Optional.empty();
			this.padding = PADDING_NOT_SET;
			this.heightSum = 0;
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
			this.heightSum = 0;
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
		
		/**
		 * @return The height sum
		 */
		public int getHeightSum()
		{
			return heightSum;
		}
		
		/**
		 * @param delta How much to change the height sum by
		 */
		public void changeHeightSum(int delta)
		{
			this.heightSum += delta;
		}
		
		/**
		 * @param heightSum The new height sum
		 */
		public void setHeightSum(int heightSum)
		{
			this.heightSum = heightSum;
		}
		
	}

}
