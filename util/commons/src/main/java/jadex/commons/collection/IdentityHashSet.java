package jadex.commons.collection;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *  A set based on identity comparison.
 */
// todo: Ordering for repeatability
public class IdentityHashSet<T>	implements Set<T>
{
	//-------- attributes --------
	
	/** The backing map. */
    protected	Map<T, T>	map;

    //-------- constructors --------
    
    /**
     *  Create a new identity hash set.
     */
    public IdentityHashSet()
    {
    	map	= new IdentityHashMap<T, T>();
    }

    //-------- Set interface --------
    
    // Query Operations

    /**
     * Returns the number of elements in this set (its cardinality).  If this
     * set contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this set (its cardinality).
     */
    public int size()
    {
    	return map.size();
    }

    /**
     * Returns <tt>true</tt> if this set contains no elements.
     *
     * @return <tt>true</tt> if this set contains no elements.
     */
    public boolean isEmpty()
    {
    	return map.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this set contains the specified element.  More
     * formally, returns <tt>true</tt> if and only if this set contains an
     * element <code>e</code> such that <code>(o==null ? e==null :
     * o.equals(e))</code>.
     *
     * @param o element whose presence in this set is to be tested.
     * @return <tt>true</tt> if this set contains the specified element.
     * @throws ClassCastException if the type of the specified element
     * 	       is incompatible with this set (optional).
     * @throws NullPointerException if the specified element is null and this
     *         set does not support null elements (optional).
     */
    public boolean contains(Object o)
    {
    	return map.containsKey(o);
    }

    /**
     * Returns an iterator over the elements in this set.  The elements are
     * returned in no particular order (unless this set is an instance of some
     * class that provides a guarantee).
     *
     * @return an iterator over the elements in this set.
     */
    public Iterator<T> iterator()
    {
    	return map.keySet().iterator();
    }

    /**
     * Returns an array containing all of the elements in this set.
     * Obeys the general contract of the <tt>Collection.toArray</tt> method.
     *
     * @return an array containing all of the elements in this set.
     */
    public Object[] toArray()
    {
    	return map.keySet().toArray();
    }

    /**
     * Returns an array containing all of the elements in this set; the 
     * runtime type of the returned array is that of the specified array. 
     * Obeys the general contract of the 
     * <tt>Collection.toArray(Object[])</tt> method.
     *
     * @param a the array into which the elements of this set are to
     *		be stored, if it is big enough; otherwise, a new array of the
     * 		same runtime type is allocated for this purpose.
     * @return an array containing the elements of this set.
     * @throws    ArrayStoreException the runtime type of a is not a supertype
     *            of the runtime type of every element in this set.
     * @throws NullPointerException if the specified array is <tt>null</tt>.
     */
    public <E> E[] toArray(E[] a)
    {
    	return map.keySet().toArray(a);
    }


    // Modification Operations

    /**
     * Adds the specified element to this set if it is not already present
     * (optional operation).  More formally, adds the specified element,
     * <code>o</code>, to this set if this set contains no element
     * <code>e</code> such that <code>(o==null ? e==null :
     * o.equals(e))</code>.  If this set already contains the specified
     * element, the call leaves this set unchanged and returns <tt>false</tt>.
     * In combination with the restriction on constructors, this ensures that
     * sets never contain duplicate elements.<p>
     *
     * The stipulation above does not imply that sets must accept all
     * elements; sets may refuse to add any particular element, including
     * <tt>null</tt>, and throwing an exception, as described in the
     * specification for <tt>Collection.add</tt>.  Individual set
     * implementations should clearly document any restrictions on the the
     * elements that they may contain.
     *
     * @param o element to be added to this set.
     * @return <tt>true</tt> if this set did not already contain the specified
     *         element.
     * 
     * @throws UnsupportedOperationException if the <tt>add</tt> method is not
     * 	       supported by this set.
     * @throws ClassCastException if the class of the specified element
     * 	       prevents it from being added to this set.
     * @throws NullPointerException if the specified element is null and this
     *         set does not support null elements.
     * @throws IllegalArgumentException if some aspect of the specified element
     *         prevents it from being added to this set.
     */
    public boolean add(T o)
    {
    	return map.put(o, o)==null;
    }


    /**
     * Removes the specified element from this set if it is present (optional
     * operation).  More formally, removes an element <code>e</code> such that
     * <code>(o==null ?  e==null : o.equals(e))</code>, if the set contains
     * such an element.  Returns <tt>true</tt> if the set contained the
     * specified element (or equivalently, if the set changed as a result of
     * the call).  (The set will not contain the specified element once the
     * call returns.)
     *
     * @param o object to be removed from this set, if present.
     * @return true if the set contained the specified element.
     * @throws ClassCastException if the type of the specified element
     * 	       is incompatible with this set (optional).
     * @throws NullPointerException if the specified element is null and this
     *         set does not support null elements (optional).
     * @throws UnsupportedOperationException if the <tt>remove</tt> method is
     *         not supported by this set.
     */
    public boolean remove(Object o)
    {
    	return map.remove(o)!=null;
    }


    // Bulk Operations

    /**
     * Returns <tt>true</tt> if this set contains all of the elements of the
     * specified collection.  If the specified collection is also a set, this
     * method returns <tt>true</tt> if it is a <i>subset</i> of this set.
     *
     * @param  c collection to be checked for containment in this set.
     * @return <tt>true</tt> if this set contains all of the elements of the
     * 	       specified collection.
     * @throws ClassCastException if the types of one or more elements
     *         in the specified collection are incompatible with this
     *         set (optional).
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements and this set does not support null
     *         elements (optional).
     * @throws NullPointerException if the specified collection is
     *         <tt>null</tt>.
     * @see    #contains(Object)
     */
    public boolean containsAll(Collection<?> c)
    {
    	return map.keySet().containsAll(c);
    }

    /**
     * Adds all of the elements in the specified collection to this set if
     * they're not already present (optional operation).  If the specified
     * collection is also a set, the <tt>addAll</tt> operation effectively
     * modifies this set so that its value is the <i>union</i> of the two
     * sets.  The behavior of this operation is unspecified if the specified
     * collection is modified while the operation is in progress.
     *
     * @param c collection whose elements are to be added to this set.
     * @return <tt>true</tt> if this set changed as a result of the call.
     * 
     * @throws UnsupportedOperationException if the <tt>addAll</tt> method is
     * 		  not supported by this set.
     * @throws ClassCastException if the class of some element of the
     * 		  specified collection prevents it from being added to this
     * 		  set.
     * @throws NullPointerException if the specified collection contains one
     *           or more null elements and this set does not support null
     *           elements, or if the specified collection is <tt>null</tt>.
     * @throws IllegalArgumentException if some aspect of some element of the
     *		  specified collection prevents it from being added to this
     *		  set.
     * @see #add(Object)
     */
    public boolean addAll(Collection<? extends T> c)
    {
    	boolean	ret	= false;
    	for(Iterator<? extends T> it=c.iterator(); it.hasNext();)
    	{
    		ret	= add(it.next()) || ret;
    	}
    	return ret;	
    }

    /**
     * Retains only the elements in this set that are contained in the
     * specified collection (optional operation).  In other words, removes
     * from this set all of its elements that are not contained in the
     * specified collection.  If the specified collection is also a set, this
     * operation effectively modifies this set so that its value is the
     * <i>intersection</i> of the two sets.
     *
     * @param c collection that defines which elements this set will retain.
     * @return <tt>true</tt> if this collection changed as a result of the
     *         call.
     * @throws UnsupportedOperationException if the <tt>retainAll</tt> method
     * 		  is not supported by this Collection.
     * @throws ClassCastException if the types of one or more elements in this
     *            set are incompatible with the specified collection
     *            (optional).
     * @throws NullPointerException if this set contains a null element and
     *            the specified collection does not support null elements
     *            (optional). 
     * @throws NullPointerException if the specified collection is
     *           <tt>null</tt>.
     * @see #remove(Object)
     */
    public boolean retainAll(Collection<?> c)
    {
    	boolean	ret	= false;
    	for(Iterator<?> it=map.keySet().iterator(); it.hasNext();)
    	{
    		if(!c.contains(it.next()))
    		{
    			it.remove();
    			ret	= true;
    		}
    	}
    	return ret;	
    }


    /**
     * Removes from this set all of its elements that are contained in the
     * specified collection (optional operation).  If the specified
     * collection is also a set, this operation effectively modifies this
     * set so that its value is the <i>asymmetric set difference</i> of
     * the two sets.
     *
     * @param  c collection that defines which elements will be removed from
     *           this set.
     * @return <tt>true</tt> if this set changed as a result of the call.
     * 
     * @throws UnsupportedOperationException if the <tt>removeAll</tt>
     * 		  method is not supported by this Collection.
     * @throws ClassCastException if the types of one or more elements in this
     *            set are incompatible with the specified collection
     *            (optional).
     * @throws NullPointerException if this set contains a null element and
     *            the specified collection does not support null elements
     *            (optional). 
     * @throws NullPointerException if the specified collection is
     *           <tt>null</tt>.
     * @see    #remove(Object)
     */
    public boolean removeAll(Collection<?> c)
    {
    	boolean	ret	= false;
    	for(Iterator<?> it=c.iterator(); it.hasNext();)
    	{
    		ret	= remove(it.next()) || ret;
    	}
    	return ret;	    	
    }

    /**
     * Removes all of the elements from this set (optional operation).
     * This set will be empty after this call returns (unless it throws an
     * exception).
     *
     * @throws UnsupportedOperationException if the <tt>clear</tt> method
     * 		  is not supported by this set.
     */
    public void clear()
    {
    	map.clear();
    }


    // Comparison and hashing

    /**
     * Compares the specified object with this set for equality.  Returns
     * <tt>true</tt> if the specified object is also a set, the two sets
     * have the same size, and every member of the specified set is
     * contained in this set (or equivalently, every member of this set is
     * contained in the specified set).  This definition ensures that the
     * equals method works properly across different implementations of the
     * set interface.
     *
     * @param o Object to be compared for equality with this set.
     * @return <tt>true</tt> if the specified Object is equal to this set.
     */
    public boolean equals(Object o)
    {
    	return map.keySet().equals(o);
    }

    /**
     * 
     * Returns the hash code value for this set.  The hash code of a set is
     * defined to be the sum of the hash codes of the elements in the set,
     * where the hashcode of a <tt>null</tt> element is defined to be zero.
     * This ensures that <code>s1.equals(s2)</code> implies that
     * <code>s1.hashCode()==s2.hashCode()</code> for any two sets
     * <code>s1</code> and <code>s2</code>, as required by the general
     * contract of the <tt>Object.hashCode</tt> method.
     *
     * @return the hash code value for this set.
     * @see Object#hashCode()
     * @see Object#equals(Object)
     * @see Set#equals(Object)
     */
    public int hashCode()
    {
    	return map.keySet().hashCode();
    }
    
    /**
     *  Create a string representation.
     */
    public String	toString()
    {
    	StringBuffer	ret	= new StringBuffer("{");
    	for(Iterator<T> it=iterator(); it.hasNext();)
    	{
    		ret.append(it.next());
    		if(it.hasNext())
    			ret.append(",");
    	}
    	ret.append("}");
    	return ret.toString();
    }
}