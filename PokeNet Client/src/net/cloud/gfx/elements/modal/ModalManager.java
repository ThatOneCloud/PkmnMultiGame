package net.cloud.gfx.elements.modal;

import java.util.Optional;

import net.cloud.gfx.elements.Element;

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
	 */
	public void register(AbstractModalDialog modal)
	{
		// Make sure there is not already one
		if(currentModal.isPresent())
		{
			// TODO: Return exception or false or something?
			return;
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
	
}
