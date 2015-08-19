package jadex.commons.collection;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jadex.commons.SUtil;


/**
 *  An MultiCollection is a map with the ability
 *  to store more than one element per key (an collection).
 */
public class MultiCollection<K, V> implements Map<K, Collection<V>>, Serializable, Cloneable
{
	//-------- attributes --------
	
	/** The map. */
	protected Map<K, Collection<V>> map;

	/** The collection type. */
	protected Class<?> type;
	
//	/** Alternatively the create command. */
//	protected IResultCommand<Collection<V>, Void> createcmd;

	//-------- constructors --------

	/**
	 *  Create a new multi collection.
	 */
	public	MultiCollection()
	{
		this(new HashMap<K, Collection<V>>(), ArrayList.class);
	}

	/**
	 *  Create a new multi collection.
	 *  @param map	The undelying map.
	 *  @param type	The collection type to use
	 *    (requires public empty contstructor and has to implement java.util.Collection).
	 */
	public	MultiCollection(Map<K, Collection<V>> map, Class<?> type)
	{
		this.map	= map;
		this.type	= type;
	}
	
//	/**
//	 *  Create a new multi collection.
//	 *  @param map	The undelying map.
//	 *  @param type	The collection type to use
//	 *    (requires public empty contstructor and has to implement java.util.Collection).
//	 */
//	public	MultiCollection(Map<K, Collection<V>> map, IResultCommand<Collection<V>, Void> createcmd)
//	{
//		this.map	= map;
//		this.createcmd = createcmd;
//	}

