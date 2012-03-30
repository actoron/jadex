package jadex.bridge;

import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;


/**
 *  Interface for input connection stream.
 */
public interface IInputConnection extends IConnection
{
	/**
	 *  Non-blocking read. Tries to read the next byte.
	 *  @return The next byte or -1 if the end of the stream has been reached.
	 */
	public int read();
	
	/**
	 *  Non-blocking read. Tries to fill the 
	 *  buffer from the stream.
	 *  @param buffer The buffer to read in.
	 *  @return The number of bytes that could be read
	 *  into the buffer.
	 */
	public int read(byte[] buffer);
	
	/**
	 *  Asynchronous read. 
	 *  @return Bytes one by one till end of stream or closed.
	 */
//	public IIntermediateFuture<Byte> aread();
	public ISubscriptionIntermediateFuture<Byte> aread();
	
	/**
	 *  Asynchronous read. 
	 *  @return Bytes one by one till end of stream or closed.
	 */
	public IFuture<Byte> areadNext();
	
//	/**
//	 *  Blocking read. Read the next byte.
//	 *  @return The next byte or -1 if the end of the stream has been reached.
//	 */
//	public int bread();
	
	/**
	 *  Close the stream.
	 */
	public void close();
}
