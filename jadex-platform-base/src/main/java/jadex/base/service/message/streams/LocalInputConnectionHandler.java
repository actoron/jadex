package jadex.base.service.message.streams;

import java.util.Map;


/**
 * 
 */
public class LocalInputConnectionHandler extends LocalAbstractConnectionHandler
{
	/**
	 * 
	 */
	public LocalInputConnectionHandler(Map<String, Object> nonfunc)
	{
		this(nonfunc, null);
	}
	
	/**
	 * 
	 */
	public LocalInputConnectionHandler(Map<String, Object> nonfunc, LocalAbstractConnectionHandler conhandler)
	{
		super(nonfunc, conhandler);
	}
	
	//-------- methods called from other handler side --------
	
	/**
	 * 
	 */
	public void dataReceived(byte[] data)
	{
		((InputConnection)getConnection()).addData(data);
	}
}
