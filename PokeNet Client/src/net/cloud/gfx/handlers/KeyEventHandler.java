package net.cloud.gfx.handlers;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.cloud.gfx.constants.KeyConstants;
import net.cloud.gfx.focus.FocusController;
import net.cloud.gfx.focus.Focusable;

/**
 * This KeyAdapter will listen for key events in general and 
 * take events the Element hierarchy is interested in and pass them on to 
 * the object which currently has key focus in the system
 */
public class KeyEventHandler extends KeyAdapter {
	
	/**
	 * Create a KeyEventHandler which will hand the events it receives off to 
	 * whatever Focusable which currently has key focus.
	 */
	public KeyEventHandler()
	{
	}
	
	/**
	 * A KeyTyped event is what we're really after (key pressed and released are unlikely.) 
	 * If the character is the NUL character or an undefined character was typed, this event will do nothing. 
	 * Otherwise, the character will be passed to the Focusable that currently has focus. 
	 */
	@Override
	public void keyTyped(KeyEvent event)
	{
		// Key typed events have a character. Pressed and released do not necessarily. 
		char c = event.getKeyChar();
		
		System.out.println("key typed: " + c);
		
		// A few characters are simply ignored from the get-go
		if(isIgnored(c))
		{
			return;
		}
		
		// Check for special character combinations
		c = mutateTypedKey(event, c);
		
		// The character is good to go. Pass it on
		handleChar(c);
	}
	
	/**
	 * Check to see if a character is ignored when it is typed. Some characters it's just better 
	 * to not let them even get into the application. They probably don't have any business 
	 * being around, anyways. Ignored characters pretty much just aren't handled. 
	 * @param c The character that was typed
	 * @return True if it should be ignored
	 */
	private boolean isIgnored(char c)
	{
		// One off characters. Because switch statements are better for this. (Groovy's ranged switches are awesome)
		switch(c)
		{
		case '\0':
		case KeyEvent.CHAR_UNDEFINED:
			return true;
		}
		
		// Catch some ranges of characters. Like the custom re-purposed ones. 
		if(KeyConstants.FIRST_CUSTOM_CHAR <= c && c <= KeyConstants.LAST_CUSTOM_CHAR)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Takes a key event from the KeyTyped event. For some typed keys, we want to change 
	 * them into a different character based on factors like modifier keys. This method will 
	 * check for this and return a character (defined in KeyConstants) for the special character. 
	 * @param event The key typed event
	 * @param c The character originally typed
	 * @return A special character constant from KeyConstants or the <code>c</code> parameter
	 */
	private char mutateTypedKey(KeyEvent event, char c)
	{
		// Shift tab is well.. shift and tab. 
		if(c == '\t' && event.isShiftDown())
		{
			return KeyConstants.SHIFT_TAB;
		}
		
		// No mutation. Just the same character. 
		return c;
	}
	
	/**
	 * Passes the character onto whichever object currently has key focus in the system. 
	 * If there is no such object, then nothing is done. 
	 * @param c The character to fire an event for
	 */
	private void handleChar(char c)
	{
		Focusable current = FocusController.instance().currentFocusable();
		if(current != null)
		{
			current.keyTyped(c);
		}
	}

}
