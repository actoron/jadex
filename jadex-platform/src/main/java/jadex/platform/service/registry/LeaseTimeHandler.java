package jadex.platform.service.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;

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
public class LeaseTimeHandler<T>
{
	//-------- attributes --------
	
	/** The agent. */
	protected IInternalAccess agent;
	
	/** The entries. */
	protected Map<T, Entry> entries;
	
	/** The delay. */
	protected long delay;
	
	/** The timeout factor. */
	protected double factor;

	//-------- constructors --------
	
	/**
	 *  Create a new lease time handling object.
	 */
	public LeaseTimeHandler(IInternalAccess agent)
	{
		this(agent, 2.2, 5000);
	}
	
	/**
	 *  Create a new lease time handling object.
	 */
	public LeaseTimeHandler(IInternalAccess agent, double factor, long delay)
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
//		System.out.println("refresh: "+entry);
		boolean ret = false;
		
		if(entries == null)
			entries = new HashMap<T, Entry>();
		
		// If already contained update old entry (to not loose fixed entries like master flag).
		
		Entry old = entries.get(entry);
		
		if(old!=null)
		{
			old.setEntry(entry);
			old.setTimestamp(getClockTime());
		}
		else
		{
			entries.put(entry, new Entry(entry, getClockTime()));
			ret = true;
		}
		
		return ret;
	}
	
	/**
	 *  Get an existing entry.
	 */
	public T getEntry(T old)
	{
		return entries!=null && entries.containsKey(old)? entries.get(old).getEntry(): null;
	}
	
	/**
	 *  Remove an entry.
	 */
	public boolean removeEntry(T entry)
	{
		return entries==null? false: entries.remove(entry)!=null;
	}
	
	/**
	 *  Test if contains an entry.
	 */
	public boolean containsEntry(T entry)
	{
		return entries!=null? entries.containsKey(entry): false;
	}
	
	/**
	 *  Get all entries.
	 *  @return The entries.
	 */
	public T[] getEntries()
	{
		T[] ret = null;
		
		if(entries!=null)
		{
			ret = (T[])new Object[entries.size()];
			int i=0;
			for(Entry entry: entries.values())
			{
				ret[i++] = entry.getEntry();
			}
		}
		else
		{
			ret = (T[])SUtil.EMPTY_OBJECT_ARRAY;
		}
		
		return ret;
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
					for(Iterator<Map.Entry<T, Entry>> it=entries.entrySet().iterator(); it.hasNext();)
					{
						Map.Entry<T, Entry> entry = it.next();
						
//						System.out.println("Entry: "+entry.getValue().getTimestamp()+" "+entry.getKey());
						
						// Have some time buffer before delete
						if(time>entry.getValue().getTimestamp()+delay*factor)
						{
//							System.out.println("Removing: "+entry);
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
	public long getClockTime()
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
	
	/**
	 *  Entry struct.
	 */
	public class Entry
	{
		/** The real entry. */
		protected T entry;
		
		/** The timestamp. */
		protected long timestamp;
		
		/**
		 *  Create a new entry.
		 *  @param entry The entry.
		 *  @param timestamp The timestamp.
		 */
		public Entry(T entry, long timestamp)
		{
			this.entry = entry;
			this.timestamp = timestamp;
		}

		/**
		 *  Get the entry.
		 *  @return The entry
		 */
		public T getEntry()
		{
			return entry;
		}

		/**
		 *  Set the entry.
		 *  @param entry The entry to set
		 */
		public void setEntry(T entry)
		{
			this.entry = entry;
		}

		/**
		 *  Get the timestamp.
		 *  @return The timestamp
		 */
		public long getTimestamp()
		{
			return timestamp;
		}

		/**
		 *  Set the timestamp.
		 *  @param timestamp The timestamp to set
		 */
		public void setTimestamp(long timestamp)
		{
			this.timestamp = timestamp;
		}
	}
}
