package net.cloud.client.entity.player;

import java.util.function.Consumer;

import net.cloud.client.Client;
import net.cloud.client.event.task.TaskEngine;
import net.cloud.client.game.World;

/**
 * Good old fashioned static handler class, to encapsulate the logic for the login process.
 */
public class LoginHandler {
	
	/** How long will we wait for the server to reply before giving up */
	public static final long TIMEOUT = 5000;
	
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
		// A null player indicates we're still in the initial state
		if(World.instance().getPlayer() != null)
		{
			messageCallback.accept("Client is not in initial state.");
			
			return;
		}
		
		// Well we need a connection to the server. Should probably try that.
		if(!Client.instance().nettyClient().connectToServer())
		{
			// Connecting failed. Let the player know, our process stops here.
			messageCallback.accept("Could not connect to server.");
			
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
