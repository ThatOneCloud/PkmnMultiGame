package net.cloud.gfx.handlers;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.cloud.gfx.elements.Element;

/**
 * This KeyAdapter will listen for key events in general and 
 * take events the Element hierarchy is interested in and pass them on to 
 * the quasi-root Interface.
 */
public class KeyEventHandler extends KeyAdapter {
	
	/** The element that currently has focus is the element that will receive key events */
	private Element focusedElement;
	
	/**
	 * Create a KeyEventHandler which will hand the events it receives off to the 
	 * element which currently has key focus.
	 * @param initialElement The Element that will be given focus from the start
	 */
	public KeyEventHandler(Element initialElement)
	{
		// An initial element means no annoying clicking at startup to gain focus
		this.focusedElement = initialElement;
	}
	
	/**
	 * A KeyTyped event is what we're really after (key pressed and released are unlikely.) 
	 * If the character is the NUL character or an undefined character was typed, this event will do nothing. 
	 * Otherwise, the character will be passed to the element that currently has focus. 
	 */
	@Override
	public void keyTyped(KeyEvent event)
	{
		char c = event.getKeyChar();
		
		System.out.println("key typed: " + c);
		
		// A few characters are simply ignored from the get-go
		if(c == '\0' || c == KeyEvent.CHAR_UNDEFINED)
		{
			return;
		}
		
		// The character is good to go. Pass it on
		if(focusedElement != null)
		{
			focusedElement.keyTyped(c);
		}
	}
	
	/**
	 * Called when an element wants to tell us that it should now have key focus. 
	 * This will mean the previous element no longer does, and the new element will 
	 * now receive key events instead. The previous element will be notified that it 
	 * no longer has focus. 
	 * @param newFocusedElement The element that will now have key focus
	 */
	public void registerFocus(Element newFocusedElement)
	{
		// Tell the previous element it no longer has focus
		focusedElement.setFocus(false);
		
		// And update it so that a new element has focus
		focusedElement = newFocusedElement;
		focusedElement.setFocus(true);
	}

}
