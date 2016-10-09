package jadex.bridge.service.search;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceRegistry.UnlimitedIntermediateDelegationResultListener;
import jadex.bridge.service.types.registry.IRegistryListener;
import jadex.commons.IAsyncFilter;
import jadex.commons.IFilter;
import jadex.commons.collection.MultiIterator;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  Service registry that holds copies of multiple other platform
 *  registries. Search methods operate transparently on all subregistries.
 */
public class MultiServiceRegistry implements IServiceRegistry, IRegistryDataProvider//extends IServiceRegistry
{
	/** The locally cloned registries of remote platforms. */
	protected Map<IComponentIdentifier, IServiceRegistry> registries;
	
	/** The persistent service queries. */
	protected Map<ClassInfo, Set<ServiceQueryInfo<?>>> queries;
	
	/** The default search functionality. */
	protected RegistrySearchFunctionality searchfunc;
	
	/**
	 *  Create a new registry.
	 */
	public MultiServiceRegistry()//RegistrySearchFunctionality searchfunc)
	{
		this.searchfunc = new RegistrySearchFunctionality(this);
	}

	/**
	 *  Get the service map.
	 *  @return The full service map.
	 */
	public Map<ClassInfo, Set<IService>> getServiceMap()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get services per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return First matching service or null.
	 */
	public Iterator<IService> getServices(ClassInfo type)
	{
		MultiIterator<IService> ret = new MultiIterator<IService>();
		
//		if(type.getTypeName().indexOf("Fact")!=-1)
//			System.out.println("hhhhhhhhhhhhheere");
		
		if(registries!=null)
		{
			for(Map.Entry<IComponentIdentifier, IServiceRegistry> entry: registries.entrySet())
			{
				IServiceRegistry reg = entry.getValue();
				Iterator<IService> it = reg.getServices(type);
				if(it!=null)
					ret.addIterator(it);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	public IFuture<Void> addService(ClassInfo key, IService service)
	{
		IServiceRegistry reg = getSubregistry(service.getServiceIdentifier().getProviderId());
		return reg.addService(key, service);
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	public void removeService(ClassInfo key, IService service)
	{
		IServiceRegistry reg = getSubregistry(service.getServiceIdentifier().getProviderId());
		reg.removeService(key, service);
	}
	
	/**
	 *  Add an excluded component. 
	 *  @param The component identifier.
	 */
	public void addExcludedComponent(IComponentIdentifier cid)
	{
		IServiceRegistry reg = getSubregistry(cid);
		reg.addExcludedComponent(cid);
	}
	
	/**
	 *  Remove an excluded component. 
	 *  @param The component identifier.
	 */
	public IFuture<Void> removeExcludedComponent(IComponentIdentifier cid)
	{
		IServiceRegistry reg = getSubregistry(cid);
		return reg.removeExcludedComponent(cid);
	}
	
	/**
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	public boolean isIncluded(IComponentIdentifier cid, IService ser)
	{
		IServiceRegistry reg = getSubregistry(cid);
		return reg.isIncluded(cid, ser);
	}
	
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		if(queries==null)
			queries = new HashMap<ClassInfo, Set<ServiceQueryInfo<?>>>();
		
		Set<ServiceQueryInfo<T>> mqs = (Set)queries.get(query.getType());
		if(mqs==null)
		{
			mqs = new HashSet<ServiceQueryInfo<T>>();
			queries.put(query.getType(), (Set)mqs);
		}
		mqs.add(new ServiceQueryInfo<T>(query, ret));
		
		// addQueryOnAllRegistries
		if(registries!=null)
		{
			for(final IServiceRegistry reg: registries.values())
			{
				ISubscriptionIntermediateFuture<T> fut = reg.addQuery(query);
				fut.addIntermediateResultListener(new UnlimitedIntermediateDelegationResultListener<T>(ret));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> void removeQuery(ServiceQuery<T> query)
	{
		if(queries!=null)
		{
			Set<ServiceQueryInfo<T>> mqs = (Set)queries.get(query.getType());
			if(mqs!=null)
			{
				for(ServiceQueryInfo<T> sqi: mqs)
				{
					if(sqi.getQuery().equals(query))
					{
						sqi.getFuture().terminate();
						mqs.remove(sqi);
						break;
					}
				}
				if(mqs.size()==0)
					queries.remove(query.getType());
				
				// removeQueryOnAllRegistries
				if(registries!=null)
				{
					for(IServiceRegistry reg: registries.values())
					{
						reg.removeQuery(query);
					}
				}
			}
		}
	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	public void removeQueries(IComponentIdentifier owner)
	{
		if(queries!=null)
		{
			// removeQueryOnAllRegistries
			if(registries!=null)
			{
				for(IServiceRegistry reg: registries.values())
				{
					reg.removeQueries(owner);
				}
			}
			
			for(Map.Entry<ClassInfo, Set<ServiceQueryInfo<?>>> entry: queries.entrySet())
			{
				for(ServiceQueryInfo<?> query: entry.getValue().toArray(new ServiceQueryInfo<?>[entry.getValue().size()]))
				{
					if(owner.equals(query.getQuery().getOwner()))
					{
						removeQuery(query.getQuery());
//						entry.getValue().remove(query);
					}
				}
			}
		}
	}
	
	/**
	 *  Get queries per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return The queries.
	 */
	public <T> Set<ServiceQueryInfo<T>> getQueries(ClassInfo type)
	{
		return queries==null? Collections.EMPTY_SET: queries.get(type); 
	}
	
	/**
	 *  Search for services.
	 */
	public <T> T searchService(ClassInfo type, IComponentIdentifier cid, String scope)
	{
		return searchService(type, cid, scope, false);
	}
	
	/**
	 *  Search for services.
	 */
	// read
	public <T> T searchService(ClassInfo type, IComponentIdentifier cid, String scope, boolean excluded)
	{
		return searchfunc.searchService(type, cid, scope, excluded);
	}
	
	/**
	 *  Search for services.
	 */
	// read
	public <T> Collection<T> searchServices(ClassInfo type, IComponentIdentifier cid, String scope)
	{
		return searchfunc.searchServices(type, cid, scope);
	}
	
	/**
	 *  Search for service.
	 */
	public <T> T searchService(ClassInfo type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		return searchfunc.searchService(type, cid, scope, filter);
	}
	
	/**
	 *  Search for service.
	 */
	public <T> Collection<T> searchServices(ClassInfo type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		return searchfunc.searchServices(type, cid, scope, filter);
	}
	
	/**
	 *  Search for service.
	 */
	public <T> IFuture<T> searchService(ClassInfo type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		return searchfunc.searchService(type, cid, scope, filter);
	}
	
	/**
	 *  Search for services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> searchServices(ClassInfo type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		return searchfunc.searchServices(type, cid, scope, filter);
	}
	
	/**
	 *  Search for services.
	 */
	public  <T> IFuture<T> searchGlobalService(final ClassInfo type, IComponentIdentifier cid, final IAsyncFilter<T> filter)
	{
		return searchfunc.searchService(type, cid, RequiredServiceInfo.SCOPE_GLOBAL, filter);
	}
	
	/**
	 *  Search for services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> searchGlobalServices(ClassInfo type, IComponentIdentifier cid, IAsyncFilter<T> filter)
	{
		return searchfunc.searchServices(type, cid, RequiredServiceInfo.SCOPE_GLOBAL, filter);
	}
	
	/**
	 *  Add an event listener.
	 *  @param listener The listener.
	 */
	public void addEventListener(IRegistryListener listener)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Remove an event listener.
	 *  @param listener The listener.
	 */
	public void removeEventListener(IRegistryListener listener)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get a subregistry.
	 *  @param cid The platform id.
	 *  @return The registry.
	 */
	public IServiceRegistry getSubregistry(IComponentIdentifier cid)
	{
		if(cid!=null)
			cid = cid.getRoot();
		if(registries==null)
			registries = new HashMap<IComponentIdentifier, IServiceRegistry>();
		IServiceRegistry ret = registries.get(cid);
		if(ret==null)
		{
			ret = new ServiceRegistry();
//			System.out.println("Created registry for: "+cid);
			addSubregistry(cid, ret);
		}
		return ret;
	}
	
	/**
	 *  Add a new registry.
	 *  @param registry The registry.
	 */
	public void addSubregistry(IComponentIdentifier cid, IServiceRegistry registry)
	{
		if(registries==null)
			registries = new HashMap<IComponentIdentifier, IServiceRegistry>();
		if(registries.containsKey(cid))
			throw new RuntimeException("Registry already contained: "+cid);
		registries.put(cid, registry);
		
		registryAdded(registry);
	}
	
	/**
	 *  Remove an existing registry.
	 *  @param cid The component id to remove.
	 */
	public void removeSubregistry(IComponentIdentifier cid)
	{
		if(registries==null || !registries.containsKey(cid))
			throw new RuntimeException("Registry not contained: "+cid);
		
		// Remove all services to trigger removed events
		IServiceRegistry reg = registries.get(cid);
		Map<ClassInfo, Set<IService>> sers = reg.getServiceMap();
		if(sers!=null)
		{
			for(Map.Entry<ClassInfo, Set<IService>> entry: sers.entrySet())
			{
				for(IService ser: entry.getValue())
				{
					reg.removeService(entry.getKey(), ser);
				}
			}
		}
		
		registries.remove(cid);
	}
	
	/**
	 *  Called when a new registry was added.
	 */
	protected void registryAdded(IServiceRegistry registry)
	{
		// Add existing queries on the new registry
		if(queries!=null && registries!=null)
		{
			for(Set<ServiceQueryInfo<?>> queries: queries.values().toArray(new Set[queries.size()]))
			{
				for(ServiceQueryInfo<?> query: queries)
				{
					registry.addQuery(query.getQuery()).addIntermediateResultListener(
						new UnlimitedIntermediateDelegationResultListener(query.getFuture()));
				}
			}
		}
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "MultiServiceRegistry [registries=" + registries.keySet()+"]";
	}
	
	
}
