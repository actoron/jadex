package jadex.bridge;

import java.io.OutputStream;

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
	public ISubscriptionIntermediateFuture<byte[]> aread();
	
	/**
	 *  Get the number of available bytes.
	 *  @return The number of available bytes. 
	 */
	public int available();
	
//	/**
//	 *  Asynchronous read. 
//	 *  @return Bytes one by one till end of stream or closed.
//	 */
//	public IFuture<Byte> areadNext();
	
//	/**
//	 *  Blocking read. Read the next byte.
//	 *  @return The next byte or -1 if the end of the stream has been reached.
//	 */
//	public int bread();
	
	/**
	 *  Write all data from the connection to the output stream.
	 *  The result is an intermediate future that reports back the size that was read.
	 *  It can also be used to terminate reading.
	 *  @param is The input stream.
	 *  @param component The component.
	 */
	public ISubscriptionIntermediateFuture<Long> writeToOutputStream(final OutputStream os, final IExternalAccess component);
}
