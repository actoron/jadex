package jadex.bdiv3.features.impl;

import java.util.Collection;

import jadex.bdiv3.actions.ExecutePlanStepAction;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.impl.RCapability;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.service.IRequiredServiceFetcher;
import jadex.bridge.service.component.RequiredServicesComponentFeature;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.IFuture;
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

	/**
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name)
	{
		return super.getRequiredService(rename(name), false, null);
	}
	
	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return The service.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name)
	{
		return super.getRequiredServices(rename(name), false, null);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind)
	{
		return super.getRequiredService(rename(name), rebind, null);
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind)
	{
		return super.getRequiredServices(rename(name), rebind, null);
	}
	
	/**
	 *  Get a required services.
	 *  @return The services.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind, IAsyncFilter<T> filter)
	{
		return super.getRequiredServices(rename(name), rebind, filter);
	}
	
	/**
	 *  Get a multi service.
	 *  @param reqname The required service name.
	 *  @param multitype The interface of the multi service.
	 */
	public <T> T getMultiService(String reqname, Class<T> multitype)
	{
		return super.getMultiService(rename(reqname), multitype);
	}
	
//	/**
//	 *  Get a required service.
//	 *  @return The service.
//	 */
//	protected <T> IFuture<T> getRequiredService(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind, IAsyncFilter<T> filter)
//	{
//		super.getRequiredService(info, binding, rebind, filter);
//	}
	
//	/**
//	 *  Get required services.
//	 *  @return The services.
//	 */
//	protected <T> ITerminableIntermediateFuture<T> getRequiredServices(RequiredServiceInfo info, RequiredServiceBinding binding, boolean rebind, IAsyncFilter<T> filter)
//	{
//		super.getRequiredService(info, binding, rebind, filter);
//	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind, IAsyncFilter<T> filter)
	{
		return super.getRequiredService(rename(name), rebind, filter);
	}
	
	/**
	 *  Get the result of the last search.
	 *  @param name The required service name.
	 *  @return The last result.
	 */
	public <T> T getLastRequiredService(String name)
	{
		return super.getLastRequiredService(rename(name));
	}
	
	/**
	 *  Get the result of the last search.
	 *  @param name The required services name.
	 *  @return The last result.
	 */
	public <T> Collection<T> getLastRequiredServices(String name)
	{
		return super.getLastRequiredService(rename(name));
	}
	
	/**
	 *  Get a required service fetcher.
	 *  @param name The required service name.
	 *  @return The service fetcher.
	 */
	protected IRequiredServiceFetcher getRequiredServiceFetcher(String name)
	{
		return super.getRequiredServiceFetcher(rename(name));
	}
	
	/**
	 *  Rename the service name according to the current capability.
	 */
	protected String rename(String name)
	{
		if(name.indexOf(MElement.CAPABILITY_SEPARATOR)!=-1)
			return name;
		
		RPlan rplan = ExecutePlanStepAction.RPLANS.get();
		String capa = null;
		if(rplan!=null)
		{
			MPlan mplan = (MPlan)rplan.getModelElement();
			capa = RCapability.getCapabilityPart(mplan.getName());
		}
		return capa!=null ? capa+MElement.CAPABILITY_SEPARATOR+name : name;
	}
}
