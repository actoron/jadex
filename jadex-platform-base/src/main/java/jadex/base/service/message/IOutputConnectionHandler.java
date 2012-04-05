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
	 *  Flush the data.
	 */
	public void flush();
}
