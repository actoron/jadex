package jadex.bdi.model;

import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;

/**
 *  Extended service info to include scope.
 *  of service.
 */
public class ScopedProvidedServiceInfo	extends ProvidedServiceInfo
{
	//-------- attributes --------
	
	/** The scope (mcapability). */
	protected Object	scope;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service info.
	 */
	public ScopedProvidedServiceInfo()
	{
		// Bean constructor.
	}
	
	/**
	 *  Create a new service info.
	 */
	public ScopedProvidedServiceInfo(String name, Class type, ProvidedServiceImplementation implementation, Object scope)
	{
		super(name, type, implementation);
		this.scope	= scope;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the scope.
	 */
	// No bean accessor, because only used internally.
	public Object	fetchScope()
	{
		return scope;
	}
	
	/**
	 *  Set the scope.
	 */
	// No bean accessor, because only used internally.
	public void	putScope(Object scope)
	{
		this.scope	= scope;
	}
}
