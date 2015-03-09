package net.cloud.gfx.elements.modal;

import java.util.Optional;

import net.cloud.client.util.function.InputValidator;
import net.cloud.gfx.Mainframe;
import net.cloud.gfx.elements.Container;
import net.cloud.gfx.elements.Element;
import net.cloud.gfx.elements.Interface;
import net.cloud.gfx.elements.decorator.DraggableElement;
import net.cloud.gfx.elements.decorator.FramedElement;
import net.cloud.gfx.focus.FocusController;

/**
 * The front-end and work center for modal dialog creation. Offers quick and easy ways of creating 
 * modal dialogs, doing much of the configuration and background effort. Also allows a calling thread to 
 * be blocked until input is received on the dialog. (Great for grabbing quick user input) <br>
 * In general, the methods will allow an outside call to request input from a modal dialog, and have 
 * that input returned to them via blocking behavior. 
 */
public class ModalManager {
	
	/** Singleton instance */
	private static ModalManager instance;
	
	/** The modal dialog that is currently up, if any */
	private Optional<AbstractModalDialog> currentModal;
	
	/** The factory we're gonna use for dialog creation */
	private ModalFactory factory = ModalFactory.newFactory();
	
	/** Private constructor for singleton pattern */
	private ModalManager()
	{
		currentModal = Optional.empty();
	}
	
