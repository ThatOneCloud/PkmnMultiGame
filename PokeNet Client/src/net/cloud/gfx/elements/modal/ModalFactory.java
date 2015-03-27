package net.cloud.gfx.elements.modal;

import net.cloud.client.util.function.InputValidator;
import net.cloud.gfx.elements.Element;

/**
 * An instanced factory class. Contains the code to create various modal dialogs.
 * Most creation methods will also decorate the dialog. This class is primarily intended 
 * for use by the ModalManager, but may also be used wherever else when a feature-enriched 
 * modal dialog is desired.
 */
public class ModalFactory {
	
	/** Standard width of a MessageDialog */
	private static final int MSG_WIDTH = 300;
	
	/** Standard height of a MessageDialog */
	private static final int MSG_HEIGHT = 200;
	
	/**
	 * Create a ModalFactory for usage. Sure, you could just use the constructor. 
	 * But this <i>is</i> a factory after all.
	 * @return A ModalFactory to create dialogs
	 */
	public static ModalFactory newFactory()
	{
		return new ModalFactory();
	}
	
	/**
	 * Create a message dialog meant to be placed in the center of the given element. 
	 * (The location will be centered based on the element's width and height) 
	 * Nothing extra will be added, a dialog will just be created.
	 * @param parent The element which will contain this dialog
	 * @param message The message to show on the dialog
	 * @return A MessageDialog centered in the parent, with the given message
	 */
	public MessageDialog createMessageDialog(Element parent, String message)
	{
		// Figure out where the location should be, using the default dimensions
		int locX = (parent.getWidth() - MSG_WIDTH) / 2;
		int locY = (parent.getHeight() - MSG_HEIGHT) / 2;
		
		return new MessageDialog(message, locX, locY, MSG_WIDTH, MSG_HEIGHT);
	}
	
	/**
	 * Create a confirmation dialog meant to be placed in the center of the given element. 
	 * (The location will be centered based on the element's width and height) 
	 * Nothing extra will be added, a dialog will just be created.
	 * @param parent The element which will contain this dialog
	 * @param prompt The message to show on the dialog
	 * @return A ConfirmationDialog centered in the parent, with the given message
	 */
	public ConfirmationDialog createConfirmationDialog(Element parent, String prompt)
	{
		// Figure out where the location should be, using the default dimensions
		int locX = (parent.getWidth() - MSG_WIDTH) / 2;
		int locY = (parent.getHeight() - MSG_HEIGHT) / 2;
		
		return new ConfirmationDialog(prompt, locX, locY, MSG_WIDTH, MSG_HEIGHT);
	}
	
	/**
	 * Create an input dialog meant to be placed in the center of the given element. 
	 * (The location will be centered based on the element's width and height) 
	 * Nothing extra will be added, a dialog will just be created.
	 * @param parent The element which will contain this dialog
	 * @param prompt The message to show on the dialog
	 * @return An InputDialog centered in the parent, with the given message
	 */
	public InputDialog createInputDialog(Element parent, String prompt)
	{
		// Figure out where the location should be, using the default dimensions
		int locX = (parent.getWidth() - MSG_WIDTH) / 2;
		int locY = (parent.getHeight() - MSG_HEIGHT) / 2;
		
		return new InputDialog(prompt, locX, locY, MSG_WIDTH, MSG_HEIGHT);
	}
	
	/**
	 * Create an input dialog meant to be placed in the center of the given element. 
	 * (The location will be centered based on the element's width and height) 
	 * Nothing extra will be added, a dialog will just be created.
	 * @param parent The element which will contain this dialog
	 * @param prompt The message to show on the dialog
	 * @param inputValidator An object to make sure the dialog returns only valid input
	 * @return An InputDialog centered in the parent, with the given message
	 */
	public InputDialog createInputDialog(Element parent, String prompt, InputValidator<String> inputValidator)
	{
		// Figure out where the location should be, using the default dimensions
		int locX = (parent.getWidth() - MSG_WIDTH) / 2;
		int locY = (parent.getHeight() - MSG_HEIGHT) / 2;
		
		return new InputDialog(prompt, locX, locY, MSG_WIDTH, MSG_HEIGHT, inputValidator);
	}

}
