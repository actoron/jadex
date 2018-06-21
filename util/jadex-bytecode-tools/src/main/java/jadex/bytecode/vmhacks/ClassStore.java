package jadex.bytecode.vmhacks;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 *  Class used to store injected classes globally.
 *
 */
public class ClassStore implements Map<Object[], Class<?>>
{
	/** The internal map. */
	private Map<ClassLoader, Map<String, WeakReference<Class<?>>>> injections = new WeakHashMap<ClassLoader, Map<String, WeakReference<Class<?>>>>();
	
	/** Name of the calling class that is allowed to write to the store. */
	private String allowedcaller = "jadex.bytecode.vmhacks.VmHacks";
	
	/**
	 *  Creates the store.
	 */
	public ClassStore()
	{
	}
	
	/** Override */
	public Class<?> get(Object key)
	{
		Class<?> ret = null;
		if (key instanceof Object[])
		{
			ClassLoader cl = (ClassLoader) ((Object[]) key)[0];
			String name = (String) ((Object[]) key)[1];
			
			synchronized(injections)
			{
				Map<String, WeakReference<Class<?>>> inner = injections.get(cl);
				if (inner != null)
				{
					WeakReference<Class<?>> wr = inner.get(name);
					if (wr != null)
						ret = wr.get();
				}
			}
		}
		
		return ret;
	}

	/** Override */
	public Class<?> put(Object[] key, Class<?> value)
	{
		StackTraceElement[] st = new RuntimeException().getStackTrace();
		String callername = st[0].getClassName();
		for (int i = 1; i < st.length && ClassStore.class.getName().equals(callername); ++i)
			callername = st[i].getClassName();
		
		if (!allowedcaller.equals(callername))
			throw new SecurityException("Caller not allowed: " + callername);
		
		ClassLoader cl = (ClassLoader) key[0];
		String name = (String) key[1];
		
		synchronized(injections)
		{
			Map<String, WeakReference<Class<?>>> inner = injections.get(cl);
			if (inner == null)
			{
				inner = new HashMap<String, WeakReference<Class<?>>>();
				injections.put(cl, inner);
			}
			
			inner.put(name, new WeakReference<Class<?>>(value));
		}
		
		return value;
	}
	
	/** Override */
	public int size()
	{
		return 0;
	}

	/** Override */
	public boolean isEmpty()
	{
		return false;
	}

	/** Override */
	public boolean containsKey(Object key)
	{
		return false;
	}

	/** Override */
	public boolean containsValue(Object value)
	{
		return false;
	}

	

	/** Override */
	public Class<?> remove(Object key)
	{
		return null;
	}

	/** Override */
	public void putAll(Map<? extends Object[], ? extends Class<?>> m)
	{
	}

	/** Override */
	public void clear()
	{
	}

	/** Override */
	public Set<Object[]> keySet()
	{
		return null;
	}

	/** Override */
	public Collection<Class<?>> values()
	{
		return null;
	}

	/** Override */
	public Set<Entry<Object[], Class<?>>> entrySet()
	{
		return null;
	}
}
