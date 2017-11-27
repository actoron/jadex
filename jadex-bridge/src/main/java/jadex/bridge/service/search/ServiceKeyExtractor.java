package jadex.bridge.service.search;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.IService;

/**
 *  Responsible for extracting values.
 */
public class ServiceKeyExtractor implements IKeyExtractor<IService>
{
	/** Key type for the service interface. */
	public static final String KEY_TYPE_INTERFACE = "interface";
	
	/** Key type for the service tags. */
	public static final String KEY_TYPE_TAGS = "tags";
	
	/** Key type for the service provider. */
	public static final String KEY_TYPE_PROVIDER = "provider";
	
	/** Key type for the service platform. */
	public static final String KEY_TYPE_PLATFORM = "platform";
	
	/** Key type for the service id. */
	public static final String KEY_TYPE_SID = "serviceid";
	
	/** Key type for the networks. */
	public static final String KEY_TYPE_NETWORKS = "networks";
	
	/** Key type for the unrestricted mode. */
	public static final String KEY_TYPE_UNRESTRICTED = "unrestricted";

	
	/** The key types. */
	public static final String[] SERVICE_KEY_TYPES;
	
	static
	{
		List<String> keytypes = new ArrayList<String>();
		try
		{
			Field[] fields = ServiceKeyExtractor.class.getDeclaredFields();
			for (Field field : fields)
			{
				if (field.getName().startsWith("KEY_TYPE_"))
				{
					keytypes.add((String) field.get(null));
				}
			}
		}
		catch (Exception e)
		{
		}
		SERVICE_KEY_TYPES = keytypes.toArray(new String[keytypes.size()]);
	}
	
	/**
	 *  Extracts keys from a service.
	 *  
	 *  @param keytype The type of key being extracted.
	 *  @param service The service.
	 *  @return The keys matching the type.
	 */
	public Set<String> getKeyValues(String keytype, IService serv)
	{
		return getKeysStatic(keytype, serv);
	}
	
	/**
	 *  Get the key names for this type of extractor.
	 *  @return The key names.
	 */
	public String[] getKeyNames()
	{
		return SERVICE_KEY_TYPES;
	}
	
	
	/**
	 *  Extracts keys from a service.
	 *  
	 *  @param keytype The type of key being extracted.
	 *  @param service The service.
	 *  @return The keys matching the type.
	 */
	@SuppressWarnings("unchecked")
	public static final Set<String> getKeysStatic(String keytype, IService serv)
	{
//		if(serv instanceof IService)
//		{
//			if(((IService)serv).getServiceIdentifier().getServiceType().getTypeName().indexOf("ITest")!=-1)
//				System.out.println("sdhgfsdh");
//		}
		Set<String> ret = null;
		
		IService service = (IService)serv;
		
		if(KEY_TYPE_INTERFACE.equals(keytype))
		{
			ret = new HashSet<String>();
			ret.add(service.getServiceIdentifier().getServiceType().toString());
			ClassInfo[] supertypes = service.getServiceIdentifier().getServiceSuperTypes();
			if (supertypes != null)
			{
				for (ClassInfo supertype : supertypes)
					ret.add(supertype.toString());
			}
		}
		else if(KEY_TYPE_TAGS.equals(keytype))
		{
//			Map<String, Object> sprops = service.getPropertyMap();
//			if(sprops != null)
//				ret = (Set<String>)sprops.get(TagProperty.SERVICE_PROPERTY_NAME);
			ret = service.getServiceIdentifier().getTags();
		}
		else if(KEY_TYPE_PROVIDER.equals(keytype))
		{
			ret = new SetWrapper<String>(service.getServiceIdentifier().getProviderId().toString());
		}
		else if(KEY_TYPE_PLATFORM.equals(keytype))
		{
			ret = new SetWrapper<String>(service.getServiceIdentifier().getProviderId().getRoot().toString());
		}
		else if(KEY_TYPE_SID.equals(keytype))
		{
			ret = new SetWrapper<String>(service.getServiceIdentifier().toString());
		}
		else if(KEY_TYPE_NETWORKS.equals(keytype))
		{
			ret = new HashSet<String>(service.getServiceIdentifier().getNetworkNames());
		}
		else if(KEY_TYPE_UNRESTRICTED.equals(keytype))
		{
			ret = new SetWrapper<String>(""+service.getServiceIdentifier().isUnrestricted());
		}
		return ret;
	}
	
	/**
	 *  Extracts the matching mode from a multivalued term.
	 *  true = AND, false = OR
	 *  
	 *  @param keytype The type of key being extracted.
	 *  @param value The value.
	 *  @return The key matching mode.
	 */
	public Boolean getKeyMatchingMode(String keytype, IService value)
	{
		if(KEY_TYPE_TAGS.equals(keytype))
			return Boolean.TRUE;
		return null;
	}
	
	/**
	 *  Efficiently wrap a single value as a Set.
	 */
	public static class SetWrapper<T> implements Set<T>
	{
		private T wrappedobject;
		
		@SuppressWarnings("unused")
		public SetWrapper()
		{
		}
		
		public SetWrapper(T wrappedobject)
		{
			this.wrappedobject = wrappedobject;
		}
		
		public int size()
		{
			return wrappedobject != null ? 1 : 0;
		}

		public boolean isEmpty()
		{
			return wrappedobject == null;
		}

		public boolean contains(Object o)
		{
			return wrappedobject != null ? wrappedobject.equals(o) : false;
		}
		
		public Iterator<T> iterator()
		{
			return new Iterator<T>()
			{
				boolean next = true;
				
				public boolean hasNext()
				{
					return next;
				}

				public T next()
				{
					if (next)
					{
						next = false;
						return wrappedobject;
					}
					else
						throw new NoSuchElementException();
				}
				
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}

		public Object[] toArray()
		{
			return new Object[] { wrappedobject };
		}

		@SuppressWarnings("unchecked")
		public Object[] toArray(Object[] a)
		{
			if (wrappedobject != null)
			{
				if (a != null && a.length > 1)
				{
					a[0] = wrappedobject;
					return a;
				}
			}
			return new Object[] { wrappedobject };
		}

		public boolean add(T e)
		{
			if (wrappedobject != null)
			{
				if (wrappedobject.equals(e))
					return false;
				else
					throw new IllegalArgumentException();
			}
			wrappedobject = e;
			return true;
		}

		public boolean remove(Object o)
		{
			if (wrappedobject != null && wrappedobject.equals(o))
			{
				wrappedobject = null;
				return true;
			}
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c)
		{
			if (wrappedobject != null && c.size() == 1 && wrappedobject.equals(c.iterator().next()))
				return true;
			return false;
		}

		public boolean addAll(Collection<? extends T> c)
		{
			throw new UnsupportedOperationException();
		}

		public boolean retainAll(Collection<?> c)
		{
			throw new UnsupportedOperationException();
		}
		
		public boolean removeAll(Collection<?> c)
		{
			throw new UnsupportedOperationException();
		}
		
		public void clear()
		{
			wrappedobject = null;
		}
	}
}
