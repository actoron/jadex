package jadex.bridge.service.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import jadex.base.PlatformConfiguration;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.commons.IAsyncFilter;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  Synchronized version of the multi service registry.
 */
public class SynchronizedMultiServiceRegistry extends MultiServiceRegistry
{
	/** The reader count. */
	protected int readercnt; 
	
	/** Flag that a writer is performing updates. */
	protected ReentrantLock lock = new ReentrantLock();
	
	protected List<IResultCommand<IFuture<Void>, Void>> writeactions = new ArrayList<IResultCommand<IFuture<Void>,Void>>();
	
	//-------- methods --------
	
	/**
	 *  Add a service to the registry.
	 *  @param sid The service id.
	 */
	public IFuture<Void> addService(final ClassInfo key, final IService service)
	{
		final Future<Void> ret = new Future<Void>();

		boolean schedule = true;
		
		if(lock.tryLock())
		{
			if(readercnt==0)
			{
				super.addService(key, service).addResultListener(new DelegationResultListener<Void>(ret));
				schedule = false;
			}
			
			// Unlock write lock after synchronous end of method (all modifications are done synchronously)
			// Will require a read lock for checks notifications (async part)
			lock.unlock();
		}
		
		if(schedule)
		{
			// schedule
			writeactions.add(new IResultCommand<IFuture<Void>, Void>()
			{
				public IFuture<Void> execute(Void args)
				{
					IFuture<Void> fut = SynchronizedMultiServiceRegistry.this.addService(key, service);
					fut.addResultListener(new DelegationResultListener<Void>(ret));
					return fut;
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Remove a service from the registry.
	 *  @param sid The service id.
	 */
	public void removeService(final ClassInfo key, final IService service)
	{
		boolean schedule = true;
		
		if(lock.tryLock())
		{
			if(readercnt==0)
			{
				super.removeService(key, service);
				schedule = false;
			}
			
			// Unlock write lock after synchronous end of method (all modifications are done synchronously)
			// Will require a read lock for checks notifications (async part)
			lock.unlock();
		}
		
		if(schedule)
		{
			// schedule
			writeactions.add(new IResultCommand<IFuture<Void>, Void>()
			{
				public IFuture<Void> execute(Void args)
				{
					SynchronizedMultiServiceRegistry.super.removeService(key, service);
					return IFuture.DONE;
				}
			});
		}
	}
	
	/**
	 *  Add a service query to the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(final ServiceQuery<T> query)
	{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		boolean schedule = true;
		
		if(lock.tryLock())
		{
			if(readercnt==0)
			{
				super.addQuery(query).addIntermediateResultListener(new IntermediateDelegationResultListener<T>(ret));
				schedule = false;
			}
			
			// Unlock write lock after synchronous end of method (all modifications are done synchronously)
			// Will require a read lock for checks notifications (async part)
			lock.unlock();
		}
		
		if(schedule)
		{
			// schedule
			writeactions.add(new IResultCommand<IFuture<Void>, Void>()
			{
				public IFuture<Void> execute(Void args)
				{
					ISubscriptionIntermediateFuture<T> fut = SynchronizedMultiServiceRegistry.super.addQuery(query);
					fut.addIntermediateResultListener(new IntermediateDelegationResultListener<T>(ret));
					
					final Future<Void> myret = new Future<Void>();
					fut.addIntermediateResultListener(new IIntermediateResultListener<T>()
					{
						public void finished()
						{
							myret.setResult(null);
						}
						public void exceptionOccurred(Exception exception)
						{
							myret.setException(exception);
						}
						public void intermediateResultAvailable(T result)
						{
						}
						public void resultAvailable(Collection<T> result)
						{
							finished();
						}
					});
					
					return myret;
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Remove a service query from the registry.
	 *  @param query ServiceQuery.
	 */
	public <T> void removeQuery(final ServiceQuery<T> query)
	{
		boolean schedule = true;
		
		if(lock.tryLock())
		{
			if(readercnt==0)
			{
				super.removeQuery(query);
				schedule = false;
			}
			
			// Unlock write lock after synchronous end of method (all modifications are done synchronously)
			// Will require a read lock for checks notifications (async part)
			lock.unlock();
		}
		
		if(schedule)
		{
			// schedule
			writeactions.add(new IResultCommand<IFuture<Void>, Void>()
			{
				public IFuture<Void> execute(Void args)
				{
					SynchronizedMultiServiceRegistry.super.removeQuery(query);
					return IFuture.DONE;
				}
			});
		}
	}
	
	/**
	 *  Check the persistent queries for a new service.
	 *  @param ser The service.
	 */
	protected IFuture<Void> checkQueries(IService ser)
	{
		readerInc();
			
//		lock("checkQuery");
		IFuture<Void> ret = super.checkQueries(ser);
		ret.addResultListener(new ReadDecListener<Void>());
		
		readerDec();
		
		return ret;
	}
	
//	/**
//	 *  Check the persistent queries against a new service.
//	 *  @param it The queries.
//	 *  @param service the service.
//	 */
//	protected IFuture<Void> checkQueriesLoop(final Iterator<ServiceQueryInfo<?>> it, final IService service)
//	{
//		return super.checkQueriesLoop(it, service);
//	}
	
	/**
	 *  Check a persistent query with one service.
	 *  @param queryinfo The query.
	 *  @param service The service.
	 *  @return True, if services matches to query.
	 */
	protected IFuture<Boolean> checkQuery(final ServiceQueryInfo<?> queryinfo, final IService service)
	{
		return super.checkQuery(queryinfo, service);
	}
	
	/**
	 *  Remove all service queries of a specific component from the registry.
	 *  @param owner The query owner.
	 */
	public void removeQueries(final IComponentIdentifier owner)
	{
		boolean schedule = true;
		
		if(lock.tryLock())
		{
			if(readercnt==0)
			{
				super.removeQueries(owner);
				schedule = false;
			}
			
			// Unlock write lock after synchronous end of method (all modifications are done synchronously)
			// Will require a read lock for checks notifications (async part)
			lock.unlock();
		}
		
		if(schedule)
		{
			// schedule
			writeactions.add(new IResultCommand<IFuture<Void>, Void>()
			{
				public IFuture<Void> execute(Void args)
				{
					SynchronizedMultiServiceRegistry.super.removeQueries(owner);
					return IFuture.DONE;
				}
			});
		}
	}
	
	/**
	 *  Search for services.
	 */
	public <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope)
	{
		readerInc();
		
		T ret = super.searchService(type, cid, scope);
	
		readerDec();
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope)
	{
		readerInc();
		
		Collection<T> ret = super.searchServices(type, cid, scope);
	
		readerDec();
		
		return ret;
	}
	
	/**
	 *  Search for service.
	 */
	public <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		readerInc();
		
		T ret = super.searchService(type, cid, scope, filter);
	
		readerDec();
		
		return ret;
	}
	
	/**
	 *  Search for service.
	 */
	public <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		readerInc();
		
		Collection<T> ret = super.searchServices(type, cid, scope, filter);
	
		readerDec();
		
		return ret;
	}
	
	/**
	 *  Search for service.
	 */
	public <T> IFuture<T> searchService(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		readerInc();
		
		IFuture<T> ret = super.searchService(type, cid, scope, filter);
		ret.addResultListener(new ReadDecListener<T>());
		
		return ret;
	}
	
//	/**
//	 *  Perform the search in a loop.
//	 *  @param filter The filter.
//	 *  @param it The iterator.
//	 *  @param cid The component id.
//	 *  @param scope The scope.
//	 */
//	protected <T> IFuture<T> searchLoopService(final IAsyncFilter<T> filter, final Iterator<T> it, final IComponentIdentifier cid, final String scope)
//	{
//		return super.searchLoopService(filter, it, cid, scope);
//	}
	
	/**
	 *  Search for services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		readerInc();
		
		ISubscriptionIntermediateFuture<T> ret = super.searchServices(type, cid, scope, filter);
		ret.addResultListener(new IntermediateReaderDecListener<T>());
			
		return ret;
	}
	
//	/**
//	 *  Perform the search in a loop.
//	 */
//	protected <T> ISubscriptionIntermediateFuture<T> searchLoopServices(final IAsyncFilter<T> filter, final Iterator<T> it, final IComponentIdentifier cid, final String scope)
//	{
//		return super.searchLoopServices(filter, it, cid, scope);
//	}
	
	/**
	 *  Search for services.
	 */
	public <T> IFuture<T> searchGlobalService(final Class<T> type, IComponentIdentifier cid, final IAsyncFilter<T> filter)
	{
		readerInc();
		
		IFuture<T> ret = super.searchGlobalService(type, cid, filter);
		ret.addResultListener(new ReadDecListener<T>());
		
		return ret;
	}
	
	/**
	 *  Search for services.
	 */
	public <T> ITerminableIntermediateFuture<T> searchGlobalServices(Class<T> type, IComponentIdentifier cid, IAsyncFilter<T> filter)
	{
		readerInc();
		
		ITerminableIntermediateFuture<T> ret = super.searchGlobalServices(type, cid, filter);
		ret.addResultListener(new IntermediateReaderDecListener<T>());
		
		return ret;
	}
	
	/**
	 *  Get the registry from a component.
	 */
	public static SynchronizedMultiServiceRegistry getRegistry(IComponentIdentifier platform)
	{
		return (SynchronizedMultiServiceRegistry)PlatformConfiguration.getPlatformValue(platform, PlatformConfiguration.DATA_SERVICEREGISTRY);
	}
	
	/**
	 *  Get the registry from a component.
	 */
	public static SynchronizedMultiServiceRegistry getRegistry(IInternalAccess ia)
	{
		return getRegistry(ia.getComponentIdentifier());
	}
	
	/**
	 * 
	 */
	protected void readerInc()
	{
//		System.out.println("readerInc: "+lock.isLocked()+" "+readercnt);
		
		// let readers wait until no write operation is performed
		lock.lock();
		readercnt++;
		lock.unlock();
	}
	
	/**
	 * 
	 */
	protected void readerDec()
	{
		// let readers wait until no write operation is performed
		lock.lock();
		readercnt--;
		
		if(readercnt==0)
			performWrites();
		
		lock.unlock();
	}
	
	/**
	 * 
	 */
	protected void performWrites()
	{
		// Write actions need to perform the writes synchroneously
		// If the methods are async only notifications/checks are allowed async
		
		for(IResultCommand<IFuture<Void>, Void> action: writeactions)
		{
			action.execute(null);
		}
		writeactions.clear();
	}
	
	/**
	 *  Listener to decrement the reader cnt.
	 */
	protected class ReadDecListener<T> implements IResultListener<T>
	{
		public void resultAvailable(T result)
		{
			readerDec();
		}
		
		public void exceptionOccurred(Exception exception)
		{
			readerDec();
		}
	}
	
	/**
	 *  Listener to decrement the reader cnt.
	 */
	protected class IntermediateReaderDecListener<T> implements IIntermediateResultListener<T>
	{
		public void intermediateResultAvailable(T result)
		{
		}
		
		public void finished()
		{
			readerDec();
		}
		
		public void exceptionOccurred(Exception exception)
		{
			readerDec();
		}
		
		public void resultAvailable(Collection<T> result)
		{
			readerDec();
		}
	}
}
