package net.cloud.server.entity.player.save;

import java.io.IOException;
import java.io.RandomAccessFile;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.cloud.server.entity.player.Player;
import net.cloud.server.file.FileRequestException;
import net.cloud.server.file.FileServer;
import net.cloud.server.file.address.FileAddressBuilder;
import net.cloud.server.file.request.RandomAccessFileLoadRequest;
import net.cloud.server.nio.bufferable.BufferableException;

/**
 * A class to handle loading a player's data from file storage. Handles the extra complexity of loading from a file, 
 * as compared to just deserializing. 
 */
public class PlayerLoadHandler {
	
	/** The player whose data we are going to be loading */
	private final Player player;
	
	/**
	 * Create a new handler object for the given player. This is really only needed for loading, 
	 * and it could probably be static... but whatever. It's more similar to the save handler this way.
	 * @param player The player whose data is going to be loaded
	 */
	public PlayerLoadHandler(Player player)
	{
		this.player = player;
	}
	
	/**
	 * Restore a player's data. This is not for loading from a file. Instead, it's an optional route to 
	 * <code>player.restore(buffer)</code>. Of course, rather than create this just for that, just call the 
	 * method in player. 
	 * @param buffer The buffer the data is in
	 * @throws BufferableException If the player's data could not be restored
	 */
	public void deserialize(ByteBuf buffer) throws BufferableException
	{
		player.restore(buffer);
	}
	
	/**
	 * Load a player's data from the save file. This does not validate the player's credentials. Instead, it will 
	 * take an existing player object and have the rest of the information restored to the save state. This only needs 
	 * to be done once near the end of login. It will also tell the player that it has been loaded, so that it will 
	 * be ready for saving. <br>
	 * All of the work is done on the calling thread, so this does not return until the player has been completely 
	 * and successfully loaded. If something goes wrong, an exception <b>will</b> be thrown. 
	 * @throws PlayerLoadException If the player data could not be restored for some reason
	 */
	public void loadFromFile() throws PlayerLoadException
	{
		// Try-with-resources to the File Server. Neat. So the all important file will be closed when we're done
		try (RandomAccessFile dataFile = getSaveFile(player.getUsername())) 
		{
			loadFromFile(dataFile);
		} 
		catch(IOException e) {
			// Only occurs when the file could not be closed. So... re-throw I guess. It'd probably become an issue going forward.
			throw new PlayerLoadException(player, "Player save file could not be closed. Unsafe to proceed.", e);
		}
	}
	
	/**
	 * Load a player's data from the provided save file. This does not validate the player's credentials. Instead, it will 
	 * take an existing player object and have the rest of the information restored to the save state. This only needs 
	 * to be done once near the end of login. It will also tell the player that it has been loaded, so that it will 
	 * be ready for saving. <br>
	 * All of the work is done on the calling thread, so this does not return until the player has been completely 
	 * and successfully loaded. If something goes wrong, an exception <b>will</b> be thrown. 
	 * @param dataFile An open file to read player save data from
	 * @throws PlayerLoadException If the player data could not be restored for some reason
	 */
	public void loadFromFile(RandomAccessFile dataFile) throws PlayerLoadException
	{
		// Read in the data
		byte[] data = getFileBytes(player, dataFile);
		
		// Wrap the array into a ByteBuf
		ByteBuf nettyBuffer = Unpooled.wrappedBuffer(data);

		// Have the player object go through deserialization - not on a different thread
		restorePlayerData(nettyBuffer);

		// At the end of all the loading, tell the player they have been restored and are now ready to save data
		player.finishedLoading();
	}
	
	/**
	 * The player this handler is created for will have its username and password set from the data in the data 
	 * file for the player with the given username. Only the username and password are set. 
	 * If there is no such player save matching the username, then an IO exception is thrown immediately - 
	 * no file is opened and nothing is read.
	 * Regardless of the exception, assume it is unsafe to proceed. It is unknown whether the username and/or/neither 
	 * password have actually been set.
	 * @param username Username of the player
	 * @throws PlayerLoadException Save file could not be found
	 * @throws IOException Save file did not exist, could not be read from, or could not be closed
	 */
	public void loadUserAndPass(String username) throws PlayerLoadException, IOException
	{
		// Check to see if there is save data for the requested player
		if(!saveFileExists(username))
		{
			// Since it does not exist, we don't want to proceed. Rather than return false, make it easy on the caller
			throw new IOException("No player save file for: " + username);
		}
		
		// Try-with-resources to the File Server. Neat. So the all important file will be closed when we're done
		try (RandomAccessFile dataFile = getSaveFile(username)) 
		{
			loadUserAndPass(dataFile);
		}
	}
	
	/**
	 * The player this handler is created for will have its username and password set from the data in the file. 
	 * This assumes the file has just been opened and the pointer is at the beginning.
	 * @param dataFile The player data
	 * @throws IOException If the file could not be read
	 */
	public void loadUserAndPass(RandomAccessFile dataFile) throws IOException
	{
		// We'll have the player only load its username and password
		player.restoreUserAndPass(dataFile);
	}
	
	/**
	 * Check to see if there is a save file matching the given username
	 * @param username The player data to look for
	 * @return True if there is a save file
	 */
	private boolean saveFileExists(String username)
	{
		return FileServer.instance().fileExists(FileAddressBuilder.createPlayerDataAddress(username));
	}
	
	/**
	 * Obtain a RAF from the File Server for the player's save data. Waits until the request has been served. 
	 * @param username Username of the player
	 * @return A RAF from the File Server for the player's save data
	 * @throws PlayerLoadException If the request could not be served
	 */
	private RandomAccessFile getSaveFile(String username) throws PlayerLoadException
	{
		// Get a RAF to the save data
		RandomAccessFileLoadRequest req = new RandomAccessFileLoadRequest(FileAddressBuilder.createPlayerDataAddress(username));
		try {
			return FileServer.instance().submitAndWaitForDescriptor(req);
		} catch (FileRequestException e) {
			// Re-throw. Code below this should be able to safely assume file is not null
			throw new PlayerLoadException(player, "Could not obtain player's save data", e);
		}
	}
	
	/**
	 * Obtain a byte array with the contents of the file
	 * @param player The player whose file we're looking into
	 * @param dataFile The file to read the player data from
	 * @return A byte array containing the data from the file
	 * @throws PlayerLoadException If the file could not be read
	 */
	private byte[] getFileBytes(Player player, RandomAccessFile dataFile) throws PlayerLoadException
	{
		// Read all of the bytes into an array
		try {
			byte[] data = new byte[(int) dataFile.length()];
			
			dataFile.read(data);
			
			return data;
		} catch(IOException e) {
			throw new PlayerLoadException(player, "Could not read data file", e);
		}
	}
	
	/**
	 * Deserialize the player data. Happens on the calling thread. Afterwards the given player object 
	 * will have its data restored to reflect the data contained in the buffer. 
	 * @param nettyBuffer The buffer with the player object data
	 * @throws PlayerLoadException If the data could not be deserialized
	 */
	private void restorePlayerData(ByteBuf nettyBuffer) throws PlayerLoadException
	{
		// Restore the player. Have the player object kick off restoring itself
		try {
			deserialize(nettyBuffer);
		} catch (BufferableException e) {
			// Re-throw as a uniform exception type.
			throw new PlayerLoadException(player, "Could not memory map player's save data", e);
		}
	}
	
}
