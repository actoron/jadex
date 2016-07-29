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
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;

/**
 *  Local service registry. 
 *  
 *  - Search fetches services by types and excludes some according to the scope. 
 *  - Allows for adding persistent queries.
 */
public class LocalServiceRegistry extends AbstractServiceRegistry
{
	//-------- attributes --------
	
	/** The map of published services sorted by type. */
	protected Map<ClassInfo, Set<IService>> services;
	
	/** The excluded components. */
	protected Set<IComponentIdentifier> excluded;
	
	/** The persistent service queries. */
	protected Map<ClassInfo, Set<ServiceQueryInfo<?>>> queries;
	
	/** The excluded services cache. */
	protected Map<IComponentIdentifier, Set<IService>> excludedservices;
	
	//-------- methods --------
	
	/**
	 *  Get a service per type.
	 *  @param type The interface type.
	 *  @return First matching service or null.
	 */
	protected <T> T getService(Class<T> type)
	{
		Set<T> sers = services==null? null: (Set<T>)services.get(new ClassInfo(type));
		return sers==null || sers.size()==0? null: (T)sers.iterator().next();
	}

	/**
	 *  Get services per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return First matching service or null.
	 */
	public Iterator<IService> getServices(ClassInfo type)
	{
		Set<IService> ret = Collections.emptySet();
		if(services!=null)
		{
			if(type!=null)
			{
				ret = services.get(type);
			}
			else
			{
				// Return all if type is null
				ret = new HashSet<IService>();
				for(ClassInfo t: services.keySet())
				{
					ret.addAll(services.get(t));
				}
			}
		}
		
		return ret.iterator();
	}
	
	/**
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	public boolean isIncluded(IComponentIdentifier cid, IService ser)
	{
		boolean ret = true;
		if(excluded!=null && excluded.contains(ser.getServiceIdentifier().getProviderId()) && cid!=null)
		{
			IComponentIdentifier target = ser.getServiceIdentifier().getProviderId();
			ret = getDotName(cid).endsWith(getDotName(target));
		}
		return ret;
	}
	
	/**
	 *  Add an excluded component. 
	 *  @param The component identifier.
	 */
	public void addExcludedComponent(IComponentIdentifier cid)
	{
		if(excluded==null)
			excluded = new HashSet<IComponentIdentifier>();
		excluded.add(cid);
	}
	
	/**
	 *  Remove an excluded component. 
	 *  @param The component identifier.
	 */
	public IFuture<Void> removeExcludedComponent(IComponentIdentifier cid)
	{
		Future<Void> ret = new Future<Void>();
		CounterResultListener<Void> lis = null;
		
//		System.out.println("cache size: "+excludedservices==null? "0":excludedservices.size());
		
		if(excluded!=null)
		{
			if(excluded.remove(cid))
			{
				Set<IService> exs = excludedservices.remove(cid);
				
				// Notify queries that new services are available
				// Must iterate over all services :-( todo: add index?
				if(queries!=null && queries.size()>0)
				{
					if(excludedservices!=null)
					{
						// Get and remove services from cache
						if(exs!=null)
						{
							lis = new CounterResultListener<Void>(exs.size(), 
								new DelegationResultListener<Void>(ret));
							for(IService ser: exs)
							{
								checkQueries(ser).addResultListener(lis);
							}
						}
					}
					
//					bar = new FutureBarrier<Void>();
//					
//					for(Set<IService> sers: services.values())
//					{
//						for(IService ser: sers)
//						{
//							if(ser.getServiceIdentifier().getProviderId().equals(cid))
//							{
//								bar.addFuture(checkQueries(ser));
//							}
//						}
//					}
//					
//					bar.waitFor().addResultListener(new DelegationResultListener<Void>(ret));;
				}
			}
		}
		
		if(lis==null)
			ret.setResult(null);
		
		return ret;
	}
	
