package net.cloud.gfx.elements;

/**
 * An enumerable type that defines the various types of buttons that may be added to a frame. 
 * Yes, they are enumerable rather than adding any button. It's less to restrict possibilities than it 
 * is to make the creation code for a FramedElement cleaner and more concise from a client perspective.
 */
public enum FrameButton {
	
	/** The classic button with an 'X' or whatever to close the frame. It's default action is REMOVE_ELEMENT */
	CLOSE(15, FrameButtonAction.REMOVE_ELEMENT);
	
	/** The default image */
	private int defaultSprite;
	
	/** The default action to take when the button is pressed */
	private FrameButtonAction defaultAction;
	
	/**
	 * @param defaultSprite The default image
	 * @param defaultAction The default action
	 */
	private FrameButton(int defaultSprite, FrameButtonAction defaultAction)
	{
		this.defaultSprite = defaultSprite;
		this.defaultAction = defaultAction;
	}
	
	/**
	 * Obtain the sprite ID that this button will use by default, if no other image is specified. 
	 * The image will come from the Button sprite set.
	 * @return The Sprite ID of the image that will be used for the button
	 */
	public int getDefaultSprite()
	{
		return defaultSprite;
	}
	
	/**
	 * The action that will be performed when the button is pressed if no other action is specified.
	 * @return The action to take when the button is pressed
	 */
	public FrameButtonAction getDefaultAction()
	{
		return defaultAction;
	}
}