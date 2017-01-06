package jadex.commons.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jadex.commons.ICommand;
import jadex.commons.collection.LeaseTimeCollection.SynchronizedLeaseTimeCollection;

/**
 *  Lease time map with supervised write/update access.
 *  For every entry a (potentially different) leasetime is used. 
 */
public class LeaseTimeMap<K, V> implements Map<K, V>
{
	/** The map. */
	protected Map<K, V> map;
	
	/** Lease time map with keys. */
	protected ILeaseTimeCollection<K> times;
	
	/** Flag if touch on read. */
	protected boolean touchonread;
	
	/** Flag if touch on read. */
	protected boolean touchonwrite;
	
	/**
	 *  Create a new lease time map.
	 */
	public LeaseTimeMap(long leasetime)
	{
		this(leasetime, null, true, true);
	}
	

	/**
	 *  Create a new lease time map.
	 */
	public LeaseTimeMap(long leasetime, final ICommand<K> removecmd, boolean touchonread, boolean touchonwrite)
	{
		this(leasetime, removecmd, touchonread, touchonwrite, null, true);
	}
	
	/**
	 *  Create a new lease time map.
	 */
	public LeaseTimeMap(long leasetime, final ICommand<K> removecmd, boolean touchonread, boolean touchonwrite, IDelayRunner timer, boolean sync)
	{
		this.touchonread = touchonread;
		this.touchonwrite = touchonwrite;
		this.map = new HashMap<K, V>();
		
		ICommand<K> rcmd = new ICommand<K>()
		{
			public void execute(K args)
			{
//				System.out.println("removed: "+args);
				LeaseTimeMap.this.map.remove(args);
				if(removecmd!=null)
					removecmd.execute(args);
			}
		};
		
		this.times = LeaseTimeCollection.createLeaseTimeCollection(leasetime, rcmd, timer, sync, this);
	}
	
//	/**
//	 *  Create a new lease time map.
//	 */
//	public LeaseTimeMap(Map<K, V> map, ILeaseTimeCollection<K> times, long leasetime, final ICommand<K> removecmd, boolean touchonread, boolean touchonwrite)
//	{
//		this.touchonread = touchonread;
//		this.touchonwrite = touchonwrite;
//		this.map = map!=null? map: new HashMap<K, V>();
//		
//		ICommand<K> rcmd = new ICommand<K>()
//		{
//			public void execute(K args)
//			{
////				System.out.println("removed: "+args);
//				LeaseTimeMap.this.map.remove(args);
//				if(removecmd!=null)
//					removecmd.execute(args);
//			}
//		};
//		
//		if(times!=null)
//		{
//			this.times = times;
//			this.times.setRemoveCommand(rcmd);
//		}
//		else
//		{
//			this.times = LeaseTimeCollection.createLeaseTimeCollection(leasetime, rcmd, this);
//		}
//		
////		this.times = times!=null? times: new LeaseTimeCollection<K>(leasetime, rcmd);
//	}

	//-------- Map methods --------

	// Query Operations

	/**
	 * Returns the number of elements added to this map.
	 */
	public int size()
	{
		return map.size();
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
		if(touchonread)
			touch((K)key);
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
//		if(touchonread)
//			touch((K)key);
		return map.containsValue(value);
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
	public V get(Object key)
	{
		if(touchonread)
			touch((K)key);
		return map.get(key);
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
	public V put(K key, V value)
	{
		if(map.containsKey(key))
		{
			if(touchonwrite)
				touch((K)key);
		}
		else
		{
			times.add(key);
		}
		return map.put(key, value);
	}
	
	public V put(K key, V value, long leasetime)
	{
		if(map.containsKey(key))
		{
			if(touchonwrite)
				times.touch(key, leasetime);
		}
		else
		{
			times.add(key, leasetime);
		}
		return map.put(key, value);
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
	public void putAll(Map<? extends K, ? extends V> t)
	{
		for(Map.Entry<? extends K, ? extends V> entry: t.entrySet())
		{
			put(entry.getKey(), entry.getValue());
		}
	}
	
	public void putAll(Map<? extends K, ? extends V> t, long leasetime)
	{
		for(Map.Entry<? extends K, ? extends V> entry: t.entrySet())
		{
			put(entry.getKey(), entry.getValue(), leasetime);
		}
	}

	/**
	 * Removes all mappings from this map.
	 */
	public void clear()
	{
		times.clear();
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
	public Collection<V> values()
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
	public Set<Map.Entry<K, V>> entrySet()
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
		return (o instanceof LeaseTimeMap) && hashCode()==o.hashCode();
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
	public String toString()
	{
		return "LeaseTimeMap(map="+map+")";
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
	public V remove(Object key)
	{
		times.remove(key);
		return map.remove(key);
	}
	
	/**
	 *  Update the timestamp of e.
	 *  @param entry The entry.
	 */
	public void touch(K key)
	{
		times.touch(key);
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		LeaseTimeMap<String, String> map = new LeaseTimeMap<String, String>(3000);
		
		map.put("99", "99", 10000);
		
		for(int i=0; i<5; i++)
		{
			String str = ""+Integer.valueOf(i);
			map.put(str, str);
			
			map.touch("1");
			
//			if(i%1000==0)
			{
				try
				{
					Thread.sleep(1000);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		while(true)
		{
			map.touch("1");
			try
			{
				Thread.sleep(1000);
				System.out.print(".");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
}
