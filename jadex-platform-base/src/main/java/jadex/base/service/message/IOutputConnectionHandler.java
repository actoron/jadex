package jadex.base.service.message;

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
	 *  @return Calls future when next data can be written.
	 */
	public IFuture<Void> waitForReady();
	
	/**
	 *  Flush the data.
	 */
	public void flush();
}
