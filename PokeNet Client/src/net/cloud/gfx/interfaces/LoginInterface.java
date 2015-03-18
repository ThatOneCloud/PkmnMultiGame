package net.cloud.gfx.interfaces;

import net.cloud.client.entity.player.LoginHandler;
import net.cloud.gfx.Mainframe;
import net.cloud.gfx.constants.Colors;
import net.cloud.gfx.elements.Button;
import net.cloud.gfx.elements.CenteredText;
import net.cloud.gfx.elements.Interface;
import net.cloud.gfx.elements.PasswordField;
import net.cloud.gfx.elements.TextField;

/**
 * The login interface, to present the player with a way to uh.. login to the game. 
 * Has fields for the username and password, as well as a button to login.
 */
public class LoginInterface extends Interface {
	
	/** The username field */
	private TextField usernameField;
	
	/** The password field */
	private PasswordField passwordField;
	
	/** A text label to show messages back to the player */
	private CenteredText messageLabel;
	
	/**
	 * Create a LoginInterface. It will be sized to fill up the entire game screen.
	 */
	public LoginInterface()
	{
		super(0, 0, Mainframe.WIDTH, Mainframe.HEIGHT);
		
		// Local constants
		int CENTER_X = (Mainframe.WIDTH / 2);
		int CENTER_Y = (Mainframe.HEIGHT / 2);
		int GAP = 10;
		
		// Add fields for the username and password
		usernameField = new TextField(CENTER_X - GAP - 100, CENTER_Y - GAP - 22, 100, 22, "Username");
		usernameField.setActionHandler(this::loginViaFieldAction);
		add(usernameField);
		
		passwordField = new PasswordField(CENTER_X + GAP, CENTER_Y - GAP - 22, 100, 22);
		passwordField.setActionHandler(this::loginViaFieldAction);
		add(passwordField);
		
		usernameField.linkNextFocusable(usernameField, passwordField);
		passwordField.linkNextFocusable(passwordField, usernameField);
		
		// Add a button to login
		Button loginButton = new Button("Log In", CENTER_X - GAP - 75, CENTER_Y + GAP, 75, 25);
		loginButton.setActionHandler(this::loginViaButtonAction);
		add(loginButton);
		
		// Add a button to logout, for testing stuff
		Button logoutButton = new Button("Log Out", CENTER_X + GAP, CENTER_Y + GAP, 75, 25);
		logoutButton.setActionHandler(this::logoutViaButtonAction);
		add(logoutButton);
		
		// Prep the feedback message, although there will not be any text to start
		messageLabel = new CenteredText("", GAP, CENTER_Y - GAP - 50, Mainframe.WIDTH - 2*GAP, 25);
		messageLabel.setColor(Colors.RED.get());
		add(messageLabel);
	}
	
	/**
	 * A method to call when either of the text fields are acted on. 
	 * If both are filled out, then try the login process
	 * @param field Text field
	 * @param text Text in that field
	 */
	private void loginViaFieldAction(TextField field, String text)
	{
		// We don't really need this fields info here, just move onto generic login code
		doLogin();
	}
	
	/**
	 * Comparable to loginViaFieldAction
	 * @param button The button that was clicked
	 */
	private void loginViaButtonAction(Button button)
	{
		// Like above, don't really need info on the button itself
		doLogin();
	}
	
	/**
	 * A method to call when the log out button is clicked. Does the log out process
	 * @param button The button that was clicked
	 */
	private void logoutViaButtonAction(Button button)
	{
		doLogout();
	}
	
	/**
	 * Kick off the login process, by calling on the LoginHandler to do so. 
	 * If the text fields don't have anything in them, a message is shown and nothing happens.
	 */
	private void doLogin()
	{
		// Call it redundant, but this is a good time to clear the message label
		messageLabel.setText("");
		
		// Neither field can be empty for this to proceed
		if(usernameField.isEmpty() || passwordField.isEmpty())
		{
			messageLabel.setText("You kinda need to fill in both fields...");
			return;
		}
		
		// Call on the login handler to take the wheel (again - functional programming - cool!)
		LoginHandler.startLogin(usernameField.getText(), passwordField.getText(), messageLabel::setText);
	}
	
	/**
	 * Kick off the logout process, by calling on the LoginHandler to do so. 
	 * Yeah, it's got multiple talents
	 */
	private void doLogout()
	{
		LoginHandler.startLogout(messageLabel::setText);
	}

}
