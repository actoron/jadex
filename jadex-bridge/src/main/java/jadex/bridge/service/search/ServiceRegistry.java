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
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.ServiceIdentifier;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.pawareness.IPassiveAwarenessService;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.commons.ChangeEvent;
import jadex.commons.IAsyncFilter;
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
	protected Indexer<IService> indexer;
	
	/** The persistent service queries. */
	protected Indexer<ServiceQueryInfo<IService>> queries;
	
	/** The excluded services cache. */
	protected Map<IComponentIdentifier, Set<IService>> excludedservices;
	
	/** The local platform cid. */
	protected IComponentIdentifier cid;
	
	/** The timer. */
	protected Timer timer;
	
	/** The global query delay, i.e. how often is polled. */
	protected long delay;
	
	/** The selected superpeer. */
	protected IComponentIdentifier superpeer;
	
//	/** The superpeer search time. */
//	protected volatile long searchtime;
	
	//-------- methods --------
	
	/**
	 *  Create a new registry.
	 */
	public ServiceRegistry(IComponentIdentifier cid, long delay)
	{
		this.cid = cid;
		this.rwlock = new ReentrantReadWriteLock(false);
		this.indexer = new Indexer<IService>(new ServiceKeyExtractor(), false, ServiceKeyExtractor.SERVICE_KEY_TYPES);
		this.queries = new Indexer<ServiceQueryInfo<IService>>(new QueryInfoExtractor(), true, QueryInfoExtractor.QUERY_KEY_TYPES_INDEXABLE);
		this.delay = delay;

		TransformSet<String> nnames = (TransformSet<String>)Starter.getPlatformValue(cid, Starter.DATA_NETWORKNAMESCACHE);
		nnames.addChangeListener(new IChangeListener<String>()
		{
			public void changeOccurred(ChangeEvent<String> event)
			{
				System.out.println("Network names change: "+event);

				rwlock.writeLock().lock();
				try
				{
					indexer.updateIndex(ServiceKeyExtractor.KEY_TYPE_NETWORKS);
					queries.updateIndex(QueryInfoExtractor.KEY_TYPE_NETWORKS);
				}
				finally
				{
					rwlock.writeLock().unlock();
				}

				checkQueriesForAllServices(ServiceEvent.SERVICE_CHANGED);
			}
		});
	}
	
	/**
	 *  Check if the platform is a registry superpeer.
	 *  (Tests if the IRegistrySynchronizationService is registered).
	 *  @return True, if is superpeer.
	 */
	protected boolean isSuperpeer()
	{
		return searchServiceSync(new ServiceQuery<ISuperpeerRegistrySynchronizationService>(ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM, null, cid, null))!=null;
	}
	
//	/**
//	 *  Check if the platform is a registry superpeer.
//	 *  (Tests if the IRegistrySynchronizationService is registered).
//	 *  @return True, if is superpeer.
//	 */
//	protected boolean isSuperpeerAvailable()
//	{
//		return getSuperpeer()!=null;
//	}

//	/**
//	 *  Get the superpeer.
//	 *  @param force If true searches superpeer anew.
//	 *  @return The superpeer.
//	 */
//	public IFuture<IComponentIdentifier> getSuperpeer(boolean force)
//	{
//		if(force)
//			resetSuperpeer();
//		
//		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
//		
//		if(superpeer!=null)
//		{
//			ret.setResult(superpeer);
//		}
//		else
//		{
//			long ct = System.currentTimeMillis();
//			if(superpeer==null && searchtime<ct)
//			{
//				synchronized(this)
//				{
//					if(superpeer==null && searchtime<ct)
//					{
//						// Ensure that a delay is waited between searches
//						searchtime = ct+delay;
//						searchSuperpeer().addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
//						{
//							public void customResultAvailable(IComponentIdentifier result)
//							{
//								superpeer = result;
//								addQueriesToNewSuperpeer();
//								super.customResultAvailable(result);
//							}
//						});
//					}
//					else
//					{
//						ret.setException(new ComponentNotFoundException("No superpeer found."));
//					}
//				}
//			}
//			else
//			{
//				ret.setException(new ComponentNotFoundException("No superpeer found."));
//			}
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Adapt the existing queries to a new superpeer.
	 *  (Needs not to remove from old superpeer to lease time cleanup).
	 */
	protected void addQueriesToNewSuperpeer()
	{
		if(superpeer!=null)
		{
			Set<ServiceQueryInfo<IService>> qs = queries.getValues(QueryInfoExtractor.KEY_TYPE_ISREMOTE, "true");
			
//			// get all queries in which the superpeer was set
//			final Set<ServiceQueryInfo<?>> aqs = queries.getQueries(new IFilter<ServiceQueryInfo<?>>()
//			{
//				public boolean filter(ServiceQueryInfo<?> query) 
//				{
//					return query.getSuperpeer()!=null;
//				}
//			});
			if(qs!=null)
			{
				for(Iterator<ServiceQueryInfo<IService>> it = qs.iterator(); it.hasNext();)
				{
					ServiceQueryInfo<IService> query = it.next();
					ISubscriptionIntermediateFuture<?> rfut = addQueryOnPlatform(superpeer, query);
					query.setRemoteFuture((ISubscriptionIntermediateFuture)rfut);
				}
			}
		}
	}
	
	/**
	 *  Add a query on another platorm (superpeer).
	 *  @param spcid The platform id.
	 *  @param sqi The query.
	 */
	protected <T> ISubscriptionIntermediateFuture<T> addQueryOnPlatform(final IComponentIdentifier spcid, final ServiceQueryInfo<T> sqi)
	{
		if(RequiredServiceInfo.SCOPE_NONE.equals(sqi.getQuery().getScope()))
		{
			return new SubscriptionIntermediateFuture<T>(new ServiceNotFoundException(sqi.getQuery().getServiceType() != null? sqi.getQuery().getServiceType().getTypeName() : sqi.getQuery().toString()));
		}
		else
		{
			@SuppressWarnings("unchecked")
			ISubscriptionIntermediateFuture<T>	ret	= executeOnLocalComponent(ISubscriptionIntermediateFuture.class, new IComponentStep<Collection<T>>()
			{
				@Override
				public ISubscriptionIntermediateFuture<T> execute(IInternalAccess ia)
				{
					// Set superpeer in query info for later removal
					sqi.setSuperpeer(spcid);
					enhanceQuery(sqi.getQuery());
					ISuperpeerRegistrySynchronizationService	srs	= SServiceProvider.getServiceProxy(ia, spcid, ISuperpeerRegistrySynchronizationService.class);
					return srs.addQuery(sqi.getQuery());
				}
			});
			return ret;
		}
	}
	
	/**
	 * 
	 * @param spcid
	 * @param sqi
	 * @return
	 */
	protected <T> IFuture<Void> removeQueryOnPlatform(final IComponentIdentifier spcid, final ServiceQueryInfo<T> sqi)
	{
		@SuppressWarnings("unchecked")
		IFuture<Void>	ret	= executeOnLocalComponent(IFuture.class, new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// Reset superpeer in query info
				sqi.setSuperpeer(null);
				ISuperpeerRegistrySynchronizationService	srs	= SServiceProvider.getServiceProxy(ia, spcid, ISuperpeerRegistrySynchronizationService.class);
				return srs.removeQuery(sqi.getQuery());
			}
		});
		return ret;
	}
	
