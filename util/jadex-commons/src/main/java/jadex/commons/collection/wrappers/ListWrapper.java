package jadex.commons.collection.wrappers;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * 
 */
public abstract class ListWrapper<T> extends CollectionWrapper<T> implements List<T>
{
	/**
	 *  Create a new wrapper.
	 *  @param delegate The delegate.
	 */
	public ListWrapper(List<T> delegate)
	{
		super(delegate);
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
		entryChanged(ret, element, index);
//		unobserveValue(ret);
//		observeValue(element);
//		getRuleSystem().addEvent(new Event(changeevent, new ChangeInfo<T>(element, ret, index)));
////				new Object[]{ret, element, Integer.valueOf(index)}));
//		publishToolBeliefEvent();
		return ret;
	}

	/**
	 *  
	 */
	public void add(int index, T element)
	{
		getList().add(index, element);
		entryAdded(element, index);
//		observeValue(element);
//		getRuleSystem().addEvent(new Event(addevent, new ChangeInfo<T>(element, null, index)));
//		publishToolBeliefEvent();
	}

	/**
	 *  
	 */
	public T remove(int index)
	{
		T ret = getList().remove(index);
		entryRemoved(ret, index);
//		unobserveValue(ret);
//		getRuleSystem().addEvent(new Event(remevent, new ChangeInfo<T>(null, ret, index)));
//		publishToolBeliefEvent();
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
