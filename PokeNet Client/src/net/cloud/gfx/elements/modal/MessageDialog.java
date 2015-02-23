package net.cloud.gfx.elements.modal;

import java.util.Optional;

import net.cloud.gfx.elements.Button;
import net.cloud.gfx.elements.TextArea;

/**
 * A very simple dialog. It presents some text message within its interface, and offers a single 
 * button so that the user may confirm that they've seen the message. The text is placed at the top 
 * left of the interface and will line wrap. The button will appear below the text.
 */
public class MessageDialog extends AbstractModalDialog {

	/** Gap between the edge of this interface and the text area */
	private static final int INSET = 15;
	
	/**
	 *  A method that will be notified when the 'okay' button is pressed. No return value, parameters, or exceptions.
	 *  Before this is called, this dialog will attempt to close itself by removing itself from its parent.
	 */
	private Optional<Runnable> buttonCallback;
	
	/**
	 * Minimal constructor. The message and location are given, as well as the dimensions of the interface. 
	 * The button will always be placed at the bottom, regardless of the amount of text.
	 * @param message The message
	 * @param x X location
	 * @param y Y location
	 * @param width Width of the interface. Text will wrap based on this width
	 * @param height Height of the interface. 
	 */
	public MessageDialog(String message, int x, int y, int width, int height) {
		// Start with a height of one. We don't really know yet, we'll adjust it soon
		super(x, y, width, height);
		
		buttonCallback = Optional.empty();
	
		// Place a text area, inset a bit to give it a border
		TextArea textArea = new TextArea(message, INSET, INSET, width - (2 * INSET));
		add(textArea);
		
		// Place a button at the bottom of the interface, in the center
		int X_CENTER = (width / 2);
		int B_WIDTH = 100;
		int B_HEIGHT = 25;
		Button button = new Button("Okay", X_CENTER - (B_WIDTH / 2), height - INSET - B_HEIGHT, B_WIDTH, B_HEIGHT);
		add(button);
		
		// When the button is clicked it'll tell us and then we'll tell whoever is listening to us. Functional!
		button.setActionHandler(this::buttonAction);
	}
	
	/**
	 * Provide a listener to this dialog that will be informed when the 'okay' button is pressed. 
	 * There are no return values or parameters, so a Runnable is fitting. The dialog will be closed 
	 * before this is called. 
	 * Null may be provided to remove an existing listener
	 * @param listener Action to take when the 'okay' button is clicked
	 */
	public void setConfirmListener(Runnable listener)
	{
		buttonCallback = Optional.ofNullable(listener);
	}
	
	/**
	 * When the button is clicked, this is called. It'll relay the action to 
	 * whoever is listening outside of this dialog. Functional programming is so cool!
	 * @param b The button that was clicked
	 */
	private void buttonAction(Button b)
	{
		// And now inform the action listener if there is one
		buttonCallback.ifPresent((callback) -> callback.run());
	}

}
