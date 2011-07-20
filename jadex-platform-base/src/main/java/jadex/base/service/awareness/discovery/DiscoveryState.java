package jadex.base.service.awareness.discovery;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 */
public class DiscoveryState
{
	/** The external access. */
	protected IExternalAccess access;
	
	/** The send (remotes) delay. */
	protected long delay;
	
	/** Flag for enabling fast startup awareness (pingpong send behavior). */
	protected boolean fast;
	
	/** The includes list. */
	protected String[] includes;
	
	/** The excludes list. */
	protected String[] excludes;

	/** Flag indicating that the agent is started and the send behavior may be activated. */
	protected boolean started;

	/** Flag indicating agent killed. */
	protected boolean killed;

	/** The timer. */
	protected Timer	timer;

	/** The root component id. */
	protected IComponentIdentifier root;

	/**
	 * 
	 */
	public DiscoveryState(IExternalAccess access)
	{
		this.access = access;
	}
	
	/**
	 *  Get the includes.
	 *  @return the includes.
	 */
	public String[] getIncludes()
	{
		return includes;
	}

	/**
	 *  Set the includes.
	 *  @param includes The includes.
	 */
	public void setIncludes(String[] includes)
	{
		this.includes = includes;
	}
	
	/**
	 *  Get the excludes.
	 *  @return the excludes.
	 */
	public String[] getExcludes()
	{
		return excludes;
	}
	
	/**
	 *  Set the excludes.
	 *  @param excludes The excludes.
	 */
	public void setExcludes(String[] excludes)
	{
		this.excludes = excludes;
	}

	/**
	 *  Get the started.
	 *  @return the started.
	 */
	public boolean isStarted()
	{
		return started;
	}

	/**
	 *  Set the started.
	 *  @param started The started to set.
	 */
	public void setStarted(boolean started)
	{
		this.started = started;
	}

	/**
	 *  Get the killed.
	 *  @return the killed.
	 */
	public boolean isKilled()
	{
		return killed;
	}

	/**
	 *  Set the killed.
	 *  @param killed The killed to set.
	 */
	public void setKilled(boolean killed)
	{
		this.killed = killed;
	}

	/**
	 *  Get the timer.
	 *  @return the timer.
	 */
	public Timer getTimer()
	{
		return timer;
	}

	/**
	 *  Set the timer.
	 *  @param timer The timer to set.
	 */
	public void setTimer(Timer timer)
	{
		this.timer = timer;
	}

	/**
	 *  Get the root.
	 *  @return the root.
	 */
	public IComponentIdentifier getRoot()
	{
		return root;
	}

	/**
	 *  Set the root.
	 *  @param root The root to set.
	 */
	public void setRoot(IComponentIdentifier root)
	{
		this.root = root;
	}
	
	/**
	 *  Get the access.
	 *  @return the access.
	 */
	public IExternalAccess getExternalAccess()
	{
		return access;
	}
	
	/**
	 *  Get the delay.
	 *  @return the delay.
	 */
	public long getDelay()
	{
		return delay;
	}

	/**
	 *  Set the delay.
	 *  @param delay The delay to set.
	 */
	public void setDelay(long delay)
	{
		this.delay = delay;
	}

	/**
	 *  Set the fast startup awareness flag
	 */
	public void setFastAwareness(boolean fast)
	{
		this.fast = fast;
	}
	
	/**
	 *  Get the fast startup awareness flag.
	 *  @return The fast flag.
	 */
	public boolean isFastAwareness()
	{
		return this.fast;
	}
	
	/**
	 *  Get the current time.
	 */
	public long getClockTime()
	{
//		return clock.getTime();
		return System.currentTimeMillis();
	}
	
	/**
	 *  Overriden wait for to not use platform clock.
	 */
	public void	doWaitFor(long delay, final IComponentStep step)
	{
//		waitFor(delay, step);
		
		if(timer==null)
			timer	= new Timer(true);
		
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				access.scheduleStep(step);
			}
		}, delay);
	}
}
