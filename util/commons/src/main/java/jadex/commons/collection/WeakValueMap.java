package jadex.commons.collection;

import java.lang.ref.ReferenceQueue;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.SUtil;

/**
 *  A map with weak values.
 */
public class WeakValueMap<K, V>	implements Map<K, V>
{
	//-------- attributes --------
	
	/** The contents. */
	protected Map<K, WeakEntry<V>>	contents;
	
	/** The reference queue. */
	protected ReferenceQueue<V>	queue;
	
	//-------- constructors --------
	
	/**
	 *  Create a new weak value map.
	 */
	public WeakValueMap()
	{
		this.contents	= new LinkedHashMap<K, WeakEntry<V>>();
		this.queue	= new ReferenceQueue<V>();
	}
	
	//-------- Map methods --------
	
	/**
	 *  Get the size.
	 */
	public int size()
	{
		expungeStaleEntries();
		return contents.size();
	}

	/**
	 *  Check if empty.
	 */
	public boolean isEmpty()
	{
		expungeStaleEntries();
		return contents.isEmpty();
	}

	/**
	 *  Test if key contained.
	 */
	public boolean containsKey(Object key)
	{
		expungeStaleEntries();
		return contents.containsKey(key);
	}

	/**
	 *  Test if value contained.
	 */
	public boolean containsValue(Object value)
	{
		expungeStaleEntries();
		return contents.containsValue(value);
	}

	/**
	 *  Get value for key.
	 */
	public V get(Object key)
	{
		expungeStaleEntries();
		WeakObject<V>	ret	= (WeakObject<V>)contents.get(key);
		return ret!=null ? ret.get() : null;
	}

	/**
	 *  Add value for key.
	 */
	public V put(final K key, V value)
	{
		expungeStaleEntries();
		WeakEntry<V> ret;
		if(value!=null)
		{
			WeakEntry<V> we	= new WeakEntry<V>(value, key, queue);
			ret = contents.put(key, we);
		}
		else
		{
			ret = contents.put(key, null);
		}
		return ret!=null ? ret.get() : null;
	}

	/**
	 *  Remove value for key.
	 */
	public V remove(Object key)
	{
		expungeStaleEntries();
		WeakEntry<V> ret = contents.remove(key);
		return ret!=null ? ret.get() : null;
	}

	/**
	 *  Clear the map.
	 */
	public void clear()
	{
		expungeStaleEntries();
		contents.clear();
	}
	
	/**
	 *  Add all mappings.
	 */
	public void putAll(Map<? extends K, ? extends V> m)
	{
		for(Iterator<? extends K> it=m.keySet().iterator(); it.hasNext(); )
		{
			K	key	= it.next();
			put(key, m.get(key));
		}
	}

	/**
	 *  Get the key set.
	 */
	public Set<K> keySet()
	{
		return new AbstractSet<K>()
		{
			public Iterator<K> iterator()
			{
				expungeStaleEntries();
				return contents.keySet().iterator();
			}

			public int size()
			{
				return WeakValueMap.this.size();
			}			
		};
	}

	/**
	 *  Get the values.
	 */
	public Collection<V> values()
	{
		return new AbstractCollection<V>()
		{
			public Iterator<V> iterator()
			{
				expungeStaleEntries();
				return new Iterator<V>()
				{
					protected Iterator<WeakEntry<V>>	it	= contents.values().iterator();
					
					public boolean hasNext()
					{
						return it.hasNext();
					}

					public V next()
					{
						WeakObject<V>	ret	= (WeakObject<V>)it.next();
						return ret!=null ? ret.get() : null;
					}

					public void remove()
					{
						it.remove();
					}					
				};
			}

			public int size()
			{
				return WeakValueMap.this.size();
			}
		};
	}

	/**
	 *  Get the entries.
	 */
	public Set<Entry<K, V>> entrySet()
	{
		return new AbstractSet<Entry<K, V>>()
		{
			public Iterator<Entry<K, V>> iterator()
			{
				expungeStaleEntries();
				final Iterator<Entry<K, WeakEntry<V>>> oit = contents.entrySet().iterator();
				Iterator<Entry<K, V>> it = new Iterator<Entry<K, V>>()
				{
					public boolean hasNext()
					{
						return oit.hasNext();
					}
					public java.util.Map.Entry<K, V> next()
					{
						final Entry<K, WeakEntry<V>> ret = oit.next();
						final WeakEntry<V> we = ret.getValue();
						if(SReflect.isAndroid() && SUtil.androidUtils().getAndroidVersion() <= 8)
						{
							return new Entry<K, V>() {
								public K getKey() {
									return ret.getKey();
								}
								public V getValue() {
									return we != null ? we.get() : null;
								}
								public V setValue(V arg0) {
									return getValue();
								}
							};
						}
						else
						{
							return new AbstractMap.SimpleEntry<K, V>(ret.getKey(), we!=null ? we.get() : null);
						}
					}
					public void remove()
					{
						oit.remove();
					}
				};
				return it;
			}

			public int size()
			{
				return WeakValueMap.this.size();
			}			
		};
	}
	
	//-------- helper methods --------
	
	/**
	 *  Remove garbage collected entries.
	 */
	protected final void expungeStaleEntries()
	{
		WeakEntry<V> entry;
		while((entry=(WeakEntry<V>)queue.poll())!=null)
		{
//			System.out.println("removing: "+entry);
			contents.remove(entry.getArgument());
		}
	}
	
//	/**
//	 * 
//	 */
//	public static void main(String[] args)
//	{
//		Object o = new Object();
//		WeakValueMap<String, Object> map = new WeakValueMap<String, Object>();
//		map.put("hallo", o);
//		boolean b = true;
//		while(b)
//		{
//			try
//			{
//				Thread.currentThread().sleep(1000);
//				System.out.print(".");
//				map.get("hallo");
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//		System.out.println(o);
//	}
}
