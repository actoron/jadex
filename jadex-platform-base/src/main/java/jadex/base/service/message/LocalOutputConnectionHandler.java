package jadex.base.service.message;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class LocalOutputConnectionHandler extends LocalAbstractConnectionHandler 
	implements IOutputConnectionHandler
{
	/** The maximum bytes of data that can be stored in connection (without being consumed). */
	protected int maxstored;
	
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
	
	/**
	 *  Wait until the connection is ready for the next write.
	 *  @return Calls future when next data can be written.
	 */
	public IFuture<Integer> waitForReady()
	{
		// todo: how to implement locally without timer :-( ?
		return new Future<Integer>(32768);	// todo: useful value?
	}
	
//	/**
//	 *  Test if stop is activated (too much data arrived).
//	 */
//	protected boolean isStop()
//	{
//		return getInputConnection().getStoredDataSize()>=maxstored;
//	}
}
