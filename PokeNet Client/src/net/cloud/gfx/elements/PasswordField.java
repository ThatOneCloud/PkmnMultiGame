package net.cloud.gfx.elements;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Arrays;

/**
 * A text field where rather than showing the actual text in the field, 
 * asterisks are used instead. The hint text is still shown. 
 * Everything else works just like with a text field, you can still change settings and obtain the true text.
 */
public class PasswordField extends TextField {
	
	/**
	 * Create a PasswordField giving only the coordinates and leaving the rest to default. 
	 * The hint text will not automatically be 'Password'
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 */
	public PasswordField(int x, int y) {
		super(x, y);
		
		// Use our own displayable text which swaps in the asterisks
		super.displayText = new DisplayablePasswordText();
	}
	
	/**
	 * Create a PasswordField which will have Password as the hint text by default
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param width The width of the text area itself
	 * @param height The height of the text area itself
	 */
	public PasswordField(int x, int y, int width, int height) {
		// Password is the hint text we go with
		super(x, y, width, height, "Password");
		
		// Use our own displayable text which swaps in the asterisks
		super.displayText = new DisplayablePasswordText();
	}

	/**
	 * Create a PasswordField which will have Password as the hint text by default
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param width The width of the text area itself
	 * @param height The height of the text area itself
	 * @param firstSpriteID The ID of the first sprite in the set of text field sprites
	 */
	public PasswordField(int x, int y, int width, int height, int firstSpriteID) {
		// Password is the hint text we go with
		super(x, y, width, height, "Password", firstSpriteID);
		
		// Use our own displayable text which swaps in the asterisks
		super.displayText = new DisplayablePasswordText();
	}
	
	/**
	 * Create a PasswordField which will have possibly some different hint text
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param width The width of the text area itself
	 * @param height The height of the text area itself
	 * @param hintText Your own hints to the user
	 * @param firstSpriteID The ID of the first sprite in the set of text field sprites
	 */
	public PasswordField(int x, int y, int width, int height, String hintText, int firstSpriteID) {
		super(x, y, width, height, hintText, firstSpriteID);
		
		// Use our own displayable text which swaps in the asterisks
		super.displayText = new DisplayablePasswordText();
	}
	
	/**
	 * A surrogate string for the current text where it is the same length but all 
	 * of the characters are asterisks
	 * @return A surrogate string for current text with only asterisks
	 */
	private String starText()
	{
		// Treat it different if the hint is showing. Then we want to allow normal text.
		if(super.showingHint())
		{
			return super.getText();
		}
		// As compared to usual - where we use all asterisk characters
		else {
			char[] surrogateChars = new char[super.getText().length()];
			Arrays.fill(surrogateChars, '*');
			return new String(surrogateChars);
		}
	}
	
	
	/**
	 * Almost identical to the super class DisplayableText but will instead 
	 * display asterisks rather than the normal text. There will be the same number of asterisks 
	 * as plain text, with the different sizing taken care of.
	 */
	class DisplayablePasswordText extends DisplayableText {
		
		/** Create new displayable text on the premise the text field starts blank */
		public DisplayablePasswordText()
		{
			super();
		}
		
		/**
		 * Figure out what text should be displayed. 
		 * Upon completion, the displayText will have its text and indices changed to reflect what 
		 * should currently be drawn into the field. 
		 * @param g2d The graphics object currently being used for drawing
		 */
		protected void updateDisplay(Graphics2D g2d) 
		{
			FontMetrics metrics = g2d.getFontMetrics();
			
			// See if all of the text will fit. Then we'll just use all of the text. 
			int allTextWidth = metrics.stringWidth(starText());
			if(allTextWidth < getWidth())
			{
				// It'll all fit. Display all of it.
				displayAll();
				
				return;
			}
			
			// See if we're checking size from the left or right
			if(hintLeft)
			{
				determineLeftwise(metrics);
			}
			else {
				determineRightwise(metrics);
			}
		}

		/**
		 * Update the display by maintaining the right index and showing as much text as possible 
		 * moving right to left. The display field members are changed, the glyph vector is not invalidated.
		 * @param metrics A FontMetrics object to measure text with
		 */
		protected void determineRightwise(FontMetrics metrics) 
		{
			// Since right - start at the end
			int startIdx = rightIdx;
			int endIdx = startIdx;
			
			String surrogateText = starText();
			
			// Still go until we've checked if everything will fit...
			while(endIdx > 0)
			{
				int trialWidth = metrics.stringWidth(surrogateText.substring(endIdx, startIdx));
				
				// ... or until no more will fit. 
				if(trialWidth + 5 > getWidth())
				{
					endIdx++;
					break;
				} else {
					endIdx--;
				}
			}
			
			// And update the display similarly
			setFields(surrogateText.substring(endIdx, startIdx), endIdx, startIdx);
		}

		/**
		 * Update the display by maintaining the left index and showing as much text as possible 
		 * moving left to right. The display field members are changed, the glyph vector is not invalidated.
		 * @param metrics A FontMetrics object to measure text with
		 */
		protected void determineLeftwise(FontMetrics metrics) 
		{
			// Since left - start at the left end
			int startIdx = leftIdx;
			int endIdx = startIdx;
			
			String surrogateText = starText();
			
			// Go until we've checked if everything will fit...
			while(endIdx < surrogateText.length())
			{
				// Width of current subsection of text
				int trialWidth = metrics.stringWidth(surrogateText.substring(leftIdx, endIdx));
				
				// ... or until no more will fit. 
				if(trialWidth + 5 > getWidth())
				{
					endIdx--;
					break;
				} else {
					endIdx++;
				}
			}
			
			// Now we know how much to show. Update the display appropriately. 
			setFields(surrogateText.substring(startIdx, endIdx), startIdx, endIdx);
		}

		/**
		 * Set the field members to display all of the text currently in the field. 
		 * (As asterisks, of course.)
		 */
		protected void displayAll() 
		{
			String surrogateText = starText();
			
			// Display all of the text, from start to end. 
			setFields(surrogateText, 0, surrogateText.length());
		}
		
	}
	
}
