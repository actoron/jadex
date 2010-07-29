package jadex.bridge;

import jadex.service.IServiceIdentifier;


/**
 *  Helper struct for the remote method invocation service.
 *  Stores all necessary information for invoking a method.
 */
public class RemoteMethodInvocationInfo
{
	//-------- attributes --------
	
	/** The service identifier. */
	protected IServiceIdentifier sid;
	
	/** The methodname. */
	protected String methodname;
	
	/** The parameter types. */
	protected Class[] parametertypes;
	
	/** The parameter values. */
	protected Object[] parametervalues;

	//-------- constructors --------
	
	/**
	 *  Create a new invacation info.
	 */
	public RemoteMethodInvocationInfo()
	{
	}
	
	/**
	 *  Create a new invacation info.
	 */
	public RemoteMethodInvocationInfo(IServiceIdentifier sid, String methodname, 
		Class[] parametertypes, Object[] parametervalues)
	{
		this.sid = sid;
		this.methodname = methodname;
		this.parametertypes = parametertypes;
		this.parametervalues = parametervalues;
	}

	//-------- methods --------
	
	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}

	/**
	 *  Set the service identifier.
	 *  @param sid The service identifier to set.
	 */
	public void setServiceIdentifier(IServiceIdentifier sid)
	{
		this.sid = sid;
	}

	/**
	 *  Get the methodname.
	 *  @return the methodname.
	 */
	public String getMethodName()
	{
		return methodname;
	}

	/**
	 *  Set the methodname.
	 *  @param methodname The methodname to set.
	 */
	public void setMethodName(String methodname)
	{
		this.methodname = methodname;
	}
	
	/**
	 *  Get the parametertypes.
	 *  @return the parametertypes.
	 */
	public Class[] getParameterTypes()
	{
		return parametertypes;
	}
	
	/**
	 *  Set the parametertypes.
	 *  @param parametertypes The parametertypes to set.
	 */
	public void setParameterTypes(Class[] parametertypes)
	{
		this.parametertypes = parametertypes;
	}

	/**
	 *  Get the parametervalues.
	 *  @return the parametervalues.
	 */
	public Object[] getParameterValues()
	{
		return parametervalues;
	}

	/**
	 *  Set the parametervalues.
	 *  @param parametervalues The parametervalues to set.
	 */
	public void setParameterValues(Object[] parametervalues)
	{
		this.parametervalues = parametervalues;
	}
}
