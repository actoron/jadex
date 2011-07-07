package jadex.bridge.service;

import jadex.bridge.JadexCloner;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.IRemotable;
import jadex.commons.collection.LRU;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Static helper class for searching services.
 */
public class SServiceProvider
{	
	//-------- constants --------
	
	/** The sequential search manager. */
	public static ISearchManager sequentialmanager = new SequentialSearchManager();
//	public static ISearchManager sequentialmanagerforced = new SequentialSearchManager(true, true, true);

	/** The parallel search manager. */
	public static ISearchManager parallelmanager = sequentialmanager;//new ParallelSearchManager();
//	public static ISearchManager parallelmanagerforced = new ParallelSearchManager(true, true, true);
	
	/** The sequential search manager that searches only upwards. */
	public static ISearchManager upwardsmanager = new SequentialSearchManager(true, false);

	/** The sequential search manager that searches only locally. */
	public static ISearchManager localmanager = new LocalSearchManager();
//	public static ISearchManager localmanagerforced = new LocalSearchManager(true);
	
	/** The visit decider that stops searching after one result has been found. */
//	public static IVisitDecider abortdecider = new DefaultVisitDecider();
//	public static IVisitDecider rabortdecider = new DefaultVisitDecider(true, RequiredServiceInfo.GLOBAL_SCOPE);

	/** The visit decider that never stops. */
	public static IVisitDecider contdecider = new DefaultVisitDecider(false);
	public static IVisitDecider rcontdecider = new DefaultVisitDecider(false, RequiredServiceInfo.SCOPE_GLOBAL);

	public static IResultSelector contanyselector = new AnyResultSelector(false);
	public static IResultSelector abortanyselector = new AnyResultSelector(true);

	public static Map avisitdeciders;
	public static Map visitdeciders;
	
	/** The reference class cache (clazz->boolean (is reference)). */
	public static Map references;
	
	/** The reference method cache (method -> boolean[] (is reference)). */
	public static Map methodreferences;
	
	static
	{
		avisitdeciders = new HashMap();
		avisitdeciders.put(RequiredServiceInfo.SCOPE_LOCAL, new DefaultVisitDecider(true, RequiredServiceInfo.SCOPE_LOCAL));
		avisitdeciders.put(RequiredServiceInfo.SCOPE_COMPONENT, new DefaultVisitDecider(true, RequiredServiceInfo.SCOPE_COMPONENT));
		avisitdeciders.put(RequiredServiceInfo.SCOPE_APPLICATION, new DefaultVisitDecider(true, RequiredServiceInfo.SCOPE_APPLICATION));
		avisitdeciders.put(RequiredServiceInfo.SCOPE_PLATFORM, new DefaultVisitDecider(true, RequiredServiceInfo.SCOPE_PLATFORM));
		avisitdeciders.put(RequiredServiceInfo.SCOPE_GLOBAL, new DefaultVisitDecider(true, RequiredServiceInfo.SCOPE_GLOBAL));
		
		visitdeciders = new HashMap();
		visitdeciders.put(RequiredServiceInfo.SCOPE_LOCAL, new DefaultVisitDecider(false, RequiredServiceInfo.SCOPE_LOCAL));
		visitdeciders.put(RequiredServiceInfo.SCOPE_COMPONENT, new DefaultVisitDecider(false, RequiredServiceInfo.SCOPE_COMPONENT));
		visitdeciders.put(RequiredServiceInfo.SCOPE_APPLICATION, new DefaultVisitDecider(false, RequiredServiceInfo.SCOPE_APPLICATION));
		visitdeciders.put(RequiredServiceInfo.SCOPE_PLATFORM, new DefaultVisitDecider(false, RequiredServiceInfo.SCOPE_PLATFORM));
		visitdeciders.put(RequiredServiceInfo.SCOPE_GLOBAL, new DefaultVisitDecider(false, RequiredServiceInfo.SCOPE_GLOBAL));

		references = Collections.synchronizedMap(new LRU(500));
		methodreferences = Collections.synchronizedMap(new LRU(500));
	}
	