	/**
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	public IFuture<Void> addService(ClassInfo key, IService service)
	{
//		if(service.getServiceIdentifier().getServiceType().getTypeName().indexOf("ITest")!=-1)
//			System.out.println("added: "+service.getServiceIdentifier().getServiceType()+" - "+service.getServiceIdentifier().getProviderId());
		
		if(services==null)
			services = new HashMap<ClassInfo, Set<IService>>();
		
		Set<IService> sers = services.get(key);
		if(sers==null)
		{
			sers = new HashSet<IService>();
			services.put(key, sers);
		}
		
		sers.add(service);
		
		// If services belongs to excluded component cache them
		IComponentIdentifier cid = service.getServiceIdentifier().getProviderId();
		if(excluded!=null && excluded.contains(cid))
		{
			if(excludedservices==null)
				excludedservices = new HashMap<IComponentIdentifier, Set<IService>>();
			Set<IService> exsers = excludedservices.get(cid);
			if(exsers==null)
			{
				exsers = new HashSet<IService>();
				excludedservices.put(cid, exsers);
			}
			exsers.add(service);
		}
		
		return checkQueries(service);
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	public void removeService(ClassInfo key, IService service)
	{
//		if(service.getServiceIdentifier().getServiceType().getTypeName().indexOf("ITest")!=-1)
//			System.out.println("removed: "+service.getServiceIdentifier().getServiceType()+" - "+service.getServiceIdentifier().getProviderId());
		
		if(services!=null)
		{
			Set<IService> sers = services.get(key);
			if(sers!=null)
			{
				sers.remove(service);
			}
			else
			{
				System.out.println("Could not remove service from registry: "+key+", "+service.getServiceIdentifier());
			}
		}
		else
		{
			System.out.println("Could not remove service from registry: "+key+", "+service.getServiceIdentifier());
		}
	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		ret.setTerminationCommand(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				removeQuery(query);
			}
		});
		
		if(queries==null)
			queries = new HashMap<ClassInfo, Set<ServiceQueryInfo<?>>>();
		
		Set<ServiceQueryInfo<T>> mqs = (Set)queries.get(query.getType());
		if(mqs==null)
		{
			mqs = new HashSet<ServiceQueryInfo<T>>();
			queries.put(query.getType(), (Set)mqs);
		}
		mqs.add(new ServiceQueryInfo(query, ret));
		
		// deliver currently available services
		Set<T> sers = (Set<T>)getServices(query.getType());
		if(sers!=null)
		{
			searchLoopServices(query.getFilter(), sers.iterator(), query.getOwner(), query.getScope())
				.addIntermediateResultListener(new IIntermediateResultListener<T>()
			{
				public void intermediateResultAvailable(T result)
				{
					ret.addIntermediateResultIfUndone(result);
				}
	
				public void finished()
				{
					// the query is not finished after the status quo is delivered
				}
	
				public void resultAvailable(Collection<T> results)
				{
					for(T result: results)
					{
						intermediateResultAvailable(result);
					}
					// the query is not finished after the status quo is delivered
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// the query is not finished after the status quo is delivered
				}
			});
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
			Set<ServiceQuery<T>> mqs = (Set)queries.get(query.getType());
			if(mqs!=null)
			{
				mqs.remove(query);
				if(mqs.size()==0)
					queries.remove(query.getType());
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
			for(Map.Entry<ClassInfo, Set<ServiceQueryInfo<?>>> entry: queries.entrySet())
			{
				for(ServiceQueryInfo<?> query: entry.getValue())
				{
					if(owner.equals(query.getQuery().getOwner()))
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
	 *  Get the service map.
	 */
	public Map<ClassInfo, Set<IService>> getServiceMap()
	{
		return services;
	}
	
	/**
	 *  Check if service is ok with respect to search scope of caller.
	 */
	protected boolean checkSearchScope(IComponentIdentifier cid, IService ser, String scope)
	{
		if(!isIncluded(cid, ser))
		{
			return false;
		}
		else
		{
			return super.checkSearchScope(cid, ser, scope);
		}
	}

	
}
