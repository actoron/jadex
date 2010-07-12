package jadex.service;

import jadex.commons.IFuture;

/**
 * 
 */
public class SServiceProvider
{
	protected static ISearchManager sequentialmanager = new SequentialSearchManager();
	protected static ISearchManager parallelmanager = new ParallelSearchManager();
	protected static ISearchManager upwardsmanager = new SequentialSearchManager(true, false);
	protected static IVisitDecider abortdecider = new DefaultVisitDecider();
	protected static IVisitDecider contdecider = new DefaultVisitDecider(false);
	
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
}
