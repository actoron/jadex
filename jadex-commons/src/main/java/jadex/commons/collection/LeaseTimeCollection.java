package jadex.commons.collection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

import jadex.commons.ICommand;

/**
 *  Collection that remove elements after a lease time automatically.
 */
public class LeaseTimeCollection<E> implements ILeaseTimeCollection<E>
{
	/** Constant for no leasetime. */
	public static final long NONE = -1;
	
	/** Constant for unset leasetime (use global default otherwise no leasetime). */
	public static final long UNSET = -2;
	
	//-------- attributes --------
	
	/** The entries. */
	// DEFAULT_INITIAL_CAPACITY=11, no constructor without ic in java <1.8
	protected PriorityQueue<E> entries = new PriorityQueue<E>(11, new Comparator<E>()
	{
		// could also use simple t1-t2 if Long.MAX_VALUE would be used for no leasetime. */
		public int compare(E e1, E e2)
		{
			long t1 = times.get(e1).longValue();
			long t2 = times.get(e2).longValue();
			int ret = 0;
			if(t1<=0 && t2<=0)
				ret = 0;
			else if(t1>0 && t2>0)
				ret = (int)(t1-t2);
			else if(t1<=0)
				ret = 1;
			else
				ret = -1;
			
//			if(ret<0)
//				System.out.println(e1+" < "+e2);
//			else if(ret>0)
//				System.out.println(e1+" > "+e2);
//			else
//				System.out.println(e1+" = "+e2);
			return ret;
//			return t1>0 && t2>0? (int)(t1-t2): t1<=0 && t2<=0? 0: t1>0? 1: -1;
//			return (int)(t1-t2);
		}
	});
	
	/** The timestamps. */
	protected Map<E, Long> times = new HashMap<E, Long>();
	
	/** The timer. */
	protected IDelayRunner	timer;
	
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
	protected LeaseTimeCollection()
	{
		this(5000);
	}
	
	/**
	 *  Create a new lease time handling object.
	 */
	protected LeaseTimeCollection(long leasetime)
	{
		this(leasetime, null);
//		this.leasetime = leasetime;
	}
	
	/**
	 *  Create a new lease time handling object.
	 */
	protected LeaseTimeCollection(ICommand<E> removecmd)
	{
		// per default no general leasetime
		this(UNSET, removecmd);
	}
	
	/**
	 *  Create a new lease time handling object.
	 */
	protected LeaseTimeCollection(long leasetime, ICommand<E> removecmd)
	{
		this(leasetime, removecmd, null);
	}
	
	/**
	 *  Create a new lease time handling object.
	 */
	protected LeaseTimeCollection(long leasetime, ICommand<E> removecmd, IDelayRunner timer)
	{
		this.leasetime = leasetime;
		this.removecmd = removecmd;
		this.timer = timer!=null? timer: new TimerDelayRunner();
	}
	
	/**
	 *  Create a lease time collection with java util timer.
	 */
	public static <E> ILeaseTimeCollection<E> createLeaseTimeCollection(long leasetime)
	{
		return new SynchronizedLeaseTimeCollection<E>(new LeaseTimeCollection(leasetime));
	}
	
	/**
	 *  Create a lease time collection with java util timer.
	 */
	public static <E> ILeaseTimeCollection<E> createLeaseTimeCollection(long leasetime, ICommand<E> removecmd)
	{
		return new SynchronizedLeaseTimeCollection<E>(new LeaseTimeCollection(leasetime, removecmd));
	}
	
	/**
	 *  Create a lease time collection with java util timer.
	 */
	public static <E> ILeaseTimeCollection<E> createLeaseTimeCollection(long leasetime, ICommand<E> removecmd, IDelayRunner timer, boolean sync)
	{
		return sync? new SynchronizedLeaseTimeCollection<E>(new LeaseTimeCollection(leasetime, removecmd, timer)): new LeaseTimeCollection(leasetime, removecmd, timer);
	}
	
	//-------- methods --------

    public int size()
    {
    	return entries.size();
    }

    public boolean isEmpty()
    {
    	return entries.isEmpty();
    }

    public boolean contains(Object o)
    {
    	return entries.contains(o);
    }

