package jadex.bridge.service.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import jadex.base.Starter;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IRemoteExecutionFeature;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.component.impl.IInternalRemoteExecutionFeature;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceIdentifier;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.pawareness.IPassiveAwarenessService;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.DuplicateRemovalIntermediateResultListener;
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
import jadex.commons.future.UnlimitedIntermediateDelegationResultListener;
import jadex.commons.transformation.traverser.TransformSet;

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
	protected Indexer<IServiceIdentifier> indexer;
	
	/** The persistent service queries. */
	protected Indexer<ServiceQueryInfo<IServiceIdentifier>> queries;
	
	/** The excluded services cache. */
	protected Map<IComponentIdentifier, Set<IServiceIdentifier>> excludedservices;
	
	//-------- methods --------
	
	/**
	 *  Create a new registry.
	 */
	public ServiceRegistry()
	{
		this.rwlock = new ReentrantReadWriteLock(false);
		this.indexer = new Indexer<IServiceIdentifier>(new ServiceKeyExtractor(), false, ServiceKeyExtractor.SERVICE_KEY_TYPES);
		this.queries = new Indexer<ServiceQueryInfo<IServiceIdentifier>>(new QueryInfoExtractor(), true, QueryInfoExtractor.QUERY_KEY_TYPES_INDEXABLE);
	}
	
	/**
	 *  Search for services.
	 */
	// read
