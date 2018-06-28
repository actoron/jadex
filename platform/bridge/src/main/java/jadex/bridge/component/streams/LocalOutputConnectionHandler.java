package jadex.bridge.component.streams;

import java.util.Map;

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
	
	/** The ready future. */
	protected Future<Integer> readyfuture;
	
	/**
	 * 
	 */
	public LocalOutputConnectionHandler(Map<String, Object> nonfunc)
	{
		this(nonfunc, null);
	}
	
	/**
	 * 
	 */
	public LocalOutputConnectionHandler(Map<String, Object> nonfunc, LocalAbstractConnectionHandler conhandler)
	{
		super(nonfunc, conhandler);
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
		Future<Integer> ret = null;
		
		int allowed = ((LocalInputConnectionHandler)getConnectionHandler()).getAllowedSendSize();
		
//		System.out.println("allowed: "+allowed);
		
		if(allowed>0)
		{
			ret = new Future<Integer>(Integer.valueOf(allowed));
		}
		else
		{
			if(readyfuture==null)
			{
				readyfuture = new Future<Integer>();
			}
			ret = readyfuture;
		}
		
		return ret;
	}
	
	/**
	 *  Called by local input connection handler to signal
	 *  that user has read some data.
	 */
	public void ready(int available)
	{
		if(readyfuture!=null)
		{
//			System.out.println("ready: "+available);
			Future<Integer> fut = readyfuture;
			readyfuture = null;
			fut.setResult(Integer.valueOf(available));
		}
	}
	
//	/**
//	 *  Test if stop is activated (too much data arrived).
//	 */
//	protected boolean isStop()
//	{
//		return getInputConnection().getStoredDataSize()>=maxstored;
//	}
}
