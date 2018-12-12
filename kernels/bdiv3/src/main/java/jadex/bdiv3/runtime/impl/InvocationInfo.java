package jadex.bdiv3.runtime.impl;

/**
 *  Info about a service invocation containing parameters and result.
 */
public class InvocationInfo
{
	/** The parameters. */
	protected Object[] params;
	
	/** The result value. */
	protected Object result;

	/**
	 *  Create a new InvocationInfo. 
	 */
	public InvocationInfo(Object[] params)
	{
		this.params = params;
	}

	/**
	 *  Get the params.
	 *  @return The params.
	 */
	public Object[] getParams()
	{
		return params;
	}

	/**
	 *  Set the params.
	 *  @param params The params to set.
	 */
	public void setParams(Object[] params)
	{
		this.params = params;
	}

	/**
	 *  Get the result.
	 *  @return The result.
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
}
