package jadex.commons.collection;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import jadex.commons.SUtil;


/**
 *	This class combines the list and map interface.
 *  So it provides fast lookup (via map) and ordering
 *  (via list).
 *  Because the remove(Object) method has different return types
 *  in map and list, index map cannot implement both interfaces.
 *  Methods are provided to get a map instance and a list instance
 *  of an index map.
 */
public class IndexMap<K, V>	implements Serializable, Cloneable
{
	//-------- attributes --------

	/** The key list. */
	protected List<K>	list;

	/** The key/value map. */
	protected Map<K, V>	map; 

	/** The index map as java.util.Map. */
	protected Map<K, V>	asmap;

	/** The index map as java.util.List. */
	protected List<V>	aslist;

	//-------- Constructors --------

	/**
	 *	Create a new index map. 
	 */
	public IndexMap()
	{
		this(new ArrayList<K>(), new HashMap<K, V>());
	}
	
	/**
	 *	Create a new index map. 
	 */
	public IndexMap(IndexMap<K, V> imap)
	{
		this();
		for(K key: imap.keySet())
		{
			put(key, imap.get(key));
		}
	}

	/**
	 *	Create a new index map using the specified collections as backup.
	 *  @param list	The key list.
	 *  @param map	The key/value map.
	 */
	public IndexMap(List<K> list, Map<K, V> map)
	{
		this.list	= list;
		this.map	= map;
	}

	/**
	 *  Clone an index map.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		IndexMap<K, V> ret = (IndexMap<K, V>) super.clone();
		ArrayList<K> listcopy = SCollection.createArrayList();
		listcopy.addAll(list);
		Map<K, V> mapcopy = SCollection.createHashMap();
		mapcopy.putAll(map);
		ret.list = listcopy;
		ret.map = mapcopy;
		return ret;
//		return new IndexMap<K, V>(listcopy, mapcopy);
	}

	//-------- Map methods --------

	// Query Operations

	/**
	 * Returns the number of key-value mappings in this map.  If the
	 * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 * @return the number of key-value mappings in this map.
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
	public boolean containsKey(Object key)  // not V because of Map interface
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
	public boolean containsValue(Object value) // not V because of Map interface
	{
		return map.containsValue(value);
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
	public V get(Object key)  // not K because of Map interface
	{
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
	public V put(K key, V value)
	{
		list.remove(key);
		list.add(key);
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
		Iterator<? extends K>	it	= t.keySet().iterator();
		while(it.hasNext())
		{
			K key	= it.next();
			put(key, t.get(key));
		}
	}

	/**
	 * Removes all mappings from this map.
	 */
	public void clear()
	{
		list.clear();
		map.clear();
	}


	// Views

	/**
	 * Returns a set view of the keys contained in this map.  The set is
	 * backed by the map, so changes to the map are reflected in the set, and
	 * vice-versa.  If the map is modified while an iteration over the set is
	 * in progress, the results of the iteration are undefined.
	 *
	 * No key order is reflected in the set!
	 *
	 * @return a set view of the keys contained in this map.
	 */
	public Set<K> keySet()
	{
		return new KeySetWrapper();
	}

	/**
	 * Returns a collection view of the values contained in this map.  The
	 * collection is backed by the map, so changes to the map are reflected in
	 * the collection, and vice-versa.  If the map is modified while an
	 * iteration over the collection is in progress, the results of the
	 * iteration are undefined.
	 *
	 * @return a collection view of the values contained in this map.
	 */
	public Collection<V> values()
	{
		return getAsList();
	}

	/**
	 * Returns a set view of the mappings contained in this map.  Each element
	 * in the returned set is a Map.Entry.  The set is backed by the
	 * map, so changes to the map are reflected in the set, and vice-versa.
	 * If the map is modified while an iteration over the set is in progress,
	 * the results of the iteration are undefined.
	 *
 	 * No key order is reflected in the set!
	 *
	 * @return a set view of the mappings contained in this map.
	 */
	public Set<Map.Entry<K, V>> entrySet()
	{
		return Collections.unmodifiableSet(map.entrySet());
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
		return (o instanceof IndexMap) && hashCode()==o.hashCode();
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
		return map.hashCode() + list.hashCode();
	}

	/**
	 *  Create a string representation of this map.
	 */
	public String	toString()
	{
		return "IndexMap(map="+map+", list="+list+")";
	}

	//-------- List interface --------

	/**
	 * 
	 * Returns <tt>true</tt> if this list contains the specified element.
	 * More formally, returns <tt>true</tt> if and only if this list contains
	 * at least one element <tt>e</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 *
	 * @param o element whose presence in this list is to be tested.
	 * @return <tt>true</tt> if this list contains the specified element.
	 * @throws ClassCastException if the type of the specified element
	 * 	       is incompatible with this list (optional).
	 * @throws NullPointerException if the specified element is null and this
	 *         list does not support null elements (optional).
	 */
	public boolean	contains(Object o)
	{
		return map.containsValue(o);
	}

	/**
	 * Returns an iterator over the elements in this list in proper sequence.
	 *
	 * @return an iterator over the elements in this list in proper sequence.
	 */
	public Iterator<V>	iterator()
	{
		return new Iterator<V>()
		{
			Iterator<K>	i	= list.iterator();

			public boolean	hasNext()
			{
				return i.hasNext();
			}

			public V	next()
			{
				return map.get(i.next());
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}
	    };
	}

	/**
	 * Returns an array containing all of the elements in this list in proper
	 * sequence.  Obeys the general contract of the
	 * <tt>Collection.toArray</tt> method.
	 *
	 * @return an array containing all of the elements in this list in proper
	 *	       sequence.
	 * @see Arrays#asList(Object[])
	 */
	public Object[]	toArray()
	{
		Object[]	array	= new Object[list.size()];
		for(int i=0; i<list.size(); i++)
		{
			array[i]	= map.get(list.get(i));
		}
		return array;
	}

	/**
	 * Returns an array containing all of the elements in this list in proper
	 * sequence; the runtime type of the returned array is that of the
	 * specified array.  Obeys the general contract of the
	 * <tt>Collection.toArray(Object[])</tt> method.
	 *
	 * @param array the array into which the elements of this list are to
	 *		be stored, if it is big enough; otherwise, a new array of the
	 * 		same runtime type is allocated for this purpose.
	 * @return  an array containing the elements of this list.
	 * 
	 * @throws ArrayStoreException if the runtime type of the specified array
	 * 		  is not a supertype of the runtime type of every element in
	 * 		  this list.
	 * @throws NullPointerException if the specified array is <tt>null</tt>.
	 */
	public <T> T[] toArray(T[] array)
	{
		if(array.length<list.size())
		{
			array	= (T[])Array.newInstance(
				array.getClass().getComponentType(), list.size());
		}

		for(int i=0; i<list.size(); i++)
		{
			array[i]	= (T)map.get(list.get(i));
		}

		return array;
	}

	// Bulk Operations

