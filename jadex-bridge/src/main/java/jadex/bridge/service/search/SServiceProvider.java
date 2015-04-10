package jadex.bridge.service.search;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IntermediateComponentResultListener;
import jadex.bridge.nonfunctional.search.IRankingSearchTerminationDecider;
import jadex.bridge.nonfunctional.search.IServiceRanker;
import jadex.bridge.nonfunctional.search.ServiceRankingDelegationResultListener;
import jadex.bridge.nonfunctional.search.ServiceRankingDelegationResultListener2;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.commons.IAsyncFilter;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.transformation.annotations.Classname;

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
	
	//-------- sync method (only local search) --------
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(final IInternalAccess provider, final Class<T> type)
	{
		return getLocalService(provider, type, (String)null);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(final IInternalAccess provider, final Class<T> type, final String scope)
	{
		return getLocalService(provider, type, scope, null);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> T getLocalService(final IInternalAccess provider, final Class<T> type, final String scope, final IFilter<T> filter)
	{
		T ret = ((IPlatformComponentAccess)provider).getServiceRegistry().searchService(type, provider.getComponentIdentifier(), scope, filter);
		if(ret==null)
		{
			throw new ServiceNotFoundException(type.getName());
		}
		
		return ret;
	}
	
	public static <T> T getLocalService(final IInternalAccess provider, final Class<T> type, final IComponentIdentifier target)
	{
		T ret = ((IPlatformComponentAccess)provider).getServiceRegistry().searchService(type, provider.getComponentIdentifier(), RequiredServiceInfo.SCOPE_PLATFORM, new IFilter<T>() 
		{
			public boolean filter(T obj) 
			{
				return ((IService)obj).getServiceIdentifier().getProviderId().equals(target);
			}
		});
		if(ret==null)
		{
			throw new ServiceNotFoundException(type.getName());
		}
		
		return ret;
	}

	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> Collection<T> getLocalServices(final IInternalAccess provider, final Class<T> type)
	{
		return getLocalServices(provider, type, null);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> Collection<T> getLocalServices(final IInternalAccess provider, final Class<T> type, final String scope)
	{
		return getLocalServices(provider, type, scope, null);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> Collection<T> getLocalServices(final IInternalAccess provider, final Class<T> type, final String scope, final IFilter<T> filter)
	{
		Collection<T> ret = ((IPlatformComponentAccess)provider).getServiceRegistry().searchServices(type, provider.getComponentIdentifier(), scope, filter);
		
		return ret;
	}
	
	//-------- async methods --------

	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IInternalAccess provider, Class<T> type)
	{
		return getService(provider, type, null);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IInternalAccess provider, Class<T> type, String scope)
	{
		return getService(provider, type, scope, (IAsyncFilter<T>)null);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(final IInternalAccess provider, final Class<T> type, final String scope, final IAsyncFilter<T> filter)
	{
		final Future<T> ret = new Future<T>();

		if(!RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
		{
			if(filter==null)
			{
				T ser = ((IPlatformComponentAccess)provider).getServiceRegistry().searchService(type, provider.getComponentIdentifier(), scope);
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
				((IPlatformComponentAccess)provider).getServiceRegistry().searchService(type, provider.getComponentIdentifier(), scope, filter)
					.addResultListener(new ComponentResultListener<T>(new DelegationResultListener<T>(ret), provider));
			}
		}
		else
		{
			((IPlatformComponentAccess)provider).getServiceRegistry().searchGlobalService(type, provider.getComponentIdentifier(), filter)
				.addResultListener(new ComponentResultListener<T>(new DelegationResultListener<T>(ret), provider));
		}
		
		return ret;
	}
	

	
	/**
	 *  Get one service with id.
	 *  @param clazz The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(final IInternalAccess provider, final IServiceIdentifier sid)
	{
		final Future<T> ret = new Future<T>();
		
		if(sid.getProviderId().equals(provider.getComponentIdentifier()))
		{
			ret.setResult((T)provider.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(sid.getServiceName()));
		}
		else
		{
			SServiceProvider.getService(provider, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, T>(ret)
			{
				public void customResultAvailable(IComponentManagementService cms)
				{
					cms.getExternalAccess(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
					{
						public void customResultAvailable(IExternalAccess ea)
						{
							ea.scheduleImmediate(new IComponentStep<T>()
							{
								@Classname("getService(final IInternalAccess provider, final IServiceIdentifier sid)")

								public IFuture<T> execute(IInternalAccess ia)
								{
									return getService(ia, sid);
								}
							}).addResultListener(new ComponentResultListener<T>(new DelegationResultListener<T>(ret), provider));
						}
					});
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Get a service from a specific component.
	 *  @param provider A service provider.
	 *  @param cid The target component identifier.
	 *  @param type The service type.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(final IInternalAccess provider, final IComponentIdentifier cid, final Class<T> type)
	{
		final Future<T> ret = new Future<T>();
		
		if(cid.equals(provider.getComponentIdentifier()))
		{
			ret.setResult((T)provider.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(type));
		}
		else
		{
			SServiceProvider.getService(provider, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, T>(ret)
			{
				public void customResultAvailable(IComponentManagementService cms)
				{
					cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
					{
						public void customResultAvailable(IExternalAccess ea)
						{
							ea.scheduleImmediate(new IComponentStep<T>()
							{
								@Classname("getService(final IInternalAccess provider, final IComponentIdentifier cid, final Class<T> type)")
								
								public IFuture<T> execute(IInternalAccess ia)
								{
									return getService(ia, cid, type);
								}
							}).addResultListener(new ComponentResultListener<T>(new DelegationResultListener<T>(ret), provider));
						}
					});
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IInternalAccess provider, Class<T> type)
	{
		return getServices(provider, type, null);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IInternalAccess provider, Class<T> type, String scope)
	{
		return getServices(provider, type, scope, (IAsyncFilter<T>)null);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IInternalAccess provider, Class<T> type, String scope, IAsyncFilter<T> filter)
	{
		final TerminableIntermediateDelegationFuture<T> ret = new TerminableIntermediateDelegationFuture<T>();
		
		if(!RequiredServiceInfo.SCOPE_GLOBAL.equals(scope))
		{
			if(filter==null)
			{
				Collection<T> sers = ((IPlatformComponentAccess)provider).getServiceRegistry().searchServices(type, provider.getComponentIdentifier(), scope);
				ret.setResult(sers==null? Collections.EMPTY_SET: sers);
			}
			else
			{
				((IPlatformComponentAccess)provider).getServiceRegistry().searchServices(type, provider.getComponentIdentifier(), scope, filter).addResultListener(
					new IntermediateComponentResultListener<T>(new IntermediateDelegationResultListener<T>(ret), provider));
			}
		}
		else
		{
			((IPlatformComponentAccess)provider).getServiceRegistry().searchGlobalServices(type, provider.getComponentIdentifier(), filter).addResultListener(
				new IntermediateComponentResultListener<T>(new IntermediateDelegationResultListener<T>(ret), provider));
		}
		
		return ret;
	}
	
//	/**
//	 *  Get one service of a type and only search upwards (parents).
//	 *  @param type The class.
//	 *  @return The corresponding service.
//	 */
//	public static <T> IFuture<T> getServiceUpwards(IInternalAccess provider, Class<T> type)
//	{
//		return getService(provider, type, RequiredServiceInfo.SCOPE_UPWARDS);
//	}
	
	/**
	 *  Get all declared services of the given provider.
	 *  @return The corresponding services.
	 */
	public static <T> IFuture<T> getDeclaredService(IInternalAccess provider, Class<T> type)
	{
		return getService(provider, type, RequiredServiceInfo.SCOPE_LOCAL);
	}
	
	/**
	 *  Get all declared services of the given provider.
	 *  @return The corresponding services.
	 */
	public static IIntermediateFuture<IService> getDeclaredServices(IInternalAccess provider)
	{
		final IntermediateFuture<IService> ret = new IntermediateFuture<IService>();
		
		for(Object s: provider.getComponentFeature(IProvidedServicesFeature.class).getProvidedServices(null))
		{
			ret.addIntermediateResult((IService)s);
		}
		ret.setFinished();
		
		return ret;
	}
	
	//-------- external access method duplicates --------
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IExternalAccess provider, Class<T> type)
	{
		return getService(provider, type, null);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IExternalAccess provider, Class<T> type, String scope)
	{
		return getService(provider, type, scope, (IAsyncFilter<T>)null);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IExternalAccess provider, final Class<T> type, final String scope, final IAsyncFilter<T> filter)
	{
//		if(scope==null && type.getName().indexOf("ICompo")!=-1)
//			System.out.println("gfdfgdfg");
		
		return provider.scheduleImmediate(new IComponentStep<T>()
		{
			@Classname("getService(IExternalAccess provider, final Class<T> type, final String scope, final IAsyncFilter<T> filter)")
			public IFuture<T> execute(IInternalAccess ia)
			{
				return getService(ia, type, scope, filter);
			}
		});
	}
	
	/**
	 *  Get one service with id.
	 *  @param clazz The class.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IExternalAccess provider, final IServiceIdentifier sid)
	{
		return provider.scheduleImmediate(new IComponentStep<T>()
		{
			@Classname("getService(IExternalAccess provider, final IServiceIdentifier sid)")
			public IFuture<T> execute(IInternalAccess ia)
			{
				return getService(ia, sid);
			}
		});
	}
	
	/**
	 *  Get a service from a specific component.
	 *  @param provider A service provider.
	 *  @param cid The target component identifier.
	 *  @param type The service type.
	 *  @return The corresponding service.
	 */
	public static <T> IFuture<T> getService(IExternalAccess provider, final IComponentIdentifier cid, final Class<T> type)
	{
		return provider.scheduleImmediate(new IComponentStep<T>()
		{
			@Classname("getService(IExternalAccess provider, final IComponentIdentifier cid, final Class<T> type)")
			public IFuture<T> execute(IInternalAccess ia)
			{
				return getService(ia, cid, type);
			}
		});
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IExternalAccess provider, Class<T> type)
	{
		return getServices(provider, type, null);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IExternalAccess provider, Class<T> type, String scope)
	{
		return getServices(provider, type, scope, (IAsyncFilter<T>)null);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static <T> ITerminableIntermediateFuture<T> getServices(IExternalAccess provider, final Class<T> type, final String scope, final IAsyncFilter<T> filter)
	{
		final TerminableIntermediateDelegationFuture<T> ret = new TerminableIntermediateDelegationFuture<T>();
		
		provider.scheduleImmediate(new IComponentStep<Collection<T>>()
		{
			@Classname("getServices(IExternalAccess provider, final Class<T> type, final String scope, final IAsyncFilter<T> filter)")
			public IFuture<Collection<T>> execute(IInternalAccess ia)
			{
				getServices(ia, type, scope, filter).addResultListener(new IntermediateDelegationResultListener<T>(ret));
				Future<Collection<T>> ret = new Future<Collection<T>>();
				ret.setResult(null);
				return ret;
			}
		});
		
		return ret;
	}
	
//	/**
//	 *  Get one service of a type and only search upwards (parents).
//	 *  @param type The class.
//	 *  @return The corresponding service.
//	 */
//	public static <T> IFuture<T> getServiceUpwards(IExternalAccess provider, Class<T> type)
//	{
//		return getService(provider, type, RequiredServiceInfo.SCOPE_UPWARDS);
//	}
	
	/**
	 *  Get all declared services of the given provider.
	 *  @return The corresponding services.
	 */
	public static <T> IFuture<T> getDeclaredService(IExternalAccess provider, Class<T> type)
	{
		return getService(provider, type, RequiredServiceInfo.SCOPE_LOCAL);
	}
	
	/**
	 *  Get all declared services of the given provider.
	 *  @return The corresponding services.
	 */
	public static IIntermediateFuture<IService> getDeclaredServices(IExternalAccess provider)
	{
		return (IIntermediateFuture<IService>)provider.scheduleImmediate(new IComponentStep<Collection<IService>>()
		{
			@Classname("getDeclaredServices(IExternalAccess provider)")
			public IFuture<Collection<IService>> execute(IInternalAccess ia)
			{
				return getDeclaredServices(ia);
			}
		});
	}

	//-------- other methods --------
	
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
}
