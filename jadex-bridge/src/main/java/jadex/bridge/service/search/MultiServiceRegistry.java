package jadex.bridge.service.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceRegistry.UnlimitedIntermediateDelegationResultListener;
import jadex.bridge.service.types.registry.IRegistryListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  Service registry that holds copies of multiple other platform
 *  registries. Search methods operate transparently on all subregistries.
 */
public class MultiServiceRegistry implements IMultiServiceRegistry//extends IServiceRegistry
{
	/** Platform ID. */
	protected IComponentIdentifier platformid;
	
	/** The locally cloned registries of remote platforms. */
	protected Map<IComponentIdentifier, IServiceRegistry> remoteregistries;
	
	/** The local registry. */
	protected IServiceRegistry localregistry;
	
	/** Currently active global queries (local registry is constant so we don't need to save local queries). */
	protected Map<ServiceQuery<?>, ServiceQueryInfo<?>> globalqueries;
	
	/** The registry listeners. */
	protected List<IRegistryListener> listeners;
	
	public MultiServiceRegistry(IComponentIdentifier platformid)
	{
		this.platformid = platformid;
		localregistry = new ServiceRegistry();
		remoteregistries = new HashMap<IComponentIdentifier, IServiceRegistry>();
	}

	/**
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	// write
	public IFuture<Void> addService(ClassInfo key, IService service)
	{
//		return getSubregistry(service.getServiceIdentifier().getProviderId().getRoot()).addService(key, service);
		IComponentIdentifier serviceplatform = service.getServiceIdentifier().getProviderId().getRoot();
		getSubregistry(serviceplatform).addService(key, service);
		return IFuture.DONE;
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	// write
	public void removeService(ClassInfo key, IService service)
	{
		//getSubregistry(service.getServiceIdentifier().getProviderId().getRoot()).removeService(key, service);
		IComponentIdentifier serviceplatform = service.getServiceIdentifier().getProviderId().getRoot();
		getSubregistry(serviceplatform).removeService(key, service);
	}
	
	/**
	 *  Search for services.
	 */
	// read
	public <T> T searchServiceSync(ServiceQuery<T> query)
	{
		T ret = null;
		
		ret = localregistry.searchServiceSync(query);
		
		if (ret == null)
		{
			Iterator<Map.Entry<IComponentIdentifier, IServiceRegistry>> it = remoteregistries.entrySet().iterator();
			while (it.hasNext() && ret == null)
			{
				ret = it.next().getValue().searchServiceSync(query);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public <T> Collection<T> searchServicesSync(ServiceQuery<T> query)
	{
		Collection<T> ret = localregistry.searchServicesSync(query);
		Iterator<Map.Entry<IComponentIdentifier, IServiceRegistry>> it = remoteregistries.entrySet().iterator();
		while (it.hasNext())
		{
			Collection<T> c = it.next().getValue().searchServicesSync(query);
			if (ret != null)
				ret.addAll(c);
			else
				ret = c;
		}
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public <T> IFuture<T> searchServiceAsync(final ServiceQuery<T> query)
	{
		final Future<T> ret = new Future<T>();
		
		localregistry.searchServiceAsync(query).addResultListener(new IResultListener<T>()
		{
			public void resultAvailable(T result)
			{
				if (result != null)
					ret.setResult(result);
				else
				{
					List<IServiceRegistry> regs = new ArrayList<IServiceRegistry>(remoteregistries.values());
					if (regs.size() > 0)
					{
						final Iterator<IServiceRegistry> it = regs.iterator();
						it.next().searchServiceAsync(query).addResultListener(new IResultListener<T>()
						{
							public void resultAvailable(T result)
							{
								if (result != null)
									ret.setResult(result);
								else
								{
									if (it.hasNext())
										it.next().searchServiceAsync(query).addResultListener(this);
									else
										ret.setResult(null);
								}
							};
							
							public void exceptionOccurred(Exception exception)
							{
								resultAvailable(null);
							}
						});
					}
					else
						ret.setResult(null);
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				resultAvailable(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> searchServicesAsync(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		final List<IServiceRegistry> remoteregs = new ArrayList<IServiceRegistry>(remoteregistries.values());
		localregistry.searchServicesAsync(query).addResultListener(new IResultListener<Collection<T>>()
		{
			public void resultAvailable(Collection<T> result)
			{
				if (result != null)
				{
					for (T res : result)
						ret.addIntermediateResult(res);
				}
				
				if (!remoteregs.isEmpty())
				{
					IServiceRegistry reg = remoteregs.remove(remoteregs.size() - 1);
					reg.searchServicesAsync(query).addResultListener(this);
				}
				else
					ret.setFinished();
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		});
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
//	// read
	@Deprecated
	public <T> T searchService(ServiceQuery<T> query, boolean excluded)
	{
		return localregistry.searchService(query, excluded);
	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	// write
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		localregistry.addQuery(query).addIntermediateResultListener(new UnlimitedIntermediateDelegationResultListener<T>(ret));
		// Termination is handled by the synchronization layer since a write lock is needed
		// to remove query.
		
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(query.getScope()))
		{
			globalqueries.put(query, new ServiceQueryInfo<T>(query, ret));
			for (IServiceRegistry reg : remoteregistries.values())
				reg.addQuery(query).addIntermediateResultListener(new UnlimitedIntermediateDelegationResultListener<T>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	// write
	public <T> void removeQuery(ServiceQuery<T> query)
	{
		localregistry.removeQuery(query);
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(query.getScope()))
		{
			for (IServiceRegistry reg : remoteregistries.values())
				reg.removeQuery(query);
			// finishedIfUndone because this method gets invoked by the termination command as well.
			globalqueries.remove(query).getFuture().setFinishedIfUndone();
		}
	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	// write
	public void removeQueries(IComponentIdentifier owner)
	{
		// This should be optimized!
		
		localregistry.removeQueries(owner);
		for (IServiceRegistry rreg : remoteregistries.values())
			rreg.removeQueries(owner);
		for (Iterator<Map.Entry<ServiceQuery<?>, ServiceQueryInfo<?>>> it = globalqueries.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<ServiceQuery<?>, ServiceQueryInfo<?>> entry = it.next();
			if (owner.equals(entry.getKey().getOwner()))
				it.remove();
		}
	}
	
	/**
	 *  Get queries per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return The queries.
	 */
	// read
//	public <T> Set<ServiceQueryInfo<T>> getQueries(ClassInfo type);
	
	/**
	 *  Add an excluded component. 
	 *  @param The component identifier.
	 */
	// write
	public void addExcludedComponent(IComponentIdentifier cid)
	{
		localregistry.addExcludedComponent(cid);
	}
	
	/**
	 *  Remove an excluded component. 
	 *  @param The component identifier.
	 */
	// write
	public IFuture<Void> removeExcludedComponent(IComponentIdentifier cid)
	{
		return localregistry.removeExcludedComponent(cid);
	}
	
	/**
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	// read
	public boolean isIncluded(IComponentIdentifier cid, IService ser)
	{
		return localregistry.isIncluded(cid, ser);
	}
	
	/**
	 *  Add a new registry.
	 *  @param registry The registry.
	 */
//	public void addSubregistry(IComponentIdentifier cid, IServiceRegistry registry)
//	{
//		if(registries.containsKey(cid))
//			throw new RuntimeException("Registry already contained: "+cid);
//		registries.put(cid, registry);
//		
////		registryAdded(registry);
//	}
	
	/**
	 *  Remove a subregistry.
	 *  @param cid The platform id.
	 */
	// write
	public void removeSubregistry(IComponentIdentifier cid)
	{
		// Remove all services to trigger removed events
//		IServiceRegistry reg = registries.get(cid);
//		ServiceQuery<IService> query = new ServiceQuery<IService>();
//		query.setScope(RequiredServiceInfo.SCOPE_PLATFORM);
//		Collection<IService> sers = reg.searchServicesSync(query);
//		if(sers!=null)
//		{
//			for(IService ser: sers)
//			{
//				reg.removeService(ser.getServiceIdentifier().getServiceType(), ser);
//			}
//		}
		IServiceRegistry removedreg = remoteregistries.remove(cid);
		removedreg.searchServicesSync(new ServiceQuery<T>());
	}
	
	/**
	 *  Sets the platform ID.
	 */
	// write
//	public void setPlatform(IComponentIdentifier platformid)
//	{
//		this.platformid = platformid;
//		localregistry = remoteregistries.remove(platformid);
//		if (localregistry == null)
//			localregistry = new ServiceRegistry();
//	}
	
	/**
	 *  Get a subregistry.
	 *  @param cid The platform id.
	 *  @return The registry.
	 */
	protected IServiceRegistry getSubregistry(IComponentIdentifier cid)
	{
		IServiceRegistry ret = null;
		if(cid!=null)
			cid = cid.getRoot();
		if (platformid != null && platformid.equals(cid))
		{
			ret = localregistry;
		}
		else
		{
			ret = remoteregistries.get(cid);
			if(ret==null)
			{
				ret = new ServiceRegistry();
				remoteregistries.put(cid, ret);
//				addSubregistry(cid, ret);
			}
		}
		return ret;
	}
}
