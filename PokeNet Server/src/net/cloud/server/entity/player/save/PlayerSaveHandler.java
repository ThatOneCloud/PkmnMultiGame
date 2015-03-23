package net.cloud.server.entity.player.save;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.cloud.server.entity.player.Player;
import net.cloud.server.file.FileRequestException;
import net.cloud.server.file.FileServer;
import net.cloud.server.file.address.FileAddressBuilder;
import net.cloud.server.file.request.FileOutputStreamRequest;
import net.cloud.server.file.request.listener.FileRequestListener;
import net.cloud.server.logging.Logger;
import net.cloud.server.nio.bufferable.BufferableException;

/**
 * A class to handle saving a single player's data to file. In particular takes care of the extra complexity involved 
 * in saving the data to a file rather than straight to a buffer. 
 */
public class PlayerSaveHandler {
	
	/** A reasonable guess, on the small end, for the save data size */
	private final static int INITIAL_SIZE_GUESS = 512;
	
	/** The player this handler is to be used for. */
	private final Player player;
	
	/** A guess as to how much space the player data will take. For tuning purpose. */
	private int saveSize;
	
	/**
	 * Create a new PlayerSaveHandler for the given player. This should only be created once the player 
	 * has been fully loaded, there is nothing to stop it from starting a save on a player whose data has 
	 * only been partially loaded. 
	 * @param player The player to handle saving for
	 */
	public PlayerSaveHandler(Player player)
	{
		this.player = player;
		
		this.saveSize = INITIAL_SIZE_GUESS;
	}
	
	/**
	 * Serialize the player data into the given buffer. This is not for saving to a file, but rather as an optional 
	 * route to <code>player.save(buffer)</code>. 
	 * @param buffer The buffer to write player data into
	 * @throws BufferableException If the player data could not be written to the buffer
	 */
	public void serialize(ByteBuf buffer) throws BufferableException
	{
		player.save(buffer);
	}
	
	/**
	 * Save the player data to its own file. This will perform the action regardless of whether or not the player 
	 * data is completely restored. This method will offload the task to the file server, and return immediately 
	 * rather than waiting for the file write to complete. So an exception may not be thrown if the data could not 
	 * be written after the hand-off. The data is still serialized on the calling thread - only the file write is 
	 * done via the FileServer thread. 
	 * @throws PlayerSaveException If the save could not be started. Not thrown if the file write fails. 
	 */
	public void saveToFile() throws PlayerSaveException
	{
		// We'll take care of prepping for write. First we'll need a ByteBuf to write to
		ByteBuf buffer = Unpooled.buffer(saveSize);
		
		// Then on this thread, have the data placed in the buffer. 
		try {
			serialize(buffer);
		} catch (BufferableException e) {
			throw new PlayerSaveException(player, "Could not serialize player data.", e);
		}
		
		// Adjust the save size based on how large this one was. It'll probably be close next time. 
		saveSize = buffer.readableBytes();
		
		// Prepare a request for the file server
		FileOutputStreamRequest req = new FileOutputStreamRequest(FileAddressBuilder.createPlayerDataAddress(player));
		req.attachListener(new PlayerSaveRequestHandler(player, buffer));
		
		// And don't forget to pass the request off. I've actually forgotten this...
		try {
			FileServer.instance().submit(req);
		} catch (FileRequestException e) {
			throw new PlayerSaveException(player, "Could not submit save request.", e);
		}
	}
	
	/**
	 * Save the player data to its own file. This will perform the action regardless of whether or not the player 
	 * data is completely restored. This variant shorts the file server and does the write on the calling 
	 * thread as well.
	 * @param An open stream to write to the save data file
	 * @throws PlayerSaveException If the save could not be started. Not thrown if the file write fails. 
	 */
	public void saveToFile(FileOutputStream saveData) throws PlayerSaveException
	{
		// We'll take care of prepping for write. First we'll need a ByteBuf to write to
		ByteBuf buffer = Unpooled.buffer(saveSize);
		
		// Then on this thread, have the data placed in the buffer. 
		try {
			serialize(buffer);
		} catch (BufferableException e) {
			throw new PlayerSaveException(player, "Could not serialize player data.", e);
		}
		
		// Adjust the save size based on how large this one was. It'll probably be close next time. 
		saveSize = buffer.readableBytes();
		
		// Short the file server and go straight to writing 
		new PlayerSaveRequestHandler(player, buffer).requestReady(saveData);
	}
	
	/**
	 * A FileRequestListener which will write to the player's data file when the file is ready. 
	 * This is so that the FileServer thread(s) handle the load rather than the calling thread. 
	 * If an exception occurs and writing is not completed, the exception will be logged with a message 
	 * stating which player's data was not saved. 
	 */
	private static class PlayerSaveRequestHandler implements FileRequestListener<FileOutputStream> {
		
		/** The player whose data needs to be written to file */
		private final Player player;
		
		/** The buffer containing the data to write */
		private final ByteBuf buffer;
		
		/**
		 * Create a new handler for when the player's data file is ready to be written to. 
		 * @param player The player whose data needs to be written to file
		 */
		public PlayerSaveRequestHandler(Player player, ByteBuf buffer)
		{
			this.player = player;
			this.buffer = buffer;
		}

		/**
		 * Called when the player's data file is ready for output. 
		 * The contents of the byte buffer will be written to the file, and the file will be closed. 
		 * @param file The player's data file
		 */
		@Override
		public void requestReady(FileOutputStream file)
		{
			// Buffer the stream. It's not done for us. Try-with-resources to close when done.
			try(BufferedOutputStream out = new BufferedOutputStream(file))
			{
				// Cool, ByteBuf has a method to go straight to an output stream
				buffer.readBytes(out, buffer.readableBytes());
			} 
			catch (IOException e) {
				// Reading exception or close exception. If both, close exception is suppressed. That's preferable, here.
				Logger.instance().logException("Could not write data for player " + player.getUsername(), e);
			} finally {
				// We need to close the file once we've got it saved
				try {
					file.close();
				} catch (IOException e) {
					Logger.instance().logException("Could not close player data file for " + player.getUsername(), e);
				}
			}
		}
		
		/**
		 * Logs the exception, and includes the name of the player whose data could not be saved. 
		 */
		public void requestException(FileRequestException ex)
		{
			Logger.instance().logException("Could not save data for player " + player.getUsername(), ex);
		}
		
	}

}
