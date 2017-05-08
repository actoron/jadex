package jadex.bridge.service.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jadex.base.PlatformConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.IAsyncFilter;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;

/**
 *  Local service registry. 
 *  
 *  - Search fetches services by types and excludes some according to the scope. 
 *  - Allows for adding persistent queries.
 */
public class ServiceRegistry implements IServiceRegistry // extends AbstractServiceRegistry
{
	//-------- attributes --------
	
	/** Read-Write Lock */
	protected ReadWriteLock rwlock;
	
	/** The service indexer. */
	protected ServiceIndexer<IService> indexer;
	
	/** The excluded services cache. */
	protected Map<IComponentIdentifier, Set<IService>> excludedservices;
	
	/** The persistent service queries. */
	protected QueryInfoContainer queries;
	
	//-------- methods --------
	
	/**
	 *  Create a new registry.
	 */
	public ServiceRegistry()
	{
		rwlock = new ReentrantReadWriteLock(true);
		queries = new QueryInfoContainer();
		this.indexer = new ServiceIndexer<IService>(new JadexServiceKeyExtractor(), JadexServiceKeyExtractor.SERVICE_KEY_TYPES);
	}
	
	/**
	 *  Add a service to the registry.
	 *  @param service The service.
	 */
	// write
	public IFuture<Void> addService(IService service)
	{
		Lock lock = rwlock.writeLock();
		lock.lock();
		try
		{
			indexer.addService(service);
			
			// If services belongs to excluded component cache them
			IComponentIdentifier cid = service.getServiceIdentifier().getProviderId();
			if(excludedservices!=null && excludedservices.containsKey(cid))
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
			
			// Downgrade to read lock.
			lock = rwlock.readLock();
			lock.lock();
			rwlock.writeLock().unlock();
			
			checkQueries(service, false);
		}
		finally
		{
			lock.unlock();
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	// write
	public void removeService(IService service)
	{
		Lock lock = rwlock.writeLock();
		lock.lock();
		try
		{
			indexer.removeService(service);
			
			// Downgrade to read lock.
			lock = rwlock.readLock();
			lock.lock();
			rwlock.writeLock().unlock();
			
			checkQueries(service, true);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 *  Remove services of a platform from the registry.
	 *  @param platform The platform.
	 */
	// write
	public void removeServices(IComponentIdentifier platform)
	{
		Lock lock = rwlock.writeLock();
		lock.lock();
		try
		{
			Set<IService> pservs = indexer.getServices(JadexServiceKeyExtractor.KEY_TYPE_PLATFORM, platform.toString());
			if (pservs != null)
			{
				for (IService serv : pservs)
					indexer.removeService(serv);
			}
			
			// Downgrade to read lock.
			lock = rwlock.readLock();
			lock.lock();
			rwlock.writeLock().unlock();
			
			for (IService serv : pservs)
				checkQueries(serv, true);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 *  Search for services.
	 */
	// read
	@SuppressWarnings("unchecked")
	public <T> T searchServiceSync(final ServiceQuery<T> query)
	{
		T ret = null;
		if (!RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			if (query.getFilter() instanceof IAsyncFilter)
				throw new IllegalArgumentException("Synchronous search call with asynchronous filter in query: " + query);
			
			Set<IService> sers = getServices(query);
			IFilter<T> filter = (IFilter<T>) query.getFilter();
			filter = (IFilter<T>) (filter == null? IFilter.ALWAYS : filter);
			
			if (sers!=null && !sers.isEmpty())
			{
				for (IService ser : sers)
				{
					if(checkSearchScope(query.getOwner(), ser, query.getScope(), false) &&
					   checkPublicationScope(query.getProvider(), ser) &&
					   filter.filter((T) ser))
					{
						ret = (T)ser;
						break;
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	// read
	@SuppressWarnings("unchecked")
	public <T> Collection<T> searchServicesSync(final ServiceQuery<T> query)
	{
		Collection<T> ret = null;
		if (!RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			if (query.getFilter() instanceof IAsyncFilter)
				throw new IllegalArgumentException("Synchronous search call with asynchronous filter in query: " + query);
			
			Set<IService> sers = getServices(query);
			IFilter<T> filter = (IFilter<T>) query.getFilter();
			filter = (IFilter<T>) (filter == null? IFilter.ALWAYS : filter);
			
			if (sers!=null && !sers.isEmpty())
			{
				for (Iterator<IService> it = sers.iterator(); it.hasNext(); )
				{
					IService ser = it.next();
					if(!(checkSearchScope(query.getOwner(), ser, query.getScope(), false) && checkPublicationScope(query.getProvider(), ser) && filter.filter((T) ser)))
						it.remove();
				}
			}
			ret = (Collection<T>) sers;
		}
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	// read
	public <T> IFuture<T> searchServiceAsync(final ServiceQuery<T> query)
	{
		final Future<T> ret = new Future<T>();
		
		if (RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
		}
		else
		{
			Set<IService> sers = getServices(query);
			if (sers != null)
			{
				final Iterator<IService> it = sers.iterator();
				
				(new ICommand<Iterator<IService>>()
				{
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public void execute(final Iterator<IService> it)
					{
						IService ser = it.next();
						
						T tmp = null;
						if (query.getReturnType() != null && query.getReturnType().getTypeName().equals(ServiceEvent.CLASSINFO.getTypeName()))
						{
							tmp = (T) new ServiceEvent(ser, ServiceEvent.SERVICE_ADDED);
						}
						else
							tmp = (T) ser;
						final T obj = tmp;
						
						final ICommand<Iterator<IService>> cmd = this;
						
						boolean passes = checkSearchScope(query.getOwner(), ser, query.getScope(), false);
						passes &= checkPublicationScope(query.getProvider(), ser);
						if (query.getFilter() instanceof IFilter)
						{
							passes &= ((IFilter<T>) query.getFilter()).filter(obj);
						}
						
						if (passes)
						{
							if (query.getFilter() instanceof IAsyncFilter)
							{
								((IAsyncFilter<T>) query.getFilter()).filter(obj).addResultListener(new IResultListener<Boolean>()
								{
									public void resultAvailable(Boolean result)
									{
										if (Boolean.TRUE.equals(result))
											ret.setResult(obj);
										else
											exceptionOccurred(null);
									}
									
									public void exceptionOccurred(Exception exception)
									{
										if (it.hasNext())
											cmd.execute(it);
										else
											ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
									}
								});
							}
							else
								ret.setResult(obj);
						}
						else
						{
							if (it.hasNext())
								cmd.execute(it);
							else
								ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
						}
					};
				}).execute(it);
			}
			else
			{
//				System.out.println("FAILED: " + (query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()) + " " + query.getOwner());
//				(new RuntimeException()).printStackTrace();
				ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
			}
		}
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	// read
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> ISubscriptionIntermediateFuture<T> searchServicesAsync(final ServiceQuery<T> query)
	{	
		SubscriptionIntermediateFuture<T> ret = null;
		
		if (RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			ret = new SubscriptionIntermediateFuture<T>();
			ret.setFinished();
		}
		else
		{
			IAsyncFilter<T> filter = new QueryFilter(query);
			Set sers = getServices(query);
			if (sers != null)
				ret = (SubscriptionIntermediateFuture<T>) checkAsyncFilters(filter, sers.iterator());
			else
			{
				ret = new SubscriptionIntermediateFuture<T>();
				ret.setFinished();
			}
		}
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	// read
	@Deprecated
	@SuppressWarnings("unchecked")
	public <T> T searchService(ServiceQuery<T> query, boolean excluded)
	{
		if (RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
			return null;
		
		T ret = null;
		Set<IService> sers = getServices(query);
		if(sers!=null)
		{
			Iterator<IService> it = sers.iterator();
			while(it.hasNext())
			{
				IService ser = it.next();
				if(checkSearchScope(query.getOwner(), ser, query.getScope(), excluded) && checkPublicationScope(query.getProvider(), ser))
				{
					ret = (T)ser;
					break;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	// write
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		
		rwlock.writeLock().lock();
		Set<T> sers = null;
		try
		{
			queries.addQueryInfo(new ServiceQueryInfo<T>(query, ret));
			
			// We need the write lock during read for consistency
			// This works because rwlock is reentrant.
			// deliver currently available services
			sers = (Set<T>)getServices(query);
		}
		finally
		{
			rwlock.writeLock().unlock();
		}
		
		if(sers!=null)
		{
			IAsyncFilter<T> filter = new IAsyncFilter<T>()
			{
				public IFuture<Boolean> filter(T ser)
				{
					Future<Boolean> ret = null;
					if (!checkScope(ser, query.getOwner(), query.getScope()))
					{
						ret = new Future<Boolean>(Boolean.FALSE);
					}
					else if (query.getFilter() instanceof IAsyncFilter)
					{
						ret = (Future<Boolean>) ((IAsyncFilter) query.getFilter()).filter(ser);
					}
					else if (query.getFilter() instanceof IFilter)
					{
						ret = new Future<Boolean>(((IFilter) query.getFilter()).filter(ser));
					}
					else
					{
						ret = new Future<Boolean>(Boolean.TRUE);
					}
					
					return ret;
				}
			};
			Iterator it = sers.iterator();
			checkAsyncFilters(filter, it).addIntermediateResultListener(new UnlimitedIntermediateDelegationResultListener<T>(ret));;
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
		rwlock.writeLock().lock();
		ServiceQueryInfo<?> qinfo = null;
		try
		{
			qinfo = queries.removeQuery(query);
		}
		finally
		{
			rwlock.writeLock().unlock();
		}
		if (qinfo != null)
			qinfo.getFuture().setFinished();
	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	// write
	public void removeQueries(IComponentIdentifier owner)
	{
		rwlock.writeLock().lock();
		Set<ServiceQueryInfo<?>> qinfos = null;
		try
		{
			qinfos = queries.removeQueries(owner);
		}
		finally
		{
			rwlock.writeLock().unlock();
		}
		if (qinfos != null)
		{
			for (ServiceQueryInfo<?> qinfo : qinfos)
				qinfo.getFuture().setFinished();
		}
	}
	
	/**
	 *  Add an excluded component. 
	 *  @param The component identifier.
	 */
	// write
	public void addExcludedComponent(IComponentIdentifier cid)
	{
		rwlock.writeLock().lock();
		try
		{
			if (excludedservices == null)
				excludedservices = new HashMap<IComponentIdentifier, Set<IService>>();
			excludedservices.put(cid, null);
		}
		finally
		{
			rwlock.writeLock().unlock();
		}
	}
	
	/**
	 *  Remove an excluded component. 
	 *  @param The component identifier.
	 */
	public IFuture<Void> removeExcludedComponent(IComponentIdentifier cid)
	{
//		System.out.println("cache size: "+excludedservices==null? "0":excludedservices.size());
		
		Future<Void> ret = new Future<Void>();
		IResultListener<Void> lis = null;
		
		rwlock.readLock().lock();
		try
		{
			if(excludedservices!=null)
			{
				Set<IService> exs = excludedservices.remove(cid);
				
				// Get and remove services from cache
				if(exs!=null)
				{
					lis = new CounterResultListener<Void>(exs.size(), new DelegationResultListener<Void>(ret));
					for(IService ser: exs)
					{
						checkQueries(ser, false).addResultListener(lis);
					}
				}
			}
		}
		finally
		{
			rwlock.readLock().unlock();
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
	// read
	public boolean isIncluded(IComponentIdentifier cid, IService ser)
	{
		boolean ret = true;
		rwlock.readLock().lock();
		try
		{
			if(excludedservices!=null && ser == null && cid != null && excludedservices.containsKey(cid))
			{
				ret = false;
			}
			else if(excludedservices!=null && excludedservices.containsKey(ser.getServiceIdentifier().getProviderId()) && cid!=null)
			{
				IComponentIdentifier target = ser.getServiceIdentifier().getProviderId();
				if(target!=null)
					ret = getDotName(cid).endsWith(getDotName(target));
			}
		}
		finally
		{
			rwlock.readLock().unlock();
		}
		return ret;
	}
	
	/**
	 *  Check the persistent queries for a new service.
	 *  @param ser The service.
	 */
	// read
	protected IFuture<Void> checkQueries(IService ser, boolean removed)
	{
		Future<Void> ret = new Future<Void>();
		
//		if(queries!=null)
//		{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Set<ServiceQueryInfo<?>> sqis = null;
			if (removed)
				sqis = queries.getEventQueries(ser.getServiceIdentifier().getServiceType());
			else
				sqis = queries.getQueries(ser.getServiceIdentifier().getServiceType());
			
			if(sqis!=null)
			{
				// Clone the data to not need to synchronize async
				Set<ServiceQueryInfo<?>> clone = new LinkedHashSet<ServiceQueryInfo<?>>(sqis);
				
				checkQueriesLoop(clone.iterator(), ser, removed).addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
				ret.setResult(null);
//		}
//		else
//		{
//			ret.setResult(null);
//		}
		
		return ret;
	}
	
	/**
	 *  Check the persistent queries against a new service.
	 *  @param it The queries.
	 *  @param service the service.
	 */
	// read
	protected IFuture<Void> checkQueriesLoop(final Iterator<ServiceQueryInfo<?>> it, final IService service, final boolean removed)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(it.hasNext())
		{
			final ServiceQueryInfo<?> sqi = it.next();
//			IComponentIdentifier cid = sqi.getQuery().getOwner();
//			String scope = sqi.getQuery().getScope();
//			IAsyncFilter<IService> filter = (IAsyncFilter)sqi.getQuery().getFilter();
			
			checkQuery(sqi, service, removed).addResultListener(new ExceptionDelegationResultListener<Boolean, Void>(ret)
			{
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public void customResultAvailable(Boolean result) throws Exception
				{
					if(result.booleanValue())
					{
						Object ires = null;
						if (ServiceEvent.CLASSINFO.equals(sqi.getQuery().getReturnType()))
							ires = new ServiceEvent(service, removed ? ServiceEvent.SERVICE_REMOVED : ServiceEvent.SERVICE_ADDED);
						else
							ires = service;
						((IntermediateFuture)sqi.getFuture()).addIntermediateResult(ires);
					}
					checkQueriesLoop(it, service, removed).addResultListener(new DelegationResultListener<Void>(ret));
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
	 *  Check the services according the the scope.
	 *  @param it The services.
	 *  @param cid The component id.
	 *  @param scope The scope.
	 *  @return The services that fit to the scope.
	 */
	protected <T> boolean checkScope(final T ser, final IComponentIdentifier cid, final String scope)
	{
		return checkSearchScope(cid, (IService)ser, scope, false) && checkPublicationScope(cid, (IService)ser);
	}
	
	/**
	 *  Check the async filter.
	 *  @param filter The filter
	 *  @param it The services.
	 *  @return The services that pass the filter.
	 */
	// read -> Async is error prone when lock is held longer time spans
	protected  <T> ISubscriptionIntermediateFuture<T> checkAsyncFilters(final IAsyncFilter<T> filter, final Iterator<T> it)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		if(it.hasNext())
		{
			final T ser = it.next();
			if(filter==null)
			{
				ret.addIntermediateResult(ser);
				checkAsyncFilters(filter, it).addResultListener(new IntermediateDelegationResultListener<T>(ret));
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
						checkAsyncFilters(filter, it).addResultListener(new IntermediateDelegationResultListener<T>(ret));
					}
					
					public void exceptionOccurred(Exception exception)
					{
						checkAsyncFilters(filter, it).addResultListener(new IntermediateDelegationResultListener<T>(ret));
					}
				});
			}
		}
		else
		{
//			System.out.println("searchLoopEnd");
			ret.setFinished();
		}
		
		return ret;
	}
	
	/**
	 *  Check a persistent query with one service.
	 *  @param queryinfo The query.
	 *  @param service The service.
	 *  @return True, if services matches to query.
	 */
	// read
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected IFuture<Boolean> checkQuery(final ServiceQueryInfo<?> queryinfo, final IService service, final boolean removed)
	{
		
		
		final Future<Boolean> ret = new Future<Boolean>();
//		IComponentIdentifier cid = queryinfo.getQuery().getOwner();
//		String scope = queryinfo.getQuery().getScope();
//		@SuppressWarnings("unchecked")
		
		if (removed && !ServiceEvent.CLASSINFO.equals(queryinfo.getQuery().getReturnType()))
			ret.setResult(Boolean.FALSE);
		else
		{
			IAsyncFilter filter = new QueryFilter(queryinfo.getQuery());
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
		
		return ret;
	}
	
	/**
	 *  Check if service is ok with respect to search scope of caller.
	 */
	protected boolean checkSearchScope(IComponentIdentifier cid, IService ser, String scope, boolean excluded)
	{
		boolean ret = false;
		
		if(!excluded && !isIncluded(cid, ser))
		{
			return ret;
		}
		
		if(scope==null)
		{
			scope = RequiredServiceInfo.SCOPE_APPLICATION;
		}
		
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
		
		return ret;
	}
	
	/**
	 *  Get all services.
	 *  @return The services.
	 */
	protected Set<IService> getServices()
	{
		rwlock.readLock().lock();
		try
		{
			return indexer.getAllServices();
		}
		finally
		{
			rwlock.readLock().unlock();
		}
	}
	
	/**
	 *  Get services per query.
	 *  @param query The query.
	 *  @return First matching service or null.
	 */
	protected Set<IService> getServices(final ServiceQuery<?> query)
	{
		rwlock.readLock().lock();
		try
		{
			Set<IService> ret = indexer.getServices(query.getIndexerSearchSpec());
			return ret;
		}
		finally
		{
			rwlock.readLock().unlock();
		}
	}
	
	/**
	 *  Get the registry from a component.
	 */
	public static IServiceRegistry getRegistry(IComponentIdentifier platform)
	{
		return (IServiceRegistry)PlatformConfiguration.getPlatformValue(platform, PlatformConfiguration.DATA_SERVICEREGISTRY);
	}
	
	/**
	 *  Get the registry from a component.
	 */
	public static IServiceRegistry getRegistry(IInternalAccess ia)
	{
		return getRegistry(ia.getComponentIdentifier());
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
	 *  Get the name without @ replaced by dot.
	 */
	public static String getDotName(IComponentIdentifier cid)
	{
		return cid.getName().replace('@', '.');
//		return cid.getParent()==null? cid.getName(): cid.getLocalName()+"."+getSubcomponentName(cid);
	}
	
	/**
	 *  Listener that forwards only results and ignores finished / exception.
	 */
	public static class UnlimitedIntermediateDelegationResultListener<E> implements IIntermediateResultListener<E>
	{
		/** The delegate future. */
		protected IntermediateFuture<E> delegate;
		
		public UnlimitedIntermediateDelegationResultListener(IntermediateFuture<E> delegate)
		{
			this.delegate = delegate;
		}
		
		public void intermediateResultAvailable(E result)
		{
			delegate.addIntermediateResultIfUndone(result);
		}

		public void finished()
		{
			// the query is not finished after the status quo is delivered
		}

		public void resultAvailable(Collection<E> results)
		{
			for(E result: results)
			{
				intermediateResultAvailable(result);
			}
			// the query is not finished after the status quo is delivered
		}
		
		public void exceptionOccurred(Exception exception)
		{
			// the query is not finished after the status quo is delivered
		}
	}
	
	/**
	 *  Async filter for checking queries in one go.
	 */
	protected class QueryFilter<T> implements IAsyncFilter<T>
	{
		/** The query. */
		protected ServiceQuery<T> query;
		
		/**
		 *  Create filter. 
		 *  @param query The query.
		 */
		public QueryFilter(ServiceQuery<T> query)
		{
			this.query = query;
		}
		
		/**
		 *  Filter.
		 */
		@SuppressWarnings("unchecked")
		public IFuture<Boolean> filter(T obj)
		{
			Future<Boolean> fret = new Future<Boolean>();
			
			IService ser = (IService) obj;
			if (!(checkSearchScope(query.getOwner(), ser, query.getScope(), false) && checkPublicationScope(query.getProvider(), ser)))
			{
				fret.setResult(Boolean.FALSE);
			}
			else if (query.getFilter() instanceof IAsyncFilter)
			{
				((IAsyncFilter<T>) query.getFilter()).filter(obj).addResultListener(new DelegationResultListener<Boolean>(fret));
			}
			else if (query.getFilter() instanceof IFilter)
			{
				fret.setResult(((IFilter<T>) query.getFilter()).filter(obj));
			}
			else
			{
				fret.setResult(Boolean.TRUE);
			}
			
			return fret;
		}
		
	}
}
