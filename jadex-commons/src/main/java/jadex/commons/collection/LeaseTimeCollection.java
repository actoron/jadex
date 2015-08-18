package jadex.commons.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

import jadex.commons.ICommand;

/**
 *  Collection that remove elements after a lease time automatically.
 */
public class LeaseTimeCollection<E> implements Collection<E>
{
	//-------- attributes --------
	
	/** The entries. */
	// DEFAULT_INITIAL_CAPACITY=11, no constructor without ic in java <1.8
	protected PriorityQueue<E> entries = new PriorityQueue<E>(11, new Comparator<E>()
	{
		public int compare(E e1, E e2)
		{
			long t1 = times.get(e1).longValue();
			long t2 = times.get(e2).longValue();
			int ret = 0;
			if(t1<=0 && t2<=0)
				ret = 0;
			else if(t1>0 && t2>0)
				ret = (int)(t1-t2);
			else if(t1>0)
				ret = -1;
			else
				ret = 1;
			
			if(ret<0)
				System.out.println(e1+" < "+e2);
			else if(ret>0)
				System.out.println(e1+" > "+e2);
			else
				System.out.println(e1+" = "+e2);
			return ret;
//			return t1>0 && t2>0? (int)(t1-t2): t1<=0 && t2<=0? 0: t1>0? 1: -1;
//			return (int)(t1-t2);
		}
	});
	
	/** The timestamps. */
	protected Map<E, Long> times = new HashMap<E, Long>();
	
	/** The timer. */
	protected Timer	timer;
	
	/** The leasetime. */
	protected long leasetime;

	/** The current checker. */
	protected Checker checker;
	
	/** The cleaner. */
	protected ICommand<E> removecmd;

	//-------- constructors --------
	
	/**
	 *  Create a new lease time handling object.
	 */
	public LeaseTimeCollection()
	{
	}
	
	/**
	 *  Create a new lease time handling object.
	 */
	public LeaseTimeCollection(long leasetime)
	{
		this(leasetime, null);
//		this.leasetime = leasetime;
	}
	
	/**
	 *  Create a new lease time handling object.
	 */
	public LeaseTimeCollection(ICommand<E> removecmd)
	{
		this(0, removecmd);
	}
	
	/**
	 *  Create a new lease time handling object.
	 */
	public LeaseTimeCollection(long leasetime, ICommand<E> removecmd)
	{
		this.leasetime = leasetime;
		this.removecmd = removecmd;
	}
	
	//-------- methods --------

    public synchronized int size()
    {
    	return entries.size();
    }

    public synchronized boolean isEmpty()
    {
    	return entries.isEmpty();
    }

    public synchronized boolean contains(Object o)
    {
    	return entries.contains(o);
    }

    public synchronized Iterator<E> iterator()
    {
    	return entries.iterator();
    }

    public synchronized Object[] toArray()
    {
    	return entries.toArray();
    }

    public synchronized <T> T[] toArray(T[] a)
    {
    	return entries.toArray(a);
    }

    // Modification Operations

    public synchronized boolean add(E e)
    {
    	return add(e, getLeaseTime(e));
    }
    
    public synchronized boolean add(E e, long leasetime)
    {
    	times.put(e, Long.valueOf(leasetime>0? getClockTime()+leasetime: -1));
    	boolean ret = entries.add(e);
    
    	if(ret)
    		checkStale();
    	
    	return ret;
    }

    public synchronized boolean remove(Object o)
    {
    	times.remove(o);
    	boolean ret = entries.remove(o);
    	
    	if(ret)
    		checkStale();
    	
    	return ret;
    }

    // Bulk Operations

    public synchronized boolean containsAll(Collection<?> c)
    {
    	return entries.containsAll(c);
    }

    public synchronized boolean addAll(Collection<? extends E> c)
    {
    	boolean changed = false;
    	for(E entry: c)
    	{
    		changed |= add(entry);
    	}
    	
    	if(changed)
    		checkStale();
    	
    	return changed;
    }

    public synchronized boolean removeAll(Collection<?> c)
    {
    	boolean changed = false;
    	for(Object entry: c)
    	{
    		changed |= remove(entry);
    	}
    	
    	if(changed)
    		checkStale();
    	
    	return changed;
    }

    public synchronized boolean retainAll(Collection<?> c)
    {
//    	return entries.retainAll(c);
    	boolean changed = false;
    	for(E e: entries.toArray((E[])new Object[entries.size()]))
    	{
    		if(!c.contains(e))
    		{
    			changed = true;
    			remove(e);
    		}
    	}
    	
    	if(changed)
    		checkStale();
    	
    	return changed;
    }

    public synchronized void clear()
    {
    	times.clear();
    	entries.clear();
    	checker.cancel();
    }

