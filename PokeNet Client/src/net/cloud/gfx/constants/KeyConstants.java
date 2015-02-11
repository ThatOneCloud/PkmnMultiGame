package net.cloud.gfx.constants;

/**
 * A utility class that deals with keys and their events. For example, keys 
 * for global hotkeys are stored here. There are also some custom re-purposed 
 * characters defined here.  These are characters that could be typed, but are 
 * very unlikely to be useful in this application. So keys that normally do not have 
 * a character can be assigned to a character for homogeneous handling. 
 */
public class KeyConstants {
	
	// BEGIN CUSTOM CHARACTERS //

	/** Where the re-purposed characters begin. The lowest possible value for them. */
	public static final char FIRST_CUSTOM_CHAR = 0xFF00;

	/** The Shift+Tab combination, treated as a character of its own. */
	public static final char SHIFT_TAB = 0xFF00;
	
	/** Left arrow key */
	public static final char LEFT_ARROW = 0xFF01;
	
	/** Up arrow key */
	public static final char UP_ARROW = 0xFF02;
	
	/** Right arrow key */
	public static final char RIGHT_ARROW = 0xFF03;
	
	/** Down arrow key */
	public static final char DOWN_ARROW = 0xFF04;
	
	/** Where the re-purposed characters end. The highest possible value for them. */
	public static final char LAST_CUSTOM_CHAR = 0xFFEE;

	// END CUSTOM CHARACTERS //
	
	/** Constant for backspace because clarity */
	public static final char BACKSPACE = '\b';
	
	/** Delete has an odd character */
	public static final char DELETE = 0x007F;
	
	/** Constant for enter because clarity */
	public static final char ENTER = '\n';
	
	/** CTRL+U (because why is it NACK?) */
	public static final char CTRL_U = 0x0015;
	
	/** Global hotkey - show stat overlay */
	public static final char STAT_OVERLAY = 'o';
	
	/** Global hotkey - show test interface (set to null to disable) */
	public static final char TEST_INTERFACE = 't';
	
	/** Constant for the character which will traverse to the next Focusable in line */
	public static final char CHANGE_FOCUS_NEXT = '\t';
	
	/** Constant for the character which will traverse to the previous Focusable in line */
	public static final char CHANGE_FOCUS_PREVIOUS = SHIFT_TAB;
	
	/** The key that in general means an action should be performed */
	public static final char ACTION_KEY = ENTER;
	
	/** The "kill text" command - which removes a segment of text */
	public static final char KILL_TEXT = CTRL_U;
	
}
