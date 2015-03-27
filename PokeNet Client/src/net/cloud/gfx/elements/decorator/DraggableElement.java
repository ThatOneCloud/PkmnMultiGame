package net.cloud.gfx.elements.decorator;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

import net.cloud.gfx.elements.Element;

/**
 * An element which when dragged, will move around the screen. 
 * This decorator adds that behavior to another element. Then this reference may be used in place of the 
 * original element for outside functionality, such as mouse and key events. This decorator treats the 
 * wrapped element and itself nearly one and the same, using the wrapped element to maintain functionality. <br><br>
 * The ability to move the element may be toggled, as well as where on the element will allow the dragging and 
 * within what bounds the element can be moved.  By default these are toggled on, from anywhere, and within the 
 * parent, respectively.
 */
public class DraggableElement extends AbstractDecoratorElement {
	
	/** Flag for whether or not dragging is currently enabled */
	private boolean enabled;
	
	/** The bounds this element must stay within. If not specified, uses the parent. */
	private Optional<Rectangle> dragBounds;
	
	/** The various bounding boxes where dragging may be started from. If not specified, this is anywhere. */
	private Optional<LinkedList<Rectangle>> startBounds;
	
	/**
	 * Create a new DraggableElement by wrapping functionality around an existing element. 
	 * By default, dragging will be enabled, from anywhere, within the parent. 
	 * @param toDecorate The element to wrap around
	 */
	public DraggableElement(Element toDecorate)
	{
		this(toDecorate, true, null);
	}
	
	/**
	 * Full constructor, where everything is specified. 
	 * @param toDecorate The element to wrap around
	 * @param enabled True if dragging is enabled
	 * @param withinBounds The bounding box the element must stay in. May be null for within parent. 
	 * @param fromBounds All of the bounding boxes defining where on the element dragging may be started from. May be null.
	 */
	public DraggableElement(Element toDecorate, boolean enabled, Rectangle withinBounds, Rectangle... fromBounds)
	{
		super(toDecorate);
		
		this.enabled = enabled;
		this.dragBounds = Optional.ofNullable(withinBounds);
		
		// Do some list stuff here, rather than trying to string it all together in one shot
		if(fromBounds == null || fromBounds.length == 0)
		{
			startBounds = Optional.empty();
		}
		else {
			// Add all of the bounding rectangles to a list
			LinkedList<Rectangle> boundList = new LinkedList<>();
			for(Rectangle r : fromBounds)
			{
				boundList.add(r);
			}
			startBounds = Optional.of(boundList);
		}
	}
	
	/**
	 * Moves the element when it is dragged. No matter the result, the wrapped element 
	 * will have its dragged method called as well. 
	 * Dragging must be enabled, and the withinStart point must be within one of the starting 
	 * rectangles. Further, the element cannot be moved beyond its bounds. It will instead snug 
	 * up against them.
	 */
	@Override
	public void dragged(Element dragged, Point start, Point withinStart, Point current)
	{
		// Check if dragging is even enabled
		if(!enabled)
		{
			getDecoratedElement().dragged(dragged, start, withinStart, current);
			return;
		}
		
		// Check if the press started in an okay place
		boolean okayStart = false;
		if(startBounds.isPresent())
		{
			for(Rectangle r : startBounds.get())
			{
				if(r.contains(withinStart))
				{
					// Only need to go until we find one that works
					okayStart = true;
					break;
				}
			}
		}
		// Is there any bounding? Was the click in an okay spot?
		if(startBounds.isPresent() && !okayStart)
		{
			getDecoratedElement().dragged(dragged, start, withinStart, current);
			return;
		}
		
		// Find where it's going
		int destX = getBoundedX(current.x - withinStart.x);
		int destY = getBoundedY(current.y - withinStart.y);

		// Update the location
		getDecoratedElement().setX(destX);
		getDecoratedElement().setY(destY);
		
		// Move the call down to the wrapped element in case it does something of its own
		getDecoratedElement().dragged(dragged, start, withinStart, current);
	}
	
