package jadex.bdiv3.runtime.wrappers;

import jadex.bdiv3.runtime.impl.BDIAgentInterpreter;
import jadex.rules.eca.Event;
import jadex.rules.eca.RuleSystem;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * 
 */
public class ListWrapper<T> extends CollectionWrapper<T> implements List<T>
{
	/**
	 *  Create a new list wrapper.
	 */
	public ListWrapper(List<T> delegate, BDIAgentInterpreter interpreter, 
		String addevent, String remevent, String changeevent)
	{
		super(delegate, interpreter, addevent, remevent, changeevent);
	}

	/**
	 * 
	 */
	public List<T> getList()
	{
		return (List<T>)delegate;
	}
	
	/**
	 *  
	 */
	public boolean addAll(int index, Collection<? extends T> c)
	{
		// todo? or calls internally add?
		return getList().addAll(index, c);
	}

	/**
	 *  
	 */
	public T get(int index)
	{
		return getList().get(index);
	}

	/**
	 *  
	 */
	public T set(int index, T element)
	{
		T ret = getList().set(index, element);
		unobserveValue(ret);
		observeValue(element);
		getRuleSystem().addEvent(new Event(changeevent, new Object[]{ret, element, new Integer(index)}));
		return ret;
	}

	/**
	 *  
	 */
	public void add(int index, T element)
	{
		getList().add(index, element);
		observeValue(element);
		getRuleSystem().addEvent(new Event(addevent, element));
	}

	/**
	 *  
	 */
	public T remove(int index)
	{
		T ret = getList().remove(index);
		unobserveValue(ret);
		getRuleSystem().addEvent(new Event(remevent, ret));
		return ret;
	}

	/**
	 *  
	 */
	public int indexOf(Object o)
	{
		return getList().indexOf(o);
	}

	/**
	 *  
	 */
	public int lastIndexOf(Object o)
	{
		return getList().lastIndexOf(o);
	}

	/**
	 *  
	 */
	public ListIterator<T> listIterator()
	{
		return getList().listIterator();
	}

	/**
	 *  
	 */
	public ListIterator<T> listIterator(int index)
	{
		return getList().listIterator(index);
	}

	/**
	 *  
	 */
	public List<T> subList(int fromIndex, int toIndex)
	{
		return getList().subList(fromIndex, toIndex);
	}
}
