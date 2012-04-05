package jadex.base.service.message;

/**
 * 
 */
public class LocalInputConnectionHandler extends LocalAbstractConnectionHandler
{
	/**
	 * 
	 */
	public LocalInputConnectionHandler()
	{
	}
	
	/**
	 * 
	 */
	public LocalInputConnectionHandler(LocalAbstractConnectionHandler conhandler)
	{
		super(conhandler);
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
