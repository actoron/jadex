package org.activecomponents.webservice.messages;

import jadex.bridge.ClassInfo;

/**
 *  Message for providing a client service that can be
 *  invoked by other clients and Jadex components. 
 */
public class ServiceProvideMessage extends BaseMessage
{
	/** The type. */
	protected ClassInfo type;
	
	// todo:
	/** The provision scope. */
	protected String scope;
	
	/** The tags. */
	protected String[] tags;
	
	/**
	 *  Create a new command.
	 */ 
	public ServiceProvideMessage()
	{
	}

	/**
	 *  Create a new command.
	 *  @param callid The callid.
	 *  @param type The service type.
	 */
	public ServiceProvideMessage(String callid, ClassInfo type, String scope, String... tags)
	{
		super(callid);
		this.type = type;
		this.scope = scope;
		this.tags = tags;
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
	 *  Get the tags.
	 *  @return the tags
	 */
	public String[] getTags()
	{
		return tags;
	}

	/**
	 *  Set the tags.
	 *  @param tags The tags to set
	 */
	public void setTags(String[] tags)
	{
		this.tags = tags;
	}

	/**
	 *  Get the scope.
	 *  @return The scope
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
