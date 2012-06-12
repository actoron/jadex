package jadex.bridge.service.types.remote;


/**
 *  Exception that wraps a remote exception. 
 */
public class RemoteException extends RuntimeException
{
	//-------- attributes --------
	
	/** The remote exception type. */
	protected Class<?>	type;
	
	//-------- constructors --------
	
	/**
	 *  Create a new exception info.
	 */
	public RemoteException(Class<?> type, String message)
	{
		super(message);
		this.type	= type;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the type.
	 */
	public Class<?>	getType()
	{
		return type;
	}
	
	/**
	 *  A string representation.
	 */
	public String toString()
	{
		String	clazz	= getClass().getName() + (type!=null ? "("+type.getName()+")" : "");
        String	msg	= getLocalizedMessage();
        return msg!=null ? clazz+": "+msg : clazz;
	}
}
