
package net.cloud.gfx.handlers;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

import net.cloud.client.logging.Logger;
import net.cloud.client.util.IteratorException;
import net.cloud.gfx.Mainframe;
import net.cloud.gfx.RootPanel;
import net.cloud.gfx.constants.KeyConstants;
import net.cloud.gfx.elements.Element;
import net.cloud.gfx.elements.Interface;
import net.cloud.gfx.elements.modal.ModalManager;
import net.cloud.gfx.focus.FocusController;
import net.cloud.gfx.focus.Focusable;

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
		if(SwingUtilities.isRightMouseButton(event))
		{
			isRightClick = true;
		}
		else if(!SwingUtilities.isLeftMouseButton(event)) {
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
	 * The mouse wheel was rotated. We'll actually treat this like a key press, to utilize the chain-of-responsibility 
	 * that key events utilize. 
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent event)
	{
		// The rotation we're interested in is how many times the wheel clicked along. If you using a click-y wheel.
		int rotation = event.getWheelRotation();
		
		// It may return 0 if its a high precision wheel. We'll only fire when a full click happens */
		if(rotation == 0)
		{
			return;
		}
		
		// Translate the event to a character. Shift being held down transforms it to horizontal scrolling
		// A fun one: Everything below here could be combined in one go, in one line, one semicolon. But clarity is probably better, lol
		char eventChar;
		if(event.isShiftDown())
		{
			eventChar = rotation > 0 ? KeyConstants.SCROLL_RIGHT : KeyConstants.SCROLL_LEFT;
		}
		else {
			eventChar = rotation > 0 ? KeyConstants.SCROLL_DOWN : KeyConstants.SCROLL_UP;
		}
		
		// Yes, we have our own handle char method, just for this
		handleChar(eventChar);
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
	
	/**
	 * Our own handleChar implementation, for the mouse wheel events. Copied here rather than publicizing it in 
	 * KeyEventHandler. 
	 * @param c The character to fire an event for
	 */
	private void handleChar(char c)
	{
		Focusable current = FocusController.instance().currentFocusable();
		
		// Make sure we're sending the event somewhere
		if(current != null)
		{
			// Separately check that where we're sending it is the modal dialog, given one is present
			if(ModalManager.instance().getCurrentModal().isPresent() && !currentIsModal())
			{
				// There's a modal dialog and its lost focus. Can't allow key events to go elsewhere
				return;
			}

			current.keyTyped(c);
		}
	}
	
	/**
	 * Assumes a check has been made and a modal dialog is present. 
	 * Check to see if the current focused object is the modal dialog, or a child of the modal dialog. 
	 * If the current focused object is not an Element, then this will simply return false. 
	 * @return True if the currently focused object is within scope of the modal dialog
	 */
	private boolean currentIsModal()
	{
		// Check to see if the current focusable is not an element. If it isn't, well, it sure isn't a modal dialog
		if(!(FocusController.instance().currentFocusable() instanceof Element))
		{
			return false;
		}

		// Safe cast. Above make sure the current focused object is an Element type
		Element current = (Element) FocusController.instance().currentFocusable();

		// Modal manager can take it from here
		return ModalManager.elementWithinModal(current);
	}

}
