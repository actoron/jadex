package jadex.commons.collection;

import jadex.commons.SUtil;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 *  A list with weak entries.
 */
public class WeakList<E> implements List<E>, Serializable
{
	//-------- attributes --------

	/** The list of elements. */
	protected transient Reference<E>[] array;

	/** The number of elements. */
	protected int	size;

	/** The state (to check for modifications). */
	protected int	state;

	/** Reference queue for garbage-collected elements. */
    protected transient ReferenceQueue queue;

	//-------- constructors --------

	/**
	 *  Create a new list.
	 */
	public WeakList()
	{
		this.array	= new Reference[10];
		this.size	= 0;
		this.state	= 0;
		this.queue	= new ReferenceQueue();
	}

	//-------- methods --------

	/**
	 * Returns the number of elements in this list.  If this list contains
	 * more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 * @return the number of elements in this list.
	 */
	public int size()
	{
		expungeStaleEntries();

		return size;
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 * @return <tt>true</tt> if this list contains no elements.
	 */
	public boolean isEmpty()
	{
		expungeStaleEntries();

		return size==0;
	}

	/**
	 * Returns <tt>true</tt> if this list contains the specified element.
	 * More formally, returns <tt>true</tt> if and only if this list contains
	 * at least one element <tt>e</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 * @param o element whose presence in this list is to be tested.
	 * @return <tt>true</tt> if this list contains the specified element.
	 * @throws ClassCastException if the type of the specified element
	 * is incompatible with this list (optional).
	 * @throws NullPointerException if the specified element is null.
	 */
	public boolean contains(Object o)
	{
		expungeStaleEntries();

		for(int i=0; i<size; i++)
			if(o.equals(array[i].get()))
				return true;
		
		return false;
	}

	/**
	 * Returns an iterator over the elements in this list in proper sequence.
	 * Can handle garbage collection of elements, but fails fast in the presence
	 * of concurrent modifications. 
	 * @return an iterator over the elements in this list in proper sequence.
	 */
	public Iterator<E> iterator()
	{
		expungeStaleEntries();

		return new Iterator<E>()
		{
			int i	= 0;
			int	removeindex	= -1;
			int	startstate	= state;
			E	next	= null;
			
			public boolean hasNext()
			{
				if(startstate!=state)
					throw new ConcurrentModificationException("List must not be modified while iterating.");
				
				// Find next element.
				if(next==null && i<size)
				{
					next=array[i].get();
					while(next==null && i<size)
					{
						i++;
						next=array[i].get();
					}
				}

				return next!=null;
			}
			
			public E next()
			{
				// Find next element.
				E	ret;
				if(hasNext())
					ret	= next;
				else
					throw new NoSuchElementException("No more elements in iterator.");

				// Move cursor and reset next element.
				removeindex	= i;
				i++;
				next	= null;
				
				return ret;
			}
			
			public void remove()
			{
				if(removeindex==-1)
					throw new IllegalStateException("Remove can only be called once after a call to next() method.");
				if(startstate!=state)
					throw new ConcurrentModificationException("List must not be modified while iterating.");
				
				WeakList.this.remove(removeindex);
				removeindex	= -1;
				startstate	= state;	// Modification through iterator is allowed.
			}
		};
	}

	/**
	 * Returns an array containing all of the elements in this list in proper
	 * sequence.  Obeys the general contract of the
	 * <tt>Collection.toArray</tt> method.
	 * @return an array containing all of the elements in this list in proper
	 *         sequence.
	 * @see java.util.Arrays#asList(Object[])
	 */
	public Object[] toArray()
	{
		expungeStaleEntries();

		Object[]	ret	= new Object[size];
		int	index	= 0;
		for(int i=0; i<size; i++)
		{
			Object	o	= array[i].get();
			if(o!=null)
				ret[index++]	= o;
		}
		
		// Shrink array, if necessary. 
		if(index<size)
		{
			Object[]	ret2	= new Object[index];
			System.arraycopy(ret, 0, ret2, 0, index);
			ret	= ret2;
		}
		
		return ret;
	}

	/**
	 * Returns an array containing all of the elements in this list in proper
	 * sequence; the runtime type of the returned array is that of the
	 * specified array.  Obeys the general contract of the
	 * <tt>Collection.toArray(Object[])</tt> method.
	 * @param ret the array into which the elements of this list are to
	 * be stored, if it is big enough; otherwise, a new array of the
	 * same runtime type is allocated for this purpose.
	 * @return an array containing the elements of this list.
	 * @throws ArrayStoreException if the runtime type of the specified array
	 * is not a supertype of the runtime type of every element in
	 * this list.
	 * @throws NullPointerException if the specified array is <tt>null</tt>.
	 */
	public Object[] toArray(Object[] ret)
	{
		expungeStaleEntries();

		if(ret.length<size)
			ret	= (Object[])Array.newInstance(ret.getClass().getComponentType(), size);

		int	index	= 0;
		for(int i=0; i<size; i++)
		{
			Object	o	= array[i].get();
			if(o!=null)
				ret[index++]	= o;
		}
		
		// Shrink array, if necessary. 
		if(index<size)
		{
			Object[]	ret2	= (Object[])Array.newInstance(ret.getClass().getComponentType(), index);
			System.arraycopy(ret, 0, ret2, 0, index);
			ret	= ret2;
		}
		
		return ret;
	}

	/**
	 * Appends the specified element to the end of this list (optional
	 * operation). <p>
	 *
	 * Lists that support this operation may place limitations on what
	 * elements may be added to this list.  In particular, some
	 * lists will refuse to add null elements, and others will impose
	 * restrictions on the type of elements that may be added.  List
	 * classes should clearly specify in their documentation any restrictions
	 * on what elements may be added.
	 * @param o element to be appended to this list.
	 * @return <tt>true</tt> (as per the general contract of the
	 *         <tt>Collection.add</tt> method).
	 * @throws UnsupportedOperationException if the <tt>add</tt> method is not
	 * supported by this list.
	 * @throws ClassCastException if the class of the specified element
	 * prevents it from being added to this list.
	 * @throws NullPointerException if the specified element is nul.
	 * @throws IllegalArgumentException if some aspect of this element
	 * prevents it from being added to this list.
	 */
	public boolean add(E o)
	{
		expungeStaleEntries();

		if(o==null)
			throw new NullPointerException("Null elements not supported.");

		if(array.length==size)
		{
			Reference[]	array2	= new Reference[array.length*2];
			System.arraycopy(array, 0, array2, 0, size);
			array	= array2;
		}
		
		array[size++]	= new WeakReference(o, queue);
		state++;
		return true;
	}
	
	/**
	 * Removes the first occurrence in this list of the specified element
	 * (optional operation).  If this list does not contain the element, it is
	 * unchanged.  More formally, removes the element with the lowest index i
	 * such that <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt> (if
	 * such an element exists).
	 * @param o element to be removed from this list, if present.
	 * @return <tt>true</tt> if this list contained the specified element.
	 * @throws ClassCastException if the type of the specified element
	 * is incompatible with this list (optional).
	 * @throws NullPointerException if the specified element is null.
	 * @throws UnsupportedOperationException if the <tt>remove</tt> method is
	 * not supported by this list.
	 */
	public boolean remove(Object o)
	{
		expungeStaleEntries();

		for(int i=0; i<size; i++)
		{
			if(o.equals(array[i].get()))
			{
				size--;
				if(i<size)
					System.arraycopy(array, i+1, array, i, size-i);
				array[size]	= null;
				state++;
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Returns <tt>true</tt> if this list contains all of the elements of the
	 * specified collection.
	 * @param c collection to be checked for containment in this list.
	 * @return <tt>true</tt> if this list contains all of the elements of the
	 *         specified collection.
	 * @throws ClassCastException if the types of one or more elements
	 * in the specified collection are incompatible with this
	 * list (optional).
	 * @throws NullPointerException if the specified collection contains one
	 * or more null elements and this list does not support null
	 * elements (optional).
	 * @throws NullPointerException if the specified collection is
	 * <tt>null</tt>.
	 * @see #contains(Object)
	 */
	public boolean containsAll(Collection c)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Appends all of the elements in the specified collection to the end of
	 * this list, in the order that they are returned by the specified
	 * collection's iterator (optional operation).  The behavior of this
	 * operation is unspecified if the specified collection is modified while
	 * the operation is in progress.  (Note that this will occur if the
	 * specified collection is this list, and it's nonempty.)
	 * @param collection collection whose elements are to be added to this list.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * @throws UnsupportedOperationException if the <tt>addAll</tt> method is
	 * not supported by this list.
	 * @throws ClassCastException if the class of an element in the specified
	 * collection prevents it from being added to this list.
	 * @throws NullPointerException if the specified collection contains one
	 * or more null elements and this list does not support null
	 * elements, or if the specified collection is <tt>null</tt>.
	 * @throws IllegalArgumentException if some aspect of an element in the
	 * specified collection prevents it from being added to this
	 * list.
	 * @see #add(Object)
	 */
	public boolean addAll(Collection collection)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Inserts all of the elements in the specified collection into this
	 * list at the specified position (optional operation).  Shifts the
	 * element currently at that position (if any) and any subsequent
	 * elements to the right (increases their indices).  The new elements
	 * will appear in this list in the order that they are returned by the
	 * specified collection's iterator.  The behavior of this operation is
	 * unspecified if the specified collection is modified while the
	 * operation is in progress.  (Note that this will occur if the specified
	 * collection is this list, and it's nonempty.)
	 * @param index index at which to insert first element from the specified
	 * collection.
	 * @param collection elements to be inserted into this list.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * @throws UnsupportedOperationException if the <tt>addAll</tt> method is
	 * not supported by this list.
	 * @throws ClassCastException if the class of one of elements of the
	 * specified collection prevents it from being added to this
	 * list.
	 * @throws NullPointerException if the specified collection contains one
	 * or more null elements and this list does not support null
	 * elements, or if the specified collection is <tt>null</tt>.
	 * @throws IllegalArgumentException if some aspect of one of elements of
	 * the specified collection prevents it from being added to
	 * this list.
	 * @throws IndexOutOfBoundsException if the index is out of range (index
	 * &lt; 0 || index &gt; size()).
	 */
	public boolean addAll(int index, Collection collection)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes from this list all the elements that are contained in the
	 * specified collection (optional operation).
	 * @param c collection that defines which elements will be removed from
	 * this list.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * @throws UnsupportedOperationException if the <tt>removeAll</tt> method
	 * is not supported by this list.
	 * @throws ClassCastException if the types of one or more elements
	 * in this list are incompatible with the specified
	 * collection (optional).
	 * @throws NullPointerException if this list contains one or more
	 * null elements and the specified collection does not support
	 * null elements (optional).
	 * @throws NullPointerException if the specified collection is
	 * <tt>null</tt>.
	 * @see #remove(Object)
	 * @see #contains(Object)
	 */
	public boolean removeAll(Collection c)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Retains only the elements in this list that are contained in the
	 * specified collection (optional operation).  In other words, removes
	 * from this list all the elements that are not contained in the specified
	 * collection.
	 * @param c collection that defines which elements this set will retain.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * @throws UnsupportedOperationException if the <tt>retainAll</tt> method
	 * is not supported by this list.
	 * @throws ClassCastException if the types of one or more elements
	 * in this list are incompatible with the specified
	 * collection (optional).
	 * @throws NullPointerException if this list contains one or more
	 * null elements and the specified collection does not support
	 * null elements (optional).
	 * @throws NullPointerException if the specified collection is
	 * <tt>null</tt>.
	 * @see #remove(Object)
	 * @see #contains(Object)
	 */
	public boolean retainAll(Collection c)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes all of the elements from this list (optional operation).  This
	 * list will be empty after this call returns (unless it throws an
	 * exception).
	 */
	public void clear()
	{
		expungeStaleEntries();

		while(queue.poll()!=null);
		for(int i=0; i<size; i++)
			array[i]	= null;
		size	= 0;
		state++;
	}

	/**
	 * Returns the element at the specified position in this list.
	 * @param index index of element to return.
	 * @return the element at the specified position in this list.
	 * @throws IndexOutOfBoundsException if the index is out of range (index
	 * &lt; 0 || index &gt;= size()).
	 */
	public E get(int index)
	{
		expungeStaleEntries();

		E	ret;
		// Hack !!! Throws array index out of bounds with different index than in parameter!?
		while((ret=array[index++].get())==null);
		return ret;
	}

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element (optional operation).
	 * @param index index of element to replace.
	 * @param o element to be stored at the specified position.
	 * @return the element previously at the specified position.
	 * @throws UnsupportedOperationException if the <tt>set</tt> method is not
	 * supported by this list.
	 * @throws ClassCastException if the class of the specified element
	 * prevents it from being added to this list.
	 * @throws NullPointerException if the specified element is null and
	 * this list does not support null elements.
	 * @throws IllegalArgumentException if some aspect of the specified
	 * element prevents it from being added to this list.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 * (index &lt; 0 || index &gt;= size()).
	 */
	public Object set(int index, Object o)
	{
		expungeStaleEntries();

		if(o==null)
			throw new NullPointerException("Null elements not supported.");
		
		Object	ret;
		// Hack !!! Throws array index out of bounds with different index than in parameter!?
		while((ret=array[index].get())==null)
			index++;

		array[index]	= new WeakReference(o, queue);
		state++;
		return ret;
	}

	/**
	 * Inserts the specified element at the specified position in this list
	 * (optional operation).  Shifts the element currently at that position
	 * (if any) and any subsequent elements to the right (adds one to their
	 * indices).
	 * @param index index at which the specified element is to be inserted.
	 * @param o element to be inserted.
	 * @throws UnsupportedOperationException if the <tt>add</tt> method is not
	 * supported by this list.
	 * @throws ClassCastException if the class of the specified element
	 * prevents it from being added to this list.
	 * @throws NullPointerException if the specified element is null and
	 * this list does not support null elements.
	 * @throws IllegalArgumentException if some aspect of the specified
	 * element prevents it from being added to this list.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 * (index &lt; 0 || index &gt; size()).
	 */
	public void add(int index, Object o)
	{
		expungeStaleEntries();

		if(o==null)
			throw new NullPointerException("Null elements not supported.");
		
		if(index<0 || index>size)
			throw new IndexOutOfBoundsException("size="+size+", index="+index);

		// Insert in new array.
		if(array.length==size)
		{
			Reference[]	array2	= new Reference[array.length*2];
			if(index>0)
				System.arraycopy(array, 0, array2, 0, index);
			if(index<size-1)
				System.arraycopy(array, index, array2, index+1, size-index);
			array	= array2;
		}
		
		// Insert in current array.
		else
		{
			System.arraycopy(array, index, array, index+1, size-index);
		}
		
		array[index]	= new WeakReference(o, queue);
		size++;
		state++;
	}

	/**
	 * Removes the element at the specified position in this list (optional
	 * operation).  Shifts any subsequent elements to the left (subtracts one
	 * from their indices).  Returns the element that was removed from the
	 * list.
	 * @param index the index of the element to removed.
	 * @return the element previously at the specified position.
	 * @throws UnsupportedOperationException if the <tt>remove</tt> method is
	 * not supported by this list.
	 * @throws IndexOutOfBoundsException if the index is out of range (index
	 * &lt; 0 || index &gt;= size()).
	 */
	public E remove(int index)
	{
		expungeStaleEntries();

		E	ret;
		// Hack !!! Throws array index out of bounds with different index than in parameter!?
		while((ret=array[index].get())==null)
			index++;

		size--;
		if(index<size)
			System.arraycopy(array, index+1, array, index, size-index);
		array[size]	= null;
		state++;
		return ret;
	}

	/**
	 * Returns the index in this list of the first occurrence of the specified
	 * element, or -1 if this list does not contain this element.
	 * More formally, returns the lowest index <tt>i</tt> such that
	 * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 * @param o element to search for.
	 * @return the index in this list of the first occurrence of the specified
	 *         element, or -1 if this list does not contain this element.
	 * @throws ClassCastException if the type of the specified element
	 * is incompatible with this list (optional).
	 * @throws NullPointerException if the specified element is null.
	 */
	public int indexOf(Object o)
	{
		expungeStaleEntries();

		for(int i=0; i<size; i++)
			if(o.equals(array[i].get()))
				return i;
		
		return -1;
	}

	/**
	 * Returns the index in this list of the last occurrence of the specified
	 * element, or -1 if this list does not contain this element.
	 * More formally, returns the highest index <tt>i</tt> such that
	 * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 * @param o element to search for.
	 * @return the index in this list of the last occurrence of the specified
	 *         element, or -1 if this list does not contain this element.
	 * @throws ClassCastException if the type of the specified element
	 * is incompatible with this list (optional).
	 * @throws NullPointerException if the specified element is null.
	 */
	public int lastIndexOf(Object o)
	{
		expungeStaleEntries();

		for(int i=size-1; i>=0; i--)
			if(o.equals(array[i].get()))
				return i;
		
		return -1;
	}

	/**
	 * Returns a list iterator of the elements in this list (in proper
	 * sequence).
	 * @return a list iterator of the elements in this list (in proper
	 *         sequence).
	 */
	public ListIterator<E> listIterator()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a list iterator of the elements in this list (in proper
	 * sequence), starting at the specified position in this list.  The
	 * specified index indicates the first element that would be returned by
	 * an initial call to the <tt>next</tt> method.  An initial call to
	 * the <tt>previous</tt> method would return the element with the
	 * specified index minus one.
	 * @param index index of first element to be returned from the
	 * list iterator (by a call to the <tt>next</tt> method).
	 * @return a list iterator of the elements in this list (in proper
	 *         sequence), starting at the specified position in this list.
	 * @throws IndexOutOfBoundsException if the index is out of range (index
	 * &lt; 0 || index &gt; size()).
	 */
	public ListIterator<E> listIterator(int index)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a view of the portion of this list between the specified
	 * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.  (If
	 * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
	 * empty.)  The returned list is backed by this list, so non-structural
	 * changes in the returned list are reflected in this list, and vice-versa.
	 * The returned list supports all of the optional list operations supported
	 * by this list.<p>
	 *
	 * This method eliminates the need for explicit range operations (of
	 * the sort that commonly exist for arrays).   Any operation that expects
	 * a list can be used as a range operation by passing a subList view
	 * instead of a whole list.  For example, the following idiom
	 * removes a range of elements from a list:
	 * <pre>
	 * 	    list.subList(from, to).clear();
	 * </pre>
	 * Similar idioms may be constructed for <tt>indexOf</tt> and
	 * <tt>lastIndexOf</tt>, and all of the algorithms in the
	 * <tt>Collections</tt> class can be applied to a subList.<p>
	 *
	 * The semantics of the list returned by this method become undefined if
	 * the backing list (i.e., this list) is <i>structurally modified</i> in
	 * any way other than via the returned list.  (Structural modifications are
	 * those that change the size of this list, or otherwise perturb it in such
	 * a fashion that max_iter in progress may yield incorrect results.)
	 * @param fromIndex low endpoint (inclusive) of the subList.
	 * @param toIndex high endpoint (exclusive) of the subList.
	 * @return a view of the specified range within this list.
	 * @throws IndexOutOfBoundsException for an illegal endpoint index value
	 * (fromIndex &lt; 0 || toIndex &gt; size || fromIndex &gt; toIndex).
	 */
	public List<E> subList(int fromIndex, int toIndex)
	{
		throw new UnsupportedOperationException();
	}

	//-------- helpers --------

	/**
	 * Expunge stale entries from the list.
	 */
	private void expungeStaleEntries()
	{
		Reference	ref;
		while((ref=queue.poll())!=null)
		{
			for(int i=0; i<size; i++)
			{
				if(ref==array[i])
				{
					size--;
					if(i<size)
						System.arraycopy(array, i+1, array, i, size-i);
					array[size]	= null;
					state++;
				}
			}
		}
	}

    /**
	 *  Get the string representation.
	 */
	public String toString()
	{
		expungeStaleEntries();

		return SUtil.arrayToString(array);	// Use toArray().
	}

	//-------- serialization handling --------

	protected List<E>	serialized_list;
	
	/**
	 *  Perform special handling on serialization.
	 */
	protected Object	writeReplace() throws ObjectStreamException
	{
		expungeStaleEntries();

		// Extract weak references as they are not serializable.
		this.serialized_list	= SCollection.createLinkedList();
		for(int i=0; i<size; i++)
		{
			E	next = array[i].get();
			if(next!=null)
				serialized_list.add(next);
		}
		return this;
	}

	/**
	 *  Perform special handling on deserialization.
	 */
	protected Object	readResolve() throws ObjectStreamException
	{
		// Restore weak references as they are not serialized.
		// Use min size 10 of buffer to allow expanding with length*2.
		this.array	= new Reference[Math.max(serialized_list.size(), 10)];
		this.queue	= new ReferenceQueue();
		Iterator<E> it=serialized_list.iterator();
		for(int i=0; it.hasNext(); i++)
		{
			array[i]	= new WeakReference(it.next(), queue);
		}
		this.size	= serialized_list.size();
		this.serialized_list	= null;
		return this;
	}

	//-------- main for testing ---------
	
	/**
	 *  Main method for testing.
	 */
	public static void	main(String[] args)
	{		
		int	testsize	= 10001;
		List	list	= new WeakList();
		String teststring = "testvalue ";
		String[]	values	= createTestvalues(teststring, testsize);
		
		System.out.println("Adding odd values (at end of list).");
		for(int i=1; i<values.length; i+=2)
		{
			list.add(values[i]);
		}

		System.out.println("Inserting even values (at correct position of list).");
		for(int i=0; i<values.length; i+=2)
		{
			list.add(i, values[i]);
		}
	
		System.out.println("Checking positions using toArray().");
		Object[]	listvalues	= list.toArray();
		for(int i=0; i<values.length; i++)
		{
			if(!values[i].equals(listvalues[i]))
				throw new RuntimeException("Test failed.");
		}
		listvalues	= null;
		
		System.out.println("Checking positions using toArray(array).");
		listvalues	= list.toArray(new Object[0]);
		for(int i=0; i<values.length; i++)
		{
			if(!values[i].equals(listvalues[i]))
				throw new RuntimeException("Test failed.");
		}
		listvalues	= null;

		System.out.println("Checking positions using iterator.");
		Iterator	it	= list.iterator();
		for(int i=0; i<values.length; i++)
		{
			if(!values[i].equals(it.next()))
				throw new RuntimeException("Test failed.");
		}
		it	= null;
		
		System.out.println("Making some values (x%3==0) available to garbage collection.");
		for(int i=0; i<values.length; i+=3)
		{
			values[i]	= null;
		}
		
		System.gc();
		try
		{
			Thread.sleep(200);
		}
		catch(InterruptedException e){}
		
		System.out.println("Some elements should have been garbage collected.");
		if(!(list.size()<values.length))
			throw new RuntimeException("Test failed.");


		checkForEmptySlots(list);

		System.out.println("Checking positions using iterator.");
		it	= list.iterator();
		String	value	= null;
		for(int i=0; i<values.length && it.hasNext(); i++)
		{
			if(values[i]==null)
			{
				// Skip removed array items.
				// Test if value not yet garbage collected (then skip item also).
				value	= (String)it.next();
				int	index	= Integer.parseInt(value.substring(value.indexOf(" ")+1));
				if(index==i)
				{
					value	= null; 
				}
				continue;
			}
			if(value==null)
				value	= (String)it.next();
			if(!values[i].equals(value))
				throw new RuntimeException("Test failed: "+i+", item="+value+", test="+values[i]);
			value	= null;
		}
		it	= null;
		value	= null;
		
		System.out.println("Removing some values (x%5==0) using iterator.");
		it	= list.iterator();
		while(it.hasNext())
		{
			value	= (String)it.next();
			int	index	= Integer.parseInt(value.substring(value.indexOf(" ")+1));
			if(index%5==0)
			{
				it.remove();
			}
		}
		it	= null;
		value	= null;

		checkForEmptySlots(list);

		System.out.println("Checking positions using iterator.");
		it	= list.iterator();
		value	= null;
		for(int i=0; i<values.length && it.hasNext(); i++)
		{
			// Skip removed list items.
			if(i%5!=0)
			{
				if(values[i]==null)
				{
					// Skip removed array items.
					// Test if value not yet garbage collected (then skip item also).
					value	= (String)it.next();
					int	index	= Integer.parseInt(value.substring(value.indexOf(" ")+1));
					if(index==i)
					{
						value	= null; 
					}
					continue;
				}
				if(value==null)
					value	= (String)it.next();
				if(!values[i].equals(value))
					throw new RuntimeException("Test failed: "+i+", item="+value+", test="+values[i]);
				value	= null;
			}
		}
		it	= null;
		value	= null;

		System.out.println("Removing even values (including garbage collected ones).");
		for(int i=0; i<values.length; i+=2)
		{
			list.remove(teststring+i);
		}
		
		checkForEmptySlots(list);

		System.out.println("Checking positions using iterator.");
		it	= list.iterator();
		value	= null;
		for(int i=0; i<values.length && it.hasNext(); i++)
		{
			// Skip removed list items.
			if(i%5!=0 && i%2!=0)
			{
				if(values[i]==null)
				{
					// Skip removed array items.
					// Test if value not yet garbage collected (then skip item also).
					value	= (String)it.next();
					int	index	= Integer.parseInt(value.substring(value.indexOf(" ")+1));
					if(index==i)
					{
						value	= null; 
					}
					continue;
				}
				if(value==null)
					value	= (String)it.next();
				if(!values[i].equals(value))
					throw new RuntimeException("Test failed: "+i+", item="+value+", test="+values[i]);
				value	= null;
			}
		}
		it	= null;
		value	= null;

		System.out.println("Removing odd values (including garbage collected ones).");
		for(int i=1; i<values.length; i+=2)
		{
			list.remove(teststring+i);
		}

		checkForEmptySlots(list);

		System.out.println("Checking if the list is empty now.");
		if(list.size()>0)
			throw new RuntimeException("Test failed.");
			
		System.out.println("Doing some more tests for garbage collection."); 
		list = new WeakList();
		String[] ins = new String[testsize];
		for(int i=0; i<ins.length; i++)
			ins[i] = "ins_"+Math.random();
		for(int i=0; i<ins.length; i++)
		{
			list.add(ins[i]);
			list.add("notrem_"+Math.random());
		}

		System.out.println("Checking if some elements have been garbage collected.");
		if(!(list.size()<ins.length*2))
			throw new RuntimeException("Test failed.");

		checkForEmptySlots(list);
		
		System.out.println("Removing remebered entries.");
		for(int i=0; i<ins.length; i++)
			list.remove(ins[i]);
		
		checkForEmptySlots(list);

		System.out.println("Test successful.");
	}

	/**
	 *  Create some string values for testing.
	 */
	protected static String[]	createTestvalues(String teststring, int size)
	{
		String[]	values	= new String[size];
		for(int i=0; i<values.length; i++)
		{
			values[i]	= teststring+i;
		}
		return values;
	}

	/**
	 * 	Check for empty slots in list.
	 */
	protected static void checkForEmptySlots(List list)
	{
		Object[] listvalues;
		
		System.out.println("Checking for empty slots in toArray().");
		listvalues	= list.toArray();
		for(int i=0; i<listvalues.length; i++)
		{
			if(listvalues[i]==null)
				throw new RuntimeException("Test failed.");
		}
		
		System.out.println("Checking for empty slots in toArray(array).");
		listvalues	= list.toArray(new Object[0]);
		for(int i=0; i<listvalues.length; i++)
		{
			if(listvalues[i]==null)
				throw new RuntimeException("Test failed.");
		}
	}
}