//	/**
//	 *  Get the superpeer. Triggers search in background if none available.
//	 *  @return The superpeer.
//	 */
//	public IComponentIdentifier getSuperpeerSync()
//	{
//		long ct = System.currentTimeMillis();
//		if(superpeer==null && searchtime<ct)
//		{
//			synchronized(this)
//			{
//				if(superpeer==null && searchtime<ct)
//				{
//					// Ensure that a delay is waited between searches
//					searchtime = ct+delay;
//					searchSuperpeer().addResultListener(new IResultListener<IComponentIdentifier>()
//					{
//						public void resultAvailable(IComponentIdentifier result)
//						{
////							System.out.println("Found superpeer: "+result);
//							superpeer = result;
//							addQueriesToNewSuperpeer();
//							// initiating 
//						}
//						
//						public void exceptionOccurred(Exception exception)
//						{
////							System.out.println("No superpeer found");
//						}
//					});
//				}
//				else
//				{
//					System.out.println("No superpeer search: "+searchtime+" "+ct);
//				}
//			}
//		}
//			
//		return superpeer;
//	}
	
//	/**
//	 *  Reset the superpeer.
//	 */
//	public void resetSuperpeer()
//	{
//		this.superpeer = null;
//		this.searchtime = 0;
//	}
		
//	/**
//	 *  Search superpeer by sending requests to all known platforms if they host a IRegistrySynchronizationService service.
//	 *  @return The cids of the superpeers.
//	 */
//	protected IFuture<IComponentIdentifier> searchSuperpeer()
//	{
//		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
//
//		// Only search for super peer when super peer client agent is running. Otherwise the platform is itself a super peer (hack???)
//		// TODO: move super peer management to separate agent (common base agent also needed for relay and transport address super peer management).
//		if(getLocalServiceByClass(new ClassInfo(IPeerRegistrySynchronizationService.class))!=null)
//		{
////			System.out.println("ask all");
//			searchServiceAsyncByAskAll(new ServiceQuery<ISuperpeerRegistrySynchronizationService>(ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_GLOBAL, null, cid, null))
//				.addResultListener(new ExceptionDelegationResultListener<ISuperpeerRegistrySynchronizationService, IComponentIdentifier>(ret)
//			{
//				public void customResultAvailable(ISuperpeerRegistrySynchronizationService result)
//				{
////					System.out.println("found: "+result);
//					ret.setResult(((IService)result).getServiceIdentifier().getProviderId());
//				}
//			});
//		}
//		else
//		{
//			ret.setException(new ComponentNotFoundException("No superpeer found."));
//		}
//
//		return ret;
//	}
	
	/**
	 *  Add a service to the registry.
	 *  @param service The service.
	 */
	// write
	public IFuture<Void> addService(IService service)
	{
		IFuture<Void> ret = null;
		Lock lock = rwlock.writeLock();
		lock.lock();
		try
		{
			indexer.addValue(service);
			
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
	public void removeService(IService service)
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
		if(platform==null)
		{
			System.err.println("removeServices platform is null, cid="+cid);
			throw new NullPointerException("removeServices platform is null, cid="+cid);
		}
		Lock lock = rwlock.writeLock();
		lock.lock();
		try
		{
			Set<IService> pservs = indexer.getValues(ServiceKeyExtractor.KEY_TYPE_PLATFORM, platform.toString());
			if(pservs != null)
			{
				for(IService serv : pservs)
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
				for(IService serv : pservs)
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
			Set<IService> pservs = indexer.getAllValues();
			if(pservs != null)
			{
				for(IService serv : pservs)
				{
					if(!serv.getServiceIdentifier().getProviderId().getRoot().equals(platform))
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
				for(IService serv : pservs)
				{
					if(!serv.getServiceIdentifier().getProviderId().getRoot().equals(platform))
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
	 *  Search for services.
	 */
	// read
//	@SuppressWarnings("unchecked")
	public <T> T searchServiceSync(final ServiceQuery<T> query)
	{
		enhanceQuery(query);
		
		T ret = null;
		if(!RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			if(query.getFilter() instanceof IAsyncFilter)
				throw new IllegalArgumentException("Synchronous search call with asynchronous filter in query: " + query);
			
			Set<IService> sers = getServices(query);
			IFilter<T> filter = (IFilter<T>)query.getFilter();
			filter = (IFilter<T>)(filter == null? IFilter.ALWAYS : filter);
			
			Set<IService> ownerservices = null;
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
				for(IService ser : sers)
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
		enhanceQuery(query);
		
		Set<T> ret = null;
		if(!RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			if(query.getFilter() instanceof IAsyncFilter)
				throw new IllegalArgumentException("Synchronous search call with asynchronous filter in query: " + query);
			
			Set<IService> sers = getServices(query);
			IFilter<T> filter = (IFilter<T>) query.getFilter();
			filter = (IFilter<T>)(filter==null? IFilter.ALWAYS : filter);
			
			Set<IService> ownerservices = null;
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
				for(Iterator<IService> it = sers.iterator(); it.hasNext(); )
				{
					IService ser = it.next();
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
	 *  Get all services.
	 *  @return All services (copy).
	 */
	public Set<IService> getAllServices()
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
	public Set<ServiceQueryInfo<IService>> getAllQueries()
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
	 *  Get all services of a platform.
	 *  @return The services of the platform.
	 */
	public Set<IService> getAllServicesOfPlatform(IComponentIdentifier platform)
	{
		rwlock.readLock().lock();
		try
		{
			return SUtil.safeSet(indexer.getValues(ServiceKeyExtractor.KEY_TYPE_PLATFORM, platform.toString()));
		}
		finally
		{
			rwlock.readLock().unlock();
		}
	}
	
	

	/**
	 *  Search for services.
	 */
	// read
	public <T> IFuture<T> searchServiceAsync(final ServiceQuery<T> query)
	{
		enhanceQuery(query);
		
		final Future<T> ret = new Future<T>();
		
		if(RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
		}
		else
		{
			boolean locallyavailable = query.getTargetPlatform()==null || getAllServicesOfPlatform(query.getTargetPlatform()).size()>0;

			// When this node is superpeer or the search scope is platform or below
			if(locallyavailable && (isSuperpeer() || RequiredServiceInfo.isScopeOnLocalPlatform(query.getScope())))
			{
//				if((""+query.getServiceType()).indexOf("AutoTerminate")!=-1)
//					System.out.println("searchServiceAsync1: "+query);
				searchServiceAsyncByAskMe(query).addResultListener(new DelegationResultListener<T>(ret));
			}
			else
			{
//				IComponentIdentifier cid = getSuperpeerSync();
				IComponentIdentifier cid = getSuperpeer();
				// Search has explicit start point ask there
				if(query.getProvider()!=null)
				{
					ServiceQuery<T> cp = adaptQuery(query, null);
					searchServiceAsyncByAskOnePlatform(cp, cp.getProvider().getRoot()).addResultListener(new DelegationResultListener<T>(ret));
				}
				else if(query.getPlatform()!=null)
				{
					ServiceQuery<T> cp = adaptQuery(query, null);
					searchServiceAsyncByAskOnePlatform(cp, cp.getPlatform()).addResultListener(new DelegationResultListener<T>(ret));
				}
				// Ask superpeer if available
				else if(cid!=null)
				{
//					if((""+query.getServiceType()).indexOf("AutoTerminate")!=-1)
//						System.out.println("searchServiceAsync2: "+query);
					// Do not adapt query when searching superpeer because it only searches its own database
					searchServiceAsyncByAskOnePlatform(query, cid).addResultListener(new DelegationResultListener<T>(ret));
				}
				// else need to search by asking all other peer
				else
				{
//					if((""+query.getServiceType()).indexOf("AutoTerminate")!=-1)
//						System.out.println("searchServiceAsync3: "+query);
					searchServiceAsyncByAskAll(query).addResultListener(new DelegationResultListener<T>(ret));
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	// read
	public <T> ISubscriptionIntermediateFuture<T> searchServicesAsync(final ServiceQuery<T> query)
	{
		enhanceQuery(query);
		
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		if(RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
		}
		else
		{
			boolean locallyavailable = query.getTargetPlatform()==null || getAllServicesOfPlatform(query.getTargetPlatform()).size()>0;
			
			// When this node is superpeer or the search scope is platform or below
			if(locallyavailable && (isSuperpeer() || RequiredServiceInfo.isScopeOnLocalPlatform(query.getScope())))
			{
				searchServicesAsyncByAskMe(query).addResultListener(new IntermediateDelegationResultListener<T>(ret));
			}
			else
			{
//				IComponentIdentifier cid = getSuperpeerSync();
				IComponentIdentifier cid = getSuperpeer();
				// if search has explicit start point ask there
				if(query.getProvider()!=null)
				{
					ServiceQuery<T> cp = adaptQuery(query, null);
					searchServicesAsyncByOnePlatform(cp, cp.getProvider().getRoot()).addResultListener(new IntermediateDelegationResultListener<T>(ret));
				}
				else if(query.getPlatform()!=null)
				{
					ServiceQuery<T> cp = adaptQuery(query, null);
					searchServicesAsyncByOnePlatform(cp, cp.getPlatform()).addResultListener(new IntermediateDelegationResultListener<T>(ret));
				}
				else if(cid!=null)
				{
					// If superpeer is available ask it
					searchServicesAsyncByOnePlatform(query, cid).addResultListener(new IntermediateDelegationResultListener<T>(ret));
				}
				// Only search by ask all if the owner is local to prevent loops.
				else if (ServiceRegistry.this.cid.getPlatformName().equals(query.getOwner().getPlatformName()))
				{ 
					searchServicesAsyncByAskAll(query).addResultListener(new IntermediateDelegationResultListener<T>(ret));
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		enhanceQuery(query);
		
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
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
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	// write
//	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> ServiceQueryInfo<T> addQueryByAskMe(final ServiceQuery<T> query)
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
			Iterator<T> it = sers.iterator();
			checkAsyncFilters(filter, it).addIntermediateResultListener(new UnlimitedIntermediateDelegationResultListener<T>(fut)
			{
				public void intermediateResultAvailable(T result)
				{
					super.intermediateResultAvailable((T)wrapServiceForQuery(query, result, ServiceEvent.SERVICE_ADDED));
				}
			});
		}
	
		return ret;
	}
	
//	/**
//	 *  Add a service query to the registry of a selected superpeer.
//	 *  @param query ServiceQuery.
//	 */
//	protected <T> ISubscriptionIntermediateFuture<T> addQueryByAskSuperpeer(final ServiceQueryInfo<T> sqi, final IComponentIdentifier cid)
//	{
//		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
//		
//		final ServiceQuery<T> query = sqi.getQuery();
//		if(RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
//		{
//			ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
//		}
//		else
//		{
////			if(getSuperpeerSync()!=null)
//			if(getSuperpeer()!=null)
//			{
//				final IComponentManagementService cms = getLocalServiceByClass(new ClassInfo(IComponentManagementService.class));
//				if(cms!=null)
//				{
//					cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Collection<T>>(ret)
//					{
//						public void customResultAvailable(IExternalAccess result) throws Exception
//						{
//							try
//							{
//								// Set superpeer in query info for later removal
//								sqi.setSuperpeer(cid);
//								
//								result.scheduleStep(new IComponentStep<Collection<T>>()
//								{
//									@Classname("addQueryOnSuperpeer")
//									public ISubscriptionIntermediateFuture<T> execute(IInternalAccess ia)
//									{
//										IServiceRegistry reg = ServiceRegistry.getRegistry(ia.getComponentIdentifier());
//										return reg.addQuery(query);
//									}
//								}).addResultListener(new IntermediateDelegationResultListener<T>(ret));
//							}
//							catch(Exception e)
//							{
//								ret.setException(e);
//							}
//						}
//					});
//				}
//				else
//				{
//					ret.setException(new RuntimeException("RMS not found"));
//				}
//			}
//			else
//			{
//				ret.setException(new RuntimeException("No superpeer found"));
//			}
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	protected <T> ISubscriptionIntermediateFuture<T> addQueryByAskAll(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		// Emulate persistent query by searching periodically
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(query.getScope()))
		{
			final DuplicateRemovalIntermediateResultListener<T> lis = new DuplicateRemovalIntermediateResultListener<T>(new UnlimitedIntermediateDelegationResultListener<T>(ret))
			{
				public byte[] objectToByteArray(Object service)
				{
					return super.objectToByteArray(((IService)service).getServiceIdentifier());
				}
			};
			
			waitForDelay(delay, new Runnable()
			{
				public void run()
				{
					searchRemoteServices(query).addIntermediateResultListener(lis);
					
					if(!ret.isDone())
						waitForDelay(delay, this);
				}
			});
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
		enhanceQuery(query);
		
		if(RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
			return null;

		T ret = null;
		Set<IService> sers = getServices(query);
		if(sers!=null)
		{
			Iterator<IService> it = sers.iterator();
			while(it.hasNext())
			{
				IService ser = it.next();
				if(checkSearchScope(query.getOwner(), ser, query.getScope(), excluded) && checkPublicationScope(query.getOwner(), ser))
				{
					ret = (T)ser;
					break;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	// write
	public <T> IFuture<Void> removeQuery(final ServiceQuery<T> query)
	{
		final Future<Void> ret = new Future<Void>();
		
		rwlock.writeLock().lock();
		ServiceQueryInfo<IService> qinfo = null;
		try
		{
			Set<ServiceQueryInfo<IService>> qi = queries.getValues(QueryInfoExtractor.KEY_TYPE_ID, query.getId());
			queries.removeValue(qi.iterator().next());
		}
		finally
		{
			rwlock.writeLock().unlock();
		}
		
		if(qinfo != null)
		{
			qinfo.getFuture().setFinished();
			
			if(qinfo.getSuperpeer()!=null)
			{
				removeQueryOnPlatform(qinfo.getSuperpeer(), qinfo).addResultListener(new DelegationResultListener<Void>(ret));
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
	
//	/**
//	 *  Remove a query from the superpeer.
//	 */
//	protected <T> IFuture<Void> removeQueryFromSuperpeer(final ServiceQueryInfo<T> qinfo)
//	{
//		final Future<Void> ret = new Future<Void>();
//		
//		if(qinfo.getSuperpeer()!=null)
//		{
//			final IComponentManagementService cms = getLocalServiceByClass(new ClassInfo(IComponentManagementService.class));
//			if(cms!=null)
//			{
//				cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
//				{
//					public void customResultAvailable(IExternalAccess result) throws Exception
//					{
//						try
//						{
//							final ServiceQuery<T> query = qinfo.getQuery();
//							result.scheduleStep(new IComponentStep<Void>()
//							{
//								@Classname("removeQueryOnSuperpeer")
//								public IFuture<Void> execute(IInternalAccess ia)
//								{
//									IServiceRegistry reg = ServiceRegistry.getRegistry(ia.getComponentIdentifier());
//									reg.removeQuery(query);
//									return IFuture.DONE;
//								}
//							}).addResultListener(new DelegationResultListener<Void>(ret));
//						}
//						catch(Exception e)
//						{
//							ret.setException(e);
//						}
//					}
//				});
//			}
//			else
//			{
//				ret.setException(new RuntimeException("RMS not found"));
//			}
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	// write
	public IFuture<Void> removeQueries(IComponentIdentifier owner)
	{
		Future<Void> ret = new Future<Void>();
		
		rwlock.writeLock().lock();
		Set<ServiceQueryInfo<?>> qinfos = null;
		try
		{
			Set<ServiceQueryInfo<IService>> qs = queries.getValues(QueryInfoExtractor.KEY_TYPE_OWNER, owner.toString());
			if(qs!=null)
			{
				for(ServiceQueryInfo<IService> q: qs)
				{
					queries.removeValue(q);
				}
			}
//			qinfos = queries.removeQueries(owner);
		}
		finally
		{
			rwlock.writeLock().unlock();
		}
		
		if(qinfos != null)
		{
			FutureBarrier<Void> bar = new FutureBarrier<Void>();
			
			for(ServiceQueryInfo<?> qinfo : qinfos)
			{
				qinfo.getFuture().setFinished();
				
				if(qinfo.getSuperpeer()!=null)
					bar.addFuture(removeQueryOnPlatform(qinfo.getSuperpeer(), qinfo));
			}
			
			bar.waitFor().addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove all service queries of a specific platform from the registry.
	 *  @param platform The platform from which the query owner comes.
	 */
	// write
	public IFuture<Void> removeQueriesFromPlatform(IComponentIdentifier platform)
	{
		Future<Void> ret = new Future<Void>();
		
		rwlock.writeLock().lock();
		Set<ServiceQueryInfo<?>> qinfos = new HashSet<ServiceQueryInfo<?>>();
		try
		{
			// todo: Should use index to find all services of a platform
			Set<ServiceQueryInfo<IService>> qis = queries.getValues(QueryInfoExtractor.KEY_TYPE_OWNER_PLATORM, platform.toString());
			
			if(qis!=null)
			{
				for(ServiceQueryInfo<IService> sqi: qis)
				{
					queries.removeValue(sqi);
					qinfos.add(sqi);
				}
			}
			
//			Set<ServiceQueryInfo<?>> allqs = queries.getAllQueries();
//			if(allqs != null)
//			{
//				for(ServiceQueryInfo<?> qinfo : allqs)
//				{
//					if(qinfo.getQuery().getOwner().getRoot().equals(platform))
//					{
//						queries.removeQuery(qinfo.getQuery());
//						qinfos.add(qinfo);
//					}
//				}
//			}
//			else
//			{
//				ret.setResult(null);
//			}
		}
		finally
		{
			rwlock.writeLock().unlock();
		}
		
		if(!qinfos.isEmpty())
		{
			FutureBarrier<Void> bar = new FutureBarrier<Void>();
			
			for(ServiceQueryInfo<?> qinfo : qinfos)
			{
				qinfo.getFuture().setFinished();
				
				if(qinfo.getSuperpeer()!=null)
					bar.addFuture(removeQueryOnPlatform(qinfo.getSuperpeer(), qinfo));
			}
			
			bar.waitFor().addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
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
						checkQueries(ser, ServiceEvent.SERVICE_ADDED).addResultListener(lis);
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
	 *  @param removed Indicates if the query was removed. 
	 */
	// read
	protected IFuture<Void> checkQueries(IService ser, int type)
	{
		Future<Void> ret = new Future<Void>();
		
		Set<ServiceQueryInfo<IService>> r1 = queries.getValues(QueryInfoExtractor.KEY_TYPE_INTERFACE, ser.getServiceIdentifier().getServiceType().toString());
		Set<ServiceQueryInfo<IService>> r2 = queries.getValues(QueryInfoExtractor.KEY_TYPE_INTERFACE, "null");
		Set<ServiceQueryInfo<IService>> sqis = r1;
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
	 *  Check all services on all queries.
	 */
	protected IFuture<Void> checkQueriesForAllServices(int type)
	{
		FutureBarrier<Void> ret = new FutureBarrier<Void>();

		Set<IService> services;

		rwlock.writeLock().lock();
		try
		{
			services = indexer.getAllValues();
		}
		finally
		{
			rwlock.writeLock().unlock();
		}

		// Call check queries without lock
		for(IService service: services)
			checkQueries(service, type);

		return ret.waitFor();
	}

	/**
	 *  Check the persistent queries against a new service.
	 *  @param it The queries.
	 *  @param service the service.
	 */
	// read
	protected IFuture<Void> checkQueriesLoop(final Iterator<ServiceQueryInfo<?>> it, final IService service, final int type)
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
					if(service.toString().indexOf("Time")!=-1)
					{
						System.out.println("checkQueries: "+service+", "+type+", "+result+", "+sqi.getQuery());
					}
					if(result.booleanValue())
					{
//						System.out.println("query: "+service);
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
	protected boolean checkSearchScope(IComponentIdentifier cid, IService ser, String scope, boolean excluded)
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
			System.out.println("PROV SCOPE IS PLATFORM + " + ser.getServiceIdentifier() + " , CID IS " + cid); 
			}
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
	protected Set<IService> getServices(final ServiceQuery<?> query)
	{
		rwlock.readLock().lock();
		try
		{
			Set<IService> ret = indexer.getValues(query.getIndexerSearchSpec());
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
	 *  Search for services.
	 */
	// TODO: Should be internal, but needed by PeerRegistrySynchronizationService
	public <T> ISubscriptionIntermediateFuture<T> searchServicesAsyncByAskAll(final ServiceQuery<T> query)
	{
		enhanceQuery(query);
		
//		System.out.println("searchServicesAsyncByAskAll: "+query);
		
		SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		IIntermediateResultListener<T> reslis = new IntermediateDelegationResultListener<T>(ret)
		{
			boolean firstfinished = false;
			
			public void finished()
			{
//				System.out.println("searchServicesAsyncByAskAll: "+query.hashCode());
				if(firstfinished)
					super.finished();
				else
					firstfinished = true;
			}
		};
		
		searchServicesAsyncByAskMe(query).addIntermediateResultListener(reslis);
		searchRemoteServices(query).addIntermediateResultListener(reslis);
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	// read
	protected <T> ISubscriptionIntermediateFuture<T> searchServicesAsyncByOnePlatform(final ServiceQuery<T> query, final IComponentIdentifier cid)
	{
		if(RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			return new SubscriptionIntermediateFuture<T>(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
		}
		else
		{
			@SuppressWarnings("unchecked")
			ISubscriptionIntermediateFuture<T>	ret	= executeOnLocalComponent(SubscriptionIntermediateFuture.class, new IComponentStep<Collection<T>>()
			{
				public ISubscriptionIntermediateFuture<T> execute(IInternalAccess ia)
				{
					enhanceQuery(query);
					return performRemoteSearchServices(ia, query, cid);
				}
			});
			return ret;
		}
	}
	

	/**
	 *  Search for services.
	 */
	// read
//	@SuppressWarnings("unchecked")	
	protected <T> ISubscriptionIntermediateFuture<T> searchServicesAsyncByAskMe(final ServiceQuery<T> query)
	{	
		SubscriptionIntermediateFuture<T> ret = null;
		
		if(RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			ret = new SubscriptionIntermediateFuture<T>();
			ret.setFinished();
		}
		else
		{
			IAsyncFilter<T> filter = new QueryFilter<T>(query);
			Set<IService> sers = getServices(query);
			
			if(query.isExcludeOwner())
			{
				Set<IService> ownerservices = null;

				rwlock.readLock().lock();
				try
				{
					ownerservices = indexer.getValues(ServiceKeyExtractor.KEY_TYPE_PROVIDER, query.getOwner().toString());
				}
				finally
				{
					rwlock.readLock().unlock();
				}

				sers.removeAll(ownerservices);
			}
			
			if(sers != null && sers.size() > 0)
			{
				ret = (SubscriptionIntermediateFuture<T>)checkAsyncFilters(filter, (Iterator)sers.iterator());
			}
			else
			{
				ret = new SubscriptionIntermediateFuture<T>();
				ret.setFinished();
			}
		}
		
		return ret;
	}
	
	/**
	 *  Search for service.
	 */
	// read
	protected <T> IFuture<T> searchServiceAsyncByAskMe(final ServiceQuery<T> query)
	{
		final Future<T> ret = new Future<T>();
		
		if(RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
		}
		else
		{
			Set<IService> sers = getServices(query);
			if(sers!=null && !sers.isEmpty())
			{
				final Iterator<IService> it = sers.iterator();
				
				(new ICommand<Iterator<IService>>()
				{
//					@SuppressWarnings({ "unchecked", "rawtypes" })
					public void execute(final Iterator<IService> it)
					{
						IService ser = it.next();
						
						final T obj = (T)wrapServiceForQuery(query, ser, ServiceEvent.SERVICE_ADDED);
						
						final ICommand<Iterator<IService>> cmd = this;
						
						Set<IService> ownerservices = null;

						rwlock.readLock().lock();
						try
						{
							ownerservices = query.isExcludeOwner()? indexer.getValues(ServiceKeyExtractor.KEY_TYPE_PROVIDER, query.getOwner().toString()) : null;
						}
						finally
						{
							rwlock.readLock().unlock();
						}
//						Set<IService> ownerservices = query.isExcludeOwner()? indexer.getValues(ServiceKeyExtractor.KEY_TYPE_PROVIDER, query.getOwner().toString()) : null;
						
						boolean passes = checkSearchScope(query.getOwner(), ser, query.getScope(), false);
						passes &= checkPublicationScope(query.getOwner(), ser);
						passes &= (ownerservices == null || !ownerservices.contains(ser));
						if(query.getFilter() instanceof IFilter)
						{
							passes &= ((IFilter<T>)query.getFilter()).filter(obj);
						}
						
						if(passes)
						{
							if(query.getFilter() instanceof IAsyncFilter)
							{
								((IAsyncFilter<T>)query.getFilter()).filter(obj).addResultListener(new IResultListener<Boolean>()
								{
									public void resultAvailable(Boolean result)
									{
										if(Boolean.TRUE.equals(result))
											ret.setResult(obj);
										else
											exceptionOccurred(null);
									}
									
									public void exceptionOccurred(Exception exception)
									{
										if(it.hasNext())
											cmd.execute(it);
										else
											ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
									}
								});
							}
							else
							{
								ret.setResult(obj);
							}
						}
						else
						{
							if(it.hasNext())
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
	// Todo: public because needed by CMS to get remote CMS for external access
	public <T> IFuture<T> searchServiceAsyncByAskOnePlatform(final ServiceQuery<T> query, final IComponentIdentifier spcid)
	{
		if(RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			return	new Future<T>(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
		}
		else
		{
			@SuppressWarnings("unchecked")
			IFuture<T>	ret	= executeOnLocalComponent(Future.class, new IComponentStep<T>()
			{
				public IFuture<T> execute(IInternalAccess ia)
				{
					enhanceQuery(query);
					return performRemoteSearchService(ia, query, null);
				}
			});
			return ret;
		}
	}
	
	/**
	 *  Generic code for running on a component.
	 *  Used for executing remote requests.
	 *  Hack??? should call remote registry service directly instead of using cid.
	 */
	protected <T, FUT extends IFuture<T>>	FUT executeOnLocalComponent(Class<FUT> clazz, final IComponentStep<T> step)
	{
		// Hack!!! Logger???
		@SuppressWarnings("unchecked")
		final Future<T> ret	= (Future<T>)FutureFunctionality.getDelegationFuture(clazz, new FutureFunctionality((Logger)null));
		
		// Find component to schedule on (calling component or platform)
		IComponentIdentifier origin	= IComponentIdentifier.LOCAL.get();
		if(origin==null)	// No local cid when called from external thread (e.g. swing).
			origin	= cid;

		final IComponentManagementService cms = getLocalServiceByClass(new ClassInfo(IComponentManagementService.class));
		if(cms!=null)
		{
			cms.getExternalAccess(origin).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
			{
				public void customResultAvailable(IExternalAccess result) throws Exception
				{
					try
					{
						FutureFunctionality.connectDelegationFuture(ret, result.scheduleStep(step));
					}
					catch(Exception e)
					{
						ret.setException(e);
					}
				}
			});
		}
		else
		{
			ret.setException(new RuntimeException("RMS not found"));
		}
		
		@SuppressWarnings("unchecked")
		FUT	fret	= (FUT)ret;
		return fret;
	}
	
	/**
	 *  Search for services.
	 */
	protected <T> IFuture<T> searchServiceAsyncByAskAll(final ServiceQuery<T> query)
	{
//		if((""+query.getServiceType()).indexOf("AutoTerminate")!=-1)
//			System.out.println("searchServiceAsyncByAskAll: "+query);

		final Future<T> ret = new Future<T>();
		searchServiceAsyncByAskMe(query).addResultListener(new IResultListener<T>()
		{
			public void resultAvailable(T result)
			{
//				if((""+query.getServiceType()).indexOf("AutoTerminate")!=-1)
//					System.out.println("searchServiceAsyncByAskAll0: "+query+", "+result);
				ret.setResult(result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				if((""+query.getServiceType()).indexOf("ISuper")!=-1)
//					System.out.println("ex on: "+query+" "+exception);
				if(RequiredServiceInfo.SCOPE_GLOBAL.equals(query.getScope()))
				{
//					if((""+query.getServiceType()).indexOf("AutoTerminate")!=-1)
//						System.out.println("searchServiceAsyncByAskAll1: "+query);
					final ITerminableIntermediateFuture<T> sfut = searchRemoteServices(query);
					sfut.addIntermediateResultListener(new IIntermediateResultListener<T>()
					{
						protected boolean done = false; 
						
						public void intermediateResultAvailable(T result)
						{
							ret.setResult(result);
							sfut.terminate();
							done = true;
						};
						
						public void finished()
						{
							if(!done)
								ret.setException(new ServiceNotFoundException(query.getServiceType() != null? query.getServiceType().getTypeName() : query.toString()));
						}

						public void exceptionOccurred(Exception exception)
						{
						}

						public void resultAvailable(Collection<T> result)
						{
						};
					});
				}
				else
				{
					ret.setException(exception);
				}
			};
		});
		
		return ret;
	}
	
	/**
	 *  Search for services on remote platforms.
	 *  @param caller	The component that started the search.
	 *  @param type The type.
	 *  @param filter The filter.
	 */
	protected <T> ISubscriptionIntermediateFuture<T> searchRemoteServices(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		// Check for awareness service
		IPassiveAwarenessService	pawa	= getLocalServiceByClass(new ClassInfo(IPassiveAwarenessService.class));
		if(pawa!=null)
		{
			// Count awa search + platform searches (+ async filtering, if any).
			final AtomicInteger	cnt	= new AtomicInteger(1);
			
			// Determine local source component for sending search messages.
			IComponentIdentifier origin	= IComponentIdentifier.LOCAL.get();
			if(origin==null)	// TODO: shouldn't happen?
				origin	= cid;
			final IComponentIdentifier forigin = origin;
			final IComponentManagementService	cms	= getLocalServiceByClass(new ClassInfo(IComponentManagementService.class));
			final ServiceQuery<T> remotequery = new ServiceQuery<T>(query);
			// Disable filter, we do that locally.
			remotequery.setFilter(null);

			// Search for other platforms
			pawa.searchPlatforms().addResultListener(new IntermediateDefaultResultListener<IComponentIdentifier>()
			{
				@Override
				public void intermediateResultAvailable(final IComponentIdentifier platform)
				{
//					System.out.println(cid + " searching remote platform: "+platform+", "+query);
					
					// Only (continue to) search remote when future not yet finished or cancelled.
					if(!ret.isDone())
					{
						cnt.incrementAndGet();
						final Future<Collection<T>> remotesearch = new Future<Collection<T>>();
						cms.getExternalAccess(forigin).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Collection<T>>(remotesearch)
						{
							public void customResultAvailable(IExternalAccess result) throws Exception
							{
								result.scheduleStep(new IComponentStep<Collection<T>>()
								{
									public IFuture<Collection<T>> execute(IInternalAccess ia)
									{
										return ((IInternalRemoteExecutionFeature)ia.getComponentFeature(IRemoteExecutionFeature.class))
											.executeRemoteSearch(platform, adaptQuery(query, platform));
									}
								}).addResultListener(new DelegationResultListener<Collection<T>>(remotesearch));
							}
						});
						
						remotesearch.addResultListener(new IResultListener<Collection<T>>()
						{
							@SuppressWarnings("unchecked")
							public void resultAvailable(Collection<T> result)
							{
//								System.out.println(cid + " searched remote platform: "+platform+", "+result);
								if(result != null)
								{
									if(query.getFilter() instanceof IAsyncFilter)
									{
										for(Iterator<T> it = result.iterator(); it.hasNext(); )
										{
											final T ser = it.next();
											cnt.incrementAndGet();
											try
											{
												IFuture<Boolean> filterfut = ((IAsyncFilter<T>)query.getFilter()).filter(ser);
												filterfut.addResultListener(new IResultListener<Boolean>()
												{
													public void resultAvailable(Boolean result)
													{
														if(Boolean.TRUE.equals(result))
															ret.addIntermediateResultIfUndone(ser);
														doFinished();
													}
	
													public void exceptionOccurred(Exception exception)
													{
														doFinished();
													}
												});
											}
											catch(Exception e)
											{
												// ignore sync failure in filter (e.g. user code not properly catching and setting exception)
												doFinished();
											}
										}
									}
									else
									{
										for(Iterator<T> it = result.iterator(); it.hasNext(); )
										{
											T ser = it.next();
											try
											{
												if(query.getFilter() == null || ((IFilter<T>)query.getFilter()).filter(ser))
												{
													ret.addIntermediateResultIfUndone(ser);
												}
											}
											catch(Exception e)
											{
												// ignore failed filter.
											}
										}
									}
								}
								doFinished();
							}

							public void exceptionOccurred(Exception exception)
							{
								doFinished();
							}
						});
					}
				}
				
				@Override
				public void finished()
				{
					doFinished();
				}
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
					// ignore exception
					doFinished();
				}
				
				private void doFinished()
				{
					if(cnt.decrementAndGet()==0)
					{
						// Undone, because gets terminated on first result for search one
						ret.setFinishedIfUndone();
					}
				}
			});
		}
		else
		{
			ret.setFinished();
		}
		
		return ret;
	}

	
//	/**
//	 *  Search for services on remote platforms.
//	 *  @param caller	The component that started the search.
//	 *  @param type The type.
//	 *  @param filter The filter.
//	 */
//	protected <T> ISubscriptionIntermediateFuture<T> searchRemoteServices(final ServiceQuery<T> query)
//	{
//		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
//		// Get all proxy agents (represent other platforms)
//		Collection<IService> sers = getLocalServicesByClass(new ClassInfo(IProxyAgentService.class));
////		System.out.println("LOCAL:" + cid + " SERS: " + sers);
//		if(sers!=null && sers.size()>0)
//		{
//			FutureBarrier<IComponentIdentifier> bar = new FutureBarrier<IComponentIdentifier>();
//			for(IService ser: sers)
//			{
//				IProxyAgentService pas = (IProxyAgentService)ser;
//
//				bar.addFuture(pas.getRemoteComponentIdentifier());
////				if((""+query.getServiceType()).indexOf("AutoTerminate")!=-1)
////					System.out.println("searchRemoteServices0: "+pas+", "+query);
//			}
//			bar.waitForResultsIgnoreFailures(null).addResultListener(new IResultListener<Collection<IComponentIdentifier>>()
//			{
//				public void resultAvailable(Collection<IComponentIdentifier> result)
//				{
////					System.out.println("ask all by these: "+result);
//
////					if((""+query.getServiceType()).indexOf("AutoTerminate")!=-1)
////						System.out.println("searchRemoteServices00: "+result);
//					if(result != null && result.size() > 0)
//					{
//						FutureBarrier<Void> finishedbar = new FutureBarrier<Void>();
//						for(final IComponentIdentifier platid : result)
//						{
//							final Future<Collection<T>> remotesearch = new Future<Collection<T>>();
//							final ServiceQuery<T> remotequery = new ServiceQuery<T>(query);
//							// Disable filter, we do that locally.
//							remotequery.setFilter(null);
//
//							IComponentIdentifier origin	= IComponentIdentifier.LOCAL.get();
//							if(origin==null)	// TODO: shouldn't happen?
//								origin	= cid;
//							IComponentManagementService	cms	= getLocalServiceByClass(new ClassInfo(IComponentManagementService.class));
//							final IComponentIdentifier forigin = origin;
//							cms.getExternalAccess(origin).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Collection<T>>(remotesearch)
//							{
//								public void customResultAvailable(IExternalAccess result) throws Exception
//								{
//									try
//									{
////										if((""+query.getServiceType()).indexOf("AutoTerminate")!=-1)
////											System.out.println("searchRemoteServices1: "+result.getComponentIdentifier()+", "+platid+", "+query);
//										result.scheduleStep(new IComponentStep<Collection<T>>()
//										{
//											public IFuture<Collection<T>> execute(IInternalAccess ia)
//											{
//												return ((IInternalRemoteExecutionFeature)ia.getComponentFeature(IRemoteExecutionFeature.class))
//													.executeRemoteSearch(platid, adaptQuery(query, platid));
//											}
//										}).addResultListener(new DelegationResultListener<Collection<T>>(remotesearch)
//										{
//											@Override
//											public void customResultAvailable(Collection<T> result)
//											{
////												if((""+query.getServiceType()).indexOf("ISuper")!=-1)
////													System.out.println("Remote results: "+result);
//												super.customResultAvailable(result);
//											}
//
//											public void exceptionOccurred(Exception exception)
//											{
////												exception.printStackTrace();
//												super.exceptionOccurred(exception);
//											}
//										});
//									}
//									catch(Exception e)
//									{
//										System.out.println("cannot access: "+forigin);
//										remotesearch.setResult(null);
//									}
//								}
//							});
//
//							final Future<Void> remotefin = new Future<Void>();
//
//							remotesearch.addResultListener(new IResultListener<Collection<T>>()
//							{
//								@SuppressWarnings("unchecked")
//								public void resultAvailable(Collection<T> result)
//								{
//									if(result != null)
//									{
//										if(query.getFilter() instanceof IAsyncFilter)
//										{
//											FutureBarrier<Boolean> filterbar = new FutureBarrier<Boolean>();
//											for(Iterator<T> it = result.iterator(); it.hasNext(); )
//											{
//												final T ser = it.next();
//												IFuture<Boolean> filterfut = ((IAsyncFilter<T>)query.getFilter()).filter(ser);
//												filterfut.addResultListener(new IResultListener<Boolean>()
//												{
//													public void resultAvailable(Boolean result)
//													{
//														if(Boolean.TRUE.equals(result))
//															ret.addIntermediateResultIfUndone(ser);
//													}
//
//													public void exceptionOccurred(Exception exception)
//													{
//													}
//												});
//												filterbar.addFuture(filterfut);
//												filterbar.waitForIgnoreFailures(null).addResultListener(new DelegationResultListener<Void>(remotefin));
//											}
//										}
//										else
//										{
//											for(Iterator<T> it = result.iterator(); it.hasNext(); )
//											{
//												T ser = it.next();
//												if(query.getFilter() == null || ((IFilter<T>)query.getFilter()).filter(ser))
//												{
//													ret.addIntermediateResultIfUndone(ser);
//												}
//											}
//											remotefin.setResult(null);
//										}
//									}
//									else
//									{
//										remotefin.setResult(null);
//									}
//								}
//
//								public void exceptionOccurred(Exception exception)
//								{
////									if((""+query.getServiceType()).indexOf("LocalService")!=-1)
////									{
////										System.out.println("searchRemoteServices ex: "+exception+", "+query);
////									}
//									remotefin.setResult(null);
//								}
//							});
//							finishedbar.addFuture(remotefin);
//						}
//						finishedbar.waitForIgnoreFailures(null).addResultListener(new IResultListener<Void>()
//						{
//							public void resultAvailable(Void result)
//							{
//								ret.setFinishedIfUndone();
//							}
//
//							public void exceptionOccurred(Exception exception)
//							{
////									ret.setFinished();
//							}
//						});
//					}
//					else
//					{
//						ret.setFinishedIfUndone();
//					}
//				}
//
//				public void exceptionOccurred(Exception exception)
//				{
//					// wait ignore failures
////					ret.setExceptionIfUndone(exception);
//				}
//			});
//		}
//		else
//		{
//			ret.setFinished();
//		}
//		return ret;
//	}
	
	/**
	 *  Searches for a service by class in local registry.
	 */
//	@SuppressWarnings("unchecked")
	protected <T> T getLocalServiceByClass(ClassInfo clazz)
	{
		rwlock.readLock().lock();
		T ret = null;
		try
		{
			Set<IService> servs = indexer.getValues(ServiceKeyExtractor.KEY_TYPE_INTERFACE, clazz.getGenericTypeName());
			if (servs != null && servs.size() > 0)
				ret = (T)servs.iterator().next();
		}
		finally
		{
			rwlock.readLock().unlock();
		}
		return ret;
	}
	
	/**
	 *  Searches for services by class in local registry.
	 */
//	@SuppressWarnings("unchecked")
	protected <T> Collection<T> getLocalServicesByClass(ClassInfo clazz)
	{
		rwlock.readLock().lock();
		Set<T> ret = null;
		try
		{
			ret = (Set<T>)indexer.getValues(ServiceKeyExtractor.KEY_TYPE_INTERFACE, clazz.getGenericTypeName());
		}
		finally
		{
			rwlock.readLock().unlock();
		}
		return ret;
	}
	
	/**
	 *  Wait for delay and execute runnable.
	 */
	protected void waitForDelay(long delay, final Runnable run)
	{
		if(timer==null)
			timer = new Timer(true);
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				run.run();
			}
		}, delay);
	}
	
//	/**
//	 *  Listener that forwards only results and ignores finished / exception.
//	 */
//	public static class UnlimitedIntermediateDelegationResultListener<E> implements IIntermediateResultListener<E>
//	{
//		/** The delegate future. */
//		protected IntermediateFuture<E> delegate;
//		
//		public UnlimitedIntermediateDelegationResultListener(IntermediateFuture<E> delegate)
//		{
//			this.delegate = delegate;
//		}
//		
//		public void intermediateResultAvailable(E result)
//		{
//			delegate.addIntermediateResultIfUndone(result);
//		}
//
//		public void finished()
//		{
//			// the query is not finished after the status quo is delivered
//		}
//
//		public void resultAvailable(Collection<E> results)
//		{
//			for(E result: results)
//			{
//				intermediateResultAvailable(result);
//			}
//			// the query is not finished after the status quo is delivered
//		}
//		
//		public void exceptionOccurred(Exception exception)
//		{
//			// the query is not finished after the status quo is delivered
//		}
//	}
	
	/**
	 *  Checks the following:
	 *  a) search scope
	 *  b) publication scope
	 *  c) filter
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
			
			IService ser = (IService)obj;

			// checkPublicationScope() is used 6 times, 5 times with getOwner(), only here with getProvider().
			// TODO: Decide on search semantics with provider being set. And do not use getProvider() unconditionally!
			// if (!(checkSearchScope(query.getOwner(), ser, query.getScope(), false) && checkPublicationScope(query.getProvider(), ser)))
			if(!(checkSearchScope(query.getOwner(), ser, query.getScope(), false) && checkPublicationScope(query.getOwner(), ser)))
			{
				fret.setResult(Boolean.FALSE);
			}
			else if(query.getFilter() instanceof IAsyncFilter)
			{
				((IAsyncFilter<T>) query.getFilter()).filter(obj).addResultListener(new DelegationResultListener<Boolean>(fret));
			}
			else if(query.getFilter() instanceof IFilter)
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

	/**
	 *  Get the indexer.
	 *  @return the indexer
	 */
	public Indexer<IService> getIndexer()
	{
		return indexer;
	}

	/**
	 *  Perform a remote search via remote search command.
	 *  @param agent
	 */
	protected <T> ISubscriptionIntermediateFuture<T> performRemoteSearchServices(IInternalAccess agent, ServiceQuery<T> query, IComponentIdentifier cid)
	{
		IComponentIdentifier tcid = cid!=null? cid: query.getTargetPlatform();
		// HACK!!! TODO: probably not subscription for search anyways???
		return (ISubscriptionIntermediateFuture<T>)((IInternalRemoteExecutionFeature)agent.getComponentFeature(IRemoteExecutionFeature.class))
			.executeRemoteSearch(tcid, query);
	}

	/**
	 *  Perform a remote search via remote search command.
	 *  @param agent
	 */
	protected <T> IFuture<T> performRemoteSearchService(IInternalAccess agent, final ServiceQuery<T> query, IComponentIdentifier cid)
	{
		final Future<T> ret = new Future<T>();

		IComponentIdentifier tcid = cid!=null? cid: query.getTargetPlatform();

//		if(query.getPlatform()!=null && !query.getOwner().getRoot().equals(query.getPlatform()))
//			System.out.println("sdgfsdfgjsdfj");

		((IInternalRemoteExecutionFeature)agent.getComponentFeature(IRemoteExecutionFeature.class))
			.executeRemoteSearch(tcid, query).addResultListener(new ExceptionDelegationResultListener<Collection<T>, T>(ret)
		{
			@Override
			public void customResultAvailable(Collection<T> result) throws Exception
			{
				// ???
//				assert result!=null && !result.isEmpty();
				if(result==null || result.size()==0)
					ret.setException(new ServiceNotFoundException(""+query.getServiceType()));
				else
					ret.setResult(result.iterator().next());
			}
		});

		return ret;
	}

	/**
	 *  Adapt the query to be performed with platform scope on remote platform.
	 */
	public <T> ServiceQuery<T> adaptQuery(ServiceQuery<T> query, IComponentIdentifier targetpl)
	{
		/*IComponentIdentifier target = targetpl!=null? targetpl: query.getTargetPlatform();

		ServiceQuery<T> ret = new ServiceQuery<T>(query);
		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(ret.getScope()))
			ret.setScope(RequiredServiceInfo.SCOPE_PLATFORM);
		if(ret.getOwner()!=null && !target.equals(ret.getOwner().getRoot()))
			ret.setOwner(target);
		return ret;*/
		return query;
	}

	/**
	 *  Get the superpeer.
	 *  @return the superpeer.
	 */
	public synchronized IComponentIdentifier getSuperpeer()
	{
		return superpeer;
	}

	/**
	 *  Set the superpeer.
	 *  @param superpeer The superpeer to set.
	 */
	public synchronized void setSuperpeer(IComponentIdentifier superpeer)
	{
		// TODO: Shouldn't be called w/o change?
		if(!SUtil.equals(this.superpeer, superpeer))
		{
			this.superpeer = superpeer;
			addQueriesToNewSuperpeer();
		}
	}

	/**
	 *  Enhance a query before processing.
	 *  Does some necessary preprocessing and needs to be called at least once before processing the query,
	 *  e.g. at start of a registry method or before sending a remote query.
	 *  Safe to be called multiple times, only the first time will have effect.
	 *  @param query	The query to be enhanced.
	 */
	protected <T> void	enhanceQuery(ServiceQuery<T> query)
	{
		// Only enhance remote queries
		// TODO: more extensible way of checking for remote query
		if(query.isRemote())
		{
			// Network names not set by user?
			if(Arrays.equals(query.getNetworkNames(), ServiceQuery.NETWORKS_NOT_SET))
			{
				// Unrestricted?
				if(Boolean.TRUE.equals(query.isUnrestricted())
					|| query.getServiceType()!=null && ServiceIdentifier.isUnrestricted(ExecutionComponentFeature.LOCAL.get(), query.getServiceType().getType0()))	// hack!!! Exefeature.LOCAL should be right, but is there a better way? 
				{
					// Unrestricted -> Don't check networks.
					query.setNetworkNames(null);
				}
				else
				{
					// Not unrestricted -> only find services from my local networks
					query.setNetworkNames(ServiceQuery.getNetworkNames(cid));
				}
			}
		}
		
		// Disable local checks by default
		else if(Arrays.equals(query.getNetworkNames(), ServiceQuery.NETWORKS_NOT_SET))
		{
			query.setNetworkNames(null);
		}			
	}
}
