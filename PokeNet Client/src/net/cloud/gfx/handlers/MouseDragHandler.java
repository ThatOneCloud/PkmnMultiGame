package net.cloud.gfx.handlers;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import net.cloud.gfx.elements.Element;

/**
 * A handler class intended to deal only with mouse dragged events. 
 * Since there are so many of these events, they are split from the typical event handler. 
 * Instead, this handler should be added and then removed when it is no longer needed. 
 * Works on the assumption that drags only happen while the mouse is pressed down, and the mouse will 
 * only be pressed down on a single element - so the events are only delivered to a predetermined element.
 */
public class MouseDragHandler implements MouseMotionListener {
	
	/** The element being dragged */
	private final Element element;
	
	/** The absolute location of the mouse press starting the drag events */
	private final Point absoluteStart;
	
	/** Location of the mouse press relative to the element itself */
	private final Point relativeStart;
	
	/** Location of the mouse press relative to within the element */
	private final Point withinStart;
	
	/**
	 * Create a new MouseDrag handler for when the given element has been pressed and may now be dragged. 
	 * @param element The element the mouse was pressed on
	 * @param absoluteStart The absolute location of the mouse press
	 * @param relativeStart Location of the mouse press relative to the element itself
	 * @param withinStart Location of the mouse press relative to within the element
	 */
	public MouseDragHandler(Element element, Point absoluteStart, Point relativeStart, Point withinStart)
	{
		this.element = element;
		this.absoluteStart = absoluteStart;
		this.relativeStart = relativeStart;
		this.withinStart = withinStart;
	}

	/**
	 * Delivers the event to the element which is being dragged. The element will 
	 * have two points relative to its own coordinate space delivered to it: 
	 * The starting and current event point. 
	 */
	@Override
	public void mouseDragged(MouseEvent e)
	{
		// Determine the relative point of the mouse drag event. RC = RS + (AC - AS). 
		int curRelX = relativeStart.x + e.getX() - absoluteStart.x;
		int curRelY = relativeStart.y + e.getY() - absoluteStart.y;
		
		// Tell the element about the event, providing it both relative points
		element.dragged(element, relativeStart, withinStart, new Point(curRelX, curRelY));
	}

	/** 
	 * Does nothing. 
	 * @param e Not used. 
	 */
	@Override
	public void mouseMoved(MouseEvent e) {}

}
