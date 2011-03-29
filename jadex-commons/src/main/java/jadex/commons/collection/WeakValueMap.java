package jadex.commons.collection;

import java.lang.ref.ReferenceQueue;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *  A map with weak values.
 */
public class WeakValueMap	implements Map
{
	//-------- attributes --------
	
	/** The contents. */
	protected Map	contents;
	
	/** The reference queue. */
	protected ReferenceQueue	queue;
	
	//-------- constructors --------
	
	/**
	 *  Create a new weak value map.
	 */
	public WeakValueMap()
	{
		this.contents	= new LinkedHashMap();
		this.queue	= new ReferenceQueue();
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
	public Object get(Object key)
	{
		expungeStaleEntries();
		WeakObject	ret	= (WeakObject)contents.get(key);
		return ret!=null ? ret.get() : null;
	}

	/**
	 *  Add value for key.
	 */
	public Object put(final Object key, Object value)
	{
		expungeStaleEntries();
		if(value!=null)
		{
			value	= new WeakEntry(value, key, queue);
		}
		return contents.put(key, value);
	}

	/**
	 *  Remove value for key.
	 */
	public Object remove(Object key)
	{
		expungeStaleEntries();
		return contents.remove(key);
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
	public void putAll(Map m)
	{
		for(Iterator it=m.keySet().iterator(); it.hasNext(); )
		{
			Object	key	= it.next();
			put(key, m.get(key));
		}
	}

	/**
	 *  Get the key set.
	 */
	public Set keySet()
	{
		return new AbstractSet()
		{
			public Iterator iterator()
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
	public Collection values()
	{
		return new AbstractCollection()
		{
			public Iterator iterator()
			{
				expungeStaleEntries();
				return new Iterator()
				{
					protected Iterator	it	= contents.values().iterator();
					
					public boolean hasNext()
					{
						return it.hasNext();
					}

					public Object next()
					{
						WeakObject	ret	= (WeakObject)it.next();
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
	public Set entrySet()
	{
		return new AbstractSet()
		{
			public Iterator iterator()
			{
				expungeStaleEntries();
				return contents.entrySet().iterator();
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
		WeakEntry entry;
		while((entry=(WeakEntry)queue.poll())!=null)
		{
			contents.remove(entry.getArgument());
		}
	}
}