	/**
	 * 
	 * Returns <tt>true</tt> if this list contains all of the elements of the
	 * specified collection.
	 *
	 * @param  c collection to be checked for containment in this list.
	 * @return <tt>true</tt> if this list contains all of the elements of the
	 * 	       specified collection.
	 * @throws ClassCastException if the types of one or more elements
	 *         in the specified collection are incompatible with this
	 *         list (optional).
	 * @throws NullPointerException if the specified collection contains one
	 *         or more null elements and this list does not support null
	 *         elements (optional).
	 * @throws NullPointerException if the specified collection is
	 *         <tt>null</tt>.
	 * @see #contains(Object)
	 */
	public boolean containsAll(Collection<?> c)
	{
		Iterator<?> i= c.iterator();
		while(i.hasNext())
		{
			if(!contains(i.next()))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Removes from this list all the elements that are contained in the
	 * specified collection.
	 *
	 * @param c collection that defines which elements will be removed from
	 *          this list.
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * 
	 * @throws ClassCastException if the types of one or more elements
	 *            in this list are incompatible with the specified
	 *            collection (optional).
	 * @throws NullPointerException if this list contains one or more
	 *            null elements and the specified collection does not support
	 *            null elements (optional).
	 * @throws NullPointerException if the specified collection is
	 *            <tt>null</tt>.
	 * @see #removeValue(Object)
	 * @see #contains(Object)
	 */
	public boolean removeAll(Collection<?> c)
	{
		boolean	removed	= false;
		Iterator<?>	i	=	c.iterator();
		while(i.hasNext())
		{
			removeValue(i.next());
			removed	= true;
		}
		return removed;
	}


	/**
	 * Retains only the elements in this list that are contained in the
	 * specified collection.  In other words, removes
	 * from this list all the elements that are not contained in the specified
	 * collection.
	 *
	 * @param c collection that defines which elements this set will retain.
	 * 
	 * @return <tt>true</tt> if this list changed as a result of the call.
	 * 
	 * @throws ClassCastException if the types of one or more elements
	 *            in this list are incompatible with the specified
	 *            collection (optional).
	 * @throws NullPointerException if this list contains one or more
	 *            null elements and the specified collection does not support
	 *            null elements (optional).
	 * @throws NullPointerException if the specified collection is
	 *         <tt>null</tt>.
	 * @see #removeValue(Object)
	 * @see #contains(Object)
	 */
	public boolean retainAll(Collection<?> c)
	{
		boolean	removed	= false;
		Iterator<?>	i	= iterator();
		while(i.hasNext())
		{
			Object val = i.next();
			if(!c.contains(val))
			{
				removeValue(val);
				removed	= true;
			}
		}
		return removed;
	}

	// Positional Access Operations

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param index index of element to return.
	 * @return the element at the specified position in this list.
	 * 
	 * @throws IndexOutOfBoundsException if the index is out of range (index
	 * 		  &lt; 0 || index &gt;= size()).
	 */
	public V	get(int index)
	{
		return map.get(list.get(index));
	}

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element.
	 *
	 * @param index index of element to replace.
	 * @param element element to be stored at the specified position.
	 * @return the element previously at the specified position.
	 * 
	 * @throws    ClassCastException if the class of the specified element
	 * 		  prevents it from being added to this list.
	 * @throws    NullPointerException if the specified element is null and
	 *            this list does not support null elements.
	 * @throws    IllegalArgumentException if some aspect of the specified
	 *		  element prevents it from being added to this list.
	 * @throws    IndexOutOfBoundsException if the index is out of range
	 *		  (index &lt; 0 || index &gt;= size()).
	 */
	public V set(int index, V element)
	{
		return map.put(list.get(index), element);
	}

	/**
	 * Removes the element at the specified position in this list.
	 * Shifts any subsequent elements to the left (subtracts one
	 * from their indices).  Returns the element that was removed from the
	 * list.
	 *
	 * @param index the index of the element to removed.
	 * @return the element previously at the specified position.
	 * 
	 * @throws IndexOutOfBoundsException if the index is out of range (index
	 *            &lt; 0 || index &gt;= size()).
	 */
	public V	remove(int index)
	{
		V del	= map.remove(list.get(index));
		list.remove(index);
		return del;
	}

	// Search Operations

	/**
	 * Returns the index in this list of the first occurrence of the specified
	 * element, or -1 if this list does not contain this element.
	 * More formally, returns the lowest index <tt>i</tt> such that
	 * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 *
	 * @param o element to search for.
	 * @return the index in this list of the first occurrence of the specified
	 * 	       element, or -1 if this list does not contain this element.
	 * @throws ClassCastException if the type of the specified element
	 * 	       is incompatible with this list (optional).
	 * @throws NullPointerException if the specified element is null and this
	 *         list does not support null elements (optional).
	 */
	public int	indexOf(Object o)
	{
		for(int i=0; i<list.size(); i++)
		{
			K key	= list.get(i);
			if(map.get(key).equals(o))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index in this list of the last occurrence of the specified
	 * element, or -1 if this list does not contain this element.
	 * More formally, returns the highest index <tt>i</tt> such that
	 * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 *
	 * @param o element to search for.
	 * @return the index in this list of the last occurrence of the specified
	 * 	       element, or -1 if this list does not contain this element.
	 * @throws ClassCastException if the type of the specified element
	 * 	       is incompatible with this list (optional).
	 * @throws NullPointerException if the specified element is null and this
	 *         list does not support null elements (optional).
	 */
	public int lastIndexOf(Object o)
	{
		for(int i=list.size()-1; i>=0; i--)
		{
			K key	= list.get(i);
			if(map.get(key).equals(o))
			{
				return i;
			}
		}
		return -1;
	}

	// List Iterators

	/**
	 * Returns a list iterator of the elements in this list (in proper
	 * sequence).
	 *
	 * @return a list iterator of the elements in this list (in proper
	 * 	       sequence).
	 */
	public ListIterator<V>	listIterator()
	{
		return listIterator(0);
	}

	/**
	 * Returns a list iterator of the elements in this list (in proper
	 * sequence), starting at the specified position in this list.  The
	 * specified index indicates the first element that would be returned by
	 * an initial call to the <tt>next</tt> method.  An initial call to
	 * the <tt>previous</tt> method would return the element with the
	 * specified index minus one.
	 *
	 * @param index index of first element to be returned from the
	 *		    list iterator (by a call to the <tt>next</tt> method).
	 * @return a list iterator of the elements in this list (in proper
	 * 	       sequence), starting at the specified position in this list.
	 * @throws IndexOutOfBoundsException if the index is out of range (index
	 *         &lt; 0 || index &gt; size()).
	 */
	public ListIterator<V>	listIterator(final int index)
	{
		return new ListIterator<V>()
		{
			int i	= index;

			public boolean	hasNext()
			{
				return i<size();
			}

			public V	next()
			{
				return get(i++);
			}

			public boolean	hasPrevious()
			{
				return i>0;
			}

			public V	previous()
			{
				return get(--i);
			}

			public int	nextIndex()
			{
				return i;
			}

			public int	previousIndex()
			{
				return i-1;
			}

			public void	remove()
			{
				throw new UnsupportedOperationException();
			}

			public void	set(Object o)
			{
				throw new UnsupportedOperationException();
			}

			public void	add(Object o)
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	//-------- unsupported interface methods --------

	/**
	 *  Unsupported method.
	 *  @throws UnsupportedOperationException
	 */
	public List	subList(int fromIndex, int toIndex)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Unsupported method, due to missing key parameter.
	 *  @throws UnsupportedOperationException
	 */
	public boolean	add(Object o)
	{
		throw new UnsupportedOperationException("Unsupported method, due to missing key parameter.");
	}

	/**
	 *  Unsupported method, due to missing key parameter.
	 *  @throws UnsupportedOperationException
	 */
	public boolean addAll(Collection c)
	{
		throw new UnsupportedOperationException("Unsupported method, due to missing key parameter.");
	}

	/**
	 *  Unsupported method, due to missing key parameter.
	 *  @throws UnsupportedOperationException
	 */
	public boolean addAll(int index, Collection c)
	{
		throw new UnsupportedOperationException("Unsupported method, due to missing key parameter.");
	}

	/**
	 *  Unsupported method, due to missing key parameter.
	 *  @throws UnsupportedOperationException
	 */
	public void add(int index, Object element)
	{
		throw new UnsupportedOperationException("Unsupported method, due to missing key parameter.");
	}

	//-------- additional indexmap methods --------

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
	 * @return previous value associated with specified key, or <tt>null</tt>
	 *	       if there was no mapping for key.
	 *
	 * @throws ClassCastException if the key is of an inappropriate type for
	 * 		  this map (optional).
	 * @throws NullPointerException if the key is <tt>null</tt> and this map
	 *            does not not permit <tt>null</tt> keys (optional).
	 */
	public V	removeKey(K key)
	{
		list.remove(key);
		return map.remove(key);
	}
	
	/**
	 * Removes the first occurrence in this list of the specified element.
	 * If this list does not contain the element, it is
	 * unchanged.  More formally, removes the element with the lowest index i
	 * such that <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt> (if
	 * such an element exists).
	 *
	 * @param o element to be removed from this list, if present.
	 * @return <tt>true</tt> if this list contained the specified element.
	 * @throws ClassCastException if the type of the specified element
	 * 	          is incompatible with this list (optional).
	 * @throws NullPointerException if the specified element is null and this
	 *            list does not support null elements (optional).
	 */
	public boolean	removeValue(Object o)
	{
		for(int i=0; i<list.size(); i++)
		{
			Object key	= list.get(i);
			if(map.get(key).equals(o))
			{
				list.remove(i);
				map.remove(key);
				return true;
			}
		}
		return false;
	}

	/**
	 *  Add a new object with key and value.
	 *  The key must not exist.
	 *  @param key	The key.
	 *  @param o	The object.
	 */
	public void	add(K key, V o)
	{
		add(list.size(), key, o);
	}
	
	/**
	 *  Replace an object for the given key.
	 *  The key has to exist.
	 *  @param key	The key.
	 *  @param o	The object.
	 *  @return The old value for the key.
	 */
	public V	replace(K key, V o)
	{
		if(map.get(key)==null)
		{
			throw new RuntimeException("Old key does not exist: "+key);
		}
		return map.put(key, o);
	}

	/**
	 *	Get an indexed key.
	 *	@param index	The index.
	 *	@return	The key.
	 */
	public K	getKey(int index)
	{
		return list.get(index);
	}

	/**
	 *  Add an object to the collection.
	 *  @param index The index.
	 *  @param key The key.
	 *  @param o The object.
	 */
	public void	add(int index, K key, V o)
	{
		if(map.get(key)!=null)
		{
			throw new RuntimeException("Old key exists: "+index+" "+key);//+" "+o);
		}
		map.put(key, o);
		list.add(index, key);
	}

	/**
	 *  Return an instance of this index map accessible via map interface.
	 */
	public Map<K, V>	getAsMap()
	{
		return asmap!=null ? asmap : (asmap=new MapIndexMap<K, V>(list, map));
	}

	/**
	 *  Return an instance of this index map accessible via list interface.
	 */
	public List<V>	getAsList()
	{
		return aslist!=null ? aslist : (aslist=new ListIndexMap<K, V>(list, map));
	}

	/**
	 *  Get the values as array.
	 *  @return The array of values.
	 */
	public Object[]	getObjects()
	{
		return toArray();
	}

	/**
	 *  Get the values as array.
	 *  @param type	The component type of the array.
	 *  @return The array of values.
	 */
	public Object[]	getObjects(Class type)
	{
		return toArray((Object[])Array.newInstance(type, list.size()));
	}

	/**
	 *  Get the keys as array.
	 *  @return The array of keys.
	 */
	public Object[]	getKeys()
	{
		return list.toArray();
	}

	/**
	 *  Get the keys as array.
	 *  @param type	The component type of the array.
	 *  @return The array of keys.
	 */
	public Object[]	getKeys(Class<?> type)
	{
		return list.toArray((Object[])Array.newInstance(type, list.size()));
	}

	//-------- inner classes --------

	/**
	 *  Provide access to the index map via map interface.
	 */
	public static class MapIndexMap<K, V>	extends IndexMap<K, V>	implements Map<K, V>
	{
		//-------- constructor --------

		/**
		 *  Create a new map interface index map.
		 */
		public MapIndexMap()
		{
			super();
		}

		/**
		 *  Create a new map interface index map.
		 *  @param list	The list.
		 *  @param map	The map.
		 */
		public MapIndexMap(List<K> list, Map<K, V> map)
		{
			super(list, map);
		}

		//-------- Map interface --------

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
		 * @return previous value associated with specified key, or <tt>null</tt>
		 *	       if there was no mapping for key.
		 *
		 * @throws ClassCastException if the key is of an inappropriate type for
		 * 		  this map (optional).
		 * @throws NullPointerException if the key is <tt>null</tt> and this map
		 *            does not not permit <tt>null</tt> keys (optional).
		 */
		public V	remove(Object key)
		{
			return removeKey((K)key);
		}

		/**
		 *  Clone an index map.
		 */
		public Object clone()
		{
			ArrayList listcopy = new ArrayList();
			listcopy.addAll(list);
			HashMap mapcopy = new HashMap();
			mapcopy.putAll(map);
			return new MapIndexMap(listcopy, mapcopy);
		}
	}

	/**
	 *  Provide access to the index map via list interface.
	 */
	public static class ListIndexMap<K, V>	extends IndexMap<K, V>	implements List<V>
	{
		//-------- constructor --------

		/**
		 *  Create a new list interface index map.
		 */
		public ListIndexMap()
		{
			super();
		}

		/**
		 *  Create a new list interface index map.
		 *  @param list	The list.
		 *  @param map	The map.
		 */
		public ListIndexMap(List<K> list, Map<K, V> map)
		{
			super(list, map);
		}

		//-------- List interface --------
		
		/**
		 * Removes the first occurrence in this list of the specified element.
		 * If this list does not contain the element, it is
		 * unchanged.  More formally, removes the element with the lowest index i
		 * such that <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt> (if
		 * such an element exists).
		 *
		 * @param o element to be removed from this list, if present.
		 * @return <tt>true</tt> if this list contained the specified element.
		 * @throws ClassCastException if the type of the specified element
		 * 	          is incompatible with this list (optional).
		 * @throws NullPointerException if the specified element is null and this
		 *            list does not support null elements (optional).
		 */
		public boolean	remove(Object o)
		{
			return removeValue(o);
		}

		/**
		 *  Clone an index map.
		 */
		public Object clone()
		{
			ArrayList listcopy = new ArrayList();
			listcopy.addAll(list);
			HashMap mapcopy = new HashMap();
			mapcopy.putAll(map);
			return new ListIndexMap(listcopy, mapcopy);
		}
	}

	/**
	 *  Provide set access to the key list, while preserving ordering.
	 */
	class KeySetWrapper	implements Set<K>
	{
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
			return list.size();
		}

		/**
		 * Returns <tt>true</tt> if this set contains no elements.
		 *
		 * @return <tt>true</tt> if this set contains no elements.
		 */
		public boolean isEmpty()
		{
			return list.isEmpty();
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
			return list.contains(o);
		}

		/**
		 * Returns an iterator over the elements in this set.  The elements are
		 * returned in no particular order (unless this set is an instance of some
		 * class that provides a guarantee).
		 *
		 * @return an iterator over the elements in this set.
		 */
		public Iterator<K> iterator()
		{
			return list.iterator();
		}

		/**
		 * Returns an array containing all of the elements in this set.
		 * Obeys the general contract of the <tt>Collection.toArray</tt> method.
		 *
		 * @return an array containing all of the elements in this set.
		 */
		public Object[] toArray()
		{
			return list.toArray();
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
		public Object[] toArray(Object a[])
		{
			return list.toArray(a);
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
		public boolean add(Object o)
		{
			throw new UnsupportedOperationException("Unsupported method, due to missing value parameter.");
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
			map.remove(o);
			return list.remove(o);
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
		public boolean containsAll(Collection c)
		{
			return list.containsAll(c);
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
		public boolean addAll(Collection c)
		{
			throw new UnsupportedOperationException("Unsupported method, due to missing value parameter.");
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
		public boolean retainAll(Collection c)
		{
			boolean	modified	= false;
			Iterator	i	= list.iterator();
			while(i.hasNext())
			{
				Object	key	= i.next();
				if(c.contains(key))
				{
					modified	= remove(key);
				}
			}
			return modified;
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
		public boolean removeAll(Collection c)
		{
			boolean	modified	= false;
			Iterator	i	= c.iterator();
			while(i.hasNext())
			{
				Object	key	= i.next();
				modified	= remove(key);
			}
			return modified;
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
			IndexMap.this.clear();
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
			return (o instanceof Set) && size()==((Collection)o).size()
				&& containsAll((Collection)o);
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
			int	code	= 0;
			Iterator i = iterator();
			while(i.hasNext())
			{
				Object	o	= i.next();
				code += o!=null ? o.hashCode() : 0;
			}
			return code;
		}

		/**
		 *  A string representation of the key set.
		 */
		public String	toString()
		{
			return SUtil.arrayToString(toArray());
		}
	}
}

