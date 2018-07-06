package org.activecomponents.webservice.messages;

import jadex.bridge.ClassInfo;

/**
 *  A service search message.
 */
public class ServiceSearchMessage extends BaseMessage
{
	/** The type. */
	protected ClassInfo type;
	
	/** The multiple flag. */
	protected boolean multiple;
	
	/** The scope. */
	protected String scope;
	
	/**
	 *  Create a new service search message.
	 */
	public ServiceSearchMessage()
	{
	}
	
	/**
	 *  Create a new command.
	 *  @param callid The callid.
	 *  @param serviceId The serviceid;
	 *  @param parameterNames The parameter names.
	 *  @param parameterValues The parameter values.
	 */
	public ServiceSearchMessage(String callid, ClassInfo type, boolean multiple, String scope)
	{
		super(callid);
		this.type = type;
		this.multiple = multiple;
		this.scope = scope;
	}

	/** 
	 *  Get the type.
	 *  @return Tthe type
	 */
	public ClassInfo getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set
	 */
	public void setType(ClassInfo type)
	{
		this.type = type;
	}

	/** 
	 *  Get the multiple.
	 *  @return Tthe multiple
	 */
	public boolean isMultiple()
	{
		return multiple;
	}

	/**
	 *  Set the multiple.
	 *  @param multiple The multiple to set
	 */
	public void setMultiple(boolean multiple)
	{
		this.multiple = multiple;
	}

	/** 
	 *  Get the scope.
	 *  @return Tthe scope
	 */
	public String getScope()
	{
		return scope;
	}

	/**
	 *  Set the scope.
	 *  @param scope The scope to set
	 */
	public void setScope(String scope)
	{
		this.scope = scope;
	}
}
