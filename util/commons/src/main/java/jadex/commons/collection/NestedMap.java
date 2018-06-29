package jadex.commons.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jadex.commons.SUtil;


/**
 *  A nested map refers to parent maps for entries
 *  not found in this map.
 *  Modifications of this map do not affect the parent maps.
 */
// todo: implement views.
// todo: implement correct hashCode()/size() when keys are overridden.
public class NestedMap	implements Map, java.io.Serializable
{
	//-------- attributes --------

	/** The local map. */
	protected Map	local;

	/** The parent maps. */
	protected Map[]	parents;

	//-------- constructors --------

	/**
	 *  Create a nested map, referring to the specified parent map.
	 *  @param parent	The parent map.
	 */
	public NestedMap(Map parent)
	{
		this(new Map[]{parent});
	}

	/**
	 *  Create a nested map, referring to the specified parent maps.
	 *  @param parents	The parent maps.
	 */
	public NestedMap(Map[] parents)
	{
		this(parents, new HashMap());
	}

	/**
	 *  Create a nested map, referring to the specified parent map,
	 *  using the given map for storing local mappings.
	 *  @param parents	The parent map.
	 *  @param local	The map for local mappings.
	 */
	protected NestedMap(Map[] parents, Map local)
	{
		assert local!=null;
		assert parents!=null;
		for(int i=0; i<parents.length; i++)
			assert parents[i]!=null: this;


		this.parents	= new Map[parents.length];
		System.arraycopy(parents, 0, this.parents, 0, parents.length);
		this.local	= local;
	}

	//-------- methods --------

	/**
	 *  Get the map containing the local mappings.
	 */
	public Map	getLocalMap()
	{
		return local;
	}

	//-------- Map methods --------

	// Query Operations

	/**
	 * Returns the number of key-value mappings in this map.  If the
	 * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 * Note, complexity of the implementation is not constant but linear
	 * to the number of entries in the contained maps!
	 * @return the number of key-value mappings in this map.
	 */
	public int size()
	{
		// To determine size, build union of key sets.
		Set	keys	= new HashSet(local.keySet());
		for(int i=0; i<parents.length; i++)
		{
			keys.addAll(parents[i].keySet());
		}
		return keys.size();
	}

	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 * @return <tt>true</tt> if this map contains no key-value mappings.
	 */
	public boolean isEmpty()
	{
		boolean	empty	= local.isEmpty();
		for(int i=0; empty && i<parents.length; i++)
		{
			empty	= parents[i].isEmpty(); 
		}
		return empty;
	}

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the specified
	 * key.  More formally, returns <tt>true</tt> if and only if
	 * this map contains at a mapping for a key <tt>k</tt> such that
	 * <tt>(key==null ? k==null : key.equals(k))</tt>.  (There can be
	 * at most one such mapping.)
	 *
	 * @param key key whose presence in this map is to be tested.
	 * @return <tt>true</tt> if this map contains a mapping for the specified
	 *         key.
	 * 
	 * @throws ClassCastException if the key is of an inappropriate type for
	 * 		  this map (optional).
	 * @throws NullPointerException if the key is <tt>null</tt> and this map
	 *            does not not permit <tt>null</tt> keys (optional).
	 */
	public boolean containsKey(Object key)
	{
		boolean	contains	= local.containsKey(key);
		for(int i=0; !contains && i<parents.length; i++)
		{
			contains	= parents[i].containsKey(key); 
		}
		return contains;
	}

	/**
	 * Returns <tt>true</tt> if this map maps one or more keys to the
	 * specified value.  More formally, returns <tt>true</tt> if and only if
	 * this map contains at least one mapping to a value <tt>v</tt> such that
	 * <tt>(value==null ? v==null : value.equals(v))</tt>.  This operation
	 * will probably require time linear in the map size for most
	 * implementations of the <tt>Map</tt> interface.
	 *
	 * @param value value whose presence in this map is to be tested.
	 * @return <tt>true</tt> if this map maps one or more keys to the
	 *         specified value.
	 * @throws ClassCastException if the value is of an inappropriate type for
	 * 		  this map (optional).
	 * @throws NullPointerException if the value is <tt>null</tt> and this map
	 *            does not not permit <tt>null</tt> values (optional).
	 */
	public boolean containsValue(Object value)
	{
		boolean	contains	= local.containsValue(value);
		for(int i=0; !contains && i<parents.length; i++)
		{
			contains	= parents[i].containsValue(value); 
		}
		return contains;
	}

