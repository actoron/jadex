package jadex.bdi.testcases.beliefs;

import java.util.ArrayList;
import java.util.Collection;

import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.beans.PropertyChangeListener;

/**
 *  An array list with property change support.
 *  Throws events when the list changes.
 */
public class BeanChangesArrayList extends ArrayList
{
	//-------- attributes --------

	/** The proerty change thrower. */
	protected SimplePropertyChangeSupport pcs;

	//-------- constructors --------

	/**
	 *  Create a new array list.
	 */
	public BeanChangesArrayList()
	{
		this.pcs = new SimplePropertyChangeSupport(this);
	}

	//-------- methods --------
	
	public int	getSize()
	{
		return size();
	}
	
	/**
	 * Replaces the element at the specified position in this list with
	 * the specified element.
	 * @param index index of element to replace.
	 * @param o element to be stored at the specified position.
	 * @return the element previously at the specified position.
	 * @throws IndexOutOfBoundsException if index out of range
	 * <tt>(index &lt; 0 || index &gt;= size())</tt>.
	 */
	public Object set(int index, Object o)
	{
		//Object old = get(index);
		Object ret = super.set(index, o);
//		pcs.fireIndexedPropertyChange("list", index, ret, o);
		pcs.firePropertyChange("list", null, this);	// Hack!!!
		return ret;
	}

	/**
	 * Appends the specified element to the end of this list.
	 * @param o element to be appended to this list.
	 * @return <tt>true</tt> (as per the general contract of Collection.add).
	 */
	public boolean add(Object o)
	{
		boolean ret = super.add(o);
		pcs.firePropertyChange("list", null, this);	// Hack!!!
		return ret;
	}

	/**
	 * Inserts the specified element at the specified position in this
	 * list. Shifts the element currently at that position (if any) and
	 * any subsequent elements to the right (adds one to their indices).
	 * @param index index at which the specified element is to be inserted.
	 * @param o element to be inserted.
	 * @throws IndexOutOfBoundsException if index is out of range
	 * <tt>(index &lt; 0 || index &gt; size())</tt>.
	 */
	public void add(int index, Object o)
	{
		//Object old = get(index);
		super.add(index, o);
		pcs.firePropertyChange("list", null, this);	// Hack!!!
		//pcs.fireIndexedPropertyChange("list", index, old, o);
	}

	/**
	 * Removes the element at the specified position in this list.
	 * Shifts any subsequent elements to the left (subtracts one from their
	 * indices).
	 * @param index the index of the element to removed.
	 * @return the element that was removed from the list.
	 * @throws IndexOutOfBoundsException if index out of range <tt>(index
	 * &lt; 0 || index &gt;= size())</tt>.
	 */
	public Object remove(int index)
	{
		Object ret = super.remove(index);    //To change body of overridden methods use File | Settings | File Templates.
		pcs.firePropertyChange("list", null, this);	// Hack!!!
		return ret;
	}

	/**
	 * Removes a single instance of the specified element from this
	 * list, if it is present (optional operation).  More formally,
	 * removes an element <tt>e</tt> such that <tt>(o==null ? e==null :
	 * o.equals(e))</tt>, if the list contains one or more such
	 * elements.  Returns <tt>true</tt> if the list contained the
	 * specified element (or equivalently, if the list changed as a
	 * result of the call).<p>
	 * @param o element to be removed from this list, if present.
	 * @return <tt>true</tt> if the list contained the specified element.
	 */
	public boolean remove(Object o)
	{
		boolean ret = super.remove(o);    //To change body of overridden methods use File | Settings | File Templates.
		pcs.firePropertyChange("list", null, this);	// Hack!!!
		return ret;
	}

	/**
	 * Appends all of the elements in the specified Collection to the end of
	 * this list, in the order that they are returned by the
	 * specified Collection's Iterator.  The behavior of this operation is
	 * undefined if the specified Collection is modified while the operation
	 * is in progress.  (This implies that the behavior of this call is
	 * undefined if the specified Collection is this list, and this
	 * list is nonempty.)
	 * @param collection the elements to be inserted into this list.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * @throws NullPointerException if the specified collection is null.
	 */
	public boolean addAll(Collection collection)
	{
		boolean ret = super.addAll(collection);
		pcs.firePropertyChange("list", null, this);	// Hack!!!
		return ret;
	}

	/**
	 * Inserts all of the elements in the specified Collection into this
	 * list, starting at the specified position.  Shifts the element
	 * currently at that position (if any) and any subsequent elements to
	 * the right (increases their indices).  The new elements will appear
	 * in the list in the order that they are returned by the
	 * specified Collection's iterator.
	 * @param index index at which to insert first element
	 * from the specified collection.
	 * @param collection elements to be inserted into this list.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * @throws IndexOutOfBoundsException if index out of range <tt>(index
	 * &lt; 0 || index &gt; size())</tt>.
	 * @throws NullPointerException if the specified Collection is null.
	 */
	public boolean addAll(int index, Collection collection)
	{
		boolean ret = super.addAll(index, collection);
		pcs.firePropertyChange("list", null, this);	// Hack!!!
		return ret;
	}

	/**
	 * Removes from this List all of the elements whose index is between
	 * fromIndex, inclusive and toIndex, exclusive.  Shifts any succeeding
	 * elements to the left (reduces their index).
	 * This call shortens the list by <tt>(toIndex - fromIndex)</tt> elements.
	 * (If <tt>toIndex==fromIndex</tt>, this operation has no effect.)
	 * @param fromIndex index of first element to be removed.
	 * @param toIndex index after last element to be removed.
	 */
	public void removeRange(int fromIndex, int toIndex)
	{
		super.removeRange(fromIndex, toIndex);
		pcs.firePropertyChange("list", null, this);	// Hack!!!
	}

	//-------- property methods --------

	/**
     *  Add a PropertyChangeListener to the listener list.
     *  The listener is registered for all properties.
     *  @param listener  The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
    }

    /**
     *  Remove a PropertyChangeListener from the listener list.
     *  This removes a PropertyChangeListener that was registered
     *  for all properties.
     *  @param listener  The PropertyChangeListener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
    }

	/**
	 *  Indicate that the list has changed.
	 *  Provoke a bean change event.
	 */
	public void modified()
	{
		pcs.firePropertyChange("list", null, this);	// Hack!!!
	}

	//-------- bean accessor methods --------

	// Represents a "list" property required for throwing property changes. 
	
	/**
	 *  Get the list of elements.
	 */
	public Object	getList()
	{
		throw new UnsupportedOperationException("Dummy method. Do not use!");
	}

	/**
	 *  Set the list of elements.
	 */
	public void	setList(Object list)
	{
		throw new UnsupportedOperationException("Dummy method. Do not use!");
	}
	
	//-------- constant hascode/equals required for rete --------

	/**
	 *  Constant hashcode required for rete.
	 */
	public int hashCode()
	{
		return pcs.hashCode();
	}
	
	/**
	 *  Identity required for rete.
	 */
	public boolean equals(Object o)
	{
		return o==this;
	}
}
