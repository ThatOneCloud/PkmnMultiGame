package net.cloud.server.entity.player;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Optional;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import io.netty.buffer.ByteBuf;
import net.cloud.server.entity.Entity;
import net.cloud.server.entity.player.save.PlayerSaveException;
import net.cloud.server.entity.player.save.PlayerSaveHandler;
import net.cloud.server.nio.bufferable.Bufferable;
import net.cloud.server.nio.packet.PacketSender;
import net.cloud.server.util.ConnectionInfo;
import net.cloud.server.util.HashObj;
import net.cloud.server.util.StringUtil;

/**
 * The Player object. Home of the player. Holds the player's data. 
 * Keeps the player's data safe. A hub for all of the player's information. 
 * The root of the save data. Yes.
 */
@XStreamAlias("Player")
public class Player extends Entity implements Bufferable {
	
	/** Describes the player's connectivity to the game */
	private transient LoginState loginState;
	
	/** The PacketSender assigned to this player. Used for communication. */
	private transient PacketSender packetSender;
	
	/** The object holding information on our connection */
	private transient PlayerChannelConfig channelConfig;
	
	/** This player's save handler. They should hold onto it, it may save them one day... */
	private transient Optional<PlayerSaveHandler> saveHandler;
	
	/** The player's username. */
	private String username;
	
	/** The player's password. */
	private HashObj password;
	
	/** When the player most recently logged into the game */
	private ConnectionInfo lastLogin;
	
	/**
	 * Constructor that accepts the PacketSender
	 * @param packetSender PacketSender that will be used throughout lifetime of player
	 * @param config Object holding channel initialization info
	 */
	public Player(PacketSender packetSender, PlayerChannelConfig config)
	{
		this.packetSender = packetSender;
		this.channelConfig = config;
		
		// They start off as a new player - only connected
		setLoginState(LoginState.CONNECTED);
		
		// Don't start with save handler. Only have that once the player has been loaded
		this.saveHandler = Optional.empty();
		
		// Null here indicates the account has yet to be logged in
		this.lastLogin = null;
	}
	
	/**
	 * Request that this player be logged out from the game
	 */
	public void logout()
	{
		LoginHandler.doLogout(this);
	}
	
	/**
	 * Tell the player that it was disconnect abruptly. 
	 * May take whatever action necessary when a disconnect happens, as is relevant to this player
	 */
	public void onDisconnect()
	{
		setLoginState(LoginState.DISCONNECTED);
	}
	
	/**
	 * Tell the player that after disconnecting, they did not reconnect in time. 
	 * May take whatever action necessary for when this happens, as is relevant to this player
	 */
	public void onReconnectFailed()
	{
		setLoginState(LoginState.RECONNECT_FAILED);
	}
	
	/**
	 * Tell this player that its data has been loaded and it's time for it to be able to save data
	 */
	public void finishedLoading()
	{
		// So this is the time to make a save handler. Since we now have data to save.
		saveHandler = Optional.of(new PlayerSaveHandler(this));
	}
	
	/** @return The player's username */
	public String getUsername()
	{
		return username;
	}
	
	/** @param username the username to set */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/** @return The player's password */
	public HashObj getPassword()
	{
		return password;
	}
	
	/** @param password the password to set */
	public void setPassword(String password)
	{
		this.password = new HashObj(password);
	}
	
	/** @return The LoginState for this player. Ie, which step of login process they are in */
	public LoginState getLoginState()
	{
		return loginState;
	}

	/** @param loginState Which step of the login process the player is now in */
	public void setLoginState(LoginState loginState)
	{
		this.loginState = loginState;
	}
	
	/**
	 * The object that holds information about our connection
	 * @return The channelConfig
	 */
	public PlayerChannelConfig getChannelConfig()
	{
		return channelConfig;
	}
	
	/**
	 * @param config A new configuration object
	 */
	public void setChannelConfig(PlayerChannelConfig config)
	{
		this.channelConfig = config;
	}

	/**
	 * Get the PacketSender the server uses to communicate with the player
	 * @return The PacketSender allowing communication to the player
	 */
	public PacketSender getPacketSender()
	{
		return packetSender;
	}
	
	/**
	 * Assign a packet sender to this player. Useful for when their connection changes
	 * @param packetSender The new packet sender
	 */
	public void setPacketSender(PacketSender packetSender)
	{
		this.packetSender = packetSender;
	}
	
	/**
	 * @return When the player was last logged in. null if they never have been.
	 */
	public ConnectionInfo getLastLogin()
	{
		return lastLogin;
	}
	
	/**
	 * Sets this player's last login information to their status here and now. 
	 * Does require that they be logged into the game.
	 */
	public void updateLastLogin()
	{
		// We pull the address from the channel we're connected with - known to have an InetSocketAddress
		this.lastLogin = new ConnectionInfo(getPacketSender().channel().remoteAddress().getAddress().getHostAddress());
	}
	
	/**
	 * Save this player's data to file. Serializing data is done on the calling thread, writing to the file 
	 * is done on the File Server. 
	 * @throws PlayerSaveException If the data could not be saved. Not thrown if file could not be written. 
	 */
	public void saveToFile() throws PlayerSaveException
	{
		// Save handler existing means it's okay to try saving. 
		if(saveHandler.isPresent())
		{
			saveHandler.get().saveToFile();
		}
	}

	/**
	 * Save all of the non-transient player data to the buffer
	 */
	@Override
	public void save(ByteBuf buffer)
	{
		// Username and password
		StringUtil.writeStringToBuffer(getUsername(), buffer);
		password.save(buffer);
		
		// We have more than just username and password now!
		// Last login info may be null, so we're going to flag that
		if(lastLogin == null)
		{
			buffer.writeBoolean(false);
		}
		else {
			buffer.writeBoolean(true);
			lastLogin.save(buffer);
		}
	}

	/**
	 * Restore all of the non-transient player data from the buffer
	 */
	@Override
	public void restore(ByteBuf buffer)
	{
		// Username and password
		username = StringUtil.getFromBuffer(buffer);
		password = HashObj.createFrom(buffer);
		
		// Read last login flag to check if null, restore it if we have it
		boolean lastLoginExists = buffer.readBoolean();
		if(lastLoginExists)
		{
			lastLogin = ConnectionInfo.createFrom(buffer);
		}
		else {
			lastLogin = null;
		}
	}
	
	/**
	 * Restore only the username and password from the player save file. 
	 * This assumes the file has just been opened, and the pointer is at the 
	 * beginning of the file. 
	 * After this method returns, only the username and password fields will have been set.
	 * @param raf The file to read the username and password from
	 * @throws IOException If the file could not be read from
	 */
	public void restoreUserAndPass(RandomAccessFile raf) throws IOException
	{
		// Use variants that operate directly on the RAF
		username = StringUtil.getFromRAF(raf);
		
		password = HashObj.createFrom(raf);
	}

}
