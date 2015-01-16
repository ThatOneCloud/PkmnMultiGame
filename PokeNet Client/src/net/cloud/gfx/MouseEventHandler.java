
package net.cloud.gfx;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.cloud.gfx.elements.Interface;
import net.cloud.mmo.logging.Logger;
import net.cloud.mmo.util.IteratorException;

/**
 * This MouseAdapter will listen for mouse events in general and 
 * take events the Element hierarchy is interested in and pass them on to 
 * the quasi-root Interface.
 */
public class MouseEventHandler extends MouseAdapter {
	
	/** The root panel that bridges from swing to game graphics */
	private RootPanel rootPanel;
	
	/** The quasi-root of the element hierarchy, to where events are first handed */
	private Interface elementRoot;
	
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
	}
	
	/**
	 * The mouse was just clicked. So let the Interface know about the click, 
	 * and try again if the UI structure was modified unsafely. 
	 */
	@Override
	public void mouseClicked(MouseEvent event)
	{
		// We want the root panel to take on focus if it has lost it
		rootPanel.requestFocusInWindow();
		
		// Top of the exception chain. Try to hand the event off
		try {
			elementRoot.elementClicked(event.getPoint());
		} catch (IteratorException e) {
			// Nutter-butters. List was modified before we got to the point. Try again!
			// It'll log a message so there's at least some notification. This might run out of control, and need to be ceased.
			Logger.writer().println("[NOTICE] Iterator Exception during mouse click");
			Logger.writer().flush();
			mouseClicked(event);
		}
	}

}