    public Iterator<E> iterator()
    {
    	return entries.iterator();
    }

    public Object[] toArray()
    {
    	return entries.toArray();
    }

    public <T> T[] toArray(T[] a)
    {
    	return entries.toArray(a);
    }

    // Modification Operations

    public boolean add(E e)
    {
    	return add(e, getLeaseTime());
    }
    
    public boolean add(E e, long leasetime)
    {
    	times.put(e, getExpirationTime(leasetime));
    	boolean ret = entries.add(e);
    
    	if(ret)
    		checkStale();
    	
    	return ret;
    }

    public boolean remove(Object o)
    {
    	times.remove(o);
    	boolean ret = entries.remove(o);
    	
    	if(ret)
    		checkStale();
    	
    	return ret;
    }

    // Bulk Operations

    public boolean containsAll(Collection<?> c)
    {
    	return entries.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c)
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

    public boolean removeAll(Collection<?> c)
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

    public boolean retainAll(Collection<?> c)
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

    public void clear()
    {
    	times.clear();
    	entries.clear();
    	checker.cancel();
    }

    // Comparison and hashing

    public boolean equals(Object o)
    {
    	return entries.equals(o);
    }

    /**
     *  Get the hashcode.
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
	public boolean update(E e)
	{
		return update(e, getLeaseTime());
	}
	
	/**
	 *  Add a new entry or update an existing entry.
	 *  @param entry The entry.
	 *  @return True, if new entry.
	 */
	public boolean update(E e, long leasetime)
	{
		boolean ret = !remove(e);
		
		add(e);
		
		return ret;
	}
	
	/**
	 *  Update the timestamp of e.
	 *  @param entry The entry.
	 */
	public void touch(E e)
	{
		touch(e, getLeaseTime());
	}
	
	/**
	 *  Update the timestamp of e.
	 *  @param entry The entry.
	 */
	public void touch(E e, long leasetime)
	{
	   	times.put(e, getExpirationTime(leasetime));
		// Does only reorder when element is added again :-(
		// http://stackoverflow.com/questions/6952660/java-priority-queue-reordering-when-editing-elements
		entries.remove(e);
		entries.add(e);
		checkStale();
	}
	
