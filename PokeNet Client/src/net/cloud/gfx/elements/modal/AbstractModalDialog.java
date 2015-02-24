package net.cloud.gfx.elements.modal;

import java.awt.Color;
import java.awt.Graphics;

import net.cloud.client.util.IteratorException;
import net.cloud.gfx.elements.Interface;
import net.cloud.gfx.focus.Focusable;
import net.cloud.gfx.sprites.SpriteSet;

/**
 * Abstract base class for modal dialog interfaces. A modal dialog is an interface that appears on top of all 
 * other elements, and demands focus. (The attention whore!) While this element in itself does not enforce this 
 * behavior, that is the intended behavior and should be enforced by the outside system. (Enforce the assertion that 
 * only one modal dialog may be present at any time, and it will receive all input.) <br>
 * Notable things this superclass does: Trap key events so they will not move up the parent hierarchy, prevent 
 * linking to other focusable, and disallowing changes to the priority.
 */
public abstract class AbstractModalDialog extends Interface {
	
	/** Default priority of a modal dialog. Cannot be beat. */
	public static final int PRIORITY = Integer.MAX_VALUE;
	
	private static final int START_ALPHA = 125;
	
	private static final int ALPHA_STEP = 10;
	
	private static final Color INITIAL_HIGHLIGHT_COLOR = new Color(50, 50, 200, START_ALPHA);
	
	private Color currentHighlightColor;
	
	/**
	 * Constructor for a modal dialog. Unlike some elements, this must have a parent - it must have something 
	 * that contains it so that it may be removed. If it is not properly removed, it will be stuck and you oops'd. 
	 * This constructor will use the default background - a background is also enforced just because.
	 * @param parent The Container the dialog will be placed in
	 * @param x X location
	 * @param y Y location
	 * @param width Width of the dialog
	 * @param height Height of the dialog
	 */
	public AbstractModalDialog(int x, int y, int width, int height)
	{
		// The background is the default here
		this(x, y, width, height, 0);
	}
	
	/**
	 * Constructor for a modal dialog. Unlike some elements, this must have a parent - it must have something 
	 * that contains it so that it may be removed. If it is not properly removed, it will be stuck and you oops'd. 
	 * This constructor will use the given background - a background is also enforced just because.
	 * @param parent The Container the dialog will be placed in
	 * @param x X location
	 * @param y Y location
	 * @param width Width of the dialog
	 * @param height Height of the dialog
	 * @param bgID The ID of the background sprite. Comes from the BACKGROUND sprite set
	 */
	public AbstractModalDialog(int x, int y, int width, int height, int bgID)
	{
		super(PRIORITY, x, y, width, height);
		
		// We use a background for these dialogs mostly so that they all have them and it looks nicer
		super.setBackground(SpriteSet.BACKGROUND, bgID);
		
		// Start with no highlight. It'll only be turned on briefly when we lose focus.
		this.currentHighlightColor = null;
	}
	
	/**
	 * Calls <code>super.drawElement()</code> first. 
	 * Then, if the dialog has a highlight color (from a click happening off of it), that highlight color will be drawn over 
	 * the dialog. The highlight fades somewhat quickly.
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		super.drawElement(g, offsetX, offsetY);

		// Whilst it would be plenty possible to do this via the task engine, altering it in the loop works too. The timing may just be different.
		// Anyways, there is a highlight, so we'll need to draw that
		if(currentHighlightColor != null)
		{
			// Set the color and fill the whole dialog with it
			g.setColor(currentHighlightColor);
			g.fillRect(offsetX, offsetY, getWidth(), getHeight());
			
			// Figure out the new alpha. Generally step it down, to make the color fade
			int newAlpha = currentHighlightColor.getAlpha() - ALPHA_STEP;
			
			// The alpha can't go below 0. If it has, we're done fading the highlight - it can disappear completely
			if(newAlpha < 0)
			{
				currentHighlightColor = null;
			}
			// Otherwise, the highlight color will just fade for next time around
			else {
				currentHighlightColor = new Color(currentHighlightColor.getRed(), currentHighlightColor.getGreen(), currentHighlightColor.getBlue(), newAlpha);
			}
		}
	}
	
	/**
	 * Key events will not propagate to parents of the modal dialogue. 
	 * In other words, this traps key events and does nothing.
	 */
	@Override
	public void keyTyped(char key) 
	{
	}
	
	/**
	 * Cannot change focus from a modal dialog. It wants to keep it. 
	 * Will always throw UnsupportedOperationException
	 * @throws UnsupportedOperationException Always.
	 */
	@Override
	public void linkNextFocusable(Focusable current, Focusable next)
	{
		throw new UnsupportedOperationException("Modal dialogs would rather you not change focus away from them.");
	}
	
	/**
	 * Cannot change focus from a modal dialog. It wants to keep it. 
	 * Will always throw UnsupportedOperationException
	 * @throws UnsupportedOperationException Always.
	 */
	@Override
	public void linkPreviousFocusable(Focusable current, Focusable previous)
	{
		throw new UnsupportedOperationException("Modal dialogs would rather you not change focus away from them.");
	}
	
	/**
	 * Cannot change the priority of a modal dialog. It relies on this to maintain its always-on-top behavior.
	 * Will always throw UnsupportedOperationException
	 * @throws UnsupportedOperationException Always.
	 */
	@Override
	public void setPriority(int priority)
	{
		throw new UnsupportedOperationException("Modal dialogs are always on top. Cannot alter priority");
	}
	
	/**
	 * Notify the dialog that a click was made off of it. This will cause the dialog to show some kind of 
	 * visual notice to the user that the dialog wants attention. FEED IT! (Bad humor?)
	 */
	public void offDialogClick()
	{
		// Even if the highlight color is currently fading, we'll change it back to the starting color
		currentHighlightColor = INITIAL_HIGHLIGHT_COLOR;
	}

}
