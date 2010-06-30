package jadex.base;

import jadex.base.fipa.CMSComponentDescription;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.IThreadPool;
import jadex.service.PropertyServiceContainer;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimedObject;
import jadex.service.clock.ITimer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


/**
 *  Abstract base class for platforms.
 */
public abstract class AbstractPlatform extends PropertyServiceContainer
{
	//-------- constants --------

	/** The maximum shutdown time. */
	public static final long MAX_SHUTDOWM_TIME = 3000;

	//-------- attributes --------

	/** The logger. */
	protected Logger logger;

	/** The shutdown flag. */
	protected boolean shuttingdown;

	/** The shutdown time. */
	protected long shutdowntime;
	
	/** The threadpool. */
	protected IThreadPool threadpool;

	//-------- methods --------
		
	/**
	 *  Check if the platform is currently shutting down.
	 */
	public boolean isShuttingDown() // todo: make protected?
	{
		return shuttingdown;
	}

	/**
	 *  Get the platform logger.
	 *  @return The platform logger.
	 */
	public Logger getLogger()
	{
		return logger;
	}

	/**
	 *  Shutdown the platform.
	 */
	public IFuture shutdown()
	{
		final Future ret = new Future();
		
		//System.out.println("Shutting down the platform: "+getName());
		// Hack !!! Should be synchronized with CES.
		synchronized(this)
		{
			if(shuttingdown)
				return null; // todo: hack

			this.shuttingdown = true;
		}
		
		// Step 1: Find existing components.
		getService(IComponentManagementService.class).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IComponentManagementService	cms	= (IComponentManagementService)result;
				cms.getComponentDescriptions().addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						// Step 2: Kill existing components excepts daemons.
						final List comps = new ArrayList(Arrays.asList((IComponentDescription[])result));
						for(int i=comps.size()-1; i>-1; i--)
						{
							if(((CMSComponentDescription)comps.get(i)).isDaemon())
								comps.remove(i);
						}
						
						killComponents(comps, shutdowntime!=0 ? shutdowntime : MAX_SHUTDOWM_TIME, new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								// Step 3: Find remaining components.
								cms.getComponentDescriptions().addResultListener(new IResultListener()
								{
									public void resultAvailable(Object source, Object result)
									{
										// Step 4: Kill remaining components.
										killComponents(Arrays.asList((IComponentDescription[])result), shutdowntime!=0 ? shutdowntime : MAX_SHUTDOWM_TIME, new IResultListener()
										{
											public void resultAvailable(Object source, Object result)
											{
												// Step 5: Stop the services.
												AbstractPlatform.super.shutdown().addResultListener(new DelegationResultListener(ret));
											}
											public void exceptionOccurred(Object source, Exception exception)
											{
												ret.setException(exception);
//												listener.exceptionOccurred(source, exception);
											}
										});
									}

									public void exceptionOccurred(Object source, Exception exception)
									{
										ret.setException(exception);
//										listener.exceptionOccurred(source, exception);
									}
								});		
							}
							
							public void exceptionOccurred(Object source, Exception exception)
							{
								ret.setException(exception);
//								listener.exceptionOccurred(source, exception);
							}
						});
					}

					public void exceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
//						listener.exceptionOccurred(source, exception);
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
//				listener.exceptionOccurred(source, exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Kill the given components within the specified timeout.
	 *  @param comps	The component ids.
	 *  @param timeout	The time after which to inform the listener anyways.
	 *  @param listener	The result listener.
	 */
	protected void killComponents(final List comps, final long timeout, final IResultListener listener)
	{
		if(comps.isEmpty())
			listener.resultAvailable(this, null);
		
		getService(IClockService.class).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				// Timer entry to notify lister after timeout.
				final	boolean	notified[]	= new boolean[1];
				final ITimer killtimer	= ((IClockService)result).createTimer(timeout, new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						boolean	notify	= false;
						synchronized(notified)
						{
							if(!notified[0])
							{
								notify	= true;
								notified[0]	= true;
							}
						}
						if(notify)
						{
							listener.resultAvailable(this, null);
						}
					}
				});
				
				// Kill the given components.
				final IResultListener	rl	= new IResultListener()
				{
					int cnt	= 0;
					public void resultAvailable(Object source, Object result)
					{
						testFinished();
					}
					public void exceptionOccurred(Object source, Exception exception)
					{
						testFinished();
					}
					protected synchronized void testFinished()
					{
						cnt++;
//						System.out.println("here: "+cnt+" "+comps.size());
						if(cnt==comps.size())
						{
							killtimer.cancel();
							boolean	notify	= false;
							synchronized(notified)
							{
								if(!notified[0])
								{
									notify	= true;
									notified[0]	= true;
								}
							}
							if(notify)
							{
								listener.resultAvailable(this, null);
							}
						}
					}
				};
				
				getService(IComponentManagementService.class).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IComponentManagementService	ces	= (IComponentManagementService)result;
						for(int i=0; i < comps.size(); i++)
						{
							//System.out.println("Killing component: "+comps.get(i));
							CMSComponentDescription desc = (CMSComponentDescription)comps.get(i);
							IFuture ret = ces.destroyComponent(desc.getName());
							ret.addResultListener(rl);
						}
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						listener.exceptionOccurred(source, exception);
					}
				});
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				listener.exceptionOccurred(source, exception);
			}
		});
	}

}
