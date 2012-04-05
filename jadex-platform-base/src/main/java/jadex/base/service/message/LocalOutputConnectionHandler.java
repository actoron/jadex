package jadex.base.service.message;

import jadex.commons.future.IFuture;

/**
 * 
 */
public class LocalOutputConnectionHandler extends LocalAbstractConnectionHandler 
	implements IOutputConnectionHandler
{
	/**
	 * 
	 */
	public LocalOutputConnectionHandler()
	{
	}
	
	/**
	 * 
	 */
	public LocalOutputConnectionHandler(LocalAbstractConnectionHandler conhandler)
	{
		super(conhandler);
	}

	//-------- methods called from connection --------
	
	/**
	 *  Called from connection.
	 */
	public IFuture<Void> send(final byte[] data)
	{
		((LocalInputConnectionHandler)getConnectionHandler()).dataReceived(data);
		return IFuture.DONE;
	}

	/**
	 *  Flush the data.
	 */
	public void flush()
	{
	}
}
