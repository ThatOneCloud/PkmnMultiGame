package net.cloud.gfx.elements.modal;

/**
 * A listener that will wait until the user has input their information or canceled. 
 */
public class InputDialogListener extends DialogListener<String> {
	
	/** The dialog this listener is... listening on */
	private final InputDialog dialog;
	
	/**
	 * Create a listener on the given dialog. This listener will allow you to 
	 * wait until the dialog has been read and acted on. 
	 * Creating this will attach a listener to the dialog.
	 * @param dialog The dialog to listen on
	 */
	public InputDialogListener(InputDialog dialog)
	{
		super();
		
		this.dialog = dialog;
		
		// A confirmation dialog has two buttons
		this.dialog.setConfirmListener(this::confirmAction);
		this.dialog.setCancelListener(this::cancelAction);
	}
	
	/**
	 * Method for the dialog to call when its confirm button has been pressed. 
	 * Provides the current text in the dialog as the result
	 */
	private void confirmAction()
	{
		notifyReady(dialog.getInputText());
	}
	
	/**
	 * Method for the dialog to call when its cancel button has been pressed.
	 * Provides null as a value, to indicate it was cancel (InputDialog.CANCELED)
	 */
	private void cancelAction()
	{
		notifyReady(InputDialog.CANCELED);
	}

}
