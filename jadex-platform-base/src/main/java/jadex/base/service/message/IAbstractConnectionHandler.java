package jadex.base.service.message;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IAbstractConnectionHandler
{
	/**
	 *  Set the connection (needed as connection and handler need each other).
	 *  The connections uses this method to set itself as connection in their constructor.
	 */
	public void setConnection(final AbstractConnection con);
	
//	/**
//	 *  Get the connection.
//	 *  @return The connection.
//	 */
//	public AbstractConnection getConnection();
	
	/**
	 *  Send init message.
	 */
	public IFuture<Void> sendInit();
	
	/**
	 * 
	 */
	public void notifyInited();
	
	/**
	 *  Called from connection.
	 *  Initiates closing procedure (is different for initiator and participant).
	 */
	public IFuture<Void> doClose();

}
