package jadex.bridge.service.search;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.nonfunctional.search.IRankingSearchTerminationDecider;
import jadex.bridge.nonfunctional.search.IServiceRanker;
import jadex.bridge.nonfunctional.search.ServiceRankingDelegationResultListener;
import jadex.bridge.nonfunctional.search.ServiceRankingDelegationResultListener2;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.IRemoteFilter;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 *  Static helper class for searching services.
 */
public class SServiceProvider
{	
	/** The reference method cache (method -> boolean[] (is reference)). */
	public static final Map methodreferences = Collections.synchronizedMap(new LRU(500));
	
	//-------- methods --------

	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IServiceProvider provider, Class<T> type)
	{
		return getService(provider, type, null);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(final IServiceProvider provider, final Class<T> type, final String scope)
	{
		return getService(provider, type, scope, (IRemoteFilter<T>)null);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(final IServiceProvider provider, final Class<T> type, final String scope, final IRemoteFilter<T> filter)
	{
		final Future ret = new Future();

		if(type==null)
		{
			ret.setException(new IllegalArgumentException("Type must not null."));
			return ret;
		}
		
//		if(type.toString().indexOf("IComponentM")!=-1 && scope.equals("upwards"))
//			System.out.println("here22");
		
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, Integer.valueOf(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		
		// Hack->remove
//		IVisitDecider abortdecider = new DefaultVisitDecider();
//		IVisitDecider rabortdecider = new DefaultVisitDecider(true, false);
		
		if(provider instanceof IServiceContainer)
		{
			IServiceContainer container = (IServiceContainer)provider;
			if(!RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			{
				T ser = container.getServiceRegistry().searchService(type, provider.getId(), scope);
				if(ser!=null)
				{
					ret.setResult(ser);
				}
				else
				{
					ret.setException(new ServiceNotFoundException(type.getName()));
				}
			}
			else
			{
				container.getServiceRegistry().searchGlobalService(type).addResultListener(new DelegationResultListener<T>(ret));
			}
		}
		else
		{
			try
			{
				provider.getServices(new ClassInfo(type), scope)
//				provider.getServices(getSearchManager(false, scope), getVisitDecider(true, scope), 
//					new TypeResultSelector(type, true, RequiredServiceInfo.SCOPE_GLOBAL.equals(scope), filter))
						.addResultListener(new IIntermediateResultListener<IService>()
				{
					public void intermediateResultAvailable(IService result)
					{
	//					if(type.getName().indexOf("IRepositoryAccess")!=-1)
	//						System.out.println("ir: "+result);
						ret.setResult(result);
					}
					
					public void finished()
					{
	//					if(type.getName().indexOf("IRepositoryAccess")!=-1)
	//						System.out.println("fin");
						if(!ret.isDone())
						{
							ret.setExceptionIfUndone(new ServiceNotFoundException(type.getName()+" in "+provider.getId())
							{
	//							public void printStackTrace()
	//							{
	//								Thread.dumpStack();
	//								super.printStackTrace();
	//							}
							});
						}
					}
					
					public void resultAvailable(Collection<IService> result)
					{
	//					if(type.getName().indexOf("IRepositoryAccess")!=-1)
	//						System.out.println("ra: "+result);
						Collection<IService> res = (Collection<IService>)result;
						if(res==null || res.size()==0)
						{
		//					provider.getServices(getSearchManager(false, scope), getVisitDecider(true, scope), 
		//						new TypeResultSelector(type, true, RequiredServiceInfo.SCOPE_GLOBAL.equals(scope)))
		//						.addResultListener(new DefaultResultListener()
		//					{
		//						public void resultAvailable(Object result)
		//						{
		//							System.out.println("rrr: "+result);
		//						}
		//					});
							exceptionOccurred(new ServiceNotFoundException("No matching service found for type: "+type.getName()+" scope: "+scope));
						}
						else
						{
							ret.setResult(res.iterator().next());
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
	//					if(type.toString().indexOf("IFile")!=-1)
	//						System.out.println("Ex result: "+exception);
						if(!ret.isDone())
						{
							ret.setException(exception);
						}
					}
				});
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		return ret;
	}
	
	/**
	 *  Get one service with id.
	 *  @param clazz The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IServiceProvider provider, final IServiceIdentifier sid)
	{
		final Future<T> ret = new Future<T>();
		
		SServiceProvider.getServiceUpwards(provider, IComponentManagementService.class)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, T>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.getExternalAccess(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
				{
					public void customResultAvailable(IExternalAccess ea)
					{
//						ea.getServiceProvider().getService(getSearchManager(false, RequiredServiceInfo.SCOPE_LOCAL),
//							getVisitDecider(true, RequiredServiceInfo.SCOPE_LOCAL), new IdResultSelector(sid))
						ea.getServiceProvider().getService(sid)	
							.addResultListener(new ExceptionDelegationResultListener<IService, T>(ret)
						{
							public void customResultAvailable(IService res)
							{
								ret.setResult((T)res);
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get a service from a specific component.
	 *  @param provider A service provider.
	 *  @param cid The target component identifier.
	 *  @param type The service type.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IServiceProvider provider, final IComponentIdentifier cid, final Class<T> type)
	{
		final Future<T> ret = new Future<T>();
//		ret.addResultListener(new IResultListener<T>()
//		{
//			@Override
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("exception: "+exception);
//			}
//			public void resultAvailable(T result)
//			{
//				System.out.println("result: "+result);
//			}			
//		});
		
		SServiceProvider.getServiceUpwards(provider, IComponentManagementService.class)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, T>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
				{
					public void customResultAvailable(IExternalAccess ea)
					{
						SServiceProvider.getDeclaredService(ea.getServiceProvider(), type)
							.addResultListener(new DelegationResultListener<T>(ret));
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IServiceProvider provider, Class<T> type)
	{
		return getServices(provider, type, null);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IServiceProvider provider, Class<T> type, String scope)
	{
		return getServices(provider, type, scope, (IRemoteFilter<T>)null);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IServiceProvider provider, Class<T> type, String scope, IRemoteFilter<T> filter)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, Integer.valueOf(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final TerminableIntermediateDelegationFuture ret = new TerminableIntermediateDelegationFuture();
		
		// Hack->remove
//		IVisitDecider contdecider = new DefaultVisitDecider(false);
//		IVisitDecider rcontdecider = new DefaultVisitDecider(false, false);
		
		if(provider instanceof IServiceContainer)
		{
			IServiceContainer container = (IServiceContainer)provider;
			
			if(!RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
			{
				Collection<T> sers = container.getServiceRegistry().searchServices(type, provider.getId(), scope);
				ret.setResult(sers==null? Collections.EMPTY_SET: sers);
			}
			else
			{
				container.getServiceRegistry().searchGlobalServices(type).addResultListener(new IntermediateDelegationResultListener<T>(ret));
			}
		}
		else
		{
			try
			{
//				ITerminableIntermediateFuture fut = provider.getServices(getSearchManager(true, scope), 
//					getVisitDecider(false, scope),
//					new TypeResultSelector(type, false, RequiredServiceInfo.SCOPE_GLOBAL.equals(scope), filter));
				
				ITerminableIntermediateFuture<IService> fut = provider.getServices(new ClassInfo(type), scope);
				fut.addResultListener(new TerminableIntermediateDelegationResultListener<IService>(ret, fut));
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
//	public static <T> IIntermediateFuture<T> getServices(IServiceProvider provider, Class<T> type, 
//		String scope, IServiceSearchConstraints constraints)
//	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, Integer.valueOf(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
//		final TerminableIntermediateDelegationFuture ret = new TerminableIntermediateDelegationFuture();
		
		// Hack->remove
//		IVisitDecider contdecider = new DefaultVisitDecider(false);
//		IVisitDecider rcontdecider = new DefaultVisitDecider(false, false);
		
//		try
//		{
//			ITerminableIntermediateFuture fut = provider.getServices(getSearchManager(true, scope), 
//				getVisitDecider(false, scope),
//				new TypeResultSelector(type, false, RequiredServiceInfo.SCOPE_GLOBAL.equals(scope), constraints.getFilter()));
//			ret.addResultListener(new ServiceSearchIntermediateResultListener(ret, fut, constraints));
//		}
//		catch(Exception e)
//		{
//			ret.setException(e);
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Get one service of a type and only search upwards (parents).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getServiceUpwards(IServiceProvider provider, Class<T> type)
	{
		return getService(provider, type, RequiredServiceInfo.SCOPE_UPWARDS);
	}
	
//	/**
//	 *  Get one service of a type and only search upwards (parents).
//	 *  @param type The class.
//	 *  @return The corresponding service.
//	 */
//	public static IFuture getServiceUpwards(final IServiceProvider provider, final Class type)
//	{
////		synchronized(profiling)
////		{
////			Integer	cnt	= (Integer)profiling.get(type);
////			profiling.put(type, Integer.valueOf(cnt!=null ? cnt.intValue()+1 : 1)); 
////		}
//		final Future ret = new Future();
//		
//		// Hack->remove
////		IVisitDecider abortdecider = new DefaultVisitDecider();
//		
//		provider.getServices(upwardsmanager, getVisitDecider(true, RequiredServiceInfo.PLATFORM_SCOPE), new TypeResultSelector(type))
//			.addResultListener(new DelegationResultListener(ret)
//		{
//			public void customResultAvailable(Object result)
//			{
//				Collection res = (Collection)result;
//				if(res==null || res.size()==0)
//				{
////					provider.getServices(upwardsmanager, abortdecider, new TypeResultSelector(type))
////						.addResultListener(new DefaultResultListener()
////					{
////						public void resultAvailable(Object result)
////						{
////							System.out.println("service not found: "+result);
////						}
////					});
//					exceptionOccurred(new ServiceNotFoundException("No matching service found for type: "+type.getName()));
//				}
//				else
//					super.customResultAvailable(res.iterator().next());
//			}
//		});
//		
//		return ret;
//	}
	
//	/**
//	 *  Get the declared service of a type and only search the current provider.
//	 *  @param type The class.
//	 *  @return The corresponding service.
//	 */
//	public static IFuture getDeclaredService(IServiceProvider provider, final Class type)
//	{
////		synchronized(profiling)
////		{
////			Integer	cnt	= (Integer)profiling.get(type);
////			profiling.put(type, Integer.valueOf(cnt!=null ? cnt.intValue()+1 : 1)); 
////		}
//		final Future ret = new Future();
//		
//		// Hack->remove
////		IVisitDecider abortdecider = new DefaultVisitDecider();
//		
//		provider.getServices(localmanager, getVisitDecider(true, RequiredServiceInfo.LOCAL_SCOPE), new TypeResultSelector(type))
//			.addResultListener(new DelegationResultListener(ret)
//		{
//			public void customResultAvailable(Object result)
//			{
//				Collection res = (Collection)result;
//				if(res==null || res.size()==0)
//					exceptionOccurred(new ServiceNotFoundException("No matching service found for type: "+type.getName()));
//				else
//					super.customResultAvailable(res.iterator().next());
//			}
//		});
//		
//		return ret;
//	}
	
//	/**
//	 *  Get the declared services of a type and only search the current provider.
//	 *  @param type The class.
//	 *  @return The corresponding services.
//	 */
//	public static IFuture getDeclaredServices(IServiceProvider provider, Class type)
//	{
////		synchronized(profiling)
////		{
////			Integer	cnt	= (Integer)profiling.get(type);
////			profiling.put(type, Integer.valueOf(cnt!=null ? cnt.intValue()+1 : 1)); 
////		}
//		final Future ret = new Future();
//		
//		// Hack->remove
////		IVisitDecider abortdecider = new DefaultVisitDecider();
//		
//		provider.getServices(localmanager, getVisitDecider(false, RequiredServiceInfo.LOCAL_SCOPE), new TypeResultSelector(type))
//			.addResultListener(new DelegationResultListener(ret));
//		
//		return ret;
//	}
	
	/**
	 *  Get all declared services of the given provider.
	 *  @return The corresponding services.
	 */
	public static <T> IFuture<T> getDeclaredService(IServiceProvider provider, Class<T> type)
	{
		return SServiceProvider.getService(provider, type, RequiredServiceInfo.SCOPE_LOCAL);
	}
	
	/**
	 *  Get all declared services of the given provider.
	 *  @return The corresponding services.
	 */
	public static IIntermediateFuture<IService> getDeclaredServices(IServiceProvider provider)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, Integer.valueOf(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final IntermediateFuture<IService> ret = new IntermediateFuture<IService>();
		
		// Hack->remove
//		IVisitDecider contdecider = new DefaultVisitDecider(false);
		
//		if(Proxy.isProxyClass(provider.getClass()))
//			System.out.println("herere");
		
//		provider.getServices(getSearchManager(false, RequiredServiceInfo.SCOPE_LOCAL), contdecider, contanyselector)
//			.addResultListener(new IntermediateDelegationResultListener<IService>(ret));
		
		provider.getDeclaredServices()
			.addResultListener(new IntermediateDelegationResultListener<IService>(ret));
		
		return ret;
	}
	
//	/**
//	 *  Get all declared services of the given provider.
//	 *  @return The corresponding services.
//	 */
//	public static IIntermediateFuture getDeclaredServices(IServiceProvider provider, boolean forcedsearch)
//	{
////		synchronized(profiling)
////		{
////			Integer	cnt	= (Integer)profiling.get(type);
////			profiling.put(type, Integer.valueOf(cnt!=null ? cnt.intValue()+1 : 1)); 
////		}
//		final IntermediateFuture ret = new IntermediateFuture();
//		
//		// Hack->remove
////		IVisitDecider contdecider = new DefaultVisitDecider(false);
//		
//		provider.getServices(forcedsearch? localmanagerforced: localmanager, 
//			contdecider, contanyselector)
//				.addResultListener(new IntermediateDelegationResultListener(ret));
//		
//		return ret;
//	}
	
//	/**
//	 *  Get the declared service with id and only search the current provider.
//	 *  @param sid The service identifier.
//	 *  @return The corresponding service.
//	 */
//	public static IFuture getDeclaredService(IServiceProvider provider, final IServiceIdentifier sid)
//	{
////		synchronized(profiling)
////		{
////			Integer	cnt	= (Integer)profiling.get(type);
////			profiling.put(type, Integer.valueOf(cnt!=null ? cnt.intValue()+1 : 1)); 
////		}
//		final Future ret = new Future();
//		
//		// Hack->remove
////		IVisitDecider abortdecider = new DefaultVisitDecider();
//		
////		provider.getServices(localmanager, abortdecider, new IdResultSelector(sid))
//		provider.getServices(localmanager, getVisitDecider(true, RequiredServiceInfo.LOCAL_SCOPE), new IdResultSelector(sid))
//			.addResultListener(new DelegationResultListener(ret)
//		{
//			public void customResultAvailable(Object result)
//			{
//				Collection res = (Collection)result;
//				if(res==null || res.size()==0)
//					exceptionOccurred(new ServiceNotFoundException("No matching service found for type: "+sid));
//				else
//					super.customResultAvailable(res.iterator().next());
//			}
//		});
//		
//		return ret;
//	}
	
	/**
	 *  Rank the services of a search with a specific ranker.
	 */
	public static <S> ITerminableIntermediateFuture<S> rankServices(ITerminableIntermediateFuture<S> searchfut, 
		IServiceRanker<S> ranker, IRankingSearchTerminationDecider<S> decider)
	{
		TerminableIntermediateDelegationFuture<S> ret = new TerminableIntermediateDelegationFuture<S>();
		searchfut.addResultListener(new ServiceRankingDelegationResultListener<S>(ret, searchfut, ranker, decider));
		return ret;
	}
	
	/**
	 *  Rank the services of a search with a specific ranker and emit the scores.
	 */
	public static <S> ITerminableIntermediateFuture<Tuple2<S, Double>> rankServicesWithScores(ITerminableIntermediateFuture<S> searchfut, 
		IServiceRanker<S> ranker, IRankingSearchTerminationDecider<S> decider)
	{
		TerminableIntermediateDelegationFuture<Tuple2<S, Double>> ret = new TerminableIntermediateDelegationFuture<Tuple2<S, Double>>();
		searchfut.addResultListener(new ServiceRankingDelegationResultListener2<S>(ret, searchfut, ranker, decider));
		return ret;
	}
	
//	/**
//	 *  Get the fitting visit decider.
//	 */
//	public static IVisitDecider getVisitDecider(boolean abort)
//	{
//		return getVisitDecider(abort, null);
//	}
//	
//	/**
//	 *  Get the fitting visit decider.
//	 */
//	public static IVisitDecider getVisitDecider(boolean abort, String scope)
//	{
//		// Use application scope as default, use platform scope visit decider for upwards search
//		scope = scope==null? RequiredServiceInfo.SCOPE_APPLICATION
//			: RequiredServiceInfo.SCOPE_UPWARDS.equals(scope) ? RequiredServiceInfo.SCOPE_PLATFORM : scope;
//		return (IVisitDecider)(abort? avisitdeciders.get(scope): visitdeciders.get(scope));
//	}
//	
//	/**
//	 *  Get the fitting search manager.
//	 *  @param multiple	The multiple flag (i.e. one vs. multiple services required)
//	 */
//	public static ISearchManager	getSearchManager(boolean multiple)
//	{
//		return getSearchManager(multiple, null);
//	}
//	
//	/**
//	 *  Get the fitting search manager.
//	 *  @param multiple	The multiple flag (i.e. one vs. multiple services required)
//	 *  @param scope	The search scope.
//	 */
//	public static ISearchManager	getSearchManager(boolean multiple, String scope)
//	{
//		// Use application scope as default
//		scope = scope==null? RequiredServiceInfo.SCOPE_APPLICATION : scope;
//
//		ISearchManager	ret;
//		
//		if(RequiredServiceInfo.SCOPE_UPWARDS.equals(scope))
//		{
//			ret	= upwardsmanager;
//		}
//		else if(RequiredServiceInfo.SCOPE_LOCAL.equals(scope))
//		{
//			ret	= localmanager;
//		}
//		else if(multiple || RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
//		{
//			ret	= parallelmanager;
//		}
//		else
//		{
//			// Todo: use parallel also for single searches?
//			// Very inefficient for basic platform services :-(
//			ret	= sequentialmanager;
////			ret	= parallelmanager;
//		}
//		
//		return ret;
//	}
	
//	/**
//	 *  Test if an object has reference semantics. It is a reference when:
//	 *  - it implements IRemotable
//	 *  - it is an IService, IExternalAccess or IFuture
//	 *  - if the object has used an @Reference annotation at type level
//	 */
//	public static boolean isLocalReference(Object object)
//	{
//		return isReference(object, true);
//	}
//	
//	/**
//	 *  Test if an object has reference semantics. It is a reference when:
//	 *  - it implements IRemotable
//	 *  - it is an IService, IExternalAccess or IFuture
//	 *  - if the object has used an @Reference annotation at type level
//	 */
//	public static boolean isRemoteReference(Object object)
//	{
//		return isReference(object, false);
//	}
//		
//	/**
//	 *  Test if an object has reference semantics. It is a reference when:
//	 *  - it implements IRemotable
//	 *  - it is an IService, IExternalAccess or IFuture
//	 *  - if the object has used an @Reference annotation at type level
//	 */
//	public static boolean isReference(Object object, boolean local)
//	{
//		boolean ret = object instanceof IRemotable 
//			|| object instanceof IResultListener || object instanceof IIntermediateResultListener
//			|| object instanceof IFuture || object instanceof IIntermediateFuture
//			|| object instanceof IChangeListener || object instanceof IRemoteChangeListener;
////			|| object instanceof IService;// || object instanceof IExternalAccess;
//		
//		if(!ret && object!=null)
//		{
//			boolean localret = ret;
//			boolean remoteret = ret;
//			
//			Class cl = object.getClass();
//			boolean[] isref = (boolean[])references.get(cl);
//			if(isref!=null)
//			{
//				ret = local? isref[0]: isref[1]; 
//			}
//			else
//			{
//				List todo = new ArrayList();
//				todo.add(cl);
//				
//				while(todo.size()>0)
//				{
//					Class clazz = (Class)todo.remove(0);
//					Reference ref = (Reference)clazz.getAnnotation(Reference.class);
//					if(ref!=null)
//					{
//						localret = ref.local();
//						remoteret = ref.remote();
//						break;
//					}
//					else
//					{
//						Class superclazz = clazz.getSuperclass();
//						if(superclazz!=null && !superclazz.equals(Object.class))
//							todo.add(superclazz);
//						Class[] interfaces = clazz.getInterfaces();
//						for(int i=0; i<interfaces.length; i++)
//						{
//							todo.add(interfaces[i]);
//						}
//					}
//				}
//				
//				references.put(cl, new boolean[]{localret, remoteret});
//				ret = local? localret: remoteret;
////				System.out.println("refsize: "+references.size());
//			}
//		}
//		
////		System.out.println("object ref? "+ret+" "+object.getClass()+" "+object);
//		
//		return ret;
//	}
	
	// todo: remove these methods, move to marshal service
	
	/**
	 *  Get the copy info for method parameters.
	 */
	public static boolean[] getLocalReferenceInfo(Method method, boolean refdef)
	{
		return getReferenceInfo(method, refdef, true);
	}
	
	/**
	 *  Get the copy info for method parameters.
	 */
	public static boolean[] getRemoteReferenceInfo(Method method, boolean refdef)
	{
		return getReferenceInfo(method, refdef, false);
	}
	
	/**
	 *  Get the copy info for method parameters.
	 */
	public static boolean[] getReferenceInfo(Method method, boolean refdef, boolean local)
	{
		boolean[] ret;
		Object[] tmp = (Object[])methodreferences.get(method);
		if(tmp!=null)
		{
			ret = (boolean[])tmp[local? 0: 1];
		}
		else
		{
			int params = method.getParameterTypes().length;
			boolean[] localret = new boolean[params];
			boolean[] remoteret = new boolean[params];
			
			for(int i=0; i<params; i++)
			{
				Annotation[][] ann = method.getParameterAnnotations();
				localret[i] = refdef;
				remoteret[i] = refdef;
				for(int j=0; j<ann[i].length; j++)
				{
					if(ann[i][j] instanceof Reference)
					{
						Reference nc = (Reference)ann[i][j];
						localret[i] = nc.local();
						remoteret[i] = nc.remote();
						break;
					}
				}
			}
			
			methodreferences.put(method, new Object[]{localret, remoteret});
			ret = local? localret: remoteret;
		}
		return ret;
	}
	
	/**
	 *  Test if return value is local reference.
	 */
	public static boolean isReturnValueLocalReference(Method method, boolean refdef)
	{
		boolean ret = refdef;
		Reference ref = (Reference)method.getAnnotation(Reference.class);
		if(ref!=null)
			ret = ref.local();
		return ret;
	}
	
	/**
	 *  Get the copy info for method parameters.
	 */
	public static boolean isReturnValueRemoteReference(Method method, boolean refdef)
	{
		boolean ret = refdef;
		Reference ref = (Reference)method.getAnnotation(Reference.class);
		if(ref!=null)
			ret = ref.remote();
		return ret;
	}
	
//	/**
//	 *  Test if a call is remote.
//	 *  @param sic The service invocation context.
//	 */
//	public static boolean isRemoteObject(Object target)
//	{
//		boolean ret = false;
//		if(Proxy.isProxyClass(target.getClass()))
//		{
//			Object handler = Proxy.getInvocationHandler(target);
//			if(handler instanceof BasicServiceInvocationHandler)
//			{
//				BasicServiceInvocationHandler bsh = (BasicServiceInvocationHandler)handler;
//				// Hack! Needed for dynamically bound delegation services of composites (virtual)
//				ret = bsh.getDomainService()==null;
//				if(!ret)
//					return isRemoteObject(bsh.getDomainService());
//			}
//			else 
//			{
//				// todo: remove string based remote check! RemoteMethodInvocationHandler is in package jadex.platform.service.remote
//				ret = Proxy.getInvocationHandler(target).getClass().getName().indexOf("Remote")!=-1;
//			}
//		}
//		return ret;
////		Object target = getObject();
////		if(Proxy.isProxyClass(target.getClass()))
////			System.out.println("blubb "+Proxy.getInvocationHandler(target).getClass().getName());
////		return Proxy.isProxyClass(target.getClass()) && Proxy.getInvocationHandler(target).getClass().getName().indexOf("Remote")!=-1;
//	}
	
//	/**
//	 *  Get the copy info for method parameters.
//	 */
//	public static boolean[] getReferenceInfo(Method method, boolean copydefault)
//	{
//		boolean[] ret = (boolean[])methodreferences.get(method);
//		
//		if(ret==null)
//		{
//			int params = method.getParameterTypes().length;
//			ret = new boolean[params];
//			
//			for(int i=0; i<params; i++)
//			{
//				Annotation[][] ann = method.getParameterAnnotations();
//				ret[i] = !copydefault;
//				for(int j=0; j<ann[i].length; j++)
//				{
//					if(ann[i][j] instanceof Reference)
//					{
//						Reference nc = (Reference)ann[i][j];
//						ret[i] = nc.local();
//						break;
//					}
//				}
//			}
//			
//			methodreferences.put(method, ret);
//		}
//		return ret;
//	}
}
