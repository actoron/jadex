package jadex.bridge.service.search;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.IAsyncFilter;
import jadex.commons.IFilter;
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

/**
 *  This class contains default search functionalities for services.
 *  
 *  It also contains search based implementation of the global search.
 */
public class RegistrySearchFunctionality
{
	/** Interface to the data of the registry. */
	protected IRegistryDataProvider provider;
	
	/**
	 *  Create a new search func.
	 */
	public RegistrySearchFunctionality(IRegistryDataProvider provider)
	{
		this.provider = provider;
	}
	
	/**
	 *  Get services per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return First matching service or null.
	 */
	// read
	protected Iterator<IService> getServices(ClassInfo type)
	{
		return provider.getServices(type);
	}
	
	/**
	 *  Get queries per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return The queries.
	 */
	// read
	protected <T> Set<ServiceQueryInfo<T>> getQueries(ClassInfo type)
	{
		return provider.getQueries(type);
	}
	
	/**
	 *  Get services per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return First matching service or null.
	 */
	// read
	protected Iterator<IService> getServices(Class<?> type)
	{
		return getServices(type==null? null: new ClassInfo(type));
	}
	
	/**
	 *  Search for services.
	 */
	// read
	public <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope)
	{
		if(type!=null && type.getName().indexOf("IRegistrySer")!=-1)
			System.out.println("search: "+type+" - "+cid);
		
		T ret = null;
		Iterator<IService> sers = getServices(type);
		if(sers!=null && sers.hasNext() && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
		{
			while(sers.hasNext())
			{
				IService ser = sers.next();
				if(checkSearchScope(cid, ser, scope) && checkPublicationScope(cid, ser))
				{
//					if(ret!=null)
//						System.out.println("found another: "+ser.getServiceIdentifier());
					
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
	// read
	public <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope)
	{
		Set<T> ret = null;
		Iterator<IService> sers = getServices(type);
		if(sers!=null && sers.hasNext() && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
		{
			ret = new HashSet<T>();
			while(sers.hasNext())
			{
				IService ser = sers.next();
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
	// read
	public <T> T searchService(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		T ret = null;
		
		if(type!=null && type.getName().indexOf("IRegistrySer")!=-1)
			System.out.println("search: "+type+" - "+cid);
		
		Iterator<T> sers = (Iterator<T>)getServices(type);
		if(sers!=null && sers.hasNext() && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
		{
			while(sers.hasNext())
			{
				T ser = sers.next();
				if(checkSearchScope(cid, (IService)ser, scope) && checkPublicationScope(cid, (IService)ser))
				{
					try
					{
						if(filter==null || filter.filter(ser))
						{
							if(ret!=null)
								System.out.println("found another: "+((IService)ser).getServiceIdentifier());
							
							ret = ser;
							//break;
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
	// read
	public <T> Collection<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IFilter<T> filter)
	{
		List<T> ret = new ArrayList<T>();
		
		if(type!=null && type.toString().indexOf("Factory")!=-1)
			System.out.println("sdfsdf");
		
		Iterator<T> sers = (Iterator<T>)getServices(type);
		if(sers!=null && sers.hasNext() && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
		{
			while(sers.hasNext())
			{
				T ser = sers.next();
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
	// read
	public <T> IFuture<T> searchService(final Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		final Future<T> ret = new Future<T>();
				
//		if(type!=null && type.getName().indexOf("IRegistrySer")!=-1)
//			System.out.println("search: "+type+" - "+cid);
		
//		if(RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
//			System.out.println("global search");
		
		Iterator<T> sers = (Iterator<T>)getServices(type);
		if(sers!=null && sers.hasNext() && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
		{
			Collection<T> ssers = checkScope(sers, cid, scope, true);
			
			checkAsyncFilters(filter, ssers.iterator()).addResultListener(new IIntermediateResultListener<T>()
			{
				public void intermediateResultAvailable(T result)
				{
					ret.setResult(result);
				}
				
				public void finished()
				{
					ret.setExceptionIfUndone(new ServiceNotFoundException(type.toString()));
				}
				
				public void resultAvailable(Collection<T> result)
				{
					for(T t: result)
					{
						intermediateResultAvailable(t);
					}
					finished();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setExceptionIfUndone(exception);
				}
			});
			
			// filter checks in loop are possibly performed outside of synchronized block
//				Iterator<T> it = new HashSet<T>(sers).iterator();
//			searchLoopService(filter, sers, cid, scope).addResultListener(new DelegationResultListener<T>(ret));
		}
		else
		{
			ret.setException(new ServiceNotFoundException(type.getName()));
		}
		
		return ret;
	}
	
//	/**
//	 * 
//	 * @param filter
//	 * @param it
//	 * @return
//	 */
//	// read
//	protected <T> IFuture<T> searchLoopService(final IAsyncFilter<T> filter, final Iterator<T> it, final IComponentIdentifier cid, final String scope)
//	{
//		final Future<T> ret = new Future<T>();
//		
//		if(it.hasNext())
//		{
//			final T ser = it.next();
//			if(!checkSearchScope(cid, (IService)ser, scope) || !checkPublicationScope(cid, (IService)ser))
//			{
//				searchLoopService(filter, it, cid, scope).addResultListener(new DelegationResultListener<T>(ret));
//			}
//			else
//			{
//				if(filter==null)
//				{
//					ret.setResult(ser);
//				}
//				else
//				{
//					filter.filter(ser).addResultListener(new IResultListener<Boolean>()
//					{
//						public void resultAvailable(Boolean result)
//						{
//							if(result!=null && result.booleanValue())
//							{
//								ret.setResult(ser);
//							}
//							else
//							{
//								searchLoopService(filter, it, cid, scope).addResultListener(new DelegationResultListener<T>(ret));
//							}
//						}
//						
//						public void exceptionOccurred(Exception exception)
//						{
//							searchLoopService(filter, it, cid, scope).addResultListener(new DelegationResultListener<T>(ret));
//						}
//					});
//				}
//			}
//		}
//		else
//		{
//			ret.setException(new ServiceNotFoundException("No service that fits filter: "+filter));
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Search for services.
	 */
	// read
	public <T> ISubscriptionIntermediateFuture<T> searchServices(Class<T> type, IComponentIdentifier cid, String scope, IAsyncFilter<T> filter)
	{
		try
		{
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
		
		Iterator<T> sers = (Iterator<T>)getServices(type);
		if(sers!=null && sers.hasNext() && !RequiredServiceInfo.SCOPE_NONE.equals(scope))
		{
			Collection<T> ssers = checkScope(sers, cid, scope, false);
			
			checkAsyncFilters(filter, ssers.iterator()).addResultListener(new IntermediateDelegationResultListener<T>(ret));
			
			// filter checks in loop are possibly performed outside of synchornized block
//			searchLoopServices(filter, sers, cid, scope).addResultListener(new IntermediateDelegationResultListener<T>(ret));
		}
		else
		{
			ret.setFinished();
		}
		return ret;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
//	/**
//	 * 
//	 * @param filter
//	 * @param it
//	 * @return
//	 */
//	// read
//	protected <T> ISubscriptionIntermediateFuture<T> searchLoopServices(final IAsyncFilter<T> filter, final Iterator<T> it, final IComponentIdentifier cid, final String scope)
//	{
//		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<T>();
//		
////		System.out.println("searchLoopStart: "+it.hashCode());
//		
//		if(it.hasNext())
//		{
//			final T ser = it.next();
//			if(!checkSearchScope(cid, (IService)ser, scope) || !checkPublicationScope(cid, (IService)ser))
//			{
//				searchLoopServices(filter, it, cid, scope).addResultListener(new IntermediateDelegationResultListener<T>(ret));
//			}
//			else
//			{
//				if(filter==null)
//				{
//					ret.addIntermediateResult(ser);
//					searchLoopServices(filter, it, cid, scope).addResultListener(new IntermediateDelegationResultListener<T>(ret));
//				}
//				else
//				{
//					filter.filter(ser).addResultListener(new IResultListener<Boolean>()
//					{
//						public void resultAvailable(Boolean result)
//						{
//							if(result!=null && result.booleanValue())
//							{
//								ret.addIntermediateResult(ser);
//							}
//							searchLoopServices(filter, it, cid, scope).addResultListener(new IntermediateDelegationResultListener<T>(ret));
//						}
//						
//						public void exceptionOccurred(Exception exception)
//						{
//							searchLoopServices(filter, it, cid, scope).addResultListener(new IntermediateDelegationResultListener<T>(ret));
//						}
//					});
//				}
//			}
//		}
//		else
//		{
////			System.out.println("searchLoopEnd");
//			ret.setFinished();
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Check the services according the the scope.
	 *  @param it The services.
	 *  @param cid The component id.
	 *  @param scope The scope.
	 *  @return The services that fit to the scope.
	 */
	// read
	protected <T> Collection<T> checkScope(final Iterator<T> it, final IComponentIdentifier cid, final String scope, boolean oneresult)
	{
		Collection<T> ret = new LinkedHashSet<T>();
		
		while(it.hasNext())
		{
			final T ser = it.next();
			if(checkSearchScope(cid, (IService)ser, scope) && checkPublicationScope(cid, (IService)ser))
			{
				ret.add(ser);
				if(oneresult)
					break;
			}
		}
		
		return ret;
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
	 *  Check the persistent queries for a new service.
	 *  @param ser The service.
	 */
	// read
	protected IFuture<Void> checkQueries(IService ser)
	{
		Future<Void> ret = new Future<Void>();
		
//		if(queries!=null)
//		{
			Set<ServiceQueryInfo<?>> sqis = (Set)getQueries(ser.getServiceIdentifier().getServiceType());
			
			if(sqis!=null)
			{
				// Clone the data to not need to synchronize async
				Set<ServiceQueryInfo<?>> clone = new HashSet<ServiceQueryInfo<?>>(sqis);
				
				checkQueriesLoop(clone.iterator(), ser).addResultListener(new DelegationResultListener<Void>(ret));
			}
			else
			{
				ret.setResult(null);
			}
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
	protected IFuture<Void> checkQueriesLoop(final Iterator<ServiceQueryInfo<?>> it, final IService service)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(it.hasNext())
		{
			final ServiceQueryInfo<?> sqi = it.next();
//			IComponentIdentifier cid = sqi.getQuery().getOwner();
//			String scope = sqi.getQuery().getScope();
//			IAsyncFilter<IService> filter = (IAsyncFilter)sqi.getQuery().getFilter();
			
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
	// read
	protected IFuture<Boolean> checkQuery(final ServiceQueryInfo<?> queryinfo, final IService service)
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
	 *  Check if service is ok with respect to search scope of caller.
	 */
	protected boolean checkSearchScope(IComponentIdentifier cid, IService ser, String scope)
	{
		boolean ret = false;
		
		if(!provider.isIncluded(cid, ser))
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
	
	
}
