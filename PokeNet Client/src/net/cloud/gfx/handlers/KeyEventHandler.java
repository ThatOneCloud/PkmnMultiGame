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
	 * A KeyPressed event is fire for all keys, unlike key typed which is only fired for keys which 
	 * correspond to a character. Since some of these events are interesting, we'll check them and see 
	 * if they correspond to some artificial character constant we want to fire events for as well. 
	 * Ultimately calls <code>handleChar()</code> when a key typed event would not. 
	 * @param event The KeyEvent
	 */
	@Override
	public void keyPressed(KeyEvent event)
	{
		// Key presses are mapped to integers - even for keys with normally no character representation
		int keyCode = event.getKeyCode();
		
//		System.out.println("Key pressed: " + keyCode);
		
		// So we give the key a character that is otherwise useless for us
		char artificialChar = mutatePressedKey(event, keyCode);
		
		// And handle that artificial character. (null character means no artificial character)
		if(artificialChar != '\0')
		{
			handleChar(artificialChar);
		}
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
		
		System.out.println("key typed: " + c + ", " + (int) c);
		
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
			System.err.println("shift tab");
			return KeyConstants.SHIFT_TAB;
		}
		
		// No mutation. Just the same character. 
		return c;
	}
	
	/**
	 * Takes a key event from a KeyPressed event. For events that do not trigger a key typed event as well, 
	 * but we are still interested in handling. Takes the key code and maps it to a character constant.
	 * @param event The key event (for obtaining modifiers)
	 * @param keyCode The key code from the event
	 * @return A character to handle if interested, null character if not
	 */
	private char mutatePressedKey(KeyEvent event, int keyCode)
	{
		// Various keys. I guess sort of arrange them with most common at top?
		switch(keyCode)
		{
		
		// Arrow keys
		case KeyEvent.VK_LEFT:
			return KeyConstants.LEFT_ARROW;
		case KeyEvent.VK_UP:
			return KeyConstants.UP_ARROW;
		case KeyEvent.VK_RIGHT:
			return KeyConstants.RIGHT_ARROW;
		case KeyEvent.VK_DOWN:
			return KeyConstants.DOWN_ARROW;
			
		// Not a key press to worry about
		default:
			return '\0';
		}
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