//	@SuppressWarnings("unchecked")
	public <T> T searchServiceSync(final ServiceQuery<T> query)
	{
		T ret = null;
		if(!RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			Set<IServiceIdentifier> sers = getServices(query);
			IFilter<T> filter = (IFilter<T>)query.getFilter();
			filter = (IFilter<T>)(filter == null? IFilter.ALWAYS : filter);
			
			Set<IServiceIdentifier> ownerservices = null;
			rwlock.readLock().lock();
			try
			{
				ownerservices = query.isExcludeOwner()? indexer.getValues(ServiceKeyExtractor.KEY_TYPE_PROVIDER, query.getOwner().toString()) : null;
			}
			finally
			{
				rwlock.readLock().unlock();
			}
			
			if(sers!=null && !sers.isEmpty())
			{
				for(IServiceIdentifier ser : sers)
				{
					if(checkSearchScope(query.getOwner(), ser, query.getScope(), false) &&
					   checkPublicationScope(query.getOwner(), ser) &&
					   (ownerservices == null || !ownerservices.contains(ser)) &&
					   filter.filter((T)ser))
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
//	@SuppressWarnings("unchecked")
	public <T> Set<T> searchServicesSync(final ServiceQuery<T> query)
	{
		Set<T> ret = null;
		if(!RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			Set<IServiceIdentifier> sers = getServices(query);
			IFilter<T> filter = (IFilter<T>) query.getFilter();
			filter = (IFilter<T>)(filter==null? IFilter.ALWAYS : filter);
			
			Set<IServiceIdentifier> ownerservices = null;
			rwlock.readLock().lock();
			try
			{
				ownerservices = query.isExcludeOwner()? indexer.getValues(ServiceKeyExtractor.KEY_TYPE_PROVIDER, query.getOwner().toString()) : null;
			}
			finally
			{
				rwlock.readLock().unlock();
			}
			
			if(sers!=null && !sers.isEmpty())
			{
				for(Iterator<IServiceIdentifier> it = sers.iterator(); it.hasNext(); )
				{
					IServiceIdentifier ser = it.next();
					if(!(checkSearchScope(query.getOwner(), ser, query.getScope(), false) &&
					   checkPublicationScope(query.getOwner(), ser) &&
					   (ownerservices == null || !ownerservices.contains(ser)) &&
					   filter.filter((T)ser)))
					{
						it.remove();
					}
				}
			}
			ret = (Set<T>)sers;
		}
		
		return ret;
	}
	
	/**
	 *  Add a service to the registry.
	 *  @param service The service.
	 */
	// write
	public IFuture<Void> addService(IServiceIdentifier service)
	{
		IFuture<Void> ret = null;
		Lock lock = rwlock.writeLock();
		lock.lock();
		try
		{
			indexer.addValue(service);
			
			// If services belongs to excluded component cache them
			IComponentIdentifier cid = service.getProviderId();
			if(excludedservices!=null && excludedservices.containsKey(cid))
			{
				if(excludedservices==null)
					excludedservices = new HashMap<>();
				Set<IServiceIdentifier> exsers = excludedservices.get(cid);
				if(exsers==null)
				{
					exsers = new HashSet<>();
					excludedservices.put(cid, exsers);
				}
				exsers.add(service);
			}
			else
			{
				lock.unlock();
				lock = null;
				
				ret = checkQueries(service, ServiceEvent.SERVICE_ADDED);
			}
		}
		finally
		{
			if(lock != null)
				lock.unlock();
		}
		
		return ret == null ? IFuture.DONE : ret;
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	// write
	public void removeService(IServiceIdentifier service)
	{
		Lock lock = rwlock.writeLock();
		lock.lock();
		try
		{
			indexer.removeValue(service);
			
			lock.unlock();
			lock = null;
			
			checkQueries(service, ServiceEvent.SERVICE_REMOVED);
		}
		finally
		{
			if (lock != null)
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
			Set<IServiceIdentifier> pservs = indexer.getValues(ServiceKeyExtractor.KEY_TYPE_PLATFORM, platform.toString());
			if(pservs != null)
			{
				for(IServiceIdentifier serv : pservs)
				{
					indexer.removeValue(serv);
				}
			}
			
			// Downgrade to read lock.
			lock = rwlock.readLock();
			lock.lock();
			rwlock.writeLock().unlock();
			
			if(pservs!=null)
			{
				for(IServiceIdentifier serv : pservs)
					checkQueries(serv,  ServiceEvent.SERVICE_REMOVED);
			}
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
	public void removeServicesExcept(IComponentIdentifier platform)
	{
		Lock lock = rwlock.writeLock();
		lock.lock();
		try
		{
			Set<IServiceIdentifier> pservs = indexer.getAllValues();
			if(pservs != null)
			{
				for(IServiceIdentifier serv : pservs)
				{
					if(!serv.getProviderId().getRoot().equals(platform))
					{
						indexer.removeValue(serv);
					}
				}
			}
			
			// Downgrade to read lock.
			lock = rwlock.readLock();
			lock.lock();
			rwlock.writeLock().unlock();
			
			if(pservs != null)
			{
				for(IServiceIdentifier serv : pservs)
				{
					if(!serv.getProviderId().getRoot().equals(platform))
						checkQueries(serv, ServiceEvent.SERVICE_REMOVED);
				}
			}
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 *  Get all services.
	 *  @return All services (copy).
	 */
	public Set<IServiceIdentifier> getAllServices()
	{
		rwlock.readLock().lock();
		try
		{
			return indexer.getAllValues();
		}
		finally
		{
			rwlock.readLock().unlock();
		}
	}

	/**
	 *  Get all queries.
	 *  @return All queries (copy).
	 */
	public Set<ServiceQueryInfo<IServiceIdentifier>> getAllQueries()
	{
		rwlock.readLock().lock();
		try
		{
			return queries.getAllValues();
		}
		finally
		{
			rwlock.readLock().unlock();
		}
	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> fut = new SubscriptionIntermediateFuture<T>();
		ServiceQueryInfo<T> ret = null;
		
		fut.setTerminationCommand(new TerminationCommand()
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
			ret = new ServiceQueryInfo<T>(query, fut);
			queries.addValue((ServiceQueryInfo)ret);
			
			// We need the write lock during read for consistency
			// This works because rwlock is reentrant.
			// deliver currently available services
			sers = (Set<T>)getServices(query);
		}
		finally
		{
			rwlock.writeLock().unlock();
		}
		
		checkScope(ser, cid, scope)
		
		// Check initial services and notify the query
		if(sers!=null)
		{
			IAsyncFilter<T> filter = new IAsyncFilter<T>()
			{
				public IFuture<Boolean> filter(T ser)
				{
					Future<Boolean> ret = null;
					if(!checkScope(ser, query.getOwner(), query.getScope()))
					{
						ret = new Future<Boolean>(Boolean.FALSE);
					}
					else if(query.getFilter() instanceof IAsyncFilter)
					{
						ret = (Future<Boolean>)((IAsyncFilter<T>)query.getFilter()).filter(ser);
					}
					else if(query.getFilter() instanceof IFilter)
					{
						ret = new Future<Boolean>(((IFilter<T>)query.getFilter()).filter(ser));
					}
					else
					{
						ret = new Future<Boolean>(Boolean.TRUE);
					}
					
					return ret;
				}
			};
		}
	
		return ret;
		
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		ret.setTerminationCommand(new TerminationCommand()
		{
			@Override
			public void terminated(Exception reason)
			{
				// TODO: unregister terminated query
				System.out.println("TODO: unregister terminated query: "+query+", "+reason);
			}
		});
		
		if(RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
		}
		else
		{
			// Always add query locally to get local changes without superpeer roundtrip
			final ServiceQueryInfo<T> sqi = addQueryByAskMe(query);

			// When this node is not superpeer and the search scope is global
			if(!isSuperpeer() && !RequiredServiceInfo.isScopeOnLocalPlatform(query.getScope()))
			{
				ISubscriptionIntermediateFuture<T> fut = null;
//				IComponentIdentifier cid = getSuperpeerSync();
				IComponentIdentifier cid = getSuperpeer();
				if(cid!=null)
				{
					// If superpeer is available ask it
					fut = addQueryOnPlatform(cid, sqi);
				}
				else
				{
					// else need to search by asking all other peer
					fut = addQueryByAskAll(query);
				}
				sqi.setRemoteFuture(fut);
			}

			sqi.getFuture().addResultListener(new IntermediateDelegationResultListener<T>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	// write
	public <T> void removeQuery(final ServiceQuery<T> query)
	{
		
		rwlock.writeLock().lock();
		try
		{
			Set<ServiceQueryInfo<IServiceIdentifier>> qi = queries.getValues(QueryInfoExtractor.KEY_TYPE_ID, query.getId());
			queries.removeValue(qi.iterator().next());
		}
		finally
		{
			rwlock.writeLock().unlock();
		}
	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	// write
	public void removeQueries(IComponentIdentifier owner)
	{
		Future<Void> ret = new Future<Void>();
		
		rwlock.writeLock().lock();
		try
		{
			Set<ServiceQueryInfo<IServiceIdentifier>> qs = queries.getValues(QueryInfoExtractor.KEY_TYPE_OWNER, owner.toString());
			if(qs!=null)
			{
				for(ServiceQueryInfo<IServiceIdentifier> q: qs)
				{
					queries.removeValue(q);
				}
			}
		}
		finally
		{
			rwlock.writeLock().unlock();
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
				excludedservices = new HashMap<>();
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
	public void removeExcludedComponent(IComponentIdentifier cid)
	{
		rwlock.readLock().lock();
		try
		{
			if(excludedservices!=null)
			{
				Set<IServiceIdentifier> exs = excludedservices.remove(cid);
				
				// Get and remove services from cache
				if(exs!=null)
				{
					for(IServiceIdentifier ser: exs)
					{
						checkQueries(ser, ServiceEvent.SERVICE_ADDED);
					}
				}
			}
		}
		finally
		{
			rwlock.readLock().unlock();
		}
	}
	
	/**
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	// read
	public boolean isIncluded(IComponentIdentifier cid, IServiceIdentifier ser)
	{
		boolean ret = true;
		rwlock.readLock().lock();
		try
		{
			if(excludedservices!=null && ser == null && cid != null && excludedservices.containsKey(cid))
			{
				ret = false;
			}
			else if(excludedservices!=null && excludedservices.containsKey(ser.getProviderId()) && cid!=null)
			{
				IComponentIdentifier target = ser.getProviderId();
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
	 *  @param removed Indicates if the query was removed. 
	 */
	// read
	protected IFuture<Void> checkQueries(IServiceIdentifier ser, int type)
	{
		Future<Void> ret = new Future<Void>();
		
		Set<ServiceQueryInfo<IServiceIdentifier>> r1 = queries.getValues(QueryInfoExtractor.KEY_TYPE_INTERFACE, ser.getServiceType().toString());
		Set<ServiceQueryInfo<IServiceIdentifier>> r2 = queries.getValues(QueryInfoExtractor.KEY_TYPE_INTERFACE, "null");
		Set<ServiceQueryInfo<IServiceIdentifier>> sqis = r1;
		if(sqis!=null && r2!=null)
			sqis.addAll(r2);	// safe because indexer returns copy
		else if(r2!=null)
			sqis=r2;
		
//		if(removed)
//		{
//			sqis = queries.getEventQueries(ser.getServiceIdentifier().getServiceType());
//		}
//		else
//		{
//			sqis = queries.getQueries(ser.getServiceIdentifier().getServiceType());
//		}
		
		if(sqis!=null)
		{
			// Clone the data to not need to synchronize async
			Set<ServiceQueryInfo<?>> clone = new LinkedHashSet<ServiceQueryInfo<?>>(sqis);
			
			checkQueriesLoop(clone.iterator(), ser, type).addResultListener(new DelegationResultListener<Void>(ret));
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
	// read
	protected IFuture<Void> checkQueriesLoop(final Iterator<ServiceQueryInfo<?>> it, final IServiceIdentifier service, final int type)
	{
		final Future<Void> ret = new Future<Void>();
		
//		if(service.getServiceIdentifier().getServiceType().getTypeName().indexOf("ITime")!=-1)
//			System.out.println("hhh");
		
		if(it.hasNext())
		{
			final ServiceQueryInfo<?> sqi = it.next();
//			IComponentIdentifier cid = sqi.getQuery().getOwner();
//			String scope = sqi.getQuery().getScope();
//			IAsyncFilter<IService> filter = (IAsyncFilter)sqi.getQuery().getFilter();
			
			checkQuery(sqi, service, type).addResultListener(new ExceptionDelegationResultListener<Boolean, Void>(ret)
			{
				@SuppressWarnings({"unchecked", "rawtypes"})
				public void customResultAvailable(Boolean result) throws Exception
				{
//					if(service.toString().indexOf("Time")!=-1)
//					{
//						System.out.println("checkQueries: "+service+", "+type+", "+result+", "+sqi.getQuery());
//					}
					if(result.booleanValue())
					{
//						if(service.toString().indexOf("Time")!=-1)
//						{
//							System.out.println("query: "+service);
//						}
						((IntermediateFuture)sqi.getFuture()).addIntermediateResult(wrapServiceForQuery(sqi.getQuery(), service, type));
					}
					checkQueriesLoop(it, service, type).addResultListener(new DelegationResultListener<Void>(ret));
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
	 *  Wrap the service as serviceevent (sometimes) for queries.
	 */
	protected <T> Object wrapServiceForQuery(ServiceQuery<T> query, Object service, int type)
	{
		Object ret = null;
		if(query.getReturnType()!=null && ServiceEvent.CLASSINFO.getTypeName().equals(query.getReturnType().getTypeName()))
		{
			ret = new ServiceEvent(service, type);// removed ? ServiceEvent.SERVICE_REMOVED : ServiceEvent.SERVICE_ADDED);
		}
		else
		{
			ret = service;
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
	public <T> boolean checkScope(final T ser, final IComponentIdentifier cid, final String scope)
	{
		return checkSearchScope(cid, (IService)ser, scope, false) && checkPublicationScope(cid, (IService)ser);
	}
	
	/**
	 *  Check a persistent query with one service.
	 *  @param queryinfo The query.
	 *  @param service The service.
	 *  @return True, if services matches to query.
	 */
	// read
//	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected IFuture<Boolean> checkQuery(final ServiceQueryInfo<?> queryinfo, final IService service, final int type)
	{
		final Future<Boolean> ret = new Future<Boolean>();
//		IComponentIdentifier cid = queryinfo.getQuery().getOwner();
//		String scope = queryinfo.getQuery().getScope();
//		@SuppressWarnings("unchecked")
		
		// Only added events are of interest for observers that are not interested in events
		if(ServiceEvent.SERVICE_ADDED!=type && (queryinfo.getQuery().getReturnType()==null || !ServiceEvent.CLASSINFO.getTypeName().equals(queryinfo.getQuery().getReturnType().getTypeName())))
		{	
			ret.setResult(Boolean.FALSE);
		}
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
	protected boolean checkSearchScope(IComponentIdentifier cid, IServiceIdentifier ser, String scope, boolean excluded)
	{
		boolean ret = false;
		
		if(cid==null)
			throw new RuntimeException("Cid must not null, no owner in query specified.");

		if(!excluded && !isIncluded(cid, ser))
			return ret;
		
		if(scope==null)
			scope = RequiredServiceInfo.SCOPE_APPLICATION;
		
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
		{
			ret = true;
		}
		else if(RequiredServiceInfo.SCOPE_NETWORK.equals(scope))
		{
			// todo: fixme
			ret = true;
		}
		else if(RequiredServiceInfo.SCOPE_APPLICATION_GLOBAL.equals(scope))
		{
			// todo: fixme
			ret = true;
		}
		else if(RequiredServiceInfo.SCOPE_APPLICATION_NETWORK.equals(scope))
		{
			// todo: fixme
			ret = true;
		}
		else if(RequiredServiceInfo.SCOPE_PLATFORM.equals(scope))
		{
			// Test if searcher and service are on same platform
			ret = cid.getPlatformName().equals(ser.getProviderId().getPlatformName());
		}
		else if(RequiredServiceInfo.SCOPE_APPLICATION.equals(scope))
		{
			IComponentIdentifier sercid = ser.getProviderId();
			ret = sercid.getPlatformName().equals(cid.getPlatformName())
				&& getApplicationName(sercid).equals(getApplicationName(cid));
		}
		else if(RequiredServiceInfo.SCOPE_COMPONENT.equals(scope))
		{
			IComponentIdentifier sercid = ser.getProviderId();
			ret = getDotName(sercid).endsWith(getDotName(cid));
		}
		else if(RequiredServiceInfo.SCOPE_LOCAL.equals(scope))
		{
			// only the component itself
			ret = ser.getProviderId().equals(cid);
		}
		else if(RequiredServiceInfo.SCOPE_PARENT.equals(scope))
		{
			// check if parent of searcher reaches the service
			IComponentIdentifier sercid = ser.getProviderId();
			String subname = getSubcomponentName(cid);
			ret = sercid.getName().endsWith(subname);
		}
		
		return ret;
	}
	
	/**
	 *  Check if service is ok with respect to publication scope.
	 */
	protected boolean checkPublicationScope(IComponentIdentifier cid, IServiceIdentifier ser)
	{
		boolean ret = false;
		
		String scope = ser.getScope()!=null? ser.getScope(): RequiredServiceInfo.SCOPE_GLOBAL;
		
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
		{
			ret = true;
		}
		else if(RequiredServiceInfo.SCOPE_NETWORK.equals(scope))
		{
			// todo: fixme
			ret = true;
		}
		else if(RequiredServiceInfo.SCOPE_APPLICATION_GLOBAL.equals(scope))
		{
			// todo: fixme
			ret = true;
		}
		else if(RequiredServiceInfo.SCOPE_APPLICATION_NETWORK.equals(scope))
		{
			// todo: fixme
			ret = true;
		}
		else if(RequiredServiceInfo.SCOPE_PLATFORM.equals(scope))
		{if (ser.toString().startsWith("MessageService")) {
			System.out.println("PROV SCOPE IS PLATFORM + " + ser + " , CID IS " + cid); 
			}
			// Test if searcher and service are on same platform
			ret = cid.getPlatformName().equals(ser.getProviderId().getPlatformName());
		}
		else if(RequiredServiceInfo.SCOPE_APPLICATION.equals(scope))
		{
			// todo: special case platform service with app scope
			IComponentIdentifier sercid = ser.getProviderId();
			ret = sercid.getPlatformName().equals(cid.getPlatformName())
				&& getApplicationName(sercid).equals(getApplicationName(cid));
		}
		else if(RequiredServiceInfo.SCOPE_COMPONENT.equals(scope))
		{
			IComponentIdentifier sercid = ser.getProviderId();
			ret = getDotName(cid).endsWith(getDotName(sercid));
		}
		else if(RequiredServiceInfo.SCOPE_LOCAL.equals(scope))
		{
			// only the component itself
			ret = ser.getProviderId().equals(cid);
		}
		else if(RequiredServiceInfo.SCOPE_PARENT.equals(scope))
		{
			// check if parent of service reaches the searcher
			IComponentIdentifier sercid = ser.getProviderId();
			String subname = getSubcomponentName(sercid);
			ret = getDotName(cid).endsWith(subname);
		}
		
		return ret;
	}
	
	/**
	 *  Get all services.
	 *  @return The services.
	 */
	protected Set<IServiceIdentifier> getServices()
	{
		rwlock.readLock().lock();
		try
		{
			return indexer.getAllValues();
		}
		finally
		{
			rwlock.readLock().unlock();
		}
	}
	
	/**
	 *  Get services per query. Uses not the full query spec and
	 *  does not check scope and filter.
	 *  @param query The query.
	 *  @return First matching service or null.
	 */
	protected Set<IServiceIdentifier> getServices(final ServiceQuery<?> query)
	{
		rwlock.readLock().lock();
		try
		{
			Set<IServiceIdentifier> ret = indexer.getValues(query.getIndexerSearchSpec());
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
		return (IServiceRegistry)Starter.getPlatformValue(platform, Starter.DATA_SERVICEREGISTRY);
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
	 *  Get the indexer.
	 *  @return the indexer
	 */
	public Indexer<IServiceIdentifier> getIndexer()
	{
		return indexer;
	}

	/**
	 *  Perform a remote search via remote search command.
	 *  @param agent
	 */
	protected <T> ISubscriptionIntermediateFuture<T> performRemoteSearchServices(IInternalAccess agent, ServiceQuery<T> query, IComponentIdentifier cid)
	{
		assert query.isMultiple(): "Remote multi search requires multiple flag in query for determining correct future return type: "+query;
		IComponentIdentifier tcid = cid!=null? cid: query.getTargetPlatform();
		return (ISubscriptionIntermediateFuture<T>)((IInternalRemoteExecutionFeature)agent.getComponentFeature(IRemoteExecutionFeature.class))
			.executeRemoteSearch(tcid, query);
	}
}
