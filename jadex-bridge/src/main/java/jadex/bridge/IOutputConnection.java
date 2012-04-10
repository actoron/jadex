package jadex.bridge;

import jadex.commons.future.IFuture;

/**
 *  Interface for output connection.
 */
public interface IOutputConnection extends IConnection
{
	/**
	 *  Write the content to the stream.
	 *  @param data The data.
	 */
	public IFuture<Void> write(byte[] data);
	
	/**
	 *  Flush the data.
	 */
	public void flush();
	
	/**
	 *  Wait until the connection is ready for the next write.
	 *  @return Calls future when next data can be written.
	 */
	public IFuture<Void> waitForReady();
	
	/**
	 *  Close the connection.
	 */
	// todo: make IFuture<Void> ?
	public void close();
}
