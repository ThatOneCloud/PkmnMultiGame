package net.cloud.gfx.elements.modal;

/**
 * A listener that will wait until the user has chosen an option. The options are confirm and not confirm, 
 * so the results are true and false, respectively.
 * Of course, normal restrictions apply, make sure the wait will not deadlock your application.
 */
public class ConfirmationDialogListener extends DialogListener<Boolean> {
	
	/** The dialog this listener is... listening on */
	private final ConfirmationDialog dialog;
	
	/**
	 * Create a listener on the given dialog. This listener will allow you to 
	 * wait until the dialog has been read and acted on. 
	 * Creating this will attach a listener to the dialog.
	 * @param dialog The dialog to listen on
	 */
	public ConfirmationDialogListener(ConfirmationDialog dialog)
	{
		super();
		
		this.dialog = dialog;
		
		// A confirmation dialog has two buttons
		this.dialog.setConfirmListener(this::confirmAction);
		this.dialog.setCancelListener(this::cancelAction);
	}
	
	/**
	 * Method for the dialog to call when its confirm button has been pressed
	 */
	private void confirmAction()
	{
		notifyReady(true);
	}
	
	/**
	 * Method for the dialog to call when its cancel button has been pressed
	 */
	private void cancelAction()
	{
		notifyReady(false);
	}

}
