import net.cloud.server.entity.player.Player;
import net.cloud.server.game.action.ButtonAction;
import net.cloud.server.game.action.ButtonActionID;
import net.cloud.server.nio.bufferable.Bufferable;

import net.cloud.server.entity.player.LoginHandler;

/**
 * An action which moves a log out request to the LoginHandler (Yes, the LoginHandler)
 */
public class LogoutButtonAction extends ButtonAction {
	
	/** Calls the super constructor */
	public LogoutButtonAction(ButtonActionID id)
	{
		super(id);
	}
	
	/**
	 * Pass the request off to the LoginHandler. There are no expected arguments
	 */
	@Override
	public void handle(Player player, Bufferable[] args) throws Exception
	{
		LoginHandler.doLogout(player);
	}
	
}