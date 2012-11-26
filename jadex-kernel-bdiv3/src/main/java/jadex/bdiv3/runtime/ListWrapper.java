package jadex.bdiv3.runtime;

import jadex.rules.eca.Event;
import jadex.rules.eca.RuleSystem;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 
 */
public class ListWrapper<T> implements List<T>
{
	/** The delegate list. */
	protected List<T> delegate;
	
	/** The rulesystem. */
	protected RuleSystem rulesystem;
	
	/** The add event name. */
	protected String addevent;
	
	/** The remove event name. */
	protected String remevent;
	
	/** The change event name. */
	protected String changeevent;
	
	/**
	 * 
	 */
	public ListWrapper(List<T> delegate, RuleSystem rulesystem, 
		String addevent, String remevent, String changeevent)
	{
		this.delegate = delegate;
		this.rulesystem = rulesystem;
		this.addevent = addevent;
		this.remevent = remevent;
		this.changeevent = changeevent;
	}

	public int size()
	{
		return delegate.size();
	}

	public boolean isEmpty()
	{
		return delegate.isEmpty();
	}

	public boolean contains(Object o)
	{
		return delegate.contains(o);
	}

	public Iterator<T> iterator()
	{
		return delegate.iterator();
	}

	public Object[] toArray()
	{
		return delegate.toArray();
	}

	public <T> T[] toArray(T[] a)
	{
		return delegate.toArray(a);
	}

	public boolean add(T e)
	{
		boolean ret = delegate.add(e);
		rulesystem.addEvent(new Event(addevent, e));
		return ret;
	}

	public boolean remove(Object o)
	{
		boolean ret = delegate.remove(o);
		rulesystem.addEvent(new Event(remevent, o));
		return ret;
	}

	public boolean containsAll(Collection< ? > c)
	{
		return delegate.containsAll(c);
	}

	public boolean addAll(Collection< ? extends T> c)
	{
		// todo
		return delegate.addAll(c);
	}

	public boolean addAll(int index, Collection< ? extends T> c)
	{
		// todo
		return delegate.addAll(index, c);
	}

	public boolean removeAll(Collection< ? > c)
	{
		// todo
		return delegate.removeAll(c);
	}

	public boolean retainAll(Collection< ? > c)
	{
		// todo
		return delegate.retainAll(c);
	}

	public void clear()
	{
		// todo
		delegate.clear();
	}

	public T get(int index)
	{
		return delegate.get(index);
	}

	public T set(int index, T element)
	{
		T ret = delegate.set(index, element);
		rulesystem.addEvent(new Event(changeevent, element));
		return ret;
	}

	public void add(int index, T element)
	{
		// todo
		delegate.add(index, element);
	}

	public T remove(int index)
	{
		// todo
		return delegate.remove(index);
	}

	public int indexOf(Object o)
	{
		return delegate.indexOf(o);
	}

	public int lastIndexOf(Object o)
	{
		return delegate.lastIndexOf(o);
	}

	public ListIterator<T> listIterator()
	{
		return delegate.listIterator();
	}

	public ListIterator<T> listIterator(int index)
	{
		return delegate.listIterator(index);
	}

	public List<T> subList(int fromIndex, int toIndex)
	{
		return delegate.subList(fromIndex, toIndex);
	}
}
