package jadex.base.service.message;

import jadex.commons.future.IFuture;

/**
 * 
 */
public class LocalAbstractConnectionHandler implements IAbstractConnectionHandler
{
	/**
	 *  Set the connection (needed as connection and handler need each other).
	 *  The connections uses this method to set itself as connection in their constructor.
	 */
	public void setConnection(final AbstractConnection con)
	{
		
	}
	
	/**
	 *  Send init message.
	 */
	public IFuture<Void> sendInit()
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Called from connection.
	 *  Initiates closing procedure (is different for initiator and participant).
	 */
	public IFuture<Void> doClose()
	{
		return IFuture.DONE;
	}
}
