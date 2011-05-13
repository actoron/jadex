package jadex.bridge.service;

import jadex.bridge.service.component.BasicServiceInvocationHandler;

/**
 *  Info for provided services.
 */
public class ProvidedServiceInfo
{
	//-------- attributes --------

	// todo:
	
//	/** The name (used for referencing). */
//	protected String name;
	
	/** The service interface type. */
	protected Class type;
	
	/** The service implementation. */
	protected ProvidedServiceImplementation implementation;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo()
	{
		// bean constructor
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(Class type)
	{
		this(type, (ProvidedServiceImplementation)null);
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(Class type, String expression)
	{
		this(type, new ProvidedServiceImplementation(null, expression, BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED, null));
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(Class type, ProvidedServiceImplementation implementation)
	{
		this.type = type;
		this.implementation = implementation;
	}
	
	//-------- methods --------

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public Class getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(Class type)
	{
		this.type = type;
	}

	/**
	 *  Get the implementation.
	 *  @return The implementation.
	 */
	public ProvidedServiceImplementation getImplementation()
	{
		return implementation;
	}

	/**
	 *  Set the implementation.
	 *  @param implementation The implementation to set.
	 */
	public void setImplementation(ProvidedServiceImplementation implementation)
	{
		this.implementation = implementation;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ProvidedServiceInfo(type=" + type + ", implementation="
			+ implementation + ")";
	}
	
	
}