	/**
	 *  Clone a multi collection.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		MultiCollection ret = (MultiCollection)super.clone();
		// Hack. does not work!!! map could be of other type.
		HashMap mapcopy = new HashMap();
		mapcopy.putAll(map);
//		return new MultiCollection(mapcopy, type);
		ret.map = mapcopy;
		ret.type = type;
		return ret;
	}

	//-------- Map methods --------

	// Query Operations

	/**
	 * Returns the number of elements added to this map.
	 */
	public int size()
	{
		int	size	= 0;
		for(Iterator<K> i=map.keySet().iterator(); i.hasNext(); )
		{
			K	key	= i.next();
			Collection<V> coll = map.get(key);
			size	+= coll.size();
		}
		return size;
	}

	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 * @return <tt>true</tt> if this map contains no key-value mappings.
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
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
		return map.containsKey(key);
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
		for(Iterator<K> i=map.keySet().iterator(); i.hasNext(); )
		{
			if(map.get(i.next()).contains(value))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the collection to which this map maps the specified key.  Returns
	 * <tt>null</tt> if the map contains no mapping for this key.
	 *
	 * @param key key whose associated collection is to be returned.
	 * @return the collection to which this map maps the specified key, or
	 *	       <tt>null</tt> if the map contains no mapping for this key.
	 * 
	 * @throws ClassCastException if the key is of an inappropriate type for
	 * 		  this map (optional).
	 * @throws NullPointerException key is <tt>null</tt> and this map does not
	 *		  not permit <tt>null</tt> keys (optional).
	 * 
	 * @see #containsKey(Object)
	 */
	public Collection<V> get(Object key)
	{
		Collection<V> ret = map.get(key);
//		return ret==null? Collections.EMPTY_LIST: ret;
		return ret;
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
	public Collection<V> put(K key, Collection<V> value)
//	public Object put(Object key, Object value)
	{
		return map.put(key, value);
//		Collection<V>	col;
//		col	= map.get(key);
//		if(col==null)
//		{
//			try
//			{
//				col	= (Collection<V>)type.newInstance();
//			}
//			catch(InstantiationException e)
//			{
//				StringWriter sw = new StringWriter();
//				e.printStackTrace(new PrintWriter(sw));
//				throw new RuntimeException(sw.toString());
//			}
//			catch(IllegalAccessException e)
//			{
//				StringWriter sw = new StringWriter();
//				e.printStackTrace(new PrintWriter(sw));
//				throw new RuntimeException(sw.toString());
//			}
//			map.put(key, col);
//		}
//		col.add(value);
//		return col;
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
	public Collection<V> add(K key, V value)
	{
		Collection<V> col;
		col	= map.get(key);
		if(col==null)
		{
			col	= createCollection(key);
			map.put(key, col);
		}
		col.add(value);
		return col;
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
	public Collection<V> add(K key, Collection<V> value)
	{
		Collection<V> col;
		col	= map.get(key);
		if(col==null)
		{
			col	= createCollection(key);
			map.put(key, col);
		}
		col.addAll(value);
		return col;
	}

	/**
	 *  Add all elements of another map.
	 *  @param t The other map.
	 */
	public void addAll(Map<? extends K, ? extends V> t)
	{
		Iterator<? extends K> it = t.keySet().iterator();
		while(it.hasNext())
		{
			K key	= it.next();
			add(key, t.get(key));
		}
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
//	public void putAll(Map<? extends K, Collection<? extends V>> t)
	public void putAll(Map<? extends K, ? extends Collection<V>> t)
	{
		Iterator<? extends K> it = t.keySet().iterator();
		while(it.hasNext())
		{
			K key	= it.next();
			add(key, t.get(key));
		}
	}

	/**
	 * Removes all mappings from this map.
	 */
	public void clear()
	{
		map.clear();
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
	public Set<K> keySet()
	{
		return map.keySet();
	}

	/**
	 * Unsupported Operation.
	 * @throws UnsupportedOperationException
	 */
	public Collection<Collection<V>> values()
	{
		return map.values();
	}

    /**
     * Returns a set view of the mappings contained in this map.  Each element
     * in the returned set is a map entry.  The set is backed by the
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
	public Set<Map.Entry<K, Collection<V>>> entrySet()
	{
		return map.entrySet();
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
		return (o instanceof MultiCollection) && hashCode()==o.hashCode();
	}

	/**
	 * Returns the hash code value for this map.
	 *
	 * @return the hash code value for this map.
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @see #equals(Object)
	 */
	public int hashCode()
	{
		return map.hashCode();
	}

	/**
	 *  Create a string representation of this map.
	 */
	public String	toString()
	{
		return "MultiCollection(map="+map+")";
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
	public Collection<V> remove(Object key)
	{
		return map.remove(key);
	}

	//-------- additional multi collection methods --------

	/**
	 *  Get the values associated to a key as collection.
	 *  @param key	The key.
	 *  @return The collection of associated values.
	 */
	public Collection<V>	getCollection(Object key)
	{
		Collection<V> ret	= get(key);
		if(ret==null)
		{
			ret	= Collections.EMPTY_LIST;
		}
		return ret;
	}
	
//	/**
//	 *  Directly store a collection entry.
//	 */
//	public void putCollection(Object key, Collection value)
//	{
//		map.put(key, value);
//	}


	/**
	 *  Get the values as array.
	 *  @return The array of values.
	 */
	public Object[]	getObjects()
	{
		return getObjects(Object.class);
	}

	/**
	 *  Get the values as array.
	 *  @param type	The component type of the array.
	 *  @return The array of values.
	 */
	public Object[]	getObjects(Class<?> type)
	{
		Object	ret	= Array.newInstance(type, 0);
		for(Iterator<K> i=map.keySet().iterator(); i.hasNext(); )
		{
			ret	= SUtil.joinArrays(ret, get(i.next()).toArray());
		}
		return (Object[])ret;
	}

	/**
	 *  Get the keys as array.
	 *  @return The array of keys.
	 */
	public Object[]	getKeys()
	{
		return keySet().toArray();
	}

	/**
	 *  Get the keys as array.
	 *  @param type	The component type of the array.
	 *  @return The array of keys.
	 */
	public Object[]	getKeys(Class<?> type)
	{
		Set<K>	keys	= keySet();
		return keys.toArray((Object[])Array.newInstance(type, keys.size()));
	}

	/**
	 *  Remove a special object from the 
	 *  collection of a defined key.
	 */
	public void	removeObject(Object key, Object value)
	{
		Collection<V> coll	= map.get(key);
//		if(coll==null)
//			throw new RuntimeException("Key does not exist!"+key);
//		if(!coll.remove(value))
//			throw new RuntimeException("Value does not exist!"+value);
		if(coll!=null)
			coll.remove(value);
		if(coll!=null && coll.isEmpty())
		{
			map.remove(key);
		}
	}
	
	/**
	 *  Create a collection instance.
	 */
	public Collection<V> createCollection(K key)
	{
		Collection<V> ret = null;
		if(type!=null)
		{
			try
			{
				ret	= (Collection<V>)type.newInstance();
			}
			catch(InstantiationException e)
			{
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				throw new RuntimeException(sw.toString());
			}
			catch(IllegalAccessException e)
			{
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				throw new RuntimeException(sw.toString());
			}
		}
//		else if(createcmd!=null)
//		{
//			ret = createcmd.execute(null);
//		}
		else
		{
			ret = new ArrayList<V>();
		}
		
		return ret;
	}
}

