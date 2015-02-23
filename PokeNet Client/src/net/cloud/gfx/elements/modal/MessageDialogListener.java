package net.cloud.gfx.elements.modal;

/**
 * A listener that will wait until the user has confirmed that they have seen the message, 
 * before returning. There is no return value, it's simply a button click. 
 * Of course, normal restrictions apply, make sure the wait will not deadlock your application.
 */
public class MessageDialogListener extends DialogListener<Void> {
	
	/** The dialog this listener is... listening on */
	private final MessageDialog dialog;
	
	/**
	 * Create a listener on the given dialog. This listener will allow you to 
	 * wait until the dialog has been read and closed. 
	 * Creating this will attach a listener to the dialog.
	 * @param dialog The dialog to listen on
	 */
	public MessageDialogListener(MessageDialog dialog)
	{
		super();
		
		this.dialog = dialog;
		
		// A message dialog only has the one button
		this.dialog.setConfirmListener(this::readyAction);
	}
	
	/**
	 * Method for the dialog to call when it's ready
	 */
	private void readyAction()
	{
		notifyReady(null);
	}

}
