package jadex.commons.collection;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;

/**
 *  The extended reference queue allows for adding (weak) 
 *  object with an associated action. Whenever the object
 *  becomes unreferenced its corresponding weak entry can
 *  be fetched from the queue and when this is done the
 *  associated action will automatically be executed.
 */
public class ActionReferenceQueue extends ReferenceQueue
{
	//-------- attributes --------

	/** The list of elements. */
	protected transient List entries;

	//-------- constructors --------

	/**
	 *  Create a new list.
	 */
	public ActionReferenceQueue()
	{
		this.entries = new ArrayList();
	}

	//-------- methods --------

	/**
	 *  Add an object with an associated value.
	 *  @param o The object (becomes weak reference).
	 *  @param val The value.
	 */
	public boolean addEntry(Object o, Runnable action)
	{
		if(o==null)
			throw new NullPointerException("Null elements not supported.");

		entries.add(new WeakEntry(o, action, this));
		return true;
	}
	
	/**
	 *  Remove an entry.
	 */
	public Runnable removeEntry(Object obj)
	{
		if(obj==null)
			throw new IllegalArgumentException("Must not be null.");
		
		// todo: can this be done more efficiently?
		WeakEntry find = new WeakEntry(obj, null);
		WeakEntry ret = null;
		for(int i=0; i<entries.size(); i++)
		{
			Object tmp = entries.get(i);
			if(tmp.equals(find))
			{
				ret = (WeakEntry)tmp;
				entries.remove(i);
				break;
			}
		}
		
		//entries.remove(ret);
	
		return ret!=null? (Runnable)ret.getArgument(): null;
	}
	
	/**
	 *  Get the size of the entries (existing objects).
	 *  Does not expunge stale entries.
	 *  @return The number of existing entries.
	 */
	public int getEntriesSize()
	{
		return entries.size();
	}
	
	/**
	 *  Remove an element from the queue.
	 *  @return A weak entry.
	 */
	public Reference remove() throws InterruptedException
	{
		WeakEntry ret = (WeakEntry)super.remove();
		((Runnable)ret.getArgument()).run();
		entries.remove(ret);
		return ret;
	}
	
	/**
	 *  Remove an element from the queue.
	 *  @return A weak entry.
	 */
	public Reference remove(long timeout) throws IllegalArgumentException ,InterruptedException
	{
		WeakEntry ret = (WeakEntry)super.remove(timeout);
		((Runnable)ret.getArgument()).run();
		entries.remove(ret);
		return ret;
	}
	
	/**
	 *  Poll a stale entry.
	 *  @return A weak entry or null (if none is stale).
	 */
	public Reference poll()
	{
		WeakEntry ret = (WeakEntry)super.poll();
		if(ret!=null)
		{
//			System.out.println("Found we: "+ret);
			((Runnable)ret.getArgument()).run();
			entries.remove(ret);
		}
		return ret;
	}

    /**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return ""+entries;
	}
}
