package jadex.platform.service.message.streams;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IOutputConnectionHandler extends IAbstractConnectionHandler
{
	/**
	 *  Called from connection.
	 */
	public IFuture<Void> send(final byte[] dat);

	/**
	 *  Wait until the connection is ready for the next write.
	 *  @return Calls future when next data can be written. Provides a value of how much data should be given to the connection for best performance.
	 */
	public IFuture<Integer> waitForReady();
	
	/**
	 *  Flush the data.
	 */
	public void flush();
}
