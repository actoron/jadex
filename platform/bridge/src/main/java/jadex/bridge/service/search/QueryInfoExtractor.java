package jadex.bridge.service.search;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadex.bridge.service.search.ServiceKeyExtractor.SetWrapper;

/**
 *  Extractor for query infos.
 */
public class QueryInfoExtractor implements IKeyExtractor<ServiceQueryInfo<?>>
{
	/** Key type for the service interface. */
	public static final String KEY_TYPE_INTERFACE = "interface";
	
	/** Key type for the service tags. */
	public static final String KEY_TYPE_TAGS = "tags";
	
	/** Key type for the owner. */
	public static final String KEY_TYPE_OWNER = "owner";
	
	/** Key type for the service provider. */
	public static final String KEY_TYPE_PROVIDER = "provider";
	
	/** Key type for the service platform. */
	public static final String KEY_TYPE_PLATFORM = "platform";
	
	/** Key type for the service scope. */
	public static final String KEY_TYPE_SCOPE = "scope";

	/** Key type for the id. */
	public static final String KEY_TYPE_NETWORKS = "networks";

	
	
	/** Key type for the owner. */
	public static final String KEY_TYPE_OWNER_PLATORM = "owner";
	
	/** Key type for the superpeer boolean. */
	public static final String KEY_TYPE_ISREMOTE = "isremote";
	
	/** Key type for the id. */
	public static final String KEY_TYPE_ID = "id";
	

	
	/** The key types. */
	public static final String[] QUERY_KEY_TYPES;
	
	/** The indexable types. */
	public static final String[] QUERY_KEY_TYPES_INDEXABLE = {KEY_TYPE_INTERFACE, KEY_TYPE_TAGS, KEY_TYPE_OWNER, KEY_TYPE_PROVIDER, KEY_TYPE_PLATFORM, KEY_TYPE_OWNER_PLATORM, KEY_TYPE_ID, KEY_TYPE_NETWORKS, KEY_TYPE_ISREMOTE};
	
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
		QUERY_KEY_TYPES = keytypes.toArray(new String[keytypes.size()]);
	}
	
	/**
	 *  Get the keys per keytype.
	 *  @param keytype The key type.
	 *  @param value The value.
	 *  @return The key values.
	 */
	public Set<String> getKeyValues(String keytype, ServiceQueryInfo<?> sqi)
	{
		Set<String> ret = null;
		ServiceQuery<?> query = sqi.getQuery();
		if(ServiceKeyExtractor.KEY_TYPE_INTERFACE.equals(keytype))
		{
			if(query.getServiceType()!=null)
			{
				ret = new HashSet<String>();
				ret.add(query.getServiceType().toString());
			}
			
			// todo:
//					ClassInfo[] supertypes = service.getServiceIdentifier().getServiceSuperTypes();
//					if (supertypes != null)
//					{
//						for (ClassInfo supertype : supertypes)
//							ret.add(supertype.toString());
//					}
		}
		else if(ServiceKeyExtractor.KEY_TYPE_TAGS.equals(keytype))
		{
			String[] tags = query.getServiceTags();
			if(tags!=null)
			{
				ret = new HashSet<String>();
				for(String tag: tags)
				{
					ret.add(tag);
				}
			}
		}
		else if(KEY_TYPE_OWNER.equals(keytype))
		{
			if(query.getOwner()!=null)
				ret = new SetWrapper<String>(query.getOwner().toString());
		}
		else if(KEY_TYPE_PROVIDER.equals(keytype))
		{
			if(query.getProvider()!=null)
				ret = new SetWrapper<String>(query.getProvider().toString());
		}
		else if(KEY_TYPE_PLATFORM.equals(keytype))
		{
			if(query.getProvider()!=null)
//				ret = new SetWrapper<String>(query.getProvider().getRoot().toString());
				ret = new SetWrapper<String>(query.getPlatform().toString());
		}
		else if(KEY_TYPE_SCOPE.equals(keytype))
		{
			if(query.getProvider()!=null)
				ret = new SetWrapper<String>(query.getScope());
		}
		else if(KEY_TYPE_OWNER_PLATORM.equals(keytype))
		{
			if(query.getOwner()!=null)
				ret = new SetWrapper<String>(query.getOwner().getRoot().toString());
		}
		else if(KEY_TYPE_ISREMOTE.equals(keytype))
		{
			ret = new SetWrapper<String>(sqi.getQuery().isRemote()? "true": "false");
		}
		else if(KEY_TYPE_ID.equals(keytype))
		{
			ret = new SetWrapper<String>(sqi.getQuery().getId());
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
	public Boolean getKeyMatchingMode(String keytype, ServiceQueryInfo<?> query)
	{
		Boolean ret = query.getQuery().getMatchingMode(keytype);
		if(ret == null && KEY_TYPE_TAGS.equals(keytype))
			ret = Boolean.TRUE;
		return ret;
	}
	
	/**
	 *  Get the key names for this type of extractor.
	 *  @return The key names.
	 */
	public String[] getKeyNames()
	{
		return QUERY_KEY_TYPES;
	}
}
