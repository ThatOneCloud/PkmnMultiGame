package net.cloud.gfx.elements.modal;

import java.util.Optional;

import net.cloud.gfx.elements.Button;
import net.cloud.gfx.elements.ScrollView;
import net.cloud.gfx.elements.TextArea;
import net.cloud.gfx.elements.ScrollView.BarVisibility;

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
	public MessageDialog(String message, int x, int y, int width, int height)
	{
		super(x, y, width, height);
		
		buttonCallback = Optional.empty();
		
		// Place a button at the bottom of the interface, in the center
		int X_CENTER = (width / 2);
		int B_WIDTH = 100;
		int B_HEIGHT = 25;
		Button button = new Button("Okay", X_CENTER - (B_WIDTH / 2), height - INSET - B_HEIGHT, B_WIDTH, B_HEIGHT);
		add(button);
		
		// Place a text area, by placing it in a scroll view. The view will only show up when needed
		TextArea textArea = new TextArea(message, INSET, 0, width - (2 * INSET));
		ScrollView view = new ScrollView(textArea, INSET, INSET,
				width - (2 * INSET), height - 3*INSET - B_HEIGHT,
				BarVisibility.WHEN_NEEDED, BarVisibility.WHEN_NEEDED);
		textArea.setWidth(view.viewWidthWithBar() - textArea.getX());
		view.setFrameHiding(true);
		add(view);
		
		// When the button is clicked it'll tell us and then we'll tell whoever is listening to us. Functional!
		button.setActionHandler(this::buttonAction);
	}
	
	/**
	 * Provide a listener to this dialog that will be informed when the 'okay' button is pressed. 
	 * There are no return values or parameters, so a Runnable is fitting.
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
