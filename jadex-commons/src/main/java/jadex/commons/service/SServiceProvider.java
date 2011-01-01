package jadex.commons.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IIntermediateFuture;
import jadex.commons.IntermediateFuture;
import jadex.commons.concurrent.DelegationResultListener;

import java.util.ArrayList;
import java.util.Collection;

/**
 *  Static helper class for searching services.
 */
public class SServiceProvider
{	
	//-------- constants --------
	
	/** The sequential search manager. */
	public static ISearchManager sequentialmanager = new SequentialSearchManager();
	public static ISearchManager sequentialmanagerforced = new SequentialSearchManager(true, true, true);

	/** The parallel search manager. */
	public static ISearchManager parallelmanager = new SequentialSearchManager();//new ParallelSearchManager();
	public static ISearchManager parallelmanagerforced = new SequentialSearchManager(true, true, true);//new ParallelSearchManager(true, true, true);
	
	/** The sequential search manager that searches only upwards. */
	public static ISearchManager upwardsmanager = new SequentialSearchManager(true, false);

	/** The sequential search manager that searches only locally. */
	public static ISearchManager localmanager = new LocalSearchManager();
	public static ISearchManager localmanagerforced = new LocalSearchManager(true);
	
	/** The visit decider that stops searching after one result has been found. */
	public static IVisitDecider abortdecider = new DefaultVisitDecider();
	public static IVisitDecider rabortdecider = new DefaultVisitDecider(true, true);

	/** The visit decider that never stops. */
	public static IVisitDecider contdecider = new DefaultVisitDecider(false);
	public static IVisitDecider rcontdecider = new DefaultVisitDecider(false, true);

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
		return getService(provider, type, false);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getService(IServiceProvider provider, Class type, boolean remote)
	{
		return getService(provider, type, false, false);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getService(IServiceProvider provider, final Class type, boolean remote, boolean forcedsearch)
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
		
		provider.getServices(forcedsearch? sequentialmanagerforced: sequentialmanager, 
			remote? rabortdecider: abortdecider, 
			new TypeResultSelector(type, true, remote), new ArrayList())
				.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
//				System.out.println("Search result: "+result);
				Collection res = (Collection)result;
				if(res==null || res.size()==0)
					exceptionOccurred(new ServiceNotFoundException("No matching service found for type: "+type.getName()));
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
		
		provider.getServices(sequentialmanager, abortdecider, new IdResultSelector(sid), new ArrayList())
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
		
		provider.getServices(sequentialmanager, abortdecider, selector, new ArrayList())
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
		return getServices(provider, type, false);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static IIntermediateFuture getServices(IServiceProvider provider, Class type, boolean remote)
	{
		return getServices(provider, type, remote, false);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static IIntermediateFuture getServices(IServiceProvider provider, Class type, boolean remote, boolean forcedsearch)
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
		
		provider.getServices(forcedsearch? parallelmanagerforced: parallelmanager, 
			remote? rcontdecider: contdecider, 
			new TypeResultSelector(type, false, remote), new ArrayList())
				.addResultListener(new DelegationResultListener(ret));
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
	public static IFuture getServiceUpwards(IServiceProvider provider, final Class type)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
//		IVisitDecider abortdecider = new DefaultVisitDecider();
		
		provider.getServices(upwardsmanager, abortdecider, new TypeResultSelector(type), new ArrayList())
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				Collection res = (Collection)result;
				if(res==null || res.size()==0)
					exceptionOccurred(new ServiceNotFoundException("No matching service found for type: "+type.getName()));
				else
					super.customResultAvailable(res.iterator().next());
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the declared service of a type and only search the current provider.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getDeclaredService(IServiceProvider provider, final Class type)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
//		IVisitDecider abortdecider = new DefaultVisitDecider();
		
		provider.getServices(localmanager, abortdecider, new TypeResultSelector(type), new ArrayList())
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				Collection res = (Collection)result;
				if(res==null || res.size()==0)
					exceptionOccurred(new ServiceNotFoundException("No matching service found for type: "+type.getName()));
				else
					super.customResultAvailable(res.iterator().next());
			}
		});
		
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
//		IVisitDecider abortdecider = new DefaultVisitDecider();
		
		provider.getServices(localmanager, abortdecider, new TypeResultSelector(type), new ArrayList())
			.addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
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
		
		provider.getServices(localmanager, contdecider, contanyselector, new ArrayList())
			.addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Get all declared services of the given provider.
	 *  @return The corresponding services.
	 */
	public static IIntermediateFuture getDeclaredServices(IServiceProvider provider, boolean forcedsearch)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final IntermediateFuture ret = new IntermediateFuture();
		
		// Hack->remove
//		IVisitDecider contdecider = new DefaultVisitDecider(false);
		
		provider.getServices(forcedsearch? localmanagerforced: localmanager, 
			contdecider, contanyselector, new ArrayList())
				.addResultListener(new DelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Get the declared service with id and only search the current provider.
	 *  @param sid The service identifier.
	 *  @return The corresponding service.
	 */
	public static IFuture getDeclaredService(IServiceProvider provider, final IServiceIdentifier sid)
	{
//		synchronized(profiling)
//		{
//			Integer	cnt	= (Integer)profiling.get(type);
//			profiling.put(type, new Integer(cnt!=null ? cnt.intValue()+1 : 1)); 
//		}
		final Future ret = new Future();
		
		// Hack->remove
//		IVisitDecider abortdecider = new DefaultVisitDecider();
		
		provider.getServices(localmanager, abortdecider, new IdResultSelector(sid), new ArrayList())
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				Collection res = (Collection)result;
				if(res==null || res.size()==0)
					exceptionOccurred(new ServiceNotFoundException("No matching service found for type: "+sid));
				else
					super.customResultAvailable(res.iterator().next());
			}
		});
		
		return ret;
	}
}
