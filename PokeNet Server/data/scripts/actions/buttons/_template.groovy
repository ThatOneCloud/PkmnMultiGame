import net.cloud.server.entity.player.Player;
import net.cloud.server.game.action.ButtonAction;
import net.cloud.server.game.action.ButtonActionID;
import net.cloud.server.nio.bufferable.Bufferable;
import net.cloud.server.nio.bufferable.BufferableException;

// [ADDITIONAL IMPORTS]

/**
 * DESCRIPTION
 */
public class [NAME]ButtonAction extends ButtonAction {
	
	/** Calls the super constructor */
	public [NAME]ButtonAction(ButtonActionID id)
	{
		super(id);
	}
	
	/**
	 * WHAT_ARGS_ARE_THERE
	 */
	@Override
	public void decodeArgs(Bufferable[] args, ByteBuf data) throws BufferableException
	{
		// OPTIONALLY IMPLEMENT
	}
	
	/**
	 * DESCRIPTION_OF_WHAT_IT_DOES
	 */
	@Override
	public void handle(Player player, Bufferable[] args) throws Exception
	{
		// IMPLEMENT
	}
	
}