package net.cloud.gfx.elements;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Optional;

/**
 * An element which when dragged, will move around the screen. 
 * This decorator adds that behavior to another element. Then this reference may be used in place of the 
 * original element for outside functionality, such as mouse and key events. This decorator treats the 
 * wrapped element and itself nearly one and the same, using the wrapped element to maintain functionality. <br><br>
 * The ability to move the element may be toggled, as well as where on the element will allow the dragging and 
 * within what bounds the element can be moved.  By default these are toggled on, from anywhere, and within the 
 * parent, respectively.
 *
 * @param <T> The type of the wrapped element, so type casts are not always necessary
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

	@Override
	public void dragged(Point start, Point withinStart, Point current)
	{
		// Check if dragging is even enabled
		if(!enabled)
		{
			getDecoratedElement().dragged(start, withinStart, current);
			return;
		}
		
		// Check if the press started in an okay place
		if(startBounds.isPresent())
		{
			for(Rectangle r : startBounds.get())
			{
				if(!r.contains(withinStart))
				{
					getDecoratedElement().dragged(start, withinStart, current);
					return;
				}
			}
		}
		
		// Find where it's going
		int destX = getBoundedX(current.x - withinStart.x);
		int destY = getBoundedY(current.y - withinStart.y);

		// Update the location
		getDecoratedElement().setX(destX);
		getDecoratedElement().setY(destY);
		
		// Move the call down to the wrapped element in case it does something of its own
		getDecoratedElement().dragged(start, withinStart, current);
	}
	
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
			Element p = getParent().get();
			
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
			Element p = getParent().get();
			
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