	//-------- methods --------

//	protected static Map	profiling	= new HashMap();
//	
//	static
//	{
//		new Thread(new Runnable()
//		{
//			public void run()
//			{
//				try
//				{
//					Thread.sleep(5000);
//					
//					synchronized(profiling)
//					{
//						System.out.println("--------------------");
//						for(Iterator it=profiling.keySet().iterator(); it.hasNext(); )
//						{
//							Object	key	= it.next();
//							System.out.println(key+":\t"+profiling.get(key));
//						}
//					}
//				}
//				catch(InterruptedException e)
//				{
//					e.printStackTrace();
//				}
//			}
//		}).start();
//	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getService(IServiceProvider provider, Class type)
	{
		return getService(provider, type, null);
	}
	
//	/**
//	 *  Get one service of a type.
//	 *  @param type The class.
//	 *  @return The corresponding service.
//	 */
//	public static IFuture getService(IServiceProvider provider, Class type, boolean remote)
//	{
//		return getService(provider, type, false, false);
//	}
	
//	/**
//	 *  Get one service of a type.
//	 *  @param type The class.
//	 *  @return The corresponding service.
//	 */
//	public static IFuture getService(final IServiceProvider provider, final Class type, final boolean remote, final boolean forcedsearch)
//	{
////		synchronized(profiling)
////		{
////			Integer	cnt	= (Integer)profiling.get(type);
////			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
////		}
//		final Future ret = new Future();
//		
//		// Hack->remove
////		IVisitDecider abortdecider = new DefaultVisitDecider();
////		IVisitDecider rabortdecider = new DefaultVisitDecider(true, false);
//		
//		provider.getServices(forcedsearch? sequentialmanagerforced: sequentialmanager, 
//			remote? getVisitDecider(true, RequiredServiceInfo.GLOBAL_SCOPE): getVisitDecider(true), 
//			new TypeResultSelector(type, true, remote))
//				.addResultListener(new DelegationResultListener(ret)
//		{
//			public void customResultAvailable(Object result)
//			{
////				System.out.println("Search result: "+result);
//				Collection res = (Collection)result;
//				if(res==null || res.size()==0)
//				{
//					getService(provider, type, remote, forcedsearch).addResultListener(new DefaultResultListener()
//					{
//						public void resultAvailable(Object result)
//						{
//							System.out.println("rrr: "+result);
//						}
//					});
//					exceptionOccurred(new ServiceNotFoundException("No matching service found for type: "+type.getName()));
//				}
//				else
//					super.customResultAvailable(res.iterator().next());
//			}
//		});
//		
//		return ret;
//	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getService(final IServiceProvider provider, final Class type, final String scope)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
//		IVisitDecider abortdecider = new DefaultVisitDecider();
//		IVisitDecider rabortdecider = new DefaultVisitDecider(true, false);
		
		provider.getServices(getSearchManager(false, scope), getVisitDecider(true, scope), 
			new TypeResultSelector(type, true, RequiredServiceInfo.SCOPE_GLOBAL.equals(scope)))
				.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
//				System.out.println("Search result: "+result);
				Collection res = (Collection)result;
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
					super.customResultAvailable(res.iterator().next());
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get one service with id.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getService(IServiceProvider provider, final IServiceIdentifier sid)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
//		IVisitDecider abortdecider = new DefaultVisitDecider();
		
		provider.getServices(getSearchManager(false, RequiredServiceInfo.SCOPE_PLATFORM),
			getVisitDecider(true, RequiredServiceInfo.SCOPE_PLATFORM), new IdResultSelector(sid))
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				Collection res = (Collection)result;
				if(res==null || res.size()==0)
					exceptionOccurred(new ServiceNotFoundException("No service found for id: "+sid));
				else
					super.customResultAvailable(res.iterator().next());
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getService(IServiceProvider provider, final IResultSelector selector)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(selector.getCacheKey());
//			profiling.put(selector.getCacheKey(), new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
//		IVisitDecider abortdecider = new DefaultVisitDecider();
		
		provider.getServices(getSearchManager(false, RequiredServiceInfo.SCOPE_PLATFORM),
			getVisitDecider(true, RequiredServiceInfo.SCOPE_PLATFORM), selector)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				Collection res = (Collection)result;
				if(res==null || res.size()==0)
					exceptionOccurred(new ServiceNotFoundException("No matching service found for: "+selector));
				else
					super.customResultAvailable(res.iterator().next());
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static IIntermediateFuture getServices(IServiceProvider provider, Class type)
	{
		return getServices(provider, type, null);
	}
	
//	/**
//	 *  Get all services of a type.
//	 *  @param type The class.
//	 *  @return The corresponding services.
//	 */
//	public static IIntermediateFuture getServices(IServiceProvider provider, Class type, boolean remote)
//	{
//		return getServices(provider, type, remote, false);
//	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static IIntermediateFuture getServices(IServiceProvider provider, Class type, String scope)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final IntermediateFuture ret = new IntermediateFuture();
		
		// Hack->remove
//		IVisitDecider contdecider = new DefaultVisitDecider(false);
//		IVisitDecider rcontdecider = new DefaultVisitDecider(false, false);
		
		provider.getServices(getSearchManager(true, scope), 
			getVisitDecider(false, scope),
			new TypeResultSelector(type, false, RequiredServiceInfo.SCOPE_GLOBAL.equals(scope)))
				.addResultListener(new IntermediateDelegationResultListener(ret));
//				{
//					public void customResultAvailable(Object source, Object result)
//					{
//						System.out.println(6);
//						super.customResultAvailable(source, result);
//					}
//				});
		
