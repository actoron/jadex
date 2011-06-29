package jadex.bridge.service;

import jadex.bridge.service.component.BasicServiceInvocationHandler;

/**
 *  Info for provided services.
 */
public class ProvidedServiceInfo
{
	//-------- attributes --------

	/** The name (used for referencing). */
	protected String name;
	
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
		this(null, type);
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(String name, Class type)
	{
		this(name, type, (ProvidedServiceImplementation)null);
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(String name, Class type, String expression)
	{
		this(name, type, new ProvidedServiceImplementation(null, expression, 
			BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED, null, null));
	}
	
	/**
	 *  Create a new service info.
	 */
	public ProvidedServiceInfo(String name, Class type, ProvidedServiceImplementation implementation)
	{
		this.name = name;
		this.type = type;
		this.implementation = implementation;
	}
	
//	/**
//	 *  Create a new service info.
//	 */
//	public ProvidedServiceInfo(ProvidedServiceInfo orig)
//	{
//		this(orig.getType(), new ProvidedServiceImplementation(orig.getImplementation()));
//	}
	
	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
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
		return "ProvidedServiceInfo(name="+name+", type="+ type + ", implementation="+ implementation + ")";
	}
}
