package net.cloud.gfx.elements.modal;

import java.util.Optional;

import net.cloud.client.util.function.InputValidator;
import net.cloud.gfx.constants.Colors;
import net.cloud.gfx.constants.FontConstants;
import net.cloud.gfx.constants.Fonts;
import net.cloud.gfx.elements.Button;
import net.cloud.gfx.elements.Text;
import net.cloud.gfx.elements.TextArea;
import net.cloud.gfx.elements.TextField;

/**
 * An input dialog is a basic yet powerful way of getting user input. It simply gets it in the form of a string. 
 * There are options to confirm the input and cancel the input. Canceling the input will yield a null result, 
 * although InputDialog.CANCELED can be used instead for clarity. The empty string will be the empty string. 
 * An InputValidator may also be provided. This allows the dialog to validate input before returning it, 
 * and also display a message as to what is wrong with the input. This is more effective than looping in using code 
 * as the dialog will stay in place and it's all handled for us :)
 */
public class InputDialog extends AbstractModalDialog {
	
	/** Constant that can be used for clarity, for when cancel was pressed. It's just null. */
	public static final String CANCELED = null;
	
	/** Padding from one element to another */
	private static final int INSET = 10;
	
	/**
	 * The text field being used to gather string input
	 */
	private TextField textField;
	
	/**
	 * The text label for showing invalid reasons
	 */
	private Text invalidLabel;
	
	/**
	 *  A method that will be notified when the 'okay' button is pressed. No return value, parameters, or exceptions.
	 */
	private Optional<Runnable> okayCallback;
	
	/**
	 *  A method that will be notified when the 'cancel' button is pressed. No return value, parameters, or exceptions.
	 */
	private Optional<Runnable> cancelCallback;
	
	/**
	 * An object that will check to see if what the user has entered is valid. Dependency injection! 
	 */
	private Optional<InputValidator<String>> validator;

	/**
	 * Minimal constructor. Create a dialog at the given location that will be the given size. 
	 * There will be two buttons on it, for confirmation or cancel, as well as a prompt message 
	 * asking them for input. Sandwiched between these will be a text field for entering input.
	 * @param prompt What you're asking the user to input
	 * @param x X location
	 * @param y Y location
	 * @param width Width of the interface
	 * @param height Height of the interface
	 */
	public InputDialog(String prompt, int x, int y, int width, int height)
	{
		this(prompt, x, y, width, height, null);
	}
	
	/**
	 * Create a dialog at the given location that will be the given size. 
	 * There will be two buttons on it, for confirmation or cancel, as well as a prompt message 
	 * asking them for input. Sandwiched between these will be a text field for entering input.
	 * There will be an InputValidator making sure input is okay before calling any outside listeners
	 * @param prompt What you're asking the user to input
	 * @param x X location
	 * @param y Y location
	 * @param width Width of the interface
	 * @param height Height of the interface
	 * @param validator The InputValidator that will check to make sure the given input is okay before confirming it out
	 */
	public InputDialog(String prompt, int x, int y, int width, int height, InputValidator<String> validator)
	{
		super(x, y, width, height);
		
		okayCallback = Optional.empty();
		cancelCallback = Optional.empty();
		this.validator = Optional.ofNullable(validator);
		
		// Constants we can jump back to and use
		final int X_CENTER = (width / 2);
		final int B_WIDTH = 100;
		final int B_HEIGHT = 25;
		final int TF_HEIGHT = 22;
		final int LABEL_HEIGHT = 20;
		
		// Build from bottom-up so we can get accurate sizing for the text area
		// Confirm button
		Button confirmButton = new Button("Okay", X_CENTER - B_WIDTH - INSET, height - INSET - B_HEIGHT, B_WIDTH, B_HEIGHT);
		add(confirmButton);
		confirmButton.setActionHandler(this::confirmButtonAction);

		// Cancel button
		Button cancelButton = new Button("Cancel", X_CENTER + INSET, height - INSET - B_HEIGHT, B_WIDTH, B_HEIGHT);
		add(cancelButton);
		cancelButton.setActionHandler(this::cancelButtonAction);
		
		// Input text field
		textField = new TextField(2*INSET, confirmButton.getY() - INSET - TF_HEIGHT, width - (4 * INSET), TF_HEIGHT, "");
		add(textField);
		textField.setActionHandler(this::textFieldAction);
		
		// A label to show when the input is invalid - although to start it won't show anything
		invalidLabel = new Text("", Text.PRIORITY, 2*INSET, textField.getY() - LABEL_HEIGHT, Fonts.DEFAULT.size(FontConstants.SIZE_SMALL), Colors.RED.get());
		add(invalidLabel);
		
		// Place the text area at the top. No scroll view this time, your prompt for input should not be that long...
		TextArea textArea = new TextArea(prompt, INSET, INSET, width - (2 * INSET));
		add(textArea);
	}
	
	/**
	 * Obtain the current text in the input field of this dialog. At no point should it be null, 
	 * it will at least be the empty string.
	 * @return The current text in the input field
	 */
	public String getInputText()
	{
		return textField.getText();
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
	 * Provide an InputValidator to this dialog. The dialog will check with the validator before 
	 * confirming whatever input is present. If it is invalid, the message is displayed in red on the dialog. 
	 * Setting this null will remove any existing validator
	 * @param validator The validator to check user input with
	 */
	public void setInputValidator(InputValidator<String> validator)
	{
		this.validator = Optional.ofNullable(validator);
	}
	
	/**
	 * When the confirm button is clicked, this is called. It'll relay the action to 
	 * whoever is listening outside of this dialog. Functional programming is so cool!
	 * @param b The button that was clicked
	 */
	private void confirmButtonAction(Button b)
	{
		// Is there a validator we need to run the input through, first?
		if(validator.isPresent())
		{
			// Does the current input pass its standards?
			String input = textField.getText();
			String results = validator.get().validate(input);
			
			// No, no they do not.
			if(results != InputValidator.VALID)
			{
				// Change the invalid text label and return - we don't want to run the callback just yet
				invalidLabel.setText(results);
				return;
			}
		}
		
		// Now inform the action listener if there is one
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
	
	/**
	 * When the text field is acted on (has its text entered) it'll relay that action to this method, 
	 * which will in turn relay it to whoever is listening for confirmation on this dialog.
	 * @param field The text field
	 * @param text The text in the field
	 */
	private void textFieldAction(TextField field, String text)
	{
		// Take the same action as if the confirmation button was clicked
		okayCallback.ifPresent((callback) -> callback.run());
	}

}
