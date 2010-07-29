package jadex.bridge;


/**
 *  Helper struct for the remote search invocation service.
 *  Stores all result information of a remote search invocation.
 */
public class RemoteServiceSearchResultInfo
{
	//-------- attributes --------
	
	/** The result. */
	protected Object result;

	/** The exception. */
	protected Exception exception;
	
	//-------- constructors --------
	
	/**
	 *  Create a new result info.
	 */
	public RemoteServiceSearchResultInfo()
	{
	}
	
	/**
	 *  Create a new result info.
	 */
	public RemoteServiceSearchResultInfo(Object result, Exception exception)
	{
		this.result = result;
		this.exception = exception;
	}

	//-------- methods --------
	
	/**
	 *  Get the result.
	 *  @return the result.
	 */
	public Object getResult()
	{
		return result;
	}

	/**
	 *  Set the result.
	 *  @param result The result to set.
	 */
	public void setResult(Object result)
	{
		this.result = result;
	}
	
	/**
	 *  Get the exception.
	 *  @return the exception.
	 */
	public Exception getException()
	{
		return exception;
	}

	/**
	 *  Set the exception.
	 *  @param exception The exception to set.
	 */
	public void setException(Exception exception)
	{
		this.exception = exception;
	}
	
}
