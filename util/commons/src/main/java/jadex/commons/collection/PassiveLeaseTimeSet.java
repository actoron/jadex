package jadex.commons.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

import jadex.commons.ICommand;
import jadex.commons.Tuple2;

/**
 *  Collection that remove elements after a lease time on trigger.
 *  This class is not synchronized.
 */
public class PassiveLeaseTimeSet<E> implements ILeaseTimeSet<E>
{
	/** Constant for no leasetime. */
	public static final long NONE = -1;
	
	/** Constant for unset leasetime (use global default otherwise no leasetime). */
	public static final long UNSET = -2;
	
	//-------- attributes --------
	
	/** The entries. */
	// DEFAULT_INITIAL_CAPACITY=11, no constructor without ic in java <1.8
	@SuppressWarnings("serial")
	protected PriorityQueue<E> entries = new PriorityQueue<E>(11, new Comparator<E>()
	{
		// could also use simple t1-t2 if Long.MAX_VALUE would be used for no leasetime. */
		public int compare(E e1, E e2)
		{
			long t1 = times.get(e1).getFirstEntity().longValue();
			long t2 = times.get(e2).getFirstEntity().longValue();
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
	})
	{
		// Do not allow duplicates
		public boolean add(E e) 
		{
	        if(contains(e))
	        	return false;
	        return super.add(e);
	    }
	};
	
	/** The timestamps. */
	protected Map<E, Tuple2<Long, Long>> times = new HashMap<E, Tuple2<Long, Long>>();
	
	/** The leasetime. */
	protected long leasetime;
	
	/** The cleaner. */
	protected ICommand<Tuple2<E, Long>> removecmd;

	//-------- constructors --------
	
	/**
	 *  Create a new lease time handling object.
	 */
	public PassiveLeaseTimeSet()
	{
		this(5000);
	}
	
	/**
	 *  Create a new lease time handling object.
	 */
	public PassiveLeaseTimeSet(long leasetime)
	{
		this(leasetime, null);
//		this.leasetime = leasetime;
	}
	
	/**
	 *  Create a new lease time handling object.
	 */
	public PassiveLeaseTimeSet(ICommand<Tuple2<E, Long>> removecmd)
	{
		// per default no general leasetime
		this(UNSET, removecmd);
	}
	
	/**
	 *  Create a new lease time handling object.
	 */
	public PassiveLeaseTimeSet(long leasetime, ICommand<Tuple2<E, Long>> removecmd)
	{
		this.leasetime = leasetime;
		this.removecmd = removecmd;
	}
	
	//-------- methods --------

	/**
	 *  Set the remove cmd.
	 */
	public void setRemoveCommand(ICommand<Tuple2<E, Long>> cmd)
	{
		this.removecmd = cmd;
	}
	
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
    	times.put(e, new Tuple2<Long, Long>(getExpirationTime(leasetime), leasetime));
    	boolean ret = entries.add(e);
    
    	//if(ret)
    		checkStale();
    	
    	return ret;
    }

    public boolean remove(Object o)
    {
    	boolean ret = entries.remove(o);
    	times.remove(o);
    	
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

    @SuppressWarnings("unchecked")
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
	   	times.put(e, new Tuple2<Long, Long>(getExpirationTime(leasetime), leasetime));
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
	 *  Start removing discovered entries.
	 */
	public void checkStale()
	{
		try
		{
			long delta = -1;
			
			E first = entries.peek();
			
			if(first!=null)
			{
				long etime = times.get(first).getFirstEntity().longValue();
				Long lease = times.get(first).getSecondEntity();
				if(etime>0)
				{
					long curtime = getClockTime();
					delta = etime-curtime;
					if(delta<=0)
					{
						remove(first);
						if(removecmd!=null)
							removecmd.execute(new Tuple2<E, Long>(first, lease));
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Get the current time.
	 */
	protected long getClockTime()
	{
		return System.currentTimeMillis();
	}
	
	/**
	 *  Get the leasetime.
	 */
	public long getLeaseTime()
	{
		return leasetime;
	}
}
