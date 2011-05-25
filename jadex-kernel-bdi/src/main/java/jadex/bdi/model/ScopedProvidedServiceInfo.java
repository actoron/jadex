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
	public ScopedProvidedServiceInfo(Class type, ProvidedServiceImplementation implementation, Object scope)
	{
		super(type, implementation);
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
}