	/**
	 *  Get the expiration time.
	 *  @param leasetime
	 *  @return
	 */
	protected Long getExpirationTime(long leasetime)
	{
		long ret = UNSET;
		if(leasetime>0)
		{
			ret = getClockTime()+leasetime;
		}
		else if(NONE==leasetime)
		{
			ret = leasetime;
		}
		else if(UNSET==leasetime)
		{
			ret = getLeaseTime(); // global lease time
		}
		
		// if no leasetime can be determine use none
		if(UNSET==ret)
		{
			ret = NONE;
		}
		return Long.valueOf(ret);
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
	public Runnable doWaitFor(long delay, final Runnable step)
	{
		return timer.waitForDelay(delay, step);
		
//		if(timer==null)
//			timer = new Timer(true);
//		
//		final TimerTask tt = new TimerTask()
//		{
//			public void run()
//			{
//				step.run();
//			}
//		};
//		
//		timer.schedule(tt, delay);
//		
//		return new Runnable()
//		{
//			public void run()
//			{
//				tt.cancel();
//			}
//		};
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
	 *  Get the leasetime.
	 */
	public long getLeaseTime()
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
	 *  The checker for removing entries.
	 */
	public class Checker implements Runnable
	{
		Runnable cancel = null;

		/**
		 *  The run method.
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
//							System.out.println("first: "+first);
//							System.out.println("times: "+times);
//							System.out.println("entries: "+entries);
							
							long etime = times.get(first).longValue();
							if(etime>0)
							{
								long curtime = getClockTime();
								delta = etime-curtime;
								if(delta<=0)
								{
//									System.out.println("removed: "+etime+" "+first+" "+System.currentTimeMillis());
									remove(first);
	//								entryDeleted(first);
									if(removecmd!=null)
										removecmd.execute(first);
								}
								else
								{
//									System.out.println("delta is: "+delta+" "+first);
									break;
								}
							}
							else
							{
//								System.out.println("save value: "+first);
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
		 *  Cancel the current wait.
		 */
		public void cancel()
		{
			if(cancel!=null)
				cancel.run();
		}
	}

	/**
	 *  Synchronized lease time collection.
	 */
    public static class SynchronizedLeaseTimeCollection<E> implements ILeaseTimeCollection<E>, Serializable 
    {
        final ILeaseTimeCollection<E> c;  // Backing Collection
        final Object mutex;     // Object on which to synchronize

        public SynchronizedLeaseTimeCollection(ILeaseTimeCollection<E> c) 
        {
            if (c==null)
                throw new NullPointerException();
            this.c = c;
            mutex = this;
        }
        
        public SynchronizedLeaseTimeCollection(ILeaseTimeCollection<E> c, Object mutex) 
        {
            this.c = c;
            this.mutex = mutex;
        }

        public int size() 
        {
            synchronized (mutex) {return c.size();}
        }
        
        public boolean isEmpty() 
        {
            synchronized (mutex) {return c.isEmpty();}
        }
        
        public boolean contains(Object o) 
        {
            synchronized (mutex) {return c.contains(o);}
        }
        
        public Object[] toArray() 
        {
            synchronized (mutex) {return c.toArray();}
        }
        
        public <T> T[] toArray(T[] a) 
        {
            synchronized (mutex) {return c.toArray(a);}
        }

        public Iterator<E> iterator() 
        {
            return c.iterator(); // Must be manually synched by user!
        }

        public boolean add(E e) 
        {
            synchronized (mutex) {return c.add(e);}
        }
        
        public boolean remove(Object o) 
        {
            synchronized (mutex) {return c.remove(o);}
        }

        public boolean containsAll(Collection<?> coll) 
        {
            synchronized (mutex) {return c.containsAll(coll);}
        }
        
        public boolean addAll(Collection<? extends E> coll) 
        {
            synchronized (mutex) {return c.addAll(coll);}
        }
        
        public boolean removeAll(Collection<?> coll) 
        {
            synchronized (mutex) {return c.removeAll(coll);}
        }
        
        public boolean retainAll(Collection<?> coll) 
        {
            synchronized (mutex) {return c.retainAll(coll);}
        }
        
        public void clear() 
        {
            synchronized (mutex) {c.clear();}
        }
        
        public String toString() 
        {
            synchronized (mutex) {return c.toString();}
        }
        
        private void writeObject(ObjectOutputStream s) throws IOException 
        {
            synchronized (mutex) {s.defaultWriteObject();}
        }
        

        public boolean add(E e, long leasetime)
        {
        	 synchronized(mutex) {return c.add(e, leasetime);}
        }
    	
    	public boolean update(E e)
    	{
    		 synchronized(mutex) {return c.update(e);}
    	}
    	
    	/**
    	 *  Add a new entry or update an existing entry.
    	 *  @param entry The entry.
    	 *  @return True, if new entry.
    	 */
    	public boolean update(E e, long leasetime)
    	{
    		 synchronized(mutex) {return c.update(e, leasetime);}
    	}
    	
    	/**
    	 *  Update the timestamp of e.
    	 *  @param entry The entry.
    	 */
    	public void touch(E e)
    	{
    		 synchronized(mutex) {c.touch(e);}
    	}
    	
    	/**
    	 *  Update the timestamp of e.
    	 *  @param entry The entry.
    	 */
    	public void touch(E e, long leasetime)
    	{
    		 synchronized(mutex) {c.touch(e, leasetime);}
    	}
    }
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
//		LeaseTimeCollection<Integer> col = new LeaseTimeCollection<Integer>(3000);
		ILeaseTimeCollection<Integer> col = createLeaseTimeCollection(3000);
		
//		col.add(1);
//		col.add(2);
//		col.add(-1,-1);
//		col.add(3);
//		col.add(-2,-2);
		
		
		col.add(33, -1);
		
		for(int i=0; i<5; i++)
		{
			col.add(Integer.valueOf(i));
			
//			if(i%1000==0)
			{
				try
				{
					Thread.sleep(1000);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		col.add(44, -1);
		
		int cnt = 0;
		while(true)
		{
			try
			{
				if(cnt++==3)
					col.touch(44, 1500);
				Thread.sleep(1000);
				System.out.print(".");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
