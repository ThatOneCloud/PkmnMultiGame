package net.cloud.gfx.elements.modal;

import java.util.Optional;

import net.cloud.gfx.elements.Button;
import net.cloud.gfx.elements.ScrollView;
import net.cloud.gfx.elements.TextArea;
import net.cloud.gfx.elements.ScrollView.BarVisibility;

/**
 * A confirmation dialog is a dialog that asks the user a question and then allows them to either confirm or deny. 
 * The question is presented similar to a message dialog, in a text area that will employ wrapping and scrolling. 
 * The buttons are centered on the bottom
 */
public class ConfirmationDialog extends AbstractModalDialog {
	
	/** Padding from one element to another */
	private static final int INSET = 15;
	
	/**
	 *  A method that will be notified when the 'okay' button is pressed. No return value, parameters, or exceptions.
	 */
	private Optional<Runnable> okayCallback;
	
	/**
	 *  A method that will be notified when the 'cancel' button is pressed. No return value, parameters, or exceptions.
	 */
	private Optional<Runnable> cancelCallback;

	/**
	 * Minimal constructor. Create a dialog at the given location that will be the given size. 
	 * There will be two buttons on it, for confirmation or no, as well as a prompt message 
	 * asking them whether or not they confirm.
	 * @param prompt The question you're asking the user
	 * @param x X location
	 * @param y Y location
	 * @param width Width of the interface
	 * @param height Height of the interface
	 */
	public ConfirmationDialog(String prompt, int x, int y, int width, int height)
	{
		super(x, y, width, height);
		
		// Constants we can jump back to and use
		final int X_CENTER = (width / 2);
		final int B_WIDTH = 100;
		final int B_HEIGHT = 25;
		
		okayCallback = Optional.empty();
		cancelCallback = Optional.empty();
		
		// Place a text area, by placing it in a scroll view. The view will only show up when needed
		TextArea textArea = new TextArea(prompt, INSET, 0, width - (2 * INSET));
		ScrollView view = new ScrollView(textArea, INSET, INSET,
				width - (2 * INSET), height - 3*INSET - B_HEIGHT,
				BarVisibility.WHEN_NEEDED, BarVisibility.WHEN_NEEDED);
		textArea.setWidth(view.viewWidthWithBar() - textArea.getX());
		view.setFrameHiding(true);
		add(view);
		
		// Confirm button
		Button confirmButton = new Button("Okay", X_CENTER - B_WIDTH - INSET, height - INSET - B_HEIGHT, B_WIDTH, B_HEIGHT);
		add(confirmButton);
		confirmButton.setActionHandler(this::confirmButtonAction);
		
		// Cancel button
		Button cancelButton = new Button("Cancel", X_CENTER + INSET, height - INSET - B_HEIGHT, B_WIDTH, B_HEIGHT);
		add(cancelButton);
		cancelButton.setActionHandler(this::cancelButtonAction);
	}
	
	/**
	 * Provide a listener to this dialog that will be informed when the 'okay' button is pressed. 
	 * There are no return values or parameters, so a Runnable is fitting.
	 * Null may be provided to remove an existing listener
	 * @param listener Action to take when the 'okay' button is clicked
	 */
	public void setConfirmListener(Runnable listener)
	{
		okayCallback = Optional.ofNullable(listener);
	}
	
	/**
	 * Provide a listener to this dialog that will be informed when the 'cancel' button is pressed. 
	 * There are no return values or parameters, so a Runnable is fitting.
	 * Null may be provided to remove an existing listener
	 * @param listener Action to take when the 'cancel' button is clicked
	 */
	public void setCancelListener(Runnable listener)
	{
		cancelCallback = Optional.ofNullable(listener);
	}
	
	/**
	 * When the confirm button is clicked, this is called. It'll relay the action to 
	 * whoever is listening outside of this dialog. Functional programming is so cool!
	 * @param b The button that was clicked
	 */
	private void confirmButtonAction(Button b)
	{
		// And now inform the action listener if there is one
		okayCallback.ifPresent((callback) -> callback.run());
	}
	
	/**
	 * When the cancel button is clicked, this is called. It'll relay the action to 
	 * whoever is listening outside of this dialog. Functional programming is so cool!
	 * @param b The button that was clicked
	 */
	private void cancelButtonAction(Button b)
	{
		// And now inform the action listener if there is one
		cancelCallback.ifPresent((callback) -> callback.run());
	}
	

}
