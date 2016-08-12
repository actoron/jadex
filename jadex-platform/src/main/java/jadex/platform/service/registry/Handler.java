package jadex.platform.service.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.platform.service.awareness.discovery.DiscoveryEntry;
import jadex.platform.service.awareness.discovery.LeaseTimeHandler;

/**
 *  Used to a list of entries that is automatically
 *  removed in case no updates are received via
 *  addOrUpdate method.
 *  
 *  Subclasses may override
 *  entryDeleted(DiscoveryEntry entry)
 *  to perform actions whenever an entry
 *  was deleted.
 */
public class Handler<T>
{
	//-------- attributes --------
	
	/** The agent. */
	protected IInternalAccess agent;
	
	/** The entries. */
	protected Map<T, Long> entries;
	
	/** The delay. */
	protected long delay;
	
//	/** The timer. */
//	protected Timer	timer;
	
	/** The timeout factor. */
	protected double factor;

	//-------- constructors --------
	
	/**
	 *  Create a new lease time handling object.
	 */
	public Handler(IInternalAccess agent)
	{
		this(agent, 2.2, 5000);
	}
	
	/**
	 *  Create a new lease time handling object.
	 */
	public Handler(IInternalAccess agent, double factor, long delay)
	{
		this.agent = agent;
		this.factor = factor;
		this.delay = delay;
		startRemoveBehavior();
	}
	
	//-------- methods --------
	
	/**
	 *  Add a new entry or update an existing entry.
	 *  @param entry The entry.
	 *  @return True, if new entry.
	 */
	public boolean addOrUpdateEntry(T entry)
	{
//		System.out.println("add: "+entry);
		
		if(entries == null)
			entries = new HashMap<T, Long>();
		
		// If already contained update old entry (to not loose fixed entries like master flag).
		
//		entries.get
//		if(entries.containsKey(entry))
//		{
//			T old = entries.remove(entry);
//			entries.put(entry, getClockTime());
//		}
//		else
//		{
//			entries.put(entry, getClockTime());
//		}
//		
//		DiscoveryEntry oldentry = entries.get(entry.getInfo().getSender());
//		if(oldentry!=null)
//		{
//			oldentry.setInfo(entry.getInfo());
//			oldentry.setTime(getClockTime());
//			oldentry.setEntry(entry.getEntry());
//		}
//		else
//		{
//			entries.put(entry.getInfo().getSender(), entry);
//		}
//		
//		return oldentry==null;
	
		return false;
	}
	
	/**
	 * 
	 */
	
	
//	/**
//	 *  Add a new entry or update an existing entry.
//	 *  @param entry The entry.
//	 */
//	public synchronized void updateEntry(DiscoveryEntry entry)
//	{
//		if(entries==null)
//			entries = new LinkedHashMap();
//		
//		// If already contained update old entry (to not loose fixed entries like master flag).
//		DiscoveryEntry oldentry = (DiscoveryEntry)entries.get(entry.getInfo().getSender());
//		if(oldentry!=null)
//		{
//			oldentry.setInfo(entry.getInfo());
//			oldentry.setTime(getClockTime());
//		}
//		else
//		{
//			throw new RuntimeException("Entry not contained: "+entry.getInfo().getSender());
//		}
//	}
	
	/**
	 *  Get all entries.
	 *  @return The entries.
	 */
	public T[] getEntries()
	{
		return (T[])(entries==null? SUtil.EMPTY_OBJECT_ARRAY:
			entries.keySet().toArray(new Object[entries.size()]));
	}
	
	/**
	 *  Start removing discovered proxies.
	 */
	public void startRemoveBehavior()
	{
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, new IComponentStep<Void>()
		{
			@Classname("rem")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				List<T> todel = new ArrayList<T>();
				long time = getClockTime();
				
				if(entries!=null)
				{
					for(Iterator<Map.Entry<T, Long>> it=entries.entrySet().iterator(); it.hasNext();)
					{
						Map.Entry<T, Long> entry = it.next();
						
						// Have some time buffer before delete
						if(time>entry.getValue()+delay*factor)
						{
//								System.out.println("Removing: "+entry);
							it.remove();
							todel.add(entry.getKey());
						}
					}
				}
				
				if(todel!=null)
				{
					for(int i=0; i<todel.size(); i++)
					{
						entryDeleted(todel.get(i));
					}
				}
				
				agent.getComponentFeature(IExecutionFeature.class).waitForDelay(delay, this, true);
				
				return IFuture.DONE;
			}
		}, true);
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
	public void entryDeleted(T entry)
	{
	}
}
