package jadex.base.service.awareness.discovery;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.xml.annotation.XMLClassname;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 */
public class LeaseTimeHandler
{
	/** The external access. */
	protected IExternalAccess access;
	
	/** The entries. */
	protected Map entries;
	
	/** The timer. */
	protected Timer	timer;

	/**
	 *  Create a new lease time handling object.
	 */
	public LeaseTimeHandler(IExternalAccess access)
	{
		this.access = access;
		startRemoveBehavior();
	}
	
	/**
	 *  Add a new entry or update an existing entry.
	 *  @param entry The entry.
	 */
	public synchronized void addOrUpdateEntry(DiscoveryEntry entry)
	{
		if(entries==null)
			entries = new LinkedHashMap();
		
		// If already contained update old entry (to not loose fixed entries like master flag).
		DiscoveryEntry oldentry = (DiscoveryEntry)entries.get(entry.getComponentIdentifier());
		if(oldentry!=null)
		{
			oldentry.setDelay(entry.getDelay());
			oldentry.setTime(entry.getTime());
		}
		else
		{
			entries.put(entry.getComponentIdentifier(), entry);
		}
	}
	
	/**
	 *  Get all entries.
	 *  @return The entries.
	 */
	public synchronized DiscoveryEntry[] getEntries()
	{
		return entries==null? new DiscoveryEntry[0]: (DiscoveryEntry[])
			entries.values().toArray(new DiscoveryEntry[entries.size()]);
	}
	
	/**
	 *  Start removing discovered proxies.
	 */
	public void startRemoveBehavior()
	{
		access.scheduleStep(new IComponentStep()
		{
			@XMLClassname("rem")
			public Object execute(IInternalAccess ia)
			{
				List todel = new ArrayList();
				synchronized(LeaseTimeHandler.this)
				{
					long time = getClockTime();
					if(entries!=null)
					{
						for(Iterator it=entries.values().iterator(); it.hasNext(); )
						{
							DiscoveryEntry entry = (DiscoveryEntry)it.next();
							// five seconds buffer
							if(time>entry.getTime()+entry.getDelay()*3.2) // Have some time buffer before delete
							{
//								System.out.println("Removing: "+entry);
								it.remove();
								todel.add(entry);
							}
						}
					}
				}
				
				if(todel!=null)
				{
					for(int i=0; i<todel.size(); i++)
					{
						DiscoveryEntry entry = (DiscoveryEntry)todel.get(i);
						entryDeleted(entry);
					}
				}
				
				doWaitFor(5000, this);
				return null;
			}
		});
	}
	
	/**
	 *  Overriden wait for to not use platform clock.
	 */
	protected void	doWaitFor(long delay, final IComponentStep step)
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
	
	/**
	 *  Get the current time.
	 */
	protected long getClockTime()
	{
//		return clock.getTime();
		return System.currentTimeMillis();
	}
	
	/**
	 *  Called when an entry has been deleted.
	 */
	public void entryDeleted(DiscoveryEntry entry)
	{
	}
}
