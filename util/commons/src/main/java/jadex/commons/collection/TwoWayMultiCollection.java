package jadex.commons.collection;

import java.util.Collection;
import java.util.Iterator;


/**
 *  The two-way multi-collection allows fast reverse lookup,
 *  by containing a second multi-collection,
 *  which reversely maps values to keys.
 */
public class TwoWayMultiCollection	extends MultiCollection
{
	//-------- attributes --------

	/** The reverse multi-collection. */
	protected TwoWayMultiCollection	reverse;

	//-------- constructors --------

	/**
	 *  Create a two way map.
	 */
	public TwoWayMultiCollection()
	{
		this.reverse	= new TwoWayMultiCollection(this);
	}

	/**
	 *  internal constrcutor for connecting two
	 *  two-way multi-collection.
	 *  @reverse The reverse multi-collection.
	 */
	protected TwoWayMultiCollection(TwoWayMultiCollection reverse)
	{
		this.reverse	= reverse;
	}

	//-------- methods --------

	/**
	 *  Get the reverse multi-collection.
	 *  @return The reverse multi-collection.
	 */
	public TwoWayMultiCollection	getReverseMultiCollection()
	{
		return reverse;
	}

	/**
	 * Associates the specified value with the specified key in this map
	 * (optional operation).  If the map previously contained a mapping for
	 * this key, the old value is replaced by the specified value.  (A map
	 * <tt>m</tt> is said to contain a mapping for a key <tt>k</tt> if and only
	 * if {@link #containsKey(Object) m.containsKey(k)} would return
	 * <tt>true</tt>.)) 
	 *
	 * @param key key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
 	 * @return The collection associated to the key.
	 * 
	 * @throws UnsupportedOperationException if the <tt>put</tt> operation is
	 *	          not supported by this map.
	 * @throws ClassCastException if the class of the specified key or value
	 * 	          prevents it from being stored in this map.
	 * @throws IllegalArgumentException if some aspect of this key or value
	 *	          prevents it from being stored in this map.
	 * @throws NullPointerException this map does not permit <tt>null</tt>
	 *            keys or values, and the specified key or value is
	 *            <tt>null</tt>.
	 */
	public Collection<Object> add(Object key, Object value)
	{
		reverse._add(value, key);
		return super.add(key, value);
	}

	/**
	 *  Internal put method, which doesn't affect the reverse collection.
	 */
	protected Object _add(Object key, Object value)
	{
		return super.add(key, value);
	}

	/**
	 * Removes all mappings from this map.
	 */
	public void clear()
	{
		super.clear();
		reverse._clear();
	}

	/**
	 *  Internal clear method, which doesn't affect the reverse collection.
	 */
	protected void _clear()
	{
		super.clear();
	}


	/**
	 * Removes the mapping for this key from this map if it is present.
	 * More formally, if this map contains a mapping
	 * from key <tt>k</tt> to value <tt>v</tt> such that
	 * <code>(key==null ?  k==null : key.equals(k))</code>, that mapping
	 * is removed.  (The map can contain at most one such mapping.)
	 *
	 * <p>Returns the value to which the map previously associated the key, or
	 * <tt>null</tt> if the map contained no mapping for this key.  (A
	 * <tt>null</tt> return can also indicate that the map previously
	 * associated <tt>null</tt> with the specified key if the implementation
	 * supports <tt>null</tt> values.)  The map will not contain a mapping for
	 * the specified  key once the call returns.
	 *
	 * @param key key whose mapping is to be removed from the map.
	 * @return collection associated with specified key, or <tt>null</tt>
	 *	       if there was no mapping for key.
	 *
	 * @throws ClassCastException if the key is of an inappropriate type for
	 * 		  this map (optional).
	 * @throws NullPointerException if the key is <tt>null</tt> and this map
	 *            does not not permit <tt>null</tt> keys (optional).
	 */
	public Collection<Object>	remove(Object key)
	{
		Collection	coll	= getCollection(key);
		for(Iterator i=coll.iterator(); i.hasNext(); )
		{
			reverse._remove(i.next(), key);
		}
		return super.remove(key);
	}

	/**
	 *  Internal remove method, which doesn't affect the reverse collection.
	 */
	protected Object _remove(Object key)
	{
		return super.remove(key);
	}


	/**
	 *  Remove a special object from the 
	 *  collection of a defined key.
	 */
	public void	removeObject(Object key, Object value)
	{
		reverse._remove(value, key);
		super.removeObject(key, value);
	}

	/**
	 *  Internal remove method, which doesn't affect the reverse collection.
	 */
	protected void _remove(Object key, Object value)
	{
		super.removeObject(key, value);
	}
}

