package net.cloud.gfx.interfaces;

import net.cloud.gfx.Mainframe;
import net.cloud.gfx.elements.Button;
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
		// TODO: action handler
		add(usernameField);
		
		passwordField = new PasswordField(CENTER_X + GAP, CENTER_Y - GAP - 22, 100, 22);
		passwordField.linkNextFocusable(passwordField, usernameField);
		// TODO: action handler
		add(passwordField);
		
		// Add a button to login
		Button loginButton = new Button("Log In", CENTER_X - GAP - 75, CENTER_Y + GAP, 75, 25);
		// TODO: Action handler
		add(loginButton);
		
		// Add a button to logout, for testing stuff
		Button logoutButton = new Button("Log Out", CENTER_X + GAP, CENTER_Y + GAP, 75, 25);
		// TODO: Action handler
		add(logoutButton);
	}

}
