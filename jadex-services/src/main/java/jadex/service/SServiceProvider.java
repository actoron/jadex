package jadex.service;

import jadex.commons.IFuture;

/**
 *  Static helper class for searching services.
 */
public class SServiceProvider
{
	//-------- constants --------
	
	/** The sequential search manager. */
	public static ISearchManager sequentialmanager = new SequentialSearchManager();

	/** The parallel search manager. */
	public static ISearchManager parallelmanager = new ParallelSearchManager();
	
	/** The sequential search manager that searches only upwards. */
	public static ISearchManager upwardsmanager = new SequentialSearchManager(true, false);

	/** The sequential search manager that searches only locally. */
	public static ISearchManager localmanager = new LocalSearchManager();
	
	/** The vsist decider that stops searching after one result has been found. */
	public static IVisitDecider abortdecider = new DefaultVisitDecider();

	/** The vsist decider that never stops. */
	public static IVisitDecider contdecider = new DefaultVisitDecider(false);

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
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		return provider.getServices(sequentialmanager, abortdecider, new TypeResultSelector(type));
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
		return provider.getServices(sequentialmanager, abortdecider, new IdResultSelector(sid));
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
		return provider.getServices(sequentialmanager, abortdecider, selector);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static IFuture getServices(IServiceProvider provider, Class type)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		return provider.getServices(parallelmanager, contdecider, new TypeResultSelector(type, false));
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
		return provider.getServices(upwardsmanager, abortdecider, new TypeResultSelector(type));
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
		return provider.getServices(localmanager, abortdecider, new TypeResultSelector(type));
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
		return provider.getServices(upwardsmanager, contdecider, new TypeResultSelector(type));
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
		return provider.getServices(localmanager, abortdecider, new IdResultSelector(sid));
	}
	
	/**
	 *  Get one remote service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 * /
	public static IFuture getService(Object providerid, Class type)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		
		// Create message to issue remote search request
		
		
		return provider.getServices(sequentialmanager, abortdecider, new TypeResultSelector(type));
	}*/
}
