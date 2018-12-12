package jadex.commons.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 *  A wrapper for a list to detect undesired
 *  concurrent access.
 */
public class ConcurrencyCheckingList implements List, Serializable
{
	//------- attributes --------
	
	/** The wrapped list. */
	protected List	list;
	
	/** A map for counting thread entries. */
	protected transient	Map	threads;
	
	//-------- constructors --------
	
	/**
	 *  Wrap the given list.
	 */
	public ConcurrencyCheckingList(List list)
	{
		this.list	= list;
	}
	
	//-------- List interface --------

	public boolean add(Object o)
	{
		entry();
		boolean	ret	= list.add(o);
		exit();
		return ret;
	}

	public void add(int index, Object element)
	{
		entry();
		list.add(index, element);
		exit();
	}

	public boolean addAll(Collection c)
	{
		entry();
		boolean	ret	= list.addAll(c);
		exit();
		return ret;
	}

	public boolean addAll(int index, Collection c)
	{
		entry();
		boolean	ret	= list.addAll(index, c);
		exit();
		return ret;
	}

	public void clear()
	{
		entry();
		list.clear();
		exit();
	}

	public boolean contains(Object o)
	{
		entry();
		boolean	ret	= list.contains(o);
		exit();
		return ret;
	}

	public boolean containsAll(Collection c)
	{
		entry();
		boolean	ret	= list.containsAll(c);
		exit();
		return ret;
	}

	public Object get(int index)
	{
		entry();
		Object	ret	= list.get(index);
		exit();
		return ret;
	}

	public int indexOf(Object o)
	{
		entry();
		int	ret	= list.indexOf(o);
		exit();
		return ret;
	}

	public boolean isEmpty()
	{
		entry();
		boolean	ret	= list.isEmpty();
		exit();
		return ret;
	}

	public Iterator iterator()
	{
		entry();
		Iterator	ret	= list.iterator();
		exit();
		return ret;
	}

	public int lastIndexOf(Object o)
	{
		entry();
		int	ret	= list.lastIndexOf(o);
		exit();
		return ret;
	}

	public ListIterator listIterator()
	{
		entry();
		ListIterator	ret	= list.listIterator();
		exit();
		return ret;
	}

	public ListIterator listIterator(int index)
	{
		entry();
		ListIterator	ret	= list.listIterator(index);
		exit();
		return ret;
	}

	public Object remove(int index)
	{
		entry();
		Object	ret	= list.remove(index);
		exit();
		return ret;
	}

	public boolean remove(Object o)
	{
		entry();
		boolean	ret	= list.remove(o);
		exit();
		return ret;
	}

	public boolean removeAll(Collection c)
	{
		entry();
		boolean	ret	= list.removeAll(c);
		exit();
		return ret;
	}

	public boolean retainAll(Collection c)
	{
		entry();
		boolean	ret	= list.retainAll(c);
		exit();
		return ret;
	}

	public Object set(int index, Object element)
	{
		entry();
		Object	ret	= list.set(index, element);
		exit();
		return ret;
	}

	public int size()
	{
		entry();
		int	ret	= list.size();
		exit();
		return ret;
	}

	public List subList(int fromIndex, int toIndex)
	{
		entry();
		List	ret	= list.subList(fromIndex, toIndex);
		exit();
		return ret;
	}

	public Object[] toArray()
	{
		entry();
		Object[]	ret	= list.toArray();
		exit();
		return ret;
	}

	public Object[] toArray(Object[] a)
	{
		entry();
		Object[]	ret	= list.toArray(a);
		exit();
		return ret;
	}

	//-------- helper methods --------
	
	/**
	 *  Called for each method entry.
	 */
	protected synchronized void	entry()
	{
		if(threads==null)
			this.threads	= SCollection.createHashMap();

		Integer	cnt	= (Integer)this.threads.get(Thread.currentThread());
		cnt	= Integer.valueOf(cnt!=null ? cnt.intValue()+1 : 1);
		threads.put(Thread.currentThread(), cnt);
		
		if(threads.size()>1)
		{
			throw new RuntimeException("Concurrent access to list "+list.getClass().getName()+"@"+list.hashCode());
		}
	}

	/**
	 *  Called for each method exit.
	 */
	protected synchronized void	exit()
	{
		if(threads==null)
			this.threads	= SCollection.createHashMap();

		// Check again to throw exception on second thread also.
		if(threads.size()>1)
		{
			throw new RuntimeException("Concurrent access to list "+list.getClass().getName()+"@"+list.hashCode());
		}

		Integer	cnt	= (Integer)this.threads.get(Thread.currentThread());
		assert cnt!=null && cnt.intValue()>0;
		if(cnt.intValue()>1)
		{
			cnt	= Integer.valueOf(cnt.intValue()-1);
			threads.put(Thread.currentThread(), cnt);
		}
		else
		{
			threads.remove(Thread.currentThread());
		}
	}
}
