package jadex.bridge;

import jadex.commons.FieldInfo;
import jadex.commons.MethodInfo;

/**
 * 
 */
public class ServiceCallInfo
{
	/** The required service name. */
	protected String reqname;
	
	/** The method to call on the service. */
	protected MethodInfo servicemethod;
	
	/** The callback method in the agent. */
	protected MethodInfo callbackmethod;

	/** The callback field in the agent. */
	protected FieldInfo callbackfield;
	
	/**
	 *  Create a new service call info.
	 */
	public ServiceCallInfo()
	{
	}
	
	/**
	 *  Create a new service call info.
	 */
	public ServiceCallInfo(String reqname, MethodInfo servicemethod, MethodInfo callbackmethod)
	{
		this.reqname = reqname;
		this.servicemethod = servicemethod;
		this.callbackmethod = callbackmethod;
	}
	
	/**
	 *  Create a new service call info.
	 */
	public ServiceCallInfo(String reqname, MethodInfo servicemethod, FieldInfo callbackfield)
	{
		this.reqname = reqname;
		this.servicemethod = servicemethod;
		this.callbackfield = callbackfield;
	}

	/**
	 *  Get the reqname.
	 *  @return The reqname
	 */
	public String getRequiredName()
	{
		return reqname;
	}

	/**
	 *  The reqname to set.
	 *  @param reqname The reqname to set
	 */
	public void setRequiredName(String reqname)
	{
		this.reqname = reqname;
	}

	/**
	 *  Get the method.
	 *  @return The method
	 */
	public MethodInfo getServiceMethod()
	{
		return servicemethod;
	}

	/**
	 *  The method to set.
	 *  @param method The method to set
	 */
	public void setServiceMethod(MethodInfo method)
	{
		this.servicemethod = method;
	}

	/**
	 *  Get the callback.
	 *  @return The callback
	 */
	public MethodInfo getCallbackMethod()
	{
		return callbackmethod;
	}

	/**
	 *  The callback to set.
	 *  @param callback The callback to set
	 */
	public void setCallbackMethod(MethodInfo callback)
	{
		this.callbackmethod = callback;
	}

	/**
	 *  Get the callbackfield.
	 *  @return The callbackfield
	 */
	public FieldInfo getCallbackField()
	{
		return callbackfield;
	}

	/**
	 *  The callbackfield to set.
	 *  @param callbackfield The callbackfield to set
	 */
	public void setCallbackField(FieldInfo callbackfield)
	{
		this.callbackfield = callbackfield;
	}
}
