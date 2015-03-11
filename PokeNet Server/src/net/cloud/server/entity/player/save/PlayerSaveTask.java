package net.cloud.server.entity.player.save;

import net.cloud.server.entity.player.Player;
import net.cloud.server.event.task.voidtasks.VoidTask;
import net.cloud.server.game.World;
import net.cloud.server.logging.Logger;

/**
 * A task which is designed to be run periodically for the lifetime of the server. 
 * Each time the task executes, all of the players currently logged into the server 
 * will have their data saved. If any of them cannot be successfully saved, the task 
 * will continue past and report the error. 
 */
public class PlayerSaveTask implements VoidTask {

	/**
	 * Go through all of the players currently logged in and try to save their data. 
	 * If the save fails, report the issue and carry on. 
	 */
	@Override
	public void execute() {
		// The list takes care of conditional complexities. We just tell it what to do.
		World.instance().getPlayerMap().forAllLoggedIn(this::attemptSave);
	}
	
	/**
	 * A method for passing as a method reference. Tries the save, logs the exception
	 * @param p The player whose turn it is to have data saved
	 */
	private void attemptSave(Player p)
	{
		try {
			p.saveToFile();
		} catch (PlayerSaveException e) {
			// Didn't work so report the issue. End of the exception chain
			Logger.instance().logException("Task could not save player data", e);
		}
	}

}
