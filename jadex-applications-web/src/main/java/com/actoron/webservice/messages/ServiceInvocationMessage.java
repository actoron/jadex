package com.actoron.webservice.messages;

import jadex.commons.SUtil;

/**
 * 
 */
public class ServiceInvocationMessage extends BaseMessage
{
	protected String methodname;
	
	protected String sid;
	
	protected Object[] parametervalues;
	
	/**
	 *  Create a new command.
	 */ 
	public ServiceInvocationMessage()
	{
	}

	/**
	 *  Create a new command.
	 *  @param callid The callid.
	 *  @param serviceId The serviceid;
	 *  @param parameterNames The parameter names.
	 *  @param parameterValues The parameter values.
	 */
	public ServiceInvocationMessage(String callid, String serviceid, String methodname, Object[] parametervalues)
	{
		super(callid);
		this.sid = serviceid;
		this.methodname = methodname;
		this.parametervalues = parametervalues;
	}

	/** 
	 *  Get the serviceId.
	 *  @return Tthe serviceId
	 */
	public String getServiceId()
	{
		return sid;
	}

	/**
	 *  Set the serviceId.
	 *  @param serviceId The serviceId to set
	 */
	public void setServiceId(String serviceid)
	{
		this.sid = serviceid;
	}

	/** 
	 *  Get the parameterValues.
	 *  @return Tthe parameterValues
	 */
	public Object[] getParameterValues()
	{
		return parametervalues!=null? parametervalues: SUtil.EMPTY_OBJECT_ARRAY;
	}

	/**
	 *  Set the parameter values.
	 *  @param parametervalues The parameterValues to set
	 */
	public void setParameterValues(Object[] parametervalues)
	{
		this.parametervalues = parametervalues;
	}

	/** 
	 *  Get the methodName.
	 *  @return Tthe methodName
	 */
	public String getMethodName()
	{
		return methodname;
	}

	/**
	 *  Set the method name.
	 *  @param methodname The method name to set
	 */
	public void setMethodName(String methodname)
	{
		this.methodname = methodname;
	}
}
