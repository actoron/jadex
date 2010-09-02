package jadex.commons.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;

import java.util.ArrayList;

/**
 *  Static helper class for searching services.
 */
public class SServiceProvider
{
	//-------- constants --------
	
	/** The sequential search manager. */
	public static ISearchManager sequentialmanager = new SequentialSearchManager();
//	public static ISearchManager sequentialmanager = new ParallelSearchManager();

	/** The parallel search manager. */
	public static ISearchManager parallelmanager = new ParallelSearchManager();
	
	/** The sequential search manager that searches only upwards. */
	public static ISearchManager upwardsmanager = new SequentialSearchManager(true, false);

	/** The sequential search manager that searches only locally. */
	public static ISearchManager localmanager = new LocalSearchManager();
	
	/** The vsist decider that stops searching after one result has been found. */
	public static IVisitDecider abortdecider = new DefaultVisitDecider();
	public static IVisitDecider rabortdecider = new DefaultVisitDecider(true, false);

	/** The vsist decider that never stops. */
	public static IVisitDecider contdecider = new DefaultVisitDecider(false);
	public static IVisitDecider rcontdecider = new DefaultVisitDecider(false, false);

	public static IResultSelector contanyselector = new AnyResultSelector(false);
	public static IResultSelector abortanyselector = new AnyResultSelector(true);

	
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
		return getService(provider, type, true);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getService(IServiceProvider provider, Class type, boolean onlylocal)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
		IVisitDecider abortdecider = new DefaultVisitDecider();
		IVisitDecider rabortdecider = new DefaultVisitDecider(true, false);
		
		provider.getServices(sequentialmanager, onlylocal? abortdecider: rabortdecider, 
			new TypeResultSelector(type, true, onlylocal), new ArrayList())
				.addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Get one service with id.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getService(IServiceProvider provider, IServiceIdentifier sid)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
		IVisitDecider abortdecider = new DefaultVisitDecider();
		
		provider.getServices(sequentialmanager, abortdecider, new IdResultSelector(sid), new ArrayList())
			.addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getService(IServiceProvider provider, IResultSelector selector)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(selector.getCacheKey());
//			profiling.put(selector.getCacheKey(), new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
		IVisitDecider abortdecider = new DefaultVisitDecider();
		
		provider.getServices(sequentialmanager, abortdecider, selector, new ArrayList())
			.addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static IFuture getServices(IServiceProvider provider, Class type)
	{
		return getServices(provider, type, true);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static IFuture getServices(IServiceProvider provider, Class type, boolean local)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
		IVisitDecider contdecider = new DefaultVisitDecider(false);
		IVisitDecider rcontdecider = new DefaultVisitDecider(false, false);
		
		provider.getServices(parallelmanager, local ? contdecider : rcontdecider, new TypeResultSelector(type, false, local), new ArrayList())
			.addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}

	/**
	 *  Get one service of a type and only search upwards (parents).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getServiceUpwards(IServiceProvider provider, Class type)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
		IVisitDecider abortdecider = new DefaultVisitDecider();
		
		provider.getServices(upwardsmanager, abortdecider, new TypeResultSelector(type), new ArrayList())
			.addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Get the declared service of a type and only search the current provider.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getDeclaredService(IServiceProvider provider, Class type)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
		IVisitDecider abortdecider = new DefaultVisitDecider();
		
		provider.getServices(localmanager, abortdecider, new TypeResultSelector(type), new ArrayList())
			.addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Get the declared services of a type and only search the current provider.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static IFuture getDeclaredServices(IServiceProvider provider, Class type)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
		IVisitDecider abortdecider = new DefaultVisitDecider();
		
		provider.getServices(localmanager, abortdecider, new TypeResultSelector(type), new ArrayList())
			.addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Get all declared services of the given provider.
	 *  @return The corresponding services.
	 */
	public static IFuture getDeclaredServices(IServiceProvider provider)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
		IVisitDecider contdecider = new DefaultVisitDecider(false);
		
		provider.getServices(localmanager, contdecider, contanyselector, new ArrayList())
			.addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Get the declared service with id and only search the current provider.
	 *  @param sid The service identifier.
	 *  @return The corresponding service.
	 */
	public static IFuture getDeclaredService(IServiceProvider provider, IServiceIdentifier sid)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
		IVisitDecider abortdecider = new DefaultVisitDecider();
		
		provider.getServices(localmanager, abortdecider, new IdResultSelector(sid), new ArrayList())
			.addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Get one remote service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 * /
	public static IFuture getRemoteService(IServiceProvider provider, final IComponentIdentifier platform, final Class type)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		
		final Future ret = new Future();
		
		getService(provider, IRemoteServiceManagementService.class).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;
				ret.setResult(rms.getProxy(platform, type));
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
	
		return ret;
	}*/
}
