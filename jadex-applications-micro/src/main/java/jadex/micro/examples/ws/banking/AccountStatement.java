package jadex.micro.examples.ws.banking;

/**
 * 
 */
public class AccountStatement
{
	protected String[] data;
	
	protected Request request;
	
	/**
	 * 
	 */
	public AccountStatement()
	{
	}

	/**
	 * @param bid
	 * @param ask
	 */
	public AccountStatement(String[] data, Request request)
	{
		this.data = data;
		this.request = request;
	}

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
