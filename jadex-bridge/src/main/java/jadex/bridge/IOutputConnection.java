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
	 *  Close the connection.
	 */
	public void close();
}
