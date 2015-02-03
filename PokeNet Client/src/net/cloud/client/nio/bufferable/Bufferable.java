package net.cloud.client.nio.bufferable;

import io.netty.buffer.ByteBuf;

/**
 * This is the interface that objects should implement when they are to be serializable. 
 * Rather than use Java's in-built serialization, this provides more control and fine tuned 
 * operation. On the flipside... those darn class hierarchies. 
 * Classes that implement this interface may be packed into a ByteBuf and then restored back 
 * from that same series of bytes in a possibly different ByteBuf. Depending on how the ByteBuf 
 * was created, may be used for sending objects over the network or to and from a file. 
 */
public interface Bufferable {
	
	/**
	 * Pack the object into a ByteBuf. This should write all information necessary for <code>load(buffer)</code> 
	 * to restore the object. If another object needs to be saved, then it should also implement Bufferable so that 
	 * the calls can travel down the hierarchy (and load can restore them through the same hierarchy) <br>
	 * If this is not an option, it's okay to use some other means - such as bridging Java's own serialization 
	 * @param buffer The buffer to place all necessary data into
	 * @throws BufferableException If for some reason the data could not be written to the buffer
	 */
	public void save(ByteBuf buffer) throws BufferableException;
	
	/**
	 * Restore an object using the data in the buffer. All of the information needed should have been written via 
	 * <code>save(buffer)</code>. This should not modify the buffer, only read from it. There may be more data in the buffer 
	 * than is needed to restore - don't mutate that in any way - or the code reading after this call may break. <br>
	 * The idea is that restoration happens on an object that has already been created, so it may be necessary to 
	 * refer to some factory methods or some other construction means if simple constructor usage is not viable. 
	 * @param buffer The buffer to read all necessary data from
	 * @throws BufferableException If for some reason the data could not be restored from the buffer
	 */
	public void restore(ByteBuf buffer) throws BufferableException;

}
