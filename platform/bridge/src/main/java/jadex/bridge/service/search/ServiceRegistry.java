package jadex.bridge.service.search;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.collection.BiHashMap;
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
	}
	
	/**
	 *  Search for services.
	 */
	// read
//	@SuppressWarnings("unchecked")
	public IServiceIdentifier searchService(final ServiceQuery<?> query)
	{
		IServiceIdentifier ret = null;
		if(!RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			Set<IServiceIdentifier> sers = getServices(query);
			
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
					if(checkScope(query, ser) &&
					   (ownerservices == null || !ownerservices.contains(ser)))
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
		if(!RequiredServiceInfo.SCOPE_NONE.equals(query.getScope()))
		{
			ret = getServices(query);
			
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
			
			if(ret!=null && !ret.isEmpty())
			{
				for(Iterator<IServiceIdentifier> it = ret.iterator(); it.hasNext(); )
				{
					IServiceIdentifier ser = it.next();
					if(!checkScope(query, ser) ||
					   !(ownerservices == null || !ownerservices.contains(ser)))
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
			localserviceproxies.put(service.getServiceIdentifier(), service);
		}
		finally
		{
			proxyrwlock.writeLock().unlock();
		}
		
		addService(service.getServiceIdentifier());
	}
	
	/**
	 *  Updates a service if the service meta-information
	 *  has changes.
	 *  
	 *  @param service The service.
	 */
	public void updateService(IServiceIdentifier service)
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
	 *  @param platform The platform.
	 */
	// write
	public void removeServices(IComponentIdentifier platform)
	{
		Set<IServiceIdentifier> pservs = null;
		
		Lock lock = rwlock.writeLock();
		lock.lock();
		try
		{
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
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	@SuppressWarnings({ "rawtypes" })
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> fut = new SubscriptionIntermediateFuture<>();
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
		
		for (Iterator<IServiceIdentifier> it = sers.iterator(); it.hasNext(); )
		{
			IServiceIdentifier ser = it.next();
			dispatchQueryEvent(ret, ser, ServiceEvent.SERVICE_ADDED);
		}
		
		return fut;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	// write
	protected void removeQuery(final ServiceQuery<?> query)
	{
		
		rwlock.writeLock().lock();
		try
		{
			Set<ServiceQueryInfo<?>> qi = queries.getValues(QueryInfoExtractor.KEY_TYPE_ID, query.getId());
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
	 *  Test if a service is included.
	 *  @param ser The service.
	 *  @return True if is included.
	 */
	// read
	protected boolean isIncluded(ServiceQuery<?> query, IServiceIdentifier ser)
	{
		boolean ret = true;
		rwlock.readLock().lock();
		try
		{
			IComponentIdentifier target = ser.getProviderId();
			if(target!=null)
				ret = getDotName(query.getOwner()).endsWith(getDotName(target));
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
	protected IFuture<Void> checkQueries(IServiceIdentifier ser, int eventtype)
	{
		Future<Void> ret = new Future<Void>();
		
		Set<ServiceQueryInfo<?>> r1 = queries.getValues(QueryInfoExtractor.KEY_TYPE_INTERFACE, ser.getServiceType().toString());
		Set<ServiceQueryInfo<?>> r2 = queries.getValues(QueryInfoExtractor.KEY_TYPE_INTERFACE, "null");
		Set<ServiceQueryInfo<?>> sqis = r1;
		if(sqis!=null && r2!=null)
			sqis.addAll(r2);	// safe because indexer returns copy
		else if(r2!=null)
			sqis=r2;
		
		if(sqis!=null)
		{
			for (ServiceQueryInfo<?> sqi : sqis)
			{
				ServiceQuery<?> query = sqi.getQuery();
				
				//ServiceEvent.CLASSINFO.getTypeName().equals(query.getReturnType().getTypeName()));
				if (checkScope(query, ser))
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
		if (ServiceEvent.CLASSINFO.getTypeName().equals(query.getReturnType().getTypeName()))
		{
			ServiceEvent<IServiceIdentifier> event = new ServiceEvent<>(ser, eventtype);
			((TerminableIntermediateFuture<ServiceEvent<IServiceIdentifier>>) queryinfo.getFuture()).addIntermediateResult(event);
		}
		else if (ServiceEvent.SERVICE_ADDED==eventtype)
		{
			((TerminableIntermediateFuture<IServiceIdentifier>) queryinfo.getFuture()).addIntermediateResult(ser);
		}
	}
	
	/**
	 *  Check the services according the the scope.
	 *  @param it The services.
	 *  @param cid The component id.
	 *  @param scope The scope.
	 *  @return The services that fit to the scope.
	 */
	protected boolean checkScope(ServiceQuery<?> query, final IServiceIdentifier ser)
	{
		return checkSearchScope(query, ser) && checkPublicationScope(query, ser);
	}
	
	/**
	 *  Check if service is ok with respect to search scope of caller.
	 */
	protected boolean checkSearchScope(ServiceQuery<?> query, IServiceIdentifier ser)
	{
		boolean ret = false;
		
		String scope = query.getScope();
		
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
			ret = query.getOwner().getPlatformName().equals(ser.getProviderId().getPlatformName());
		}
		else if(RequiredServiceInfo.SCOPE_APPLICATION.equals(scope))
		{
			IComponentIdentifier sercid = ser.getProviderId();
			ret = sercid.getPlatformName().equals(query.getOwner().getPlatformName())
				&& getApplicationName(sercid).equals(getApplicationName(query.getOwner()));
		}
		else if(RequiredServiceInfo.SCOPE_COMPONENT.equals(scope))
		{
			IComponentIdentifier sercid = ser.getProviderId();
			ret = getDotName(sercid).endsWith(getDotName(query.getOwner()));
		}
		else if(RequiredServiceInfo.SCOPE_COMPONENT_ONLY.equals(scope))
		{
			// only the component itself
			ret = ser.getProviderId().equals(query.getOwner());
		}
		else if(RequiredServiceInfo.SCOPE_PARENT.equals(scope))
		{
			// check if parent of searcher reaches the service
			IComponentIdentifier sercid = ser.getProviderId();
			String subname = getSubcomponentName(query.getOwner());
			ret = sercid.getName().endsWith(subname);
		}
		
		return ret;
	}
	
	/**
	 *  Check if service is ok with respect to publication scope.
	 */
	protected boolean checkPublicationScope(ServiceQuery<?> query, IServiceIdentifier ser)
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
		{
			// Test if searcher and service are on same platform
			ret = query.getOwner().getPlatformName().equals(ser.getProviderId().getPlatformName());
		}
		else if(RequiredServiceInfo.SCOPE_APPLICATION.equals(scope))
		{
			// todo: special case platform service with app scope
			IComponentIdentifier sercid = ser.getProviderId();
			ret = sercid.getPlatformName().equals(query.getOwner().getPlatformName())
				&& getApplicationName(sercid).equals(getApplicationName(query.getOwner()));
		}
		else if(RequiredServiceInfo.SCOPE_COMPONENT.equals(scope))
		{
			IComponentIdentifier sercid = ser.getProviderId();
			ret = getDotName(query.getOwner()).endsWith(getDotName(sercid));
		}
		else if(RequiredServiceInfo.SCOPE_COMPONENT_ONLY.equals(scope))
		{
			// only the component itself
			ret = ser.getProviderId().equals(query.getOwner());
		}
		else if(RequiredServiceInfo.SCOPE_PARENT.equals(scope))
		{
			// check if parent of service reaches the searcher
			IComponentIdentifier sercid = ser.getProviderId();
			String subname = getSubcomponentName(sercid);
			ret = getDotName(query.getOwner()).endsWith(subname);
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
		return getRegistry(ia.getIdentifier());
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
	protected Indexer<IServiceIdentifier> getIndexer()
	{
		return indexer;
	}
}