	/**
	 * Returns the value to which this map maps the specified key.  Returns
	 * <tt>null</tt> if the map contains no mapping for this key.  A return
	 * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
	 * map contains no mapping for the key; it's also possible that the map
	 * explicitly maps the key to <tt>null</tt>.  The <tt>containsKey</tt>
	 * operation may be used to distinguish these two cases.
	 *
	 * <p>More formally, if this map contains a mapping from a key
	 * <tt>k</tt> to a value <tt>v</tt> such that <tt>(key==null ? k==null :
	 * key.equals(k))</tt>, then this method returns <tt>v</tt>; otherwise
	 * it returns <tt>null</tt>.  (There can be at most one such mapping.)
	 *
	 * @param key key whose associated value is to be returned.
	 * @return the value to which this map maps the specified key, or
	 *	       <tt>null</tt> if the map contains no mapping for this key.
	 * 
	 * @throws ClassCastException if the key is of an inappropriate type for
	 * 		  this map (optional).
	 * @throws NullPointerException key is <tt>null</tt> and this map does not
	 *		  not permit <tt>null</tt> keys (optional).
	 * 
	 * @see #containsKey(Object)
	 */
	public Object get(Object key)
	{
		// Have to check for containsKey, as stored value may be null. 
		Object	value	= null;
		boolean	found;
		if(found=local.containsKey(key))
		{
			value	= local.get(key);
		}

		for(int i=0; !found && i<parents.length; i++)
		{
			if(found=parents[i].containsKey(key))
			{
				value	= parents[i].get(key);
			}
		}

		return value;
	}

	// Modification Operations

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
	 * @return previous value associated with specified key, or <tt>null</tt>
	 *	       if there was no mapping for key.  A <tt>null</tt> return can
	 *	       also indicate that the map previously associated <tt>null</tt>
	 *	       with the specified key, if the implementation supports
	 *	       <tt>null</tt> values.
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
	public Object put(Object key, Object value)
	{
		return local.put(key, value);
	}

	/**
	 * Removes the mapping for this key from this map if it is present
	 * (optional operation).   More formally, if this map contains a mapping
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
	 * @return previous value associated with specified key, or <tt>null</tt>
	 *	       if there was no mapping for key.
	 *
	 * @throws ClassCastException if the key is of an inappropriate type for
	 * 		  this map (optional).
	 * @throws NullPointerException if the key is <tt>null</tt> and this map
	 *            does not not permit <tt>null</tt> keys (optional).
	 * @throws UnsupportedOperationException if the <tt>remove</tt> method is
	 *         not supported by this map.
	 */
	public Object remove(Object key)
	{
		return local.remove(key);
	}


	// Bulk Operations

	/**
	 * Copies all of the mappings from the specified map to this map
	 * (optional operation).  The effect of this call is equivalent to that
	 * of calling {@link #put(Object,Object) put(k, v)} on this map once
	 * for each mapping from key <tt>k</tt> to value <tt>v</tt> in the 
	 * specified map.  The behavior of this operation is unspecified if the
	 * specified map is modified while the operation is in progress.
	 *
	 * @param t Mappings to be stored in this map.
	 * 
	 * @throws UnsupportedOperationException if the <tt>putAll</tt> method is
	 * 		  not supported by this map.
	 * 
	 * @throws ClassCastException if the class of a key or value in the
	 * 	          specified map prevents it from being stored in this map.
	 * 
	 * @throws IllegalArgumentException some aspect of a key or value in the
	 *	          specified map prevents it from being stored in this map.
	 * @throws NullPointerException the specified map is <tt>null</tt>, or if
	 *         this map does not permit <tt>null</tt> keys or values, and the
	 *         specified map contains <tt>null</tt> keys or values.
	 */
	public void putAll(Map t)
	{
		Iterator	it	= t.keySet().iterator();
		while(it.hasNext())
		{
			Object	key	= it.next();
			put(key, t.get(key));
		}
	}