		return ret;
	}
	
	/**
	 *  Get one service of a type and only search upwards (parents).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getServiceUpwards(IServiceProvider provider, Class type)
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
////			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
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
////			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
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
////			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
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
	public static IIntermediateFuture getDeclaredServices(IServiceProvider provider)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final IntermediateFuture ret = new IntermediateFuture();
		
		// Hack->remove
//		IVisitDecider contdecider = new DefaultVisitDecider(false);
		
//		if(Proxy.isProxyClass(provider.getClass()))
//			System.out.println("herere");
		
		provider.getServices(getSearchManager(false, RequiredServiceInfo.SCOPE_LOCAL), contdecider, contanyselector)
			.addResultListener(new IntermediateDelegationResultListener(ret));
		
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
////			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
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
////			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
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
	 *  Get the fitting visit decider.
	 */
	public static IVisitDecider getVisitDecider(boolean abort)
	{
		return getVisitDecider(abort, null);
	}
	
	/**
	 *  Get the fitting visit decider.
	 */
	public static IVisitDecider getVisitDecider(boolean abort, String scope)
	{
		// Use application scope as default, use platform scope visit decider for upwards search
		scope = scope==null? RequiredServiceInfo.SCOPE_APPLICATION
			: RequiredServiceInfo.SCOPE_UPWARDS.equals(scope) ? RequiredServiceInfo.SCOPE_PLATFORM : scope;
		return (IVisitDecider)(abort? avisitdeciders.get(scope): visitdeciders.get(scope));
	}
	
	/**
	 *  Get the fitting search manager.
	 *  @param multiple	The multiple flag (i.e. one vs. multiple services required)
	 */
	public static ISearchManager	getSearchManager(boolean multiple)
	{
		return getSearchManager(multiple, null);
	}
	
	/**
	 *  Get the fitting search manager.
	 *  @param multiple	The multiple flag (i.e. one vs. multiple services required)
	 *  @param scope	The search scope.
	 */
	public static ISearchManager	getSearchManager(boolean multiple, String scope)
	{
		// Use application scope as default
		scope = scope==null? RequiredServiceInfo.SCOPE_APPLICATION : scope;

		ISearchManager	ret;
		
		if(RequiredServiceInfo.SCOPE_UPWARDS.equals(scope))
		{
			ret	= upwardsmanager;
		}
		else if(RequiredServiceInfo.SCOPE_LOCAL.equals(scope))
		{
			ret	= localmanager;
		}
		else if(multiple)
		{
			ret	= parallelmanager;
		}
		else
		{
			// Todo: use parallel also for single searches?
			ret	= sequentialmanager;
		}
		
		return ret;
	}
	
	/**
	 *  Test if an object has reference semantics. It is a reference when:
	 *  - it implements IRemotable
	 *  - it is an IService, IExternalAccess or IFuture
	 *  - if the object has used an @Reference annotation at type level
	 */
	public static boolean isLocalReference(Object object)
	{
		return isReference(object, true);
	}
	
	/**
	 *  Test if an object has reference semantics. It is a reference when:
	 *  - it implements IRemotable
	 *  - it is an IService, IExternalAccess or IFuture
	 *  - if the object has used an @Reference annotation at type level
	 */
	public static boolean isRemoteReference(Object object)
	{
		return isReference(object, false);
	}
		
	/**
	 *  Test if an object has reference semantics. It is a reference when:
	 *  - it implements IRemotable
	 *  - it is an IService, IExternalAccess or IFuture
	 *  - if the object has used an @Reference annotation at type level
	 */
	public static boolean isReference(Object object, boolean local)
	{
		boolean ret = object==null || object instanceof IRemotable || object instanceof IFuture;
//			|| object instanceof IService || object instanceof IExternalAccess;
		
		if(!ret && object!=null)
		{
			boolean localret = ret;
			boolean remoteret = ret;
			
			Class cl = object.getClass();
			boolean[] isref = (boolean[])references.get(cl);
			if(isref!=null)
			{
				ret = local? isref[0]: isref[1]; 
			}
			else
			{
				List todo = new ArrayList();
				todo.add(cl);
				
				while(todo.size()>0)
				{
					Class clazz = (Class)todo.remove(0);
					Reference ref = (Reference)clazz.getAnnotation(Reference.class);
					if(ref!=null)
					{
						localret = ref.local();
						remoteret = ref.remote();
						break;
					}
					else
					{
						Class superclazz = clazz.getSuperclass();
						if(superclazz!=null && !superclazz.equals(Object.class))
							todo.add(superclazz);
						Class[] interfaces = clazz.getInterfaces();
						for(int i=0; i<interfaces.length; i++)
						{
							todo.add(interfaces[i]);
						}
					}
				}
				
				references.put(cl, new boolean[]{localret, remoteret});
				ret = local? localret: remoteret;
//				System.out.println("refsize: "+references.size());
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the copy info for method parameters.
	 */
	public static boolean[] getLocalReferenceInfo(Method method, boolean copydefault)
	{
		return getReferenceInfo(method, copydefault, true);
	}
	
	/**
	 *  Get the copy info for method parameters.
	 */
	public static boolean[] getRemoteReferenceInfo(Method method, boolean copydefault)
	{
		return getReferenceInfo(method, copydefault, false);
	}
	
	/**
	 *  Get the copy info for method parameters.
	 */
	public static boolean[] getReferenceInfo(Method method, boolean copydefault, boolean local)
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
				localret[i] = !copydefault;
				remoteret[i] = !copydefault;
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
