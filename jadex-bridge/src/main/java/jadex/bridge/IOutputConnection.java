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
	 *  Close the connection.
	 */
	// todo: make IFuture<Void> ?
	public void close();
}
