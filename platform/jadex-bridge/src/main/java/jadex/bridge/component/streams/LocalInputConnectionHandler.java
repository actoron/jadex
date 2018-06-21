package jadex.bridge.component.streams;

import java.util.Map;


/**
 *  Local version of the input connection handler.
 */
public class LocalInputConnectionHandler extends LocalAbstractConnectionHandler implements IInputConnectionHandler
{
	/** The maximum bytes of data that can be stored in connection (without being consumed). */
	protected int maxstored;
	
	/**
	 *  Create new local input connection handler.
	 */
	public LocalInputConnectionHandler(Map<String, Object> nonfunc)
	{
		this(nonfunc, null);
	}
	
	/**
	 *  Create new local input connection handler.
	 */
	public LocalInputConnectionHandler(Map<String, Object> nonfunc, LocalAbstractConnectionHandler conhandler)
	{
		super(nonfunc, conhandler);
		this.maxstored = 10000;
	}
	
	//-------- methods called from other handler side --------
	
	/**
	 *  Called by local output connection handler to send data.
	 */
	public void dataReceived(byte[] data)
	{
		InputConnection icon = (InputConnection)getConnection();
		icon.addData(data);
	}
	
	/**
	 *  Called by connection when user read some data
	 *  so that other side can continue to send.
	 */
	public void notifyDataRead()
	{
		int all = getAllowedSendSize();
		if(all>0)
		{
			((LocalOutputConnectionHandler)getConnectionHandler()).ready(all);
		}
	}
	
	/**
	 *  Get the allowed size that can be accepted (send by the output side).
	 */
	public int getAllowedSendSize()
	{
		InputConnection icon = (InputConnection)getConnection();
		int ret = Math.max(0, maxstored-icon.getStoredDataSize());
//		System.out.println("allowed: "+ret);
		return ret;
	}
}
