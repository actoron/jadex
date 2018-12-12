package jadex.webservice.examples.ws.banking;

/**
 *  Account statement class.
 */
public class AccountStatement
{
	//-------- attributes --------
	
	/** The account data. */
	protected String[] data;
	
	/** The request. */
	protected Request request;
	
	//-------- constructors --------
	
	/**
	 *  Create an account statement.
	 */
	public AccountStatement()
	{
	}

	/**
	 *  Create an account statement.
	 */
	public AccountStatement(String[] data, Request request)
	{
		this.data = data;
		this.request = request;
	}

	//-------- methods --------
	
	/**
	 *  Get the data.
	 *  @return the data.
	 */
	public String[] getData()
	{
		return data;
	}

	/**
	 *  Set the data.
	 *  @param data The data to set.
	 */
	public void setData(String[] data)
	{
		this.data = data;
	}

	/**
	 *  Get the request.
	 *  @return the request.
	 */
	public Request getRequest()
	{
		return request;
	}

	/**
	 *  Set the request.
	 *  @param request The request to set.
	 */
	public void setRequest(Request request)
	{
		this.request = request;
	}

}
