package jadex.bridge;


/**
 *  Helper struct for the remote method invocation service.
 *  Stores all necessary information for invoking a method.
 */
public class RemoteMethodInvocationInfo
{
	//-------- attributes --------
	
	/** The target component id. */
	protected IComponentIdentifier target;
	
	/** The service class !!!todo: use id!!! . */
	protected Class service;
	
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
	public RemoteMethodInvocationInfo(IComponentIdentifier target, Class service, String methodname, 
		Class[] parametertypes, Object[] parametervalues)
	{
		this.target = target;
		this.service = service;
		this.methodname = methodname;
		this.parametertypes = parametertypes;
		this.parametervalues = parametervalues;
	}

	//-------- methods --------
	
	/**
	 *  Get the target.
	 *  @return the target.
	 */
	public IComponentIdentifier getTarget()
	{
		return target;
	}
	
	/**
	 *  Set the target.
	 *  @param target The target to set.
	 */
	public void setTarget(IComponentIdentifier target)
	{
		this.target = target;
	}

	/**
	 *  Get the service.
	 *  @return the service.
	 */
	public Class getService()
	{
		return service;
	}
	
	/**
	 *  Set the service.
	 *  @param service The service to set.
	 */
	public void setService(Class service)
	{
		this.service = service;
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