	/**
	 * Removes all mappings from this map (optional operation).
	 *
	 * @throws UnsupportedOperationException if clear is not supported by this
	 * 		  map.
	 */
	public void clear()
	{
		local.clear();
	}


	// Views

	/**
	 * Returns a set view of the keys contained in this map.  The set is
	 * backed by the map, so changes to the map are reflected in the set, and
	 * vice-versa.  If the map is modified while an iteration over the set is
	 * in progress, the results of the iteration are undefined.  The set
	 * supports element removal, which removes the corresponding mapping from
	 * the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
	 * <tt>removeAll</tt> <tt>retainAll</tt>, and <tt>clear</tt> operations.
	 * It does not support the add or <tt>addAll</tt> operations.
	 *
	 * @return a set view of the keys contained in this map.
	 */
	public Set keySet()
	{
		// todo: return a view
		// todo: eliminate dublicates
		Set ret = new HashSet(local.keySet());
		for(int i=0; i<parents.length; i++)
			ret.addAll(parents[i].keySet());
		return ret;
		//throw new UnsupportedOperationException("keySet() not supported for NestedMap.");
	}

	/**
	 * Returns a collection view of the values contained in this map.  The
	 * collection is backed by the map, so changes to the map are reflected in
	 * the collection, and vice-versa.  If the map is modified while an
	 * iteration over the collection is in progress, the results of the
	 * iteration are undefined.  The collection supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
	 * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations.
	 * It does not support the add or <tt>addAll</tt> operations.
	 *
	 * @return a collection view of the values contained in this map.
	 */
	public Collection values()
	{
		// todo: return a view
		// todo: eliminate dublicates
		Collection ret = new HashSet(local.values());
		for(int i=0; i<parents.length; i++)
			ret.addAll(parents[i].values());
		return ret;
		//throw new UnsupportedOperationException("values() not supported for NestedMap.");
	}

	/**
	 * Returns a set view of the mappings contained in this map.  Each element
	 * in the returned set is a Map.Entry.  The set is backed by the
	 * map, so changes to the map are reflected in the set, and vice-versa.
	 * If the map is modified while an iteration over the set is in progress,
	 * the results of the iteration are undefined.  The set supports element
	 * removal, which removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
	 * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not support
	 * the <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a set view of the mappings contained in this map.
	 */
	public Set entrySet()
	{
		throw new UnsupportedOperationException("entrySet() not supported for NestedMap.");
	}

	// Comparison and hashing

	/**
	 * Compares the specified object with this map for equality.  Returns
	 * <tt>true</tt> if the given object is also a map and the two Maps
	 * represent the same mappings.  More formally, two maps <tt>t1</tt> and
	 * <tt>t2</tt> represent the same mappings if
	 * <tt>t1.entrySet().equals(t2.entrySet())</tt>.  This ensures that the
	 * <tt>equals</tt> method works properly across different implementations
	 * of the <tt>Map</tt> interface.
	 *
	 * @param o object to be compared for equality with this map.
	 * @return <tt>true</tt> if the specified object is equal to this map.
	 */
	public boolean equals(Object o)
	{
		return (o instanceof Map) && hashCode()==o.hashCode();
	}

	/**
	 * Returns the hash code value for this map.  The hash code of a map
	 * is defined to be the sum of the hashCodes of each entry in the map's
	 * entrySet view.  This ensures that <tt>t1.equals(t2)</tt> implies
	 * that <tt>t1.hashCode()==t2.hashCode()</tt> for any two maps
	 * <tt>t1</tt> and <tt>t2</tt>, as required by the general
	 * contract of Object.hashCode.
	 *
	 * @return the hash code value for this map.
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @see #equals(Object)
	 */
	public int hashCode()
	{
		// Hash code is obtained as sum of the entries' hash codes. 
		int	ret	= local.entrySet().hashCode();
		for(int i=0; i<parents.length; i++)
		{
			ret	+= parents[i].entrySet().hashCode();
		}
		return ret;
	}

	/**
	 *  Create a string representation of this map.
	 */
	public String	toString()
	{
		return "NestedMap(local="+local+", parents="+SUtil.arrayToString(parents)+")";
	}
}

