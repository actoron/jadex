package jadex.base.service.awareness.discovery;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.xml.annotation.XMLClassname;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  Used to a list of entries that is automatically
 *  removed in case no updates are received via
 *  addOrUpdate method.
 *  
 *  Subclasses may override
 *  entryDeleted(DiscoveryEntry entry)
 *  to perform actions whenever an etry
 *  was deleted.
 */
public class LeaseTimeHandler
{
	//-------- attributes --------
	
	/** The state. */
	protected DiscoveryState state;
	
	/** The entries. */
	protected Map entries;
	
	/** The timer. */
	protected Timer	timer;
	
	/** The timeout factor. */
	protected double factor;

	//-------- constructors --------
	
	/**
	 *  Create a new lease time handling object.
	 */
	public LeaseTimeHandler(DiscoveryState state)
	{
		this(state, 2.2);
	}
	
	/**
	 *  Create a new lease time handling object.
	 */
	public LeaseTimeHandler(DiscoveryState state, double factor)
	{
		this.state = state;
		this.factor = factor;
		startRemoveBehavior();
	}
	
	//-------- methods --------
	
	/**
	 *  Add a new entry or update an existing entry.
	 *  @param entry The entry.
	 *  @return True, if new entry.
	 */
	public synchronized boolean addOrUpdateEntry(DiscoveryEntry entry)
	{
		if(entries==null)
			entries = new LinkedHashMap();
		
		// If already contained update old entry (to not loose fixed entries like master flag).
		DiscoveryEntry oldentry = (DiscoveryEntry)entries.get(entry.getInfo().getSender());
		if(oldentry!=null)
		{
			oldentry.setInfo(entry.getInfo());
			oldentry.setTime(getClockTime());
		}
		else
		{
			entries.put(entry.getInfo().getSender(), entry);
		}
		
		return oldentry==null;
	}
	
	/**
	 *  Add a new entry or update an existing entry.
	 *  @param entry The entry.
	 */
	public synchronized void updateEntry(DiscoveryEntry entry)
	{
		if(entries==null)
			entries = new LinkedHashMap();
		
		// If already contained update old entry (to not loose fixed entries like master flag).
		DiscoveryEntry oldentry = (DiscoveryEntry)entries.get(entry.getInfo().getSender());
		if(oldentry!=null)
		{
			oldentry.setInfo(entry.getInfo());
			oldentry.setTime(getClockTime());
		}
		else
		{
			throw new RuntimeException("Entry not contained: "+entry.getInfo().getSender());
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
		state.getExternalAccess().scheduleStep(new IComponentStep()
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
							 // Have some time buffer before delete
							if(time>entry.getTime()+entry.getInfo().getDelay()*factor)
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
				state.getExternalAccess().scheduleStep(step);
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