    // Comparison and hashing

    public synchronized boolean equals(Object o)
    {
    	return entries.equals(o);
    }

    /**
     * 
     */
    public int hashCode()
    {
    	return entries.hashCode()*23+27;
    }

	/**
	 *  Add a new entry or update an existing entry.
	 *  @param entry The entry.
	 *  @return True, if new entry.
	 */
	public synchronized boolean update(E e)
	{
		return update(e, getLeaseTime(e));
	}
	
	/**
	 *  Add a new entry or update an existing entry.
	 *  @param entry The entry.
	 *  @return True, if new entry.
	 */
	public synchronized boolean update(E e, long leasetime)
	{
		boolean ret = !remove(e);
		
		add(e);
		
		return ret;
	}
	
	/**
	 *  Update the timestamp of e.
	 *  @param entry The entry.
	 */
	public synchronized void touch(E e)
	{
		touch(e, getLeaseTime(e));
	}
	
	/**
	 *  Update the timestamp of e.
	 *  @param entry The entry.
	 */
	public synchronized void touch(E e, long leasetime)
	{
		times.put(e, Long.valueOf(leasetime>0? getClockTime()+leasetime: -1));
		// Does only reorder when element is added again :-(
		// http://stackoverflow.com/questions/6952660/java-priority-queue-reordering-when-editing-elements
		entries.remove(e);
		entries.add(e);
	}
		
	/**
	 *  Start removing discovered proxies.
	 */
	public void checkStale()
	{
		checker = new Checker();
		checker.run();
	}
	
	/**
	 *  Overriden wait for to not use platform clock.
	 */
	protected synchronized Runnable doWaitFor(long delay, final Runnable step)
	{
		if(timer==null)
			timer	= new Timer(true);
		
		final TimerTask tt = new TimerTask()
		{
			public void run()
			{
				step.run();
			}
		};
		
		timer.schedule(tt, delay);
		
		return new Runnable()
		{
			public void run()
			{
				tt.cancel();
			}
		};
	}
	
	/**
	 *  Get the current time.
	 */
	protected long getClockTime()
	{
//		return clock.getTime();
		return System.currentTimeMillis();
	}
	
//	/**
//	 *  Called when an entry has been deleted.
//	 */
//	public void entryDeleted(E entry)
//	{
//	}
	
//	/**
//	 *  Get the entry to be saved from an entry.
//	 *  Allows to transform the entry.
//	 */
//	public E getEntry(E e)
//	{
//		return e;
//	}
	
	/**
	 *  Get the leasetime for an entry.
	 */
	public long getLeaseTime(E e)
	{
		return leasetime;
	}

	/**
	 *  Release all resources.
	 */
	public void dispose()
	{
		if(timer!=null)
		{
			timer.cancel();
			timer	= null;
		}
	}
	
	/**
	 * 
	 */
	public class Checker implements Runnable
	{
		Runnable cancel = null;

		/**
		 * 
		 */
		public void run()
		{
			long delta = -1;
			
			synchronized(LeaseTimeCollection.this)
			{
				if(checker==this)
				{
					while(true)
					{
						E first = entries.peek();
						
						if(first!=null)
						{
							System.out.println("entries: "+entries);
							System.out.println("times: "+times);
							
							long etime = times.get(first).longValue();
							if(etime>0)
							{
								long curtime = getClockTime();
								delta = etime-curtime;
								if(delta<=0)
								{
									System.out.println("removed: "+etime+" "+first+" "+System.currentTimeMillis());
									remove(first);
	//								entryDeleted(first);
									if(removecmd!=null)
										removecmd.execute(first);
								}
								else
								{
									System.out.println("delta is: "+delta+" "+first);
									break;
								}
							}
							else
							{
								System.out.println("save value: "+first);
								break;
							}
						}
						else
						{
							break;
						}
					}
				}
//				else
//				{
//					System.out.println("end: "+this);
//				}
			}
			
			if(delta>0)
				cancel = doWaitFor(delta, this);
		}
		
		/**
		 * 
		 */
		public void cancel()
		{
			if(cancel!=null)
				cancel.run();
		}
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		LeaseTimeCollection<Integer> col = new LeaseTimeCollection<Integer>(3000);
		
		col.add(1);
		col.add(2);
		col.add(-1,-1);
		col.add(3);
		col.add(-2,-2);
		
//		
//		col.add(33, -1);
//		
//		for(int i=0; i<5; i++)
//		{
//			col.add(Integer.valueOf(i));
//			
////			if(i%1000==0)
//			{
//				try
//				{
//					Thread.sleep(1000);
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		}
//
//		col.add(44, -1);
//		
//		while(true)
//		{
//			try
//			{
//				Thread.sleep(1000);
//				System.out.print(".");
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
		
	}
}