	/**
	 * Obtain a reference to the singleton instance of the ModalManager class. 
	 * This allows for convenient usage of modal dialog pop-up interfaces. 
	 * @return A singleton reference
	 */
	public static ModalManager instance()
	{
		if(instance == null)
		{
			synchronized(ModalManager.class)
			{
				if(instance == null)
				{
					instance = new ModalManager();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Check to see if the given element either is, or is a child on, the current modal dialog. 
	 * If one does not exist, this will simply return false.
	 * @param element The element to check
	 * @return True if the given element is part of the modal dialog
	 */
	public static boolean elementWithinModal(Element element)
	{
		// If there isn't a modal interface, then the result is just false
		if(!instance().getCurrentModal().isPresent())
		{
			return false;
		}
		
		// There is, so is the element part of it? Start looking at the top
		while(!element.equals(instance().getCurrentModal().get()))
		{
			// Whatever element we're currently at has no parent, and we haven't hit the modal yet. 
			if(!element.getParent().isPresent())
			{
				// So we have nowhere else to look. Give up the hunt
				return false;
			}

			// From here, we know there is a parent. To continue looking, we'll move the current element up to that parent.
			element = element.getParent().get().getParent();
		}

		// Get here by breaking the while-loop (i.e. the current focused element eventually resolved to the modal)
		return true;
	}
	
	/**
	 * Show a message dialog on the root interface. The dialog will be centered, and placed within a draggable 
	 * frame. It will be reasonably sized. This method will take care of adding the dialog to the interface 
	 * and other background work. This method will block until the 'okay' button is clicked on the dialog.
	 * @param title The title to show on the frame
	 * @param message The message to display
	 * @throws ModalException If the dialog could not be shown. Results will not be available in this situation.
	 */
	public void showMessageDialog(String title, String message) throws ModalException
	{
		// Grab it, short name, we'll need the root interface often
		Interface root = Mainframe.instance().gfx().rootPanel().getQuasiRoot();
		
		// Well, we need a dialog to work with
		MessageDialog dialog = factory.createMessageDialog(root, message);
		
		// Set up a listener so that when the dialog is acted on, the method can return
		MessageDialogListener dialogListener = new MessageDialogListener(dialog);
		
		// We don't care about return value. Normally we'd return this result.
		// Anyways, this takes care of the rest of the generic process
		genericDialogStuff(title, root, dialog, dialogListener);
	}
	
	/**
	 * Show a confirmation dialog on the root interface. The dialog will be centered, and placed within a draggable 
	 * frame. It will be reasonably sized. This method will take care of adding the dialog to the interface 
	 * and other background work. This method will block until a button is clicked on the dialog.
	 * @param title The title to show on the frame
	 * @param prompt The message to display
	 * @return True if the user confirmed, false if they chose cancel
	 * @throws ModalException If the dialog could not be shown. Results will not be available in this situation.
	 */
	public boolean showConfirmationDialog(String title, String prompt) throws ModalException
	{
		// Grab it, short name, we'll need the root interface often
		Interface root = Mainframe.instance().gfx().rootPanel().getQuasiRoot();
		
		// Well, we need a dialog to work with
		ConfirmationDialog dialog = factory.createConfirmationDialog(root, prompt);
		
		// Set up a listener so that when the dialog is acted on, the method can return
		ConfirmationDialogListener dialogListener = new ConfirmationDialogListener(dialog);
		
		// This time it knows the result is a boolean. Such cool type inference!
		return genericDialogStuff(title, root, dialog, dialogListener);
	}
	
	/**
	 * Show an input dialog on the root interface. The dialog will be centered, and placed within a draggable 
	 * frame. It will be reasonably sized. This method will take care of adding the dialog to the interface 
	 * and other background work. This method will block until a button is clicked on the dialog.
	 * @param title The title to show on the frame
	 * @param prompt The message to display
	 * @return The string of the user input, or null (InputDialog.CANCELED) if they canceled
	 * @throws ModalException If the dialog could not be shown. Results will not be available in this situation.
	 */
	public String showInputDialog(String title, String prompt) throws ModalException
	{
		// Grab it, short name, we'll need the root interface often
		Interface root = Mainframe.instance().gfx().rootPanel().getQuasiRoot();
		
		// Well, we need a dialog to work with
		InputDialog dialog = factory.createInputDialog(root, prompt);
		
		// Set up a listener so that when the dialog is acted on, the method can return
		InputDialogListener dialogListener = new InputDialogListener(dialog);
		
		// This time it knows the result is a boolean. Such cool type inference!
		return genericDialogStuff(title, root, dialog, dialogListener);
	}
	
	/**
	 * Show an input dialog on the root interface. The dialog will be centered, and placed within a draggable 
	 * frame. It will be reasonably sized. This method will take care of adding the dialog to the interface 
	 * and other background work. This method will block until a button is clicked on the dialog. 
	 * This variant will provide an input validator to check input before it is returned
	 * @param title The title to show on the frame
	 * @param prompt The message to display
	 * @param inputValidator
	 * @return Validated user input, or null (InputDialog.CANCELED) if they canceled
	 * @throws ModalException If the dialog could not be shown. Results will not be available in this situation.
	 */
	public String showInputDialog(String title, String prompt, InputValidator<String> inputValidator) throws ModalException
	{
		// Grab it, short name, we'll need the root interface often
		Interface root = Mainframe.instance().gfx().rootPanel().getQuasiRoot();
		
		// Well, we need a dialog to work with
		InputDialog dialog = factory.createInputDialog(root, prompt, inputValidator);
		
		// Set up a listener so that when the dialog is acted on, the method can return
		InputDialogListener dialogListener = new InputDialogListener(dialog);
		
		// This time it knows the result is a boolean. Such cool type inference!
		return genericDialogStuff(title, root, dialog, dialogListener);
	}
	
	/**
	 * Obtain a reference to the current modal dialog, if there is one. The value is in an optional. 
	 * There will only ever be on modal dialog at a time, and another cannot be registered until it is removed.
	 * @return The [possibly present] modal dialog currently registered
	 */
	public Optional<AbstractModalDialog> getCurrentModal()
	{
		return currentModal;
	}
	
	/**
	 * Attempt to register a new modal dialog as the current modal dialog in the system. 
	 * This will only fail if there is already another modal dialog registered. Otherwise, this does 
	 * not check any of the other assumptions, such as requiring a parent, and does not take other actions, 
	 * such as adding the dialog to the parent.
	 * @param modal The new modal dialog
	 * @throws ModalException If the dialog could not be registered
	 */
	public void register(AbstractModalDialog modal) throws ModalException
	{
		// Make sure there is not already one
		if(currentModal.isPresent())
		{
			throw new ModalException("Could not register modal dialog - one is already present");
		}
		
		currentModal = Optional.of(modal);
	}
	
	/**
	 * Deregister the existing modal dialog. This does not take extra action, such as removing the dialog 
	 * or clearing its resources. If there is no current modal dialog, nothing will happen.
	 */
	public void deregister()
	{
		currentModal = Optional.empty();
	}
	
	/**
	 * I seriously could not think of a name for this one. It takes the various generic steps needed for showing a dialogue. 
	 * It will wrap the dialog in a draggable frame, put the dialog on screen, block until results are available, 
	 * remove the dialog from the screen, and then return the result from the dialog.
	 * @param title The title that will be on the frame
	 * @param parent The container that the dialog will be placed in
	 * @param dialog The dialog itself
	 * @param listener A listener that provides blocking and result obtaining functionality
	 * @return The coalesced result from the dialog, obtained via the listener
	 * @throws ModalException If results could not be reliably obtained
	 */
	private <T> T genericDialogStuff(String title, Container<? super Element> parent, AbstractModalDialog dialog, DialogListener<T> listener)
			throws ModalException
	{
		// Wrap it in a frame wrapped in a draggable element
		DraggableElement dragFrame = decoratedDialog(title, dialog);
		
		// Show the dialog and register it
		showDialog(parent, dialog, dragFrame);
		
		// The listener provides a coalesced result, and this will block until that result is available
		T results = null;
		try {
			results = listener.waitForValue();
		} catch (ModalException e) {
			// The wait didn't happen, our value is invalid. We'll re-throw the exception but we have to rollback the dialog first
			removeDialog(parent, dragFrame);
			
			throw e;
		}
		
		// Remove the dialog from the parent and deregister it
		removeDialog(parent, dragFrame);
		
		return results;
	}
	
	/**
	 * Wrap a dialog in a frame which is then wrapped in a draggable element. 
	 * This can only be dragged from the title of the frame.
	 * @param title The title that will be on the frame
	 * @param dialog The dialog itself
	 * @return A DraggableElement wrapped around the dialog
	 */
	private DraggableElement decoratedDialog(String title, AbstractModalDialog dialog)
	{
		// Decorator wrapping
		FramedElement framedDialog = new FramedElement(title, dialog);
		DraggableElement draggableDialog = new DraggableElement(framedDialog);
		
		// It can only be dragged from the title of the frame
		draggableDialog.addStartBound(framedDialog.getTitleBounds());
		
		return draggableDialog;
	}
	
	/**
	 * First registers the dialog, and then adds it to the parent
	 * @param parent The container that the dialog will be placed in
	 * @param dialog The dialog itself
	 * @param decorator The decorator wrapping the dialog
	 * @throws ModalException Registration of the dialog failed
	 */
	private void showDialog(Container<? super Element> parent, AbstractModalDialog dialog, Element decorator) throws ModalException
	{
		// Register then add.
		register(dialog);
		
		if(!FocusController.instance().register(decorator))
		{
			// Make sure we rollback changes thus far
			deregister();
			
			throw new ModalException("Could not register focus on new modal dialog");
		}
		
		// Add is last so if an exception comes up, it will never be added
		parent.add(decorator);
	}
	
	/**
	 * First removes the dialog from the parent, then deregisters it
	 * @param parent The container that the dialog will be placed in
	 * @param decorator The decorator wrapping the dialog
	 */
	private void removeDialog(Container<? super Element> parent, Element decorator)
	{
		// Opposite order from showDialog
		parent.remove(decorator);
		FocusController.instance().deregister();
		deregister();
	}
	
}
