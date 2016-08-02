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
import jadex.commons.IAsyncFilter;
import jadex.commons.collection.MultiIterator;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  Service registry that holds copies of multiple other platform
 *  registries. Search methods operate transparently on all subregistries.
 */
public class MultiServiceRegistry extends AbstractServiceRegistry
{
	/** The locally cloned registries of remote platforms. */
	protected Map<IComponentIdentifier, AbstractServiceRegistry> registries;
	
	/** The queries. */
	protected Map<ClassInfo, Set<ServiceQuery<?>>> queries;
	
	/**
	 *  Get services per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return First matching service or null.
	 */
	public Iterator<IService> getServices(ClassInfo type)
	{
		MultiIterator<IService> ret = new MultiIterator<IService>();
		
		if(registries!=null)
		{
			for(Map.Entry<IComponentIdentifier, AbstractServiceRegistry> entry: registries.entrySet())
			{
				AbstractServiceRegistry reg = entry.getValue();
				ret.addIterator(reg.getServices(type));
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
		IComponentIdentifier cid = service.getServiceIdentifier().getProviderId().getRoot();
		AbstractServiceRegistry reg = internalGetRegistry(cid);
		return reg.addService(key, service);
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	public void removeService(ClassInfo key, IService service)
	{
		IComponentIdentifier cid = service.getServiceIdentifier().getProviderId().getRoot();
		AbstractServiceRegistry reg = internalGetRegistry(cid);
		reg.removeService(key, service);
	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		if(queries==null)
			queries = new HashMap<ClassInfo, Set<ServiceQuery<?>>>();
		
		Set<ServiceQuery<T>> mqs = (Set)queries.get(query.getType());
		if(mqs==null)
		{
			mqs = new HashSet<ServiceQuery<T>>();
			queries.put(query.getType(), (Set)mqs);
		}
		mqs.add(query);
		
		// addQueryOnAllRegistries
		if(registries!=null)
		{
			for(final AbstractServiceRegistry reg: registries.values())
			{
				ISubscriptionIntermediateFuture<T> fut = reg.addQuery(query);
				fut.addIntermediateResultListener(new IIntermediateResultListener<T>()
				{
					public void intermediateResultAvailable(T result)
					{
						ret.addIntermediateResult(result);
					}
					
					public void finished()
					{
						// Ignore when single queries terminate
						System.out.println("Query finished on a registry: "+reg);
					}
					
					public void resultAvailable(Collection<T> result)
					{
						// Ignore when single queries finished
						System.out.println("Query finished on a registry: "+reg);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// Ignore when single queries finished
						System.out.println("Query exception on a registry: "+reg+" "+exception.getMessage());
					}
				});
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
			queries.remove(query);
			
			// removeQueryOnAllRegistries
			if(registries!=null)
			{
				for(AbstractServiceRegistry reg: registries.values())
				{
					reg.removeQuery(query);
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
				for(AbstractServiceRegistry reg: registries.values())
				{
					reg.removeQueries(owner);
				}
			}
			
			for(Map.Entry<ClassInfo, Set<ServiceQuery<?>>> entry: queries.entrySet())
			{
				for(ServiceQuery<?> query: entry.getValue())
				{
					if(owner.equals(query.getOwner()))
					{
						entry.getValue().remove(query);
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
	 *  Get the registry per platform identifier.
	 *  @param cid The component identifier.
	 *  @return The registry.
	 */
	protected AbstractServiceRegistry internalGetRegistry(IComponentIdentifier cid)
	{
		if(registries==null)
			registries = new HashMap<IComponentIdentifier, AbstractServiceRegistry>();
		AbstractServiceRegistry ret = registries.get(cid);
		if(ret==null)
		{
			ret = new ServiceRegistry();
			System.out.println("Created registry for: "+cid);
			addRegistry(cid, ret);
		}
		return ret;
	}
	
	/**
	 *  Add a new registry.
	 *  @param registry The registry.
	 */
	protected void addRegistry(IComponentIdentifier cid, AbstractServiceRegistry registry)
	{
		if(registries==null)
			registries = new HashMap<IComponentIdentifier, AbstractServiceRegistry>();
		if(registries.containsKey(cid))
			throw new RuntimeException("Registry already contained: "+cid);
		registries.put(cid, registry);
		
		registryAdded(registry);
	}
	
	/**
	 *  Remove an existing registry.
	 *  @param cid The component id to remove.
	 */
	protected void removeRegistry(IComponentIdentifier cid)
	{
		if(registries==null || !registries.containsKey(cid))
			throw new RuntimeException("Registry not contained: "+cid);
		registries.remove(cid);
	}
	
	/**
	 *  Called when a new registry was added.
	 */
	protected void registryAdded(AbstractServiceRegistry registry)
	{
		// Add existing queries on the new registry
		if(queries!=null && registries!=null)
		{
			for(Set<ServiceQuery<?>> queries: queries.values())
			{
				for(ServiceQuery<?> query: queries)
				{
					registry.addQuery(query);
				}
			}
		}
	}
}
