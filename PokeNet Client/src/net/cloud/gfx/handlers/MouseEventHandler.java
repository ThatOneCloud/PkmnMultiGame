
package net.cloud.gfx.handlers;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.cloud.client.logging.Logger;
import net.cloud.client.util.IteratorException;
import net.cloud.gfx.Mainframe;
import net.cloud.gfx.RootPanel;
import net.cloud.gfx.elements.Element;
import net.cloud.gfx.elements.Interface;
import net.cloud.gfx.elements.modal.ModalManager;

/**
 * This MouseAdapter will listen for mouse events in general and 
 * take events the Element hierarchy is interested in and pass them on to 
 * the quasi-root Interface. 
 * It will also take care of starting and stopping drag events on element that are pressed.
 */
public class MouseEventHandler extends MouseAdapter {
	
	/** The root panel that bridges from swing to game graphics */
	private RootPanel rootPanel;
	
	/** The quasi-root of the element hierarchy, to where events are first handed */
	private Interface elementRoot;
	
	/** Current element the mouse is pressed on, if any. */
	private Element pressedElement;
	
	/** Current handler for drag events, if any */
	private MouseDragHandler dragHandler;
	
	/**
	 * Create a MouseEventHandler which will hand the events it receives off to 
	 * the given Interface. Only the events which Elements are interested in, of course. 
	 * @param rootPanel The root panel. Needed so a click may request focus changes to the panel. 
	 * @param elementRoot The Interface which is at the top of the element hierarchy
	 */
	public MouseEventHandler(RootPanel rootPanel, Interface elementRoot)
	{
		this.rootPanel = rootPanel;
		this.elementRoot = elementRoot;
		
		this.pressedElement = null;
		this.dragHandler = null;
	}
	
	/**
	 * The mouse was pressed. This is how some elements react, and how dragging starts. 
	 * We only care about left presses, this is a simplifying assumption. 
	 * Will create and register a drag handler for whatever element the mouse was pressed on.
	 */
	public void mousePressed(MouseEvent event)
	{
		// Ignore if not the left button
		if(event.getButton() != MouseEvent.BUTTON1)
		{
			return;
		}
		
		// We want the root panel to take on focus if it has lost it, here rather than in click
		rootPanel.requestFocusInWindow();
		
		// Ignore if the mouse is already pressed on something. How did they manage?
		if(pressedElement != null)
		{
			return;
		}
		
		// Find the top element and point relative to that element.
		Point relPoint = new Point(event.getPoint());
		pressedElement = topElement(relPoint);
		
		// Modal dialogs absorb all mouse events. Any not originating on it are ignored
		if(ModalManager.instance().getCurrentModal().isPresent() && !ModalManager.elementWithinModal(pressedElement))
		{
			// There's a modal interface and the top element is not within it.
			pressedElement = null;
			return;
		}
		
		// The point is now relative to within the element. Want it relative to the element itself for drag events
		Point dragPoint = new Point(relPoint.x + pressedElement.getX(), relPoint.y + pressedElement.getY());
		
		// Create and store away a handler to deal with future drag events
		dragHandler = new MouseDragHandler(pressedElement, event.getPoint(), dragPoint, relPoint);
		
		// Add the handler, so drag events are actually picked up
		Mainframe.instance().gfx().rootPanel().addMouseMotionListener(dragHandler);
		
		// Finally ship the event off to the element, where it can do whatever with it
		pressedElement.pressed(pressedElement, relPoint);
	}
	
	public void mouseReleased(MouseEvent event)
	{
		// Ignore if not the left button
		if(event.getButton() != MouseEvent.BUTTON1)
		{
			return;
		}
		
		// Ignore if there is no pressed element (releases after a press off a modal dialog are ignored here, too)
		if(pressedElement == null)
		{
			return;
		}
		
		// Remove the handler sooner rather than later
		Mainframe.instance().gfx().rootPanel().removeMouseMotionListener(dragHandler);
		
		// Find the element the mouse was released over
		Point relPoint = new Point(event.getPoint());
		Element releasedElement = topElement(relPoint);
		
		// Tell both released element and currently pressed element about the event
		releasedElement.released(releasedElement, relPoint, true);
		if(releasedElement != pressedElement)
		{
			// Avoid duplicate events. This is only sent when the release isn't over the pressed element
			pressedElement.released(pressedElement, null, false);
		}
		
		// Nullify the fields so things go back to reset, and garbage collection can come in
		pressedElement = null;
		dragHandler = null;
	}
	
	/**
	 * The mouse was just clicked. (Pressed and released in same spot) So let the Interface know about the click, 
	 * and try again if the UI structure was modified unsafely. 
	 */
	@Override
	public void mouseClicked(MouseEvent event)
	{
		// Determine if right or left click. Others are ignored.
		boolean isRightClick = false;
		if(event.getButton() == MouseEvent.BUTTON2)
		{
			isRightClick = true;
		}
		else if(event.getButton() != MouseEvent.BUTTON1) {
			return;
		}
		
		// Figure out where the event is going...
		Point point = event.getPoint();
		Element element = topElement(point);
		
		// ... make sure the modal dialog is where its going if need be...
		if(ModalManager.instance().getCurrentModal().isPresent() && !ModalManager.elementWithinModal(element))
		{
			// There's a modal interface and the top element is not within it. Tell it about this outrage!
			ModalManager.instance().getCurrentModal().get().offDialogClick();
			return;
		}
		
		// ... and send it there!
		element.clicked(element, point, isRightClick);
	}
	
	/**
	 * Find the top element for an event. Will try even if an exception occurs, so that the returned 
	 * Element is not null. Use this result soon, or it could soon be invalidated.
	 * @param eventPoint The point of the mouse event. Translated by return time.
	 * @return The Element showing on top of the rest at the point
	 */
	private Element topElement(Point eventPoint)
	{
		Element topElement = null;
		
		// Go until success. May turn out that it goes out of control and needs to be suppressed at some time.
		while(topElement == null)
		{
			try {
				// Create an original point, have to protect from corruption if it only gets partway down
				Point relPoint = new Point(eventPoint);
				
				// Top of the exception chain. Find the element
				topElement = elementRoot.topElementAtPoint(relPoint);
				
				// Before wrapping up, the passed in event point gets translated so it is relative to the element
				eventPoint.setLocation(relPoint);
			} catch (IteratorException e) {
				// Nutter-butters. List was modified before we got to the point. Try again!
				// It'll log a message so there's at least some notification.
				Logger.writer().println("[NOTICE] Iterator Exception while finding top element for mouse event");
				Logger.writer().flush();
			}
		}
		
		return topElement;
	}

}
