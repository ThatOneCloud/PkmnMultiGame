package net.cloud.client.entity.player;

import java.util.Optional;
import java.util.function.Consumer;

import net.cloud.client.Client;
import net.cloud.client.event.task.TaskEngine;
import net.cloud.client.game.World;
import net.cloud.client.game.action.ButtonActionID;
import net.cloud.gfx.Mainframe;
import net.cloud.gfx.elements.modal.ModalManager;
import net.cloud.gfx.interfaces.LoginInterface;

/**
 * Good old fashioned static handler class, to encapsulate the logic for the login process.
 */
public class LoginHandler {
	
	/** How long will we wait for the server to reply before giving up */
	public static final long TIMEOUT = 5000;
	
	/** Grace period between channel closing and actually checking for disconnect */
	public static final long DC_GRACE = 2000;
	
	/** The most recently passed in callback function for showing a message regarding login */
	private static Optional<Consumer<String>> currentMessageCallback = Optional.empty();
	
	/**
	 * Attempt the beginning of the login process. This involves connecting to the server, and 
	 * sending the login credentials. From there, the server should reply with whether or not 
	 * login is successful. It is possible that connecting to the server fails, or that the server 
	 * takes too long to reply. In either of these failure cases, a message is shown and the connection 
	 * is sort of rolled back to the start.
	 * @param username Username to login with
	 * @param password Password that hopefully matches the username
	 * @param messageCallback A function to call which will show feedback messages
	 */
	public static void startLogin(String username, String password, Consumer<String> messageCallback)
	{
		// Update the callback function - we have a new one to use
		currentMessageCallback = Optional.ofNullable(messageCallback);
		
		// A null player indicates we're still in the initial state
		if(World.instance().getPlayer() != null)
		{
			message("Client is not in initial state.");
			
			return;
		}
		
		// Well we need a connection to the server. Should probably try that.
		if(!Client.instance().nettyClient().connectToServer())
		{
			// Connecting failed. Let the player know, our process stops here.
			message("Could not connect to server.");
			
			return;
		}
		
		// We should have a player - we'll temporarily adjust its username and password
		World.instance().getPlayer().setUsername(username);
		World.instance().getPlayer().setPassword(password);
		
		// At this point, we've connected to the server.
		World.instance().getPlayer().setLoginState(LoginState.CONNECTED);
		
		// Ship our login request off to the server
		World.instance().getPlayer().getPacketSender().sendLogin();
		
		// Prepare a task which will time-out and abort login if we're still just CONNECTED (server never replied...)
		TaskEngine.instance().submitDelayed(TIMEOUT, () ->
		{
			// This is the body of the task. If we've moved past connected, assume we're good to go
			if(World.instance().getPlayer().getLoginState() == LoginState.CONNECTED)
			{
				abortWaitingForVerification();
			}
		});
	}
	
	/**
	 * Attempt to logout from the game (SAO fans, anyone?)
	 * @param messageCallback Function to display messages
	 */
	public static void startLogout(Consumer<String> messageCallback)
	{
		// Update the callback function - we have a new one to use
		currentMessageCallback = Optional.ofNullable(messageCallback);

		// We need to be connected in general for this to work
		if(!Client.instance().nettyClient().isConnected())
		{
			// We are not, so we can't contact the server
			message("Cannot logout: Not connected to server");
			
			return;
		}
		
		// We also need to currently be logged in
		if(World.instance().getPlayer() != null && World.instance().getPlayer().getLoginState() != LoginState.LOGGED_IN)
		{
			// We are not, so there wouldn't be much point in trying to log out
			message("Cannot logout: Not logged in");
			
			return;
		}
		
		// Then to start we just tell the server we want to log out
		World.instance().getPlayer().getPacketSender().sendButtonActionPacket(ButtonActionID.LOGOUT);
	}
	
	/**
	 * Actually does the logout process, rather than requesting that we start it. 
	 * Disconnects us and returns us to the login interface
	 */
	public static void doLogout()
	{
		// Are we even connected
		if(!Client.instance().nettyClient().isConnected())
		{
			return;
		}
		
		// We also need to be logged in
		if(World.instance().getPlayer() == null || World.instance().getPlayer().getLoginState() != LoginState.LOGGED_IN)
		{
			return;
		}
		
		// We're about to move to logged out, then null the player - do this for completeness
		World.instance().getPlayer().setLoginState(LoginState.LOGGING_OUT);
		
		// Close the connection
		World.instance().getPlayer().setLoginState(LoginState.LOGGED_OUT);
		Client.instance().nettyClient().disconnect();
		
		// Show the login interface, just like when the client starts
		LoginInterface intf = new LoginInterface();
		Mainframe.root().removeAllChildren();
		Mainframe.root().add(intf);
		
		// May as well show a message
		intf.message("Logout Successful");
		ModalManager.instance().displayMessage("Logout", "You are now logged out");
	}
	
	/**
	 * Should be called when the player disconnects from the game. 
	 * It's okay if they're gracefully logging out, this will do nothing, then.
	 * Takes care of what should happen if the player abruptly disconnects from the game.
	 * @param player The player that is no longer connected to the server
	 */
	public static void handleDisconnect(Player player)
	{
		// We need to be logged in.
		if(player.getLoginState() != LoginState.LOGGED_IN)
		{
			return;
		}
		
		// However, I think it's possible that the channel was closed by the server 
		// before our login state changes. So allow a grace period for that change to happen
		TaskEngine.instance().submitDelayed(DC_GRACE, () -> resumeDisconnect(player));
	}
	
	/**
	 * Pick up where handleDisconnect left off. 
	 * Assumes our state was LOGGED_IN but may have since changed
	 * @param player The player that is no longer connected to the server
	 */
	private static void resumeDisconnect(Player player)
	{
		// Have we since gracefully logged out?  We don't need to do anything more, then
		if(player.getLoginState() != LoginState.LOGGED_IN)
		{
			return;
		}
		
		// Move into the DISCONNECTED state
		player.setLoginState(LoginState.DISCONNECTED);
		
		// Move right into reconnect failed... since we don't actually attempt to reconnect
		World.instance().getPlayer().setLoginState(LoginState.RECONNECT_FAILED);
		Client.instance().nettyClient().disconnect();
		
		// Show the login interface, just like when the client starts
		LoginInterface intf = new LoginInterface();
		Mainframe.root().removeAllChildren();
		Mainframe.root().add(intf);

		// May as well show a message
		intf.message("Disconnected from server.");
		ModalManager.instance().displayMessage("Disconnect", "You disconnected from the server. You may try to reconnect.");
	}
	
	/**
	 * Displays the message in some way. This is the most recent way defined by graphical code, which informs 
	 * this handler how to display login response messages... if that makes any sense. 
	 * In other words, a decoupled means of showing some login related message
	 * @param message The messages to show
	 */
	public static void message(String message)
	{
		currentMessageCallback.ifPresent((func) -> func.accept(message));
	}
	
	/**
	 * Abort the login process after the server fails to reply to our initial request to login, 
	 * we were stuck in the CONNECTED state for too long.
	 */
	private static void abortWaitingForVerification()
	{
		// Our actions thus far have been to connect, which in turn created the player.
		// Disconnecting will in turn destroy the player.
		Client.instance().nettyClient().disconnect();
	}

}
