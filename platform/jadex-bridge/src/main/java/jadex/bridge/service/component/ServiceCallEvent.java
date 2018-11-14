package jadex.bridge.service.component;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.MethodInfo;

/**
 *  Represents the events associated with service calls (call, response, etc.).
 */
public class ServiceCallEvent
{
	public static enum Type
	{
		CALL, RESULT, EXCEPTION, INTERMEDIATE_RESULT, FINISHED;//FORWARD_CMD, BACKWARD_CMD;
	}
	
	//-------- attributes --------
	
	/** The event type. */
	protected Type	type;
	
	/** The service. */
	protected IServiceIdentifier	service;
	
	/** The method. */
	protected MethodInfo	method;
	
	/** The caller. */
	protected IComponentIdentifier	caller;
	
	/** The event body (arguments, result, ...). */
	protected Object	body;
	
	// TODO: nonfunc?
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public ServiceCallEvent()
	{
	}
	
	/**
	 *  Instance constructor.
	 */
	public ServiceCallEvent(Type type, IServiceIdentifier service, MethodInfo method, IComponentIdentifier caller, Object body)
	{
		this.type	= type;
		this.service	= service;
		this.method	= method;
		this.caller	= caller;
		this.body	= body;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the type.
	 */
	public Type getType()
	{
		return type;
	}
	
	/**
	 *  Set the type.
	 */
	public void setType(Type type)
	{
		this.type = type;
	}
	
	/**
	 *  Get the body.
	 */
	public Object getBody()
	{
		return body;
	}
	
	/**
	 *  Set the body.
	 */
	public void setBody(Object body)
	{
		this.body = body;
	}
	
	/**
	 *  Get the service.
	 */
	public IServiceIdentifier getService()
	{
		return service;
	}
	
	/**
	 *  Set the service.
	 */
	public void setService(IServiceIdentifier service)
	{
		this.service = service;
	}
	
	/**
	 *  Get the method.
	 */
	public MethodInfo getMethod()
	{
		return method;
	}
	
	/**
	 *  Set the method.
	 */
	public void setMethod(MethodInfo method)
	{
		this.method = method;
	}
	
	/**
	 *  Get the caller.
	 */
	public IComponentIdentifier getCaller()
	{
		return caller;
	}
	
	/**
	 *  Set the caller.
	 */
	public void setCaller(IComponentIdentifier caller)
	{
		this.caller = caller;
	}
}
