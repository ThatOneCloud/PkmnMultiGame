import io.netty.buffer.ByteBuf;
import net.cloud.server.entity.player.Player;
import net.cloud.server.game.action.ButtonAction;
import net.cloud.server.game.action.ButtonActionID;
import net.cloud.server.nio.bufferable.Bufferable;
import net.cloud.server.nio.bufferable.BufferableException;
import net.cloud.server.nio.bufferable.BufferableInteger;
import net.cloud.server.nio.bufferable.BufferableString;

public class LogoutButtonAction extends ButtonAction {
	
	public LogoutButtonAction(ButtonActionID id)
	{
		super(id);
		println("created logout button action with id " + id);
	}
	
	public void decodeArgs(Bufferable[] args, ByteBuf data) throws BufferableException
	{
		args[0] = BufferableString.createFrom(data);
		args[1] = BufferableInteger.createFrom(data);
	}
	
	@Override
	public void handle(Player player, Bufferable[] args) throws Exception
	{
		println("logout button action groovy file handle method");
	}
	
}