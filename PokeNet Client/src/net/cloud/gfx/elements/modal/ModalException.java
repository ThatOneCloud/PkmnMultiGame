package net.cloud.gfx.elements.modal;

/**
 * An exception thrown by the modal manager when a request to show a modal dialog could not be 
 * completed. This may happen at any point during the process of showing a dialog, but regardless 
 * the result from the dialog is compromised. The message within this exception should provide 
 * some contextual information such as the title on the dialog's frame and what kind of dialog it was.
 * The manager should probably make some effort to remove the dialog so the screen isn't stuck...
 */
public class ModalException extends Exception {

	/** Eclipse generated serial ID */
	private static final long serialVersionUID = -7621498005526490655L;
	
	/**
	 * A ModalException with an unspecified cause.
	 * @param reason The message
	 */
	public ModalException(String reason)
	{
		super(reason);
	}
	
	/**
	 * A ModalException with an unspecified cause.
	 * @param dialogType The type of dialog. I.e. "message" or "confirmation"
	 * @param frameTitle The title on the frame (assuming the dialog was wrapped in a frame)
	 */
	public ModalException(String dialogType, String frameTitle)
	{
		super("Exception while showing " + dialogType + " dialog titled \"" + frameTitle + "\"");
	}
	
	/**
	 * A ModalException with a chained cause
	 * @param dialogType The type of dialog. I.e. "message" or "confirmation"
	 * @param frameTitle The title on the frame (assuming the dialog was wrapped in a frame)
	 * @param cause The reason the exception occurred
	 */
	public ModalException(String dialogType, String frameTitle, Throwable cause)
	{
		super("Exception while showing " + dialogType + " dialog titled \"" + frameTitle + "\"", cause);
	}

}
