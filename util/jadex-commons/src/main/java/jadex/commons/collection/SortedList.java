package jadex.commons.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;


/**
 *  A sorted list allowing duplicates of elements
 *  (unlike java.util.TreeSet).
 *  The list is kept sorted, while elements are being added.
 */
public class SortedList<T>	extends LinkedList<T>
{
	//-------- attributes --------

	/** The ordering direction of the list. */
	protected boolean ascending;

	/** The comparator to use (if any). */
	protected Comparator<? super T>	comp;
	
	//-------- constrcutors --------

	/**
	 *  Constructs an empty list with ascending order.
	 */
	public SortedList()
	{
		this(true);
	}

	/**
	 *  Constructs an empty list with given order.
	 *  @param ascending	True, if the list should sort ascending.
	 */
	public SortedList(boolean ascending)
	{
		this.ascending	= ascending;
	}

	/**
	 *  Constructs an empty list with given order.
	 *  @param comp	A comparator to use for comparing elements.
	 *  @param ascending	True, if the list shoudl sort ascending.
	 */
	public SortedList(Comparator<? super T> comp, boolean ascending)
	{
		this.comp	= comp;
		this.ascending	= ascending;
	}

	//-------- methods --------

	/**
	 * Inserts the given element at the beginning of this list.
	 * 
	 * @param o the element to be inserted at the beginning of this list.
	 */
	public void addFirst(T o)
	{
		insertElement(0, o);
	}

	/**
	 * Appends the given element to the end of this list.  (Identical in
	 * function to the <tt>add</tt> method; included only for consistency.)
	 * 
	 * @param o the element to be inserted at the end of this list.
	 */
	public void addLast(T o)
	{
		insertElement(size(), o);
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * @param o element to be appended to this list.
	 * @return <tt>true</tt> (as per the general contract of
	 * <tt>Collection.add</tt>).
	 */
	public boolean add(T o)
	{
		addLast(o);
	    return true;
	}

	/**
	 * Appends all of the elements in the specified collection to the end of
	 * this list, in the order that they are returned by the specified
	 * collection's iterator.  The behavior of this operation is undefined if
	 * the specified collection is modified while the operation is in
	 * progress.  (This implies that the behavior of this call is undefined if
	 * the specified Collection is this list, and this list is nonempty.)
	 *
	 * @param c the elements to be inserted into this list.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * @throws NullPointerException if the specified collection is null.
	 */
	public boolean addAll(Collection<? extends T> c)
	{
		for(Iterator<? extends T> it=c.iterator(); it.hasNext(); )
		{
			addLast(it.next());
		}
		return true;
	}

	/**
	 * Inserts all of the elements in the specified collection into this
	 * list, starting at the specified position.
	 *
	 * @param index index at which to insert first element
	 *		    from the specified collection.
	 * @param c elements to be inserted into this list.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * @throws IndexOutOfBoundsException if the specified index is out of
	 *            range (<tt>index &lt; 0 || index &gt; size()</tt>).
	 * @throws NullPointerException if the specified collection is null.
	 */
	public boolean addAll(int index, Collection<? extends T> c)
	{
		for(Iterator<? extends T> it=c.iterator(); it.hasNext(); )
		{
			int	idx	= insertElement(index, it.next());
			if(idx<=index) index++;
	    }
	    return true;
	}

	/**
	 * Inserts the specified element at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any
	 * subsequent elements to the right (adds one to their indices).
	 *
	 * @param index index at which the specified element is to be inserted.
	 * @param element element to be inserted.
	 * 
	 * @throws IndexOutOfBoundsException if the specified index is out of
	 *		  range (<tt>index &lt; 0 || index &gt; size()</tt>).
	 */
	public void add(int index, T element)
	{
		insertElement(index, element);
	}

	//-------- helper methods --------

	/**
	 *  Insert an element into the list.
	 *  @param index	The index where to start insertion.
	 *  @param obj	The element to insert.
	 *  @return The index where the element was actually inserted.
	 */
	public int	insertElement(int index, T obj)
	{
		while(index>0 && compare(get(index-1), obj)>0)
		{
			index--;
		}
		while(index<size() && compare(get(index), obj)<0)
		{
			index++;
		}
		super.add(index, obj);
		return index;
	}

	/**
	 *  Compare two elements.
	 */
	protected int	compare(T o1, T o2)
	{
		int	cmp = 0;

		if(comp==null)
		{
			if(o1!=null && o2!=null)
			{
				cmp	= ((Comparable<T>)o1).compareTo(o2);
			}
			else if(o1!=null)
			{
				cmp	= -1;
			}
			else if(o2!=null)
			{
				cmp	= 1;
			}
		}
		else
		{
			cmp	= comp.compare(o1, o2);
		}

		return ascending ? cmp : -cmp;
	}
}

