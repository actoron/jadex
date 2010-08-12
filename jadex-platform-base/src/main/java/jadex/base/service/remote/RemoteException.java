package jadex.base.service.remote;


/**
 *  Exception that denotes 
 */
public class RemoteException extends RuntimeException
{
	//-------- attributes --------
	
	/** The exception type. */
	protected Class type;
	
	//-------- constructors --------
	
	/**
	 *  Create a new exception info.
	 */
	public RemoteException(Class type, String message)
	{
		super(message);
		this.type = type;
	}
}
