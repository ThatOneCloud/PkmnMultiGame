package net.cloud.server.event.task.voidtasks;

import net.cloud.server.entity.player.LoginHandler;
import net.cloud.server.entity.player.LoginState;
import net.cloud.server.entity.player.Player;

/**
 * A task which will check to see if a player is still in the connected stage, 
 * and abort the connection if they are.
 */
public class ConnectTimeoutTask extends CancellableVoidTask {
	
	/** The player that just connected */
	private final Player newPlayer;
	
	/**
	 * @param newPlayer The player that has just connected
	 */
	public ConnectTimeoutTask(Player newPlayer)
	{
		this.newPlayer = newPlayer;
	}

	@Override
	public void execute()
	{
		// Body of the task. Is the player still sitting in the CONNECTED state?
		if(newPlayer.getLoginState() == LoginState.CONNECTED)
		{
			// Since they are, we never got a login request from the client
			LoginHandler.abortConnection(newPlayer);
		}
	}

}
