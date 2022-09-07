package jadex.bridge.service.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;
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
	protected Indexer<IServiceIdentifier> indexer;
	
	/** The persistent service queries. */
	protected Indexer<ServiceQueryInfo<?>> queries;
	
	/** The excluded services cache. */
	protected Map<IComponentIdentifier, Set<IServiceIdentifier>> excludedservices;
	
	/** Read-Write Lock for proxy map. */
	protected ReadWriteLock proxyrwlock;
	
	/** Map for looking up local services using the service identifier. */
	protected Map<IServiceIdentifier, IService> localserviceproxies;
	
	/** The query change subscribers. */
	protected List<SubscriptionIntermediateFuture<QueryEvent>> querysubs;
	
	//-------- methods --------
	
	/**
	 *  Create a new registry.
	 */
	public ServiceRegistry()
	{
		this.rwlock = new ReentrantReadWriteLock(false);
		this.proxyrwlock = new ReentrantReadWriteLock(false);
		this.indexer = new Indexer<>(new ServiceKeyExtractor(), false, ServiceKeyExtractor.SERVICE_KEY_TYPES);
		this.queries = new Indexer<ServiceQueryInfo<?>>(new QueryInfoExtractor(), true, QueryInfoExtractor.QUERY_KEY_TYPES_INDEXABLE);
		this.localserviceproxies = new HashMap<>();
		this.querysubs = new ArrayList<>();
	}
	
	/**
	 *  Search for services.
	 */
	// read
