package net.cloud.gfx.elements;

import java.util.function.BiConsumer;

/**
 * An encapsulated action for a FrameButton to take. A frame button may refer to one of these as a default, 
 * or when wrapping a frame around an element, one of these may be specified as the action to accompany 
 * a button being added. 
 * @see FramedElement
 * @see FrameButton
 */
public enum FrameButtonAction {
	
	/** An action where when the button is pressed, nothing happens */
	DO_NOTHING((frame, button) -> {}),
	
	/** An action that will close the frame by removing the element from its parent */
	REMOVE_ELEMENT((frame, button) -> frame.getParent().ifPresent((p) -> p.remove(frame)));
	
	/** The action to take. Has the frame and button as parameters. */
	private BiConsumer<FramedElement, AbstractButton> action;
	
	/**
	 * @param action The action
	 */
	private FrameButtonAction(BiConsumer<FramedElement, AbstractButton> action)
	{
		this.action = action;
	}
	
	/**
	 * Obtain the BiConsumer which is keeping the method for the action. 
	 * @return The action that will be performed by the a press
	 */
	public BiConsumer<FramedElement, AbstractButton> getAction()
	{
		return action;
	}
}