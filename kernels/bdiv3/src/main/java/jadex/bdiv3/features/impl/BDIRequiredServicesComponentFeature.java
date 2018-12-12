package jadex.bdiv3.features.impl;

import java.util.Collection;

import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.impl.RCapability;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.service.component.RequiredServicesComponentFeature;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

/**
 *  Feature for required services.
 */
// Todo: synchronous or asynchronous (for search)?
public class BDIRequiredServicesComponentFeature extends RequiredServicesComponentFeature
{
	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public BDIRequiredServicesComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}

	//-------- accessors for declared services --------
	
	/**
	 *  Resolve a declared required service of a given name.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getService(String name)
	{
		return super.getService(rename(name));
	}
	
	/**
	 *  Resolve a required services of a given name.
	 *  Asynchronous method for locally as well as remotely available services.
	 *  @param name The services name.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getServices(String name)
	{
		return super.getServices(rename(name));
	}
	
	/**
	 *  Resolve a declared required service of a given name.
	 *  Synchronous method only for locally available services.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> T getLocalService(String name)
	{
		return super.getLocalService(rename(name));
	}
	
	/**
	 *  Resolve a required services of a given name.
	 *  Synchronous method only for locally available services.
	 *  @param name The services name.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> Collection<T> getLocalServices(String name)
	{
		return super.getLocalServices(rename(name));
	}
	
	//-------- query methods --------

	/**
	 *  Add a query for a declared required service.
	 *  Continuously searches for matching services.
	 *  @param name The name of the required service declaration.
	 *  @return Future providing the corresponding services as intermediate results.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(String name)
	{
		return super.addQuery(rename(name));
	}
	
	//-------- template methods --------
	
	/**
	 *  Rename the service name according to the current capability.
	 */
	protected String rename(String name)
	{
		if(name.indexOf(MElement.CAPABILITY_SEPARATOR)!=-1)
			return name;
		
		RPlan rplan = RPlan.RPLANS.get();
		String capa = null;
		if(rplan!=null)
		{
			MPlan mplan = (MPlan)rplan.getModelElement();
			capa = RCapability.getCapabilityPart(mplan.getName());
		}
		return capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+name : name;
	}
}