//	@SuppressWarnings("unchecked")
	public IServiceIdentifier searchService(final ServiceQuery<?> query)
	{
//		if(query.toString().indexOf("ISecurity")!=-1)
//			System.out.println("sdgo");
		
		IServiceIdentifier ret = null;
		if(!ServiceScope.NONE.equals(query.getScope()))
		{
			Set<IServiceIdentifier> sers = getServices(query);
			
			if(query.toString().indexOf("IComponentFac")!=-1 && sers.size()==0)
				System.out.println("found: "+sers);
			
//			Set<IServiceIdentifier> ownerservices = null;
//			rwlock.readLock().lock();
//			try
//			{
//				ownerservices = query.isExcludeOwner()? indexer.getValues(ServiceKeyExtractor.KEY_TYPE_PROVIDER, query.getOwner().toString()) : null;
//			}
//			finally
//			{
//				rwlock.readLock().unlock();
//			}
			
			if(sers!=null && !sers.isEmpty())
			{
				for(IServiceIdentifier ser : sers)
				{
					if(checkRestrictions(query, ser))// &&
//					   (ownerservices == null || !ownerservices.contains(ser)))
					{
						ret = ser;
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
	public Set<IServiceIdentifier> searchServices(ServiceQuery<?> query)
	{
		Set<IServiceIdentifier> ret = null;
		if(!ServiceScope.NONE.equals(query.getScope()))
		{
			ret = getServices(query);
			
//			Set<IServiceIdentifier> ownerservices = null;
//			rwlock.readLock().lock();
//			try
//			{
//				ownerservices = query.isExcludeOwner()? indexer.getValues(ServiceKeyExtractor.KEY_TYPE_PROVIDER, query.getOwner().toString()) : null;
//			}
//			finally
//			{
//				rwlock.readLock().unlock();
//			}
			
			if(ret!=null && !ret.isEmpty())
			{
				for(Iterator<IServiceIdentifier> it = ret.iterator(); it.hasNext(); )
				{
					IServiceIdentifier ser = it.next();
					if(!checkRestrictions(query, ser)) // ||
//					   !(ownerservices == null || !ownerservices.contains(ser)))
					{
						it.remove();
					}
				}
			}
		}
		
		if (ret == null)
			ret = Collections.emptySet();
		
		return ret;
	}
	
	/**
	 *  Add a service to the registry.
	 *  @param service The service.
	 */
	// write
	public void addService(IServiceIdentifier service)
	{
		Lock lock = rwlock.writeLock();
		lock.lock();
		try
		{
			indexer.removeValue(service);
			
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
				// Downgrade to read lock.
				lock = rwlock.readLock();
				lock.lock();
				rwlock.writeLock().unlock();
				
				checkQueries(service, ServiceEvent.SERVICE_ADDED);
			}
		}
		finally
		{
			if(lock != null)
				lock.unlock();
		}
	}
	
	/**
	 *  Add a local service to the registry.
	 *  @param service The local service.
	 */
	// write
	public void addLocalService(IService service)
	{
		// TODO: Optimize write lock acquisition (done twice for local service)
		proxyrwlock.writeLock().lock();
		try
		{
			localserviceproxies.put(service.getServiceId(), service);
		}
		finally
		{
			proxyrwlock.writeLock().unlock();
		}
		
		addService(service.getServiceId());
	}
	
	/**
	 *  Updates a service if the service meta-information
	 *  has changes.
	 *  
	 *  @param service The service.
	 */
	public void updateService(IServiceIdentifier service)
	{
		if(service == null)
		{
			rwlock.writeLock().lock();
			Set<IServiceIdentifier> services = indexer.getAllValues();
			try
			{
				for(IServiceIdentifier ser : services)
				{
					indexer.removeValue(ser);
					indexer.addValue(ser);
				}
			}
			finally
			{
				rwlock.writeLock().unlock();
			}
			
			for(IServiceIdentifier ser : services)
				checkQueries(ser, ServiceEvent.SERVICE_CHANGED);
		}
		else
		{
			rwlock.writeLock().lock();
			try
			{
				indexer.removeValue(service);
				indexer.addValue(service);
			}
			finally
			{
				rwlock.writeLock().unlock();
			}
			
			checkQueries(service, ServiceEvent.SERVICE_CHANGED);
		}
	}
	
	/**
	 *  Updates a service if the service meta-information
	 *  has changes.
	 *  
	 *  @param service The service (null = all).
	 */
	public void updateService(IServiceIdentifier service, String propname)
	{
		if(indexer.isIndexed(propname) && service==null)
		{
			rwlock.writeLock().lock();
			Set<IServiceIdentifier> services = indexer.getAllValues();
			try
			{
				indexer.updateIndex(propname);
			}
			finally
			{
				rwlock.writeLock().unlock();
			}
			
			// todo: get only changed?!
			for(IServiceIdentifier ser : services)
				checkQueries(ser, ServiceEvent.SERVICE_CHANGED);
		}
		else
		{
			updateService(service);
		}
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	// write
	public void removeService(IServiceIdentifier service)
	{
		//System.out.println("removing service: "+service);
		
		Lock lock = rwlock.writeLock();
		lock.lock();
		try
		{
			indexer.removeValue(service);
			
			// Downgrade to read lock.
			lock = rwlock.readLock();
			lock.lock();
			rwlock.writeLock().unlock();
			
			checkQueries(service, ServiceEvent.SERVICE_REMOVED);
		}
		finally
		{
				lock.unlock();
		}
		
		proxyrwlock.writeLock().lock();
		try
		{
			localserviceproxies.remove(service);
		}
		finally
		{
			proxyrwlock.writeLock().unlock();
		}
	}
	
	/**
	 *  Remove services of a platform from the registry.
	 *  @param platform The platform, null for everything.
	 */
	// write
	public void removeServices(IComponentIdentifier platform)
	{
		Set<IServiceIdentifier> pservs = null;
		
		Lock lock = rwlock.writeLock();
		lock.lock();
		try
		{
			if (platform == null)
				pservs = indexer.getAllValues();
			else
				pservs = indexer.getValues(ServiceKeyExtractor.KEY_TYPE_PLATFORM, platform.toString());
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
		
		if (pservs != null && pservs.size() > 0)
		{
			proxyrwlock.writeLock().lock();
			try
			{
				for (IServiceIdentifier serv : pservs)
					localserviceproxies.remove(serv);
			}
			finally
			{
				proxyrwlock.writeLock().unlock();
			}
		}
	}
	
	/**
	 *  Remove services of a platform from the registry.
	 *  @param platform The platform.
	 */
	// write
	/*public void removeServicesExcept(IComponentIdentifier platform)
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
	}*/
	
	/** 
	 *  Returns the service proxy of a local service identified by service ID.
	 *  
	 *  @param serviceid The service ID.
	 *  @return The service proxy.
	 */
	public IService getLocalService(IServiceIdentifier serviceid)
	{
		IService ret = null;
		proxyrwlock.readLock().lock();
		try
		{
			ret = localserviceproxies.get(serviceid);
		}
		finally
		{
			proxyrwlock.readLock().unlock();
		}
		return ret;
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
	public Set<ServiceQueryInfo<?>> getAllQueries()
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
	 *  Returns the lock on the registry.
	 *  Care must be taken to perform proper unlocking
	 *  to avoid permanently blocking the registry.
	 *  Note that the lock is reentrant, so operations
	 *  can be performed while the lock is held.
	 *  
	 *  @return The registry lock.
	 */
	public ReadWriteLock getLock()
	{
		return rwlock;
	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	@SuppressWarnings({ "rawtypes" })
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> fut = new SubscriptionIntermediateFuture<>();
		if(query.getOwner()==null)
		{
			fut.setException(new IllegalArgumentException("Query owner must not null: "+query));
		}
		else
		{
			ServiceQueryInfo<T> ret = null;
			
			fut.setTerminationCommand(new TerminationCommand()
			{
				public void terminated(Exception reason)
				{
					removeQuery(query);
				}
			});
			
			rwlock.writeLock().lock();
			Set<IServiceIdentifier> sers = null;
			try
			{
				ret = new ServiceQueryInfo<T>((ServiceQuery<T>) query, fut);
				queries.addValue((ServiceQueryInfo)ret);
				
				// We need the write lock during read for consistency
				// This works because rwlock is reentrant.
				// deliver currently available services
				sers = (Set<IServiceIdentifier>)getServices(query);
			}
			finally
			{
				rwlock.writeLock().unlock();
			}
			
			for(IServiceIdentifier ser : SUtil.notNull(sers))
			{
				if(checkRestrictions(query, ser))
				{
					dispatchQueryEvent(ret, ser, ServiceEvent.SERVICE_ADDED);
				}
			}
		}
		
		notifyQueryListeners(new QueryEvent(query, QueryEvent.QUERY_ADDED));
		
		return fut;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	// write
	public void removeQuery(final ServiceQuery<?> query)
	{
		rwlock.writeLock().lock();
		try
		{
			Set<ServiceQueryInfo<?>> qi = queries.getValues(QueryInfoExtractor.KEY_TYPE_ID, query.getId());
			if(qi!=null)
			{
				queries.removeValue(qi.iterator().next());
			}
			else
			{
				System.out.println("query not found for removeQuery(): "+query.getId());
			}
		}
		finally
		{
			rwlock.writeLock().unlock();
		}
		
		//if(query.getOwner().toString().indexOf("jccweb")!=-1 && !ServiceScope.PLATFORM.equals(query.getScope()))
		//	System.out.println("query removed: "+query);
		notifyQueryListeners(new QueryEvent(query, QueryEvent.QUERY_REMOVED));
	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	// write
	public void removeQueries(IComponentIdentifier owner)
	{
		rwlock.writeLock().lock();
		try
		{
			Set<ServiceQueryInfo<?>> qs = queries.getValues(QueryInfoExtractor.KEY_TYPE_OWNER, owner.toString());
			if(qs!=null)
			{
				for(ServiceQueryInfo<?> q: qs)
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
	 *  Remove all service queries of a specific platform from the registry.
	 *  @param platform The platform from which the query owner comes.
	 */
	// write
	public void removeQueriesOfPlatform(IComponentIdentifier platform)
	{
		rwlock.writeLock().lock();
		Set<ServiceQueryInfo<?>> qinfos = new HashSet<ServiceQueryInfo<?>>();
		try
		{
			// todo: Should use index to find all services of a platform
			Set<ServiceQueryInfo<?>> qis = queries.getValues(QueryInfoExtractor.KEY_TYPE_OWNER_PLATORM, platform.toString());
			
			if(qis!=null)
			{
				for(ServiceQueryInfo<?> sqi: qis)
				{
					queries.removeValue(sqi);
					qinfos.add(sqi);
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
		Lock lock = rwlock.writeLock();
		lock.lock();
		try
		{
			if(excludedservices!=null)
			{
				Set<IServiceIdentifier> exs = excludedservices.remove(cid);
				
				lock.unlock();
				lock = null;
				
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
			if (lock != null)
				lock.unlock();
		}
	}
	
	/**
	 *  Test if a service is visible, i.e. visible after init for all components or visible for subcomponents also during init.
	 *  @param query The query.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	// read
	protected boolean checkLifecycleVisibility(ServiceQuery<?> query, IServiceIdentifier ser)
	{
		boolean inited;
		IComponentIdentifier target = ser.getProviderId();
		rwlock.readLock().lock();
		try
		{
			inited	= excludedservices == null || !excludedservices.containsKey(target);
		}
		finally
		{
			rwlock.readLock().unlock();
		}
		
		return inited || getDotName(query.getOwner()).endsWith(getDotName(target));
	}
	
	/**
	 *  Check the persistent queries for a new service.
	 *  @param ser The service.
	 *  @param removed Indicates if the query was removed. 
	 */
	// read
	protected IFuture<Void> checkQueries(IServiceIdentifier ser, int eventtype)
	{
		Future<Void> ret = new Future<Void>();
		
		List<Tuple2<String, String[]>> spec = null;
		Set<ServiceQueryInfo<?>> sqis = null;
		rwlock.readLock().lock();
		try
		{
			spec = ((QueryInfoExtractor)queries.getKeyExtractor()).getIndexerSpec(ser);
			
			sqis = queries.getValuesInverted(spec);
		}
		finally
		{
			rwlock.readLock().unlock();
		}
		
//		Set<ServiceQueryInfo<?>> r1 = queries.getValues(QueryInfoExtractor.KEY_TYPE_INTERFACE, ser.getServiceType().toString());
//		Set<ServiceQueryInfo<?>> r2 = queries.getValues(QueryInfoExtractor.KEY_TYPE_INTERFACE, "null");
//		Set<ServiceQueryInfo<?>> sqis = r1;
//		if(sqis!=null && r2!=null)
//			sqis.addAll(r2);	// safe because indexer returns copy
//		else if(r2!=null)
//			sqis=r2;
//		
		if(sqis!=null)
		{
			for(ServiceQueryInfo<?> sqi : sqis)
			{
				ServiceQuery<?> query = sqi.getQuery();
				
				//ServiceEvent.CLASSINFO.getTypeName().equals(query.getReturnType().getTypeName()));
				if(checkRestrictions(query, ser))
				{
					dispatchQueryEvent(sqi, ser, eventtype);
				}
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	protected void dispatchQueryEvent(ServiceQueryInfo<?> queryinfo, IServiceIdentifier ser, int eventtype)
	{
		ServiceQuery<?> query = queryinfo.getQuery();
		if(query.isEventMode())
		{
			ServiceEvent event = new ServiceEvent(ser, eventtype);
			((TerminableIntermediateFuture<ServiceEvent>)queryinfo.getFuture()).addIntermediateResultIfUndone(event);
		}
		else if(ServiceEvent.SERVICE_ADDED==eventtype)
		{
			((TerminableIntermediateFuture<IServiceIdentifier>)queryinfo.getFuture()).addIntermediateResultIfUndone(ser);
		}
	}
	
	/**
	 *  Check the services according additional restrictions like scope.
	 *  @param query The query.
	 *  @param ser The service.
	 *  @return True, if service passes tests.
	 */
	protected boolean checkRestrictions(ServiceQuery<?> query, final IServiceIdentifier ser)
	{
		return !(query.isExcludeOwner() && query.getOwner().equals(ser.getProviderId())) 
			&& checkSearchScope(query, ser)
			&& checkPublicationScope(query, ser)
			&& checkLifecycleVisibility(query, ser);
	}
	
	/**
	 *  Check if service is ok with respect to search scope of caller.
	 */
	protected boolean checkSearchScope(ServiceQuery<?> query, IServiceIdentifier ser)
	{
		boolean ret = false;
		
		ServiceScope scope = query.getScope();
				
//		IComponentIdentifier searchstart	= query.getProvider()!=null ? query.getProvider()
//			: query.getPlatform()!=null ? query.getPlatform() : query.getOwner();
		IComponentIdentifier searchstart = query.getSearchStart() != null ? query.getSearchStart() : query.getOwner();
		
		if(ServiceScope.GLOBAL.equals(scope))
		{
			ret = true;
		}
		else if(ServiceScope.NETWORK.equals(scope))
		{
			// todo: fixme
			ret = true;
		}
		else if(ServiceScope.APPLICATION_GLOBAL.equals(scope))
		{
			// todo: fixme
			ret = true;
		}
		else if(ServiceScope.APPLICATION_NETWORK.equals(scope))
		{
			// todo: fixme
			ret = true;
		}
		else if(ServiceScope.PLATFORM.equals(scope))
		{
			// Test if searcher and service are on same platform
			ret = searchstart.getPlatformName().equals(ser.getProviderId().getPlatformName());
		}
		else if(ServiceScope.APPLICATION.equals(scope))
		{
			IComponentIdentifier sercid = ser.getProviderId();
			ret = sercid.getPlatformName().equals(searchstart.getPlatformName())
				&& getApplicationName(sercid).equals(getApplicationName(searchstart));
		}
		else if(ServiceScope.COMPONENT.equals(scope))
		{
			IComponentIdentifier sercid = ser.getProviderId();
			ret = getDotName(sercid).endsWith(getDotName(searchstart));
		}
		else if(ServiceScope.COMPONENT_ONLY.equals(scope))
		{
			// only the component itself
			ret = ser.getProviderId().equals(searchstart);
		}
		else if(ServiceScope.PARENT.equals(scope))
		{
			// check if parent of searcher reaches the service
			IComponentIdentifier sercid = ser.getProviderId();
			String subname = getSubcomponentName(searchstart);
			ret = sercid.getName().endsWith(subname);
		}
		
		//if(query.getServiceType()!=null && query.getServiceType().toString().indexOf("Calc")!=-1)
		//	System.out.println("calc check: "+query.getOwner()+" "+scope+" "+ser+" "+ret);
		
		return ret;
	}
	
	/**
	 *  Check if service is ok with respect to publication scope.
	 */
	protected boolean checkPublicationScope(ServiceQuery<?> query, IServiceIdentifier ser)
	{
		boolean ret = false;
		
		ServiceScope scope = ser.getScope()!=null && !ServiceScope.DEFAULT.equals(ser.getScope())?
			ser.getScope(): ServiceScope.PLATFORM;
		
		if(ServiceScope.GLOBAL.equals(scope))
		{
			ret = true;
		}
		else if(ServiceScope.NETWORK.equals(scope))
		{
			// todo: fixme
			ret = true;
		}
		else if(ServiceScope.APPLICATION_GLOBAL.equals(scope))
		{
			// todo: fixme
			ret = true;
		}
		else if(ServiceScope.APPLICATION_NETWORK.equals(scope))
		{
			// todo: fixme
			ret = true;
		}
		else if(ServiceScope.PLATFORM.equals(scope))
		{
			// Test if searcher and service are on same platform
			ret = query.getOwner().getPlatformName().equals(ser.getProviderId().getPlatformName());
		}
		else if(ServiceScope.APPLICATION.equals(scope))
		{
			// todo: special case platform service with app scope
			IComponentIdentifier sercid = ser.getProviderId();
			ret = sercid.getPlatformName().equals(query.getOwner().getPlatformName())
				&& getApplicationName(sercid).equals(getApplicationName(query.getOwner()));
		}
		else if(ServiceScope.COMPONENT.equals(scope))
		{
			IComponentIdentifier sercid = ser.getProviderId();
			ret = getDotName(query.getOwner()).endsWith(getDotName(sercid));
		}
		else if(ServiceScope.COMPONENT_ONLY.equals(scope))
		{
			// only the component itself
			ret = ser.getProviderId().equals(query.getOwner());
		}
		else if(ServiceScope.PARENT.equals(scope))
		{
			//if(query.getServiceType()!=null && query.getServiceType().toString().indexOf("Calc")!=-1)
			//	System.out.println("found");
				
			// check if parent of service reaches the searcher
			IComponentIdentifier sercid = ser.getProviderId();
			String subname = getSubcomponentName(sercid);
			String owner = getDotName(query.getOwner());
			ret = owner.equals(subname) || owner.endsWith(":"+subname);
			// Must include delimiter to avoid special cases like
			// DistributedServicePoolAgent:MandelbrotAgent:DESKTOP-TMQFG7J_tyg
			// ServicePoolAgent:MandelbrotAgent:DESKTOP-TMQFG7J_tyg
			// delivering true
			
			//System.out.println("parent check: "+subname+" "+getDotName(query.getOwner())+" "+ret);
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
			Tuple2<Set<IServiceIdentifier>, Object> res = indexer.getValues(query.getIndexerSearchSpec());
			Set<IServiceIdentifier> ret = res==null? null: res.getFirstEntity();
			//if(res!=null && res.getFirstEntity()!=null && res.getFirstEntity().size()>0 && query.getServiceType()!=null && query.getServiceType().toString().indexOf("IComponentF")!=-1)
			//	System.out.println("micro kernel start bug: "+res.getSecondEntity());
			
			return ret;
		}
		finally
		{
			rwlock.readLock().unlock();
		}
		
		/*try
		{
			rwlock.readLock().tryLock(5, TimeUnit.SECONDS);
			Set<IServiceIdentifier> ret = indexer.getValues(query.getIndexerSearchSpec());
			return ret;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally
		{
			//System.out.println("getServices end: "+query);
			rwlock.readLock().unlock();
		}*/
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
		return getRegistry(ia.getId());
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
		if((idx = ret.lastIndexOf(':')) != -1)
		{
			// cut off platform name
			ret = ret.substring(0, idx);
			// cut off local name 
			if((idx = ret.indexOf('@'))!=-1)
				ret = ret.substring(idx + 1);
			if((idx = ret.indexOf(':'))!=-1)
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
	 *  Subscribe for query events.
	 */
	public ISubscriptionIntermediateFuture<QueryEvent> subscribeToQueries()
	{
		final SubscriptionIntermediateFuture<QueryEvent> fut = new SubscriptionIntermediateFuture<>();
		
		fut.setTerminationCommand(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				rwlock.writeLock().lock();
				try
				{
					querysubs.remove(fut);
					
				}
				finally
				{
					rwlock.writeLock().unlock();
				}
			}
		});
		
		rwlock.writeLock().lock();
		try
		{
			querysubs.add(fut);
			
		}
		finally
		{
			rwlock.writeLock().unlock();
		}
		
		Set<ServiceQueryInfo<?>> qs = getAllQueries();
		for(ServiceQueryInfo<?> qi : SUtil.notNull(qs))
		{
			QueryEvent event = new QueryEvent(qi.getQuery(), QueryEvent.QUERY_ADDED);
			fut.addIntermediateResultIfUndone(event);
		}
		
		return fut;
	}
	
	/**
	 *  Notify all listeners.
	 *  @param fut
	 *  @param type
	 */
	protected void notifyQueryListeners(QueryEvent event)
	{
		List<SubscriptionIntermediateFuture<QueryEvent>> qis;
		rwlock.writeLock().lock();
		try
		{
			qis = new ArrayList<>();
			qis.addAll(querysubs);
		}
		finally
		{
			rwlock.writeLock().unlock();
		}
		
		/*if(!ServiceScope.PLATFORM.equals(event.getQuery().getScope()))
			if(qis.size()==0)
				System.out.println("no query listener: "+event);
			else
				System.out.println("has query listener"+event);*/
		for(SubscriptionIntermediateFuture<QueryEvent> fut: qis)
		{
			fut.addIntermediateResultIfUndone(event);
		}
	}
	
	/**
	 *  Get the name without @ replaced by dot.
	 */
	public static String getDotName(IComponentIdentifier cid)
	{
		return cid.getName().replace('@', ':');
//		return cid.getParent()==null? cid.getName(): cid.getLocalName()+":"+getSubcomponentName(cid);
	}

	/**
	 *  Get the indexer.
	 *  @return the indexer
	 */
	protected Indexer<IServiceIdentifier> getIndexer()
	{
		return indexer;
	}
}
