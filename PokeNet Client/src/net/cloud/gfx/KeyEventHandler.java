package net.cloud.gfx;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.cloud.gfx.elements.Interface;

/**
 * This KeyAdapter will listen for key events in general and 
 * take events the Element hierarchy is interested in and pass them on to 
 * the quasi-root Interface.
 */
public class KeyEventHandler extends KeyAdapter {
	
	/** The quasi-root of the element hierarchy, to where events are first handed */
	private Interface elementRoot;
	
	/**
	 * Create a KeyEventHandler which will hand the events it receives off to 
	 * the given Interface. Only the events which Elements are interested in, of course. 
	 * @param elementRoot The Interface which is at the top of the element hierarchy
	 */
	public KeyEventHandler(Interface elementRoot)
	{
		this.elementRoot = elementRoot;
	}
	
	/**
	 * A KeyTyped event is what we're really after (key pressed and released are unlikely.) 
	 * If the character is the NUL character or an undefined character was typed, this event will do nothing. 
	 * Otherwise, the character will be passed to the root Interface. 
	 */
	@Override
	public void keyTyped(KeyEvent event)
	{
		char c = event.getKeyChar();
		
		// A few characters are simply ignored from the get-go
		if(c == '\0' || c == KeyEvent.CHAR_UNDEFINED)
		{
			return;
		}
		
		// The character is good to go. Pass it on
		elementRoot.keyTyped(event.getKeyChar());
	}

}
