package jadex.bridge.service.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.PlatformConfiguration;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.SFuture;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.commons.IAsyncFilter;
import jadex.commons.IFilter;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.commons.future.TerminationCommand;

/**
 *  Local service registry. Is used by component service containers
 *  to add/remove service registrations.
 *  
 *  Search fetches services by types and excludes some according to
 *  the scope.
 */
public class PlatformServiceRegistry
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
	 *  Add an excluded component. 
	 *  @param The component identifier.
	 */
	public synchronized void addExcludedComponent(IComponentIdentifier cid)
	{
		if(excluded==null)
		{
			excluded = new HashSet<IComponentIdentifier>();
		}
		excluded.add(cid);
	}
	
	/**
	 *  Remove an excluded component. 
	 *  @param The component identifier.
	 */
	public synchronized IFuture<Void> removeExcludedComponent(IComponentIdentifier cid)
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
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	public synchronized boolean isIncluded(IComponentIdentifier cid, IService ser)
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
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	public synchronized IFuture<Void> addService(ClassInfo key, IService service)
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
	public synchronized void removeService(ClassInfo key, IService service)
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
	public synchronized <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
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
	public synchronized <T> void removeQuery(ServiceQuery<T> query)
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
	 *  Check the persistent queries for a new service.
	 *  @param ser The service.
	 */
	protected synchronized IFuture<Void> checkQueries(IService ser)
	{
		Future<Void> ret = new Future<Void>();
		
		if(queries!=null)
		{
			Set<ServiceQueryInfo<?>> sqis = queries.get(ser.getServiceIdentifier().getServiceType());
			if(sqis!=null)
			{
				checkQueriesLoop(sqis.iterator(), ser).addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
			{
				ret.setResult(null);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Check the persistent queries against a new service.
	 *  @param it The queries.
	 *  @param service the service.
	 */
	protected synchronized IFuture<Void> checkQueriesLoop(final Iterator<ServiceQueryInfo<?>> it, final IService service)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(it.hasNext())
		{
			final ServiceQueryInfo<?> sqi = it.next();
			IComponentIdentifier cid = sqi.getQuery().getOwner();
			String scope = sqi.getQuery().getScope();
			IAsyncFilter<IService> filter = (IAsyncFilter)sqi.getQuery().getFilter();
			
			checkQuery(sqi, service).addResultListener(new ExceptionDelegationResultListener<Boolean, Void>(ret)
			{
				public void customResultAvailable(Boolean result) throws Exception
				{
					if(result.booleanValue())
						((IntermediateFuture)sqi.getFuture()).addIntermediateResult(service);
					checkQueriesLoop(it, service).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Check a persistent query with one service.
	 *  @param queryinfo The query.
	 *  @param service The service.
	 *  @return True, if services matches to query.
	 */
	protected synchronized IFuture<Boolean> checkQuery(final ServiceQueryInfo<?> queryinfo, final IService service)
	{
		final Future<Boolean> ret = new Future<Boolean>();
		
		IComponentIdentifier cid = queryinfo.getQuery().getOwner();
		String scope = queryinfo.getQuery().getScope();
		IAsyncFilter<IService> filter = (IAsyncFilter)queryinfo.getQuery().getFilter();
		if(!checkSearchScope(cid, service, scope) || !checkPublicationScope(cid, service))
		{
			ret.setResult(Boolean.FALSE);
		}
		else
		{
			if(filter==null)
			{
				ret.setResult(Boolean.TRUE);
			}
			else
			{
				filter.filter(service).addResultListener(new IResultListener<Boolean>()
				{
					public void resultAvailable(Boolean result)
					{
						ret.setResult(result!=null && result.booleanValue()? Boolean.TRUE: Boolean.FALSE);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setResult(Boolean.FALSE);
					}
				});
			}
		}
		
		return ret;
	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	public synchronized void removeQueries(IComponentIdentifier owner)
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
	 *  Search for services.
	 */
	public synchronized <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope)
	{
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			throw new IllegalArgumentException("For global searches async method searchGlobalService has to be used.");
		
		T ret = null;
		Set<IService> sers = getServices(type);
		if(sers!=null && sers.size()>0 && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
		{
			for(IService ser: sers)
			{
				if(checkSearchScope(cid, ser, scope) && checkPublicationScope(cid, ser))
				{
					ret = (T)ser;
					break;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope)
	{
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			throw new IllegalArgumentException("For global searches async method searchGlobalServices has to be used.");
		
		Set<T> ret = null;
		Set<IService> sers = getServices(type);
		if(sers!=null && sers.size()>0 && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
		{
			ret = new HashSet<T>();
			for(IService ser: sers)
			{
				if(checkSearchScope(cid, ser, scope) && checkPublicationScope(cid, ser))
				{
					ret.add((T)ser);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Search for service.
	 */
	public synchronized <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			throw new IllegalArgumentException("For global searches async method searchGlobalService has to be used.");
		
		T ret = null;
		
		Set<T> sers = (Set<T>)getServices(type);
		if(sers!=null && sers.size()>0 && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
		{
			for(T ser: sers)
			{
				if(checkSearchScope(cid, (IService)ser, scope) && checkPublicationScope(cid, (IService)ser))
				{
					try
					{
						if(filter==null || filter.filter(ser))
						{
							ret = ser;
							break;
						}
					}
					catch(Exception e)
					{
						System.out.println("Warning: filter threw exception during search: "+filter);
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Search for service.
	 */
	public synchronized <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			throw new IllegalArgumentException("For global searches async method searchGlobalService has to be used.");
		
		List<T> ret = new ArrayList<T>();
		
		Set<T> sers = (Set<T>)getServices(type);
		if(sers!=null && sers.size()>0 && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
		{
			for(T ser: sers)
			{
				if(checkSearchScope(cid, (IService)ser, scope) && checkPublicationScope(cid, (IService)ser))
				{
					try
					{
						if(filter==null || filter.filter(ser))
						{
							ret.add(ser);
						}
					}
					catch(Exception e)
					{
						System.out.println("Warning: filter threw exception during search: "+filter);
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Search for service.
	 */
	public synchronized <T> IFuture<T> searchService(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		final Future<T> ret = new Future<T>();
		
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
		{
			ret.setException(new IllegalArgumentException("For global searches searchGlobalService has to be used."));
		}
		else
		{
			Set<T> sers = (Set<T>)getServices(type);
			if(sers!=null && sers.size()>0 && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
			{
				// filter checks in loop are possibly performed outside of synchronized block
				Iterator<T> it = new HashSet<T>(sers).iterator();
				searchLoopService(filter, it, cid, scope).addResultListener(new DelegationResultListener<T>(ret));
			}
			else
			{
				ret.setException(new ServiceNotFoundException(type.getName()));
			}
		}
		return ret;
	}
	
	/**
	 * 
	 * @param filter
	 * @param it
	 * @return
	 */
	protected <T> IFuture<T> searchLoopService(final IAsyncFilter<T> filter, final Iterator<T> it, final IComponentIdentifier cid, final String scope)
	{
		final Future<T> ret = new Future<T>();
		
		if(it.hasNext())
		{
			final T ser = it.next();
			if(!checkSearchScope(cid, (IService)ser, scope) || !checkPublicationScope(cid, (IService)ser))
			{
				searchLoopService(filter, it, cid, scope).addResultListener(new DelegationResultListener<T>(ret));
			}
			else
			{
				if(filter==null)
				{
					ret.setResult(ser);
				}
				else
				{
					filter.filter(ser).addResultListener(new IResultListener<Boolean>()
					{
						public void resultAvailable(Boolean result)
						{
							if(result!=null && result.booleanValue())
							{
								ret.setResult(ser);
							}
							else
							{
								searchLoopService(filter, it, cid, scope).addResultListener(new DelegationResultListener<T>(ret));
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							searchLoopService(filter, it, cid, scope).addResultListener(new DelegationResultListener<T>(ret));
						}
					});
				}
			}
		}
		else
		{
			ret.setException(new ServiceNotFoundException("No service that fits filter: "+filter));
		}
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> ISubscriptionIntermediateFuture<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
		{
			ret.setException(new IllegalArgumentException("For global searches searchGlobalServices has to be used."));
		}
		else
		{
			if(services!=null)
			{
				Set<T> sers = (Set<T>) getServices(type);
				if(sers!=null && sers.size()>0 && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
				{
					// filter checks in loop are possibly performed outside of synchornized block
					Iterator<T> it = new HashSet<T>(sers).iterator();
					searchLoopServices(filter, it, cid, scope).addResultListener(new IntermediateDelegationResultListener<T>(ret));
				}
				else
				{
					ret.setFinished();
				}
			}
			else
			{
				ret.setFinished();
			}
		}
		return ret;
	}
	
	/**
	 * 
	 * @param filter
	 * @param it
	 * @return
	 */
	protected <T> ISubscriptionIntermediateFuture<T> searchLoopServices(final IAsyncFilter<T> filter, final Iterator<T> it, final IComponentIdentifier cid, final String scope)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		if(it.hasNext())
		{
			final T ser = it.next();
			if(!checkSearchScope(cid, (IService)ser, scope) || !checkPublicationScope(cid, (IService)ser))
			{
				searchLoopServices(filter, it, cid, scope).addResultListener(new IntermediateDelegationResultListener<T>(ret));
			}
			else
			{
				if(filter==null)
				{
					ret.addIntermediateResult(ser);
					searchLoopServices(filter, it, cid, scope).addResultListener(new IntermediateDelegationResultListener<T>(ret));
				}
				else
				{
					filter.filter(ser).addResultListener(new IResultListener<Boolean>()
					{
						public void resultAvailable(Boolean result)
						{
							if(result!=null && result.booleanValue())
							{
								ret.addIntermediateResult(ser);
							}
							searchLoopServices(filter, it, cid, scope).addResultListener(new IntermediateDelegationResultListener<T>(ret));
						}
						
						public void exceptionOccurred(Exception exception)
						{
							searchLoopServices(filter, it, cid, scope).addResultListener(new IntermediateDelegationResultListener<T>(ret));
						}
					});
				}
			}
		}
		else
		{
			ret.setFinished();
		}
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> IFuture<T> searchGlobalService(final Class<T> type, IComponentIdentifier cid, final IAsyncFilter<T> filter)
	{
		final Future<T> ret = new Future<T>();
		final IComponentIdentifier	lcid	= IComponentIdentifier.LOCAL.get();
		
		searchService(type, cid, RequiredServiceInfo.SCOPE_PLATFORM, filter).addResultListener(new IResultListener<T>()
		{
			public void resultAvailable(T result)
			{
				ret.setResult(result);
			}

			public void exceptionOccurred(Exception exception)
			{
				searchRemoteService(lcid, type, filter).addResultListener(new DelegationResultListener<T>(ret));						
			}
		});
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public synchronized <T> ITerminableIntermediateFuture<T> searchGlobalServices(Class<T> type, IComponentIdentifier cid, IAsyncFilter<T> filter)
	{
//		System.out.println("Search global services: "+type);
		final TerminableIntermediateFuture<T> ret = new TerminableIntermediateFuture<T>();
		
		final CounterResultListener<Void> lis = new CounterResultListener<Void>(2, true, new ExceptionDelegationResultListener<Void, Collection<T>>(ret)
		{
			public void customResultAvailable(Void result)
			{
				ret.setFinished();
			}
		});
		
		searchServices(type, cid, RequiredServiceInfo.SCOPE_PLATFORM, filter).addResultListener(new IntermediateDefaultResultListener<T>()
		{
			public void intermediateResultAvailable(T result)
			{
				ret.addIntermediateResult(result);
			}
			
			public void finished()
			{
				lis.resultAvailable(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				lis.resultAvailable(null);
			}
		});
		
		searchRemoteServices(IComponentIdentifier.LOCAL.get(), type, filter).addResultListener(new IntermediateDefaultResultListener<T>()
		{
			public void intermediateResultAvailable(T result)
			{
				ret.addIntermediateResult(result);
			}
			
			public void finished()
			{
				lis.resultAvailable(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				lis.resultAvailable(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Check if service is ok with respect to search scope of caller.
	 */
	protected boolean checkSearchScope(IComponentIdentifier cid, IService ser, String scope)
	{
		boolean ret = false;
		
		if(!isIncluded(cid, ser))
		{
			return ret;
		}
		
		if(scope==null)
		{
			scope = RequiredServiceInfo.SCOPE_APPLICATION;
		}
		
		if(RequiredServiceInfo.SCOPE_PLATFORM.equals(scope) || RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
		{
			ret = true;
		}
		else if(RequiredServiceInfo.SCOPE_APPLICATION.equals(scope))
		{
			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
			ret = sercid.getPlatformName().equals(cid.getPlatformName())
				&& getApplicationName(sercid).equals(getApplicationName(cid));
		}
		else if(RequiredServiceInfo.SCOPE_COMPONENT.equals(scope))
		{
			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
			ret = getDotName(sercid).endsWith(getDotName(cid));
		}
		else if(RequiredServiceInfo.SCOPE_LOCAL.equals(scope))
		{
			// only the component itself
			ret = ser.getServiceIdentifier().getProviderId().equals(cid);
		}
		else if(RequiredServiceInfo.SCOPE_PARENT.equals(scope))
		{
			// check if parent of searcher reaches the service
			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
			String subname = getSubcomponentName(cid);
			ret = sercid.getName().endsWith(subname);
		}
//		else if(RequiredServiceInfo.SCOPE_UPWARDS.equals(scope))
//		{
//			// Test if service id is part of searcher id, service is upwards from searcher
//			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
//			ret = getDotName(cid).endsWith(getDotName(sercid));
//			
////			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
////			String subname = getSubcomponentName(cid);
////			ret = sercid.getName().endsWith(subname);
////			
////			while(cid!=null)
////			{
////				if(sercid.equals(cid))
////				{
////					ret = true;
////					break;
////				}
////				else
////				{
////					cid = cid.getParent();
////				}
////			}
//		}
		
		return ret;
	}
	
	/**
	 *  Check if service is ok with respect to publication scope.
	 */
	protected boolean checkPublicationScope(IComponentIdentifier cid, IService ser)
	{
		boolean ret = false;
		
		String scope = ser.getServiceIdentifier().getScope()!=null? ser.getServiceIdentifier().getScope(): RequiredServiceInfo.SCOPE_GLOBAL;
		
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
		{
			ret = true;
		}
		else if(RequiredServiceInfo.SCOPE_PLATFORM.equals(scope))
		{
			// Test if searcher and service are on same platform
			ret = cid.getPlatformName().equals(ser.getServiceIdentifier().getProviderId().getPlatformName());
		}
		else if(RequiredServiceInfo.SCOPE_APPLICATION.equals(scope))
		{
			// todo: special case platform service with app scope
			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
			ret = sercid.getPlatformName().equals(cid.getPlatformName())
				&& getApplicationName(sercid).equals(getApplicationName(cid));
		}
		else if(RequiredServiceInfo.SCOPE_COMPONENT.equals(scope))
		{
			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
			ret = getDotName(cid).endsWith(getDotName(sercid));
		}
		else if(RequiredServiceInfo.SCOPE_LOCAL.equals(scope))
		{
			// only the component itself
			ret = ser.getServiceIdentifier().getProviderId().equals(cid);
		}
		else if(RequiredServiceInfo.SCOPE_PARENT.equals(scope))
		{
			// check if parent of service reaches the searcher
			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
			String subname = getSubcomponentName(sercid);
			ret = getDotName(cid).endsWith(subname);
		}
//		else if(RequiredServiceInfo.SCOPE_UPWARDS.equals(scope))
//		{
//			// check if searcher is upwards from service (part of name)
//			IComponentIdentifier sercid = ser.getServiceIdentifier().getProviderId();
//			ret = getDotName(sercid).endsWith(getDotName(cid));
//		}
		
		return ret;
	}
	
	/**
	 *  Search for services on remote platforms.
	 *  @param caller	The component that started the search.
	 *  @param type The type.
	 *  @param filter The filter.
	 */
	protected <T> ITerminableIntermediateFuture<T> searchRemoteServices(final IComponentIdentifier caller, final Class<T> type, final IAsyncFilter<T> filter)
	{
		final TerminableIntermediateFuture<T> ret = new TerminableIntermediateFuture<T>();
		// Must not find services twice (e.g. having two proxies for the same platform)
		final Set<T> founds = new HashSet<T>();
		
		if(services!=null)
		{
			final IRemoteServiceManagementService rms = getService(IRemoteServiceManagementService.class);
			if(rms!=null)
			{
				// Get all proxy agents (represent other platforms)
				
				Set<IService> sers = services.get(new ClassInfo(IProxyAgentService.class));
				if(sers!=null && sers.size()>0)
				{
					final CounterResultListener<Void> clis = new CounterResultListener<Void>(sers.size(), new ExceptionDelegationResultListener<Void, Collection<T>>(ret)
					{
						public void customResultAvailable(Void result)
						{
							ret.setFinished();
						}
					});
					
					for(IService ser: sers)
					{
						IProxyAgentService ps = (IProxyAgentService)ser;
						
						ps.getRemoteComponentIdentifier().addResultListener(new IResultListener<ITransportComponentIdentifier>()
						{
							public void resultAvailable(ITransportComponentIdentifier rcid)
							{
								// User RMS getServiceProxies() to fetch services
								
								IFuture<Collection<T>> rsers = rms.getServiceProxies(caller, rcid, type, RequiredServiceInfo.SCOPE_PLATFORM, filter);
								rsers.addResultListener(new IResultListener<Collection<T>>()
								{
									public void resultAvailable(Collection<T> result)
									{
										for(T t: result)
										{
											if(!founds.contains(t))
											{
												ret.addIntermediateResult(t);
											}
											founds.add(t);
										}
										clis.resultAvailable(null);
									}
									
									public void exceptionOccurred(Exception exception)
									{
										clis.resultAvailable(null);
									}
								});
							}
							
							public void exceptionOccurred(Exception exception)
							{
								clis.resultAvailable(null);
							}
						});
					}
				}
				else
				{
					ret.setFinished();					
				}
			}
			else
			{
				ret.setFinished();
			}
		}
		else
		{
			ret.setFinished();
		}
		
//		ret.addResultListener(new IntermediateDefaultResultListener<T>()
//		{
//			public void intermediateResultAvailable(T result)
//			{
//				System.out.println("found: "+result);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//			}
//		});
		
		return ret;
	}
	
	/**
	 *  Search for services on remote platforms.
	 *  @param type The type.
	 *  @param scope The scope.
	 */
	protected <T> IFuture<T> searchRemoteService(final IComponentIdentifier caller, final Class<T> type, final IAsyncFilter<T> filter)
	{
		final Future<T> ret = new Future<T>();
		
		if(services!=null)
		{
			final IRemoteServiceManagementService rms = getService(IRemoteServiceManagementService.class);
			if(rms!=null)
			{
				Set<IService> sers = getServices(IProxyAgentService.class);
				if(sers!=null && sers.size()>0)
				{
					final CounterResultListener<Void> clis = new CounterResultListener<Void>(sers.size(), new ExceptionDelegationResultListener<Void, T>(ret)
					{
						public void customResultAvailable(Void result)
						{
							ret.setExceptionIfUndone(new ServiceNotFoundException(type.getName()));
						}
					});
					
					for(IService ser: sers)
					{
						IProxyAgentService ps = (IProxyAgentService)ser;
						
						ps.getRemoteComponentIdentifier().addResultListener(new IResultListener<ITransportComponentIdentifier>()
						{
							public void resultAvailable(ITransportComponentIdentifier rcid)
							{
								IFuture<T> rsers = rms.getServiceProxy(caller, rcid, type, RequiredServiceInfo.SCOPE_PLATFORM, filter);
								rsers.addResultListener(new IResultListener<T>()
								{
									public void resultAvailable(T result)
									{
										ret.setResultIfUndone(result);
										clis.resultAvailable(null);
									}
									
									public void exceptionOccurred(Exception exception)
									{
										clis.resultAvailable(null);
									}
								});
							}
							
							public void exceptionOccurred(Exception exception)
							{
								clis.resultAvailable(null);
							}
						});
					}
				}
				else
				{
					ret.setExceptionIfUndone(new ServiceNotFoundException(type.getName()));
				}
			}
			else
			{
				ret.setExceptionIfUndone(new ServiceNotFoundException(type.getName()));
			}
		}
		else
		{
			ret.setExceptionIfUndone(new ServiceNotFoundException(type.getName()));
		}
		
		return ret;
	}
	
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
	protected Set<IService> getServices(Class<?> type)
	{
		return getServices(new ClassInfo(type));
	}
	
	/**
	 *  Get services per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return First matching service or null.
	 */
	public Set<IService> getServices(ClassInfo type)
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
		
		return ret;
	}
	
	/**
	 *  Get the service map.
	 */
	public Map<ClassInfo, Set<IService>> getServiceMap()
	{
		return services;
	}
	
	/**
	 *  Get the application name. Equals the local component name in case it is a child of the platform.
	 *  broadcast@awa.plat1 -> awa
	 *  @return The application name.
	 */
	public static String getApplicationName(IComponentIdentifier cid)
	{
		String ret = cid.getName();
		int idx;
		// If it is a direct subcomponent
		if((idx = ret.lastIndexOf('.')) != -1)
		{
			// cut off platform name
			ret = ret.substring(0, idx);
			// cut off local name 
			if((idx = ret.indexOf('@'))!=-1)
				ret = ret.substring(idx + 1);
			if((idx = ret.indexOf('.'))!=-1)
				ret = ret.substring(idx + 1);
		}
		else
		{
			ret = cid.getLocalName();
		}
		return ret;
	}
	
	/**
	 *  Get the subcomponent name.
	 *  @param cid The component id.
	 *  @return The subcomponent name.
	 */
	public static String getSubcomponentName(IComponentIdentifier cid)
	{
		String ret = cid.getName();
		int idx;
		if((idx = ret.indexOf('@'))!=-1)
			ret = ret.substring(idx + 1);
		return ret;
	}
	
	/**
	 *  
	 */
	public static String getDotName(IComponentIdentifier cid)
	{
		return cid.getName().replace('@', '.');
//		return cid.getParent()==null? cid.getName(): cid.getLocalName()+"."+getSubcomponentName(cid);
	}
	
	/**
	 *  Get the registry from a component.
	 */
	public static PlatformServiceRegistry getRegistry(IComponentIdentifier platform)
	{
		return (PlatformServiceRegistry)PlatformConfiguration.getPlatformValue(platform, PlatformConfiguration.DATA_SERVICEREGISTRY);
	}
	
	/**
	 *  Get the registry from a component.
	 */
	public static PlatformServiceRegistry getRegistry(IInternalAccess ia)
	{
		return getRegistry(ia.getComponentIdentifier());
	}
}
