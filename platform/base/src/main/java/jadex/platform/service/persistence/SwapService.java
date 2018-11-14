package jadex.platform.service.persistence;

import jadex.bridge.service.annotation.Service;

/**
 *  Automatic swapping of idle components to save memory.
 */
@Service
public class SwapService	//implements ISwapService, IIdleHook
{
//	//-------- constants --------
//	
//	/** The default minimum time span of inactivity after which a component is swapped to disk. */
//	public static final long DEFAULT_SWAP_DELAY	= BasicService.getScaledLocalDefaultTimeout(1);
//	
//	/** The offset between minimum and maximum swap delay (e.g. delay=30 offset=0.5 -> maximum=30+30*0.5=45). */
//	public static final double DEFAULT_SWAP_OFFSET	= 0.5;
//	
//	//-------- attributes --------
//	
//	/** The inactive components with last step time sorted by last activity (most recent is last). */
//	protected Map<IComponentIdentifier, Long>	idlecomponents;
//	
//	/** The time span of inactivity after which a component is swapped to disk. */
//	protected long	swapdelay;
//	
//	/** The offset between minimum and maximum swap delay. */
//	protected double swapoffset;
//	
//	/** The flag when the timer is active. */
//	protected boolean	timerrunning;
//	
//	/** The external access of the swap service component. */
//	@ServiceComponent
//	protected IExternalAccess	access;
//	
//	/** The persistence service. */
//	protected IPersistenceService	ps;
//	
//	/** The clock service. */
//	protected IClockService	cs;
//	
//	//-------- constructors --------
//	
//	/**
//	 *  Static method for reflective creation to allow platform start without add-on.
//	 */
//	public static SwapService	create()
//	{
//		return new SwapService();
//	}
//	
//	/**
//	 *  Create a persistence CMS.
//	 */
//	public SwapService()
//	{
//		this.idlecomponents	= new LinkedHashMap<IComponentIdentifier, Long>();
//		this.swapdelay	= DEFAULT_SWAP_DELAY;
//		this.swapoffset	= DEFAULT_SWAP_OFFSET;
//	}
//	
//	/**
//	 *  Service startup.
//	 */
//	@ServiceStart
//	public IFuture<Void>	start(IInternalAccess comp)
//	{
//		final Future<Void>	ret	= new Future<Void>();
//		final boolean[]	found	= new boolean[2];
//		
//		comp.getServiceContainer().searchService(IPersistenceService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//			.addResultListener(new ExceptionDelegationResultListener<IPersistenceService, Void>(ret, true)
//		{
//			public void customResultAvailable(IPersistenceService result)
//			{
//				ps	= result;
//				ps.addIdleHook(SwapService.this)
//					.addResultListener(new DelegationResultListener<Void>(ret, true)
//				{
//					public void customResultAvailable(Void result)
//					{
//						found[0] = true;
//						
//						if(found[0] && found[1])
//						{
//							ret.setResultIfUndone(null);
//						}
//					}
//				});
//			}
//		});
//		
//		comp.getServiceContainer().searchService(IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//			.addResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret, true)
//		{
//			public void customResultAvailable(IClockService result)
//			{
//				cs	= result;
//				found[1] = true;
//				
//				if(found[0] && found[1])
//				{
//					ret.setResultIfUndone(null);
//				}
//			}
//		});
//
//		return ret;
//	}
//	
//	//-------- IIdleHook interface --------
//	
//	/**
//	 *  Called when a component becomes active.
//	 *  Called on cid thread!
//	 */
//	public void	componentActive(IComponentIdentifier cid)
//	{
//		boolean	starttimer;
//		synchronized(idlecomponents)
//		{
//			starttimer	= !timerrunning;
//			if(starttimer)
//			{
//				timerrunning	= true;
//			}
//			idlecomponents.put(cid, Long.valueOf(cs.getTime()));
//		}
//		
//		if(starttimer)
//		{
//			access.scheduleStep(new IComponentStep<Void>()
//			{
//				public IFuture<Void> execute(final IInternalAccess ia)
//				{
//					ia.waitForDelay(swapdelay)
//						.addResultListener(new IResultListener<Void>()
//						{
//							public void resultAvailable(Void result)
//							{
//								System.out.println("SwapService: Checking for idle components.");
//								
//								long	current	= cs.getTime();
//								List<IComponentIdentifier>	persistables	= null;
//								long	nexttime	= -1;
//								synchronized(idlecomponents)
//								{
//									for(Map.Entry<IComponentIdentifier, Long> entry: idlecomponents.entrySet())
//									{
//										long	last	= entry.getValue().longValue();
//										if(last+swapdelay>=current)
//										{
//											if(persistables==null)
//											{
//												persistables	= new ArrayList<IComponentIdentifier>();
//											}
//											persistables.add(entry.getKey());
//										}
//										else
//										{
//											nexttime	= last - current + swapdelay + (long)(swapdelay*swapoffset);
//											break;
//										}
//									}
//									
//									if(nexttime==-1)
//									{
//										timerrunning	= false;
//									}
//								}
//								
//								if(persistables!=null)
//								{
//									for(final IComponentIdentifier cid: persistables)
//									{
//										ps.swapToStorage(cid)
//											.addResultListener(new IResultListener<IPersistInfo>()
//										{
//											public void resultAvailable(IPersistInfo pi)
//											{
//												System.out.println("SwapService: Got persist info for "+cid+": "+pi);
//											}
//
//											public void exceptionOccurred(Exception exception)
//											{
//												if(!(exception instanceof ComponentTerminatedException))
//												{
//													ia.getLogger().severe("Exception when swapping component "+cid+": "+exception);
//												}
//											}
//										});
//									}
//								}
//								
//								if(nexttime!=-1)
//								{
//									ia.waitForDelay(nexttime).addResultListener(this);
//								}
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
//								if(!(exception instanceof ComponentTerminatedException))
//								{
//									ia.getLogger().severe("Exception in persistence timer: "+exception);
//								}
//							}
//						});
//					return IFuture.DONE;
//				}
//			});
//		}
//	}
//	
//	/**
//	 *  Called when a component becomes idle.
//	 *  Called on cid thread!
//	 */
//	public void	componentIdle(IComponentIdentifier cid)
//	{
//		synchronized(idlecomponents)
//		{
//			idlecomponents.remove(cid);
//		}
//	}
//	
//	//-------- ISwapService interface --------
//	
//	/**
//	 *  Store the component state and transparently remove it from memory.
//	 *  Keeps the component available in CMS to allow restoring it on access.
//	 *  
//	 *  @param cid	The component identifier.
//	 */
//	public IFuture<Void>	swapToStorage(IComponentIdentifier cid)
//	{
//		throw new UnsupportedOperationException("todo");
//	}
//	
//	/**
//	 *  Transparently restore the component state of a previously
//	 *  swapped component.
//	 *  
//	 *  @param cid	The component identifier.
//	 */
//	public IFuture<Void>	swapFromStorage(IComponentIdentifier cid)
//	{
//		throw new UnsupportedOperationException("todo");
//	}
}
