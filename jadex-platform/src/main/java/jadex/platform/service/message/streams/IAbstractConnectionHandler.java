package jadex.platform.service.message.streams;

import java.util.Map;

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

	/**
	 *  Get the non-functional properties.
	 *  @return The non-functional properties.
	 */
	public Map<String, Object> getNonFunctionalProperties();	
}