	/**
	 * Set whether or not dragging is enabled on this element
	 * @param enabled True if dragging is okay, false if it should not happen
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	/**
	 * Set the bounds that define where the element may be moved to. This is essentially relative to the parent element. 
	 * Can be set to null to restrict to the parent in a dynamic manner, or to a very large range to allow movement essentially anywhere. 
	 * @param dragBounds The bounding box the element must stay in. May be null for within parent.
	 */
	public void setDragBounds(Rectangle dragBounds)
	{
		this.dragBounds = Optional.ofNullable(dragBounds);
	}
	
	/**
	 * Add a bounding rectangle defining where dragging may originate from. For example, in a framed element, it is useful 
	 * to add the top border of the frame as a start bound. Multiple bounds may be set, and if the start point is within 
	 * any of them, dragging will be allowed. They can be removed, via the rectangle or a point.
	 * @param startBound A bounding on where dragging may be started from
	 */
	public void addStartBound(Rectangle startBound)
	{
		// If the list doesn't exist, it should now
		if(!startBounds.isPresent())
		{
			startBounds = Optional.of(new LinkedList<Rectangle>());
		}
		
		// Add the new rectangle to the list
		startBounds.get().add(startBound);
	}
	
	/**
	 * Remove a starting bound, by providing an equivalent rectangle. The provided rectangle must be an equals(Object obj) match. 
	 * Only the first match will be removed. This will do nothing if there are no start bounds
	 * @param startBound The bounding rectangle to remove
	 */
	public void removeStartBound(Rectangle startBound)
	{
		// Only bother if the list exists
		if(startBounds.isPresent())
		{
			startBounds.get().remove(startBound);
		}
	}
	
	/**
	 * Remove all starting bounds which contain the given point. This will remove all such bounds, not just the first occurrence.
	 * This will do nothing if there are no start bounds
	 * @param startPoint A point whose matching start bounds will be removed
	 */
	public void removeStartBound(Point startPoint)
	{
		// Only bother if the list exists
		if(startBounds.isPresent())
		{
			// Iterate to remove
			Iterator<Rectangle> it = startBounds.get().iterator();
			while(it.hasNext())
			{
				// The bounding rectangle contains the point
				if(it.next().contains(startPoint))
				{
					// So remove it
					it.remove();
				}
			}
		}
	}
	
	/**
	 * Get an x coordinate which is within bounds. If the coordinate would otherwise be out 
	 * of bounds, an edge is returned. Otherwise, the same coordinate is given back.
	 * @param destX The unbounded destination coordinate
	 * @return A bounded and valid destination coordinate
	 */
	private int getBoundedX(int destX)
	{
		if(dragBounds.isPresent())
		{
			Rectangle b = dragBounds.get();
			
			// Checking the x bounds
			if(destX < b.x)
			{
				return b.x;
			}
			else if(destX + getWidth() > b.width)
			{
				return b.width - getWidth();
			}
		}
		// No bounds means within parent
		else if(getParent().isPresent())
		{
			Element p = getParent().get().getParent();
			
			// Checking the x bounds
			if(destX < 0)
			{
				return 0;
			}
			else if(destX + getWidth() > p.getWidth())
			{
				return p.getWidth() - getWidth();
			}
		}
		
		// No bounding restriction will get us here. 
		return destX;
	}
	
	/**
	 * Get an x coordinate which is within bounds. If the coordinate would otherwise be out 
	 * of bounds, an edge is returned. Otherwise, the same coordinate is given back.
	 * @param destY The unbounded destination coordinate
	 * @return A bounded and valid destination coordinate
	 */
	private int getBoundedY(int destY)
	{
		// Custom bounds
		if(dragBounds.isPresent())
		{
			Rectangle b = dragBounds.get();

			// Upper y bounds
			if(destY < b.y)
			{
				return b.y;
			}
			// Lower y bounds
			else if(destY + getHeight() > b.height)
			{
				return b.height - getHeight();
			}
		}
		// No bounds means within parent
		else if(getParent().isPresent())
		{
			Element p = getParent().get().getParent();
			
			// Upper y bounds
			if(destY < 0)
			{
				return 0;
			}
			// Lower y bounds
			else if(destY + getHeight() > p.getHeight())
			{
				return p.getHeight() - getHeight();
			}
		}
		
		// Getting here means no restriction
		return destY;
	}

}
