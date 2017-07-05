package jadex.bridge.service.search;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.search.JadexServiceKeyExtractor.SetWrapper;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;

/**
 *  Indexer for values.
 */
public class Indexer<T>
{
	/** Cutoff value for building index set intersections. */
	public static final int INTERSECT_CUTOFF = 32;
	
	/** Service key extractor. */
	protected IKeyExtractor keyextractor;
	
	/** All values. */
	protected Set<T> values = new LinkedHashSet<T>();
	
	/** The index of published values. First string is the index key name. */
	protected Map<String, Map<String, Set<T>>> indexedvalues = new HashMap<String, Map<String, Set<T>>>();
	
	/**
	 *  Create a new ServiceIndexer.
	 */
	public Indexer(IKeyExtractor keyextractor, String... indextypes)
	{
		this.keyextractor = keyextractor;
		for (String indextype: indextypes)
			indexedvalues.put(indextype, new HashMap<String, Set<T>>());
	}
	
	/**
	 *  Get values per specification.
	 *  @param spec The key values (first element is key name and array are values)
	 *  @return The values matching the spec.
	 */
	public Set<T> getValues(List<Tuple2<String, String[]>> spec)
	{
		Set<T> ret = null;
		if(spec == null || spec.size() == 0)
		{
			ret = new LinkedHashSet<T>(values);
		}
		else
		{
			List<Set<T>> valuesets = null;
			int speccount = 0;
			for(Iterator<Tuple2<String, String[]>> it = spec.iterator(); it.hasNext();)
			{
				Tuple2<String, String[]> tup = it.next();
				speccount += tup.getSecondEntity().length;
				
				// Fetch index service map per key
				Map<String, Set<T>> index = indexedvalues.get(tup.getFirstEntity());
				if(index != null)
				{
					it.remove();
					
					if(valuesets == null)
						valuesets = new ArrayList<Set<T>>();
					
					for(String key: tup.getSecondEntity())
					{
						Set<T> iset = index.get(key);
						
						if(iset == null || iset.isEmpty())
							return null;
						
						valuesets.add(iset);
					}
				}
			}
			
			if(valuesets != null)
			{
				// Start with shortest collection
				if(valuesets.size()>1)
				{
					Collections.sort(valuesets, new Comparator<Set<T>>()
					{
						public int compare(Set<T> o1, Set<T> o2)
						{
							return o1.size() - o2.size();
						}
					});
				}
				
				int i = 0;
				for(i = 0; i < valuesets.size() && (ret == null || ret.size() < INTERSECT_CUTOFF); ++i)
				{
					if(ret == null)
					{
						ret = new LinkedHashSet<T>(valuesets.get(i));
					}
					else
					{
						Set<T> iset = valuesets.get(i);
						for(Iterator<T> it = ret.iterator(); it.hasNext(); )
						{
							T serv = it.next();
							if(!iset.contains(serv))
								it.remove();
						}
					}
				}
				
				// If all were used directly return intersection result
				if(ret != null && i == speccount)
					return ret;
			}
			
			if(ret == null)
				ret = new LinkedHashSet<T>(values);
			
			// Otherwise use single matching
			for(Iterator<T> it = ret.iterator(); it.hasNext(); )
			{
				T serv = it.next();
				if(!match(spec, serv))
					it.remove();
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get values per specification.
	 *  @param spec The key values (first element is key name and array are values)
	 *  @return The values matching the spec.
	 */
	public Set<T> getValuesOr(List<Tuple2<String, String[]>> spec)
	{
		Set<T> ret = null;
		if(spec == null || spec.size() == 0)
		{
			ret = new LinkedHashSet<T>(values);
		}
		else
		{
			List<Set<T>> valuesets = null;
			int speccount = 0;
			for(Iterator<Tuple2<String, String[]>> it = spec.iterator(); it.hasNext();)
			{
				Tuple2<String, String[]> tup = it.next();
				speccount += tup.getSecondEntity().length;
				
				// Fetch index service map per key
				Map<String, Set<T>> index = indexedvalues.get(tup.getFirstEntity());
				if(index != null)
				{					
					if(valuesets == null)
						valuesets = new ArrayList<Set<T>>();
					Set<T> vals = new HashSet<T>();
					
					for(String key: tup.getSecondEntity())
					{
						Set<T> iset = index.get(key);
						if(iset!=null)
							vals.addAll(iset);
					}
					
					if(tup.getSecondEntity().length==1)
						it.remove();

					if(vals.isEmpty())
						return null;
					
					valuesets.add(vals);
				}
			}
			
			if(valuesets != null)
			{
				// Start with shortest collection
				if(valuesets.size()>1)
				{
					Collections.sort(valuesets, new Comparator<Set<T>>()
					{
						public int compare(Set<T> o1, Set<T> o2)
						{
							return o1.size() - o2.size();
						}
					});
				}
				
				int i = 0;
				for(i = 0; i < valuesets.size() && (ret == null || ret.size() < INTERSECT_CUTOFF); ++i)
				{
					if(ret == null)
					{
						ret = new LinkedHashSet<T>(valuesets.get(i));
					}
					else
					{
						Set<T> iset = valuesets.get(i);
						for(Iterator<T> it = ret.iterator(); it.hasNext(); )
						{
							T serv = it.next();
							if(!iset.contains(serv))
								it.remove();
						}
					}
				}
				
				// If all were used directly return intersection result
				if(ret != null && i == speccount)
					return ret;
			}
			
			if(ret == null)
				ret = new LinkedHashSet<T>(values);
			
			// Otherwise use single matching
			for(Iterator<T> it = ret.iterator(); it.hasNext(); )
			{
				T val = it.next();
				if(!match(spec, val))
					it.remove();
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get values per type.
	 *  @param keytype the key type.
	 *  @param key The key value.
	 *  @return The values matching the key.
	 */
	public Set<T> getValues(String keytype, String key)
	{
		Set<T> ret = null;
		Map<String, Set<T>> index = indexedvalues.get(keytype);
		if(index != null)
		{
			 ret = index.get(key);
			 if (ret != null)
				 ret = new LinkedHashSet<T>(ret);
		}
		else
		{
			for(T val: values)
			{
				Set<String> keys = keyextractor.getKeys(keytype, val);
				if (keys != null && keys.contains(key))
				{
					if (ret == null)
						ret = new LinkedHashSet<T>();
					ret.add(val);
				}
			}
		}
		return ret;
	}
	
	/** 
	 *  Returns all values. 
	 *  @return All values.
	 */
	public Set<T> getAllValues()
	{
		return new LinkedHashSet<T>(values);
	}
	
	/**
	 *  Add a value to the indexer.
	 *  @param value The value.
	 */
	public void addValue(T value)
	{
		// Add value to set of all values
		values.add(value);
		
		// Add value to 
		if(indexedvalues != null)
		{
			for(Map.Entry<String, Map<String, Set<T>>> entry: indexedvalues.entrySet())
			{
				// Fetch all keys used 
				Set<String> keys = keyextractor.getKeys(entry.getKey(), value);
				
				if(keys != null)
				{
					for(String key: keys)
					{
						// Fetch the set of indexed elements for this key value and add the value
						Set<T> valset = entry.getValue().get(key);
						if(valset == null)
						{
							valset = new HashSet<T>();
							entry.getValue().put(key, valset);
						}
						valset.add(value);
					}
				}
			}
		}
	}
	
	/**
	 *  Remove a value from the indexer.
	 *  @param value The value.
	 */
	public void removeService(T value)
	{
		if(!values.remove(value))
			System.out.println("Could not remove value from indexer: "+value+", "+value.toString());
		
		if(indexedvalues != null)
		{
			for(Map.Entry<String, Map<String, Set<T>>> entry : indexedvalues.entrySet())
			{
				Set<String> keys = keyextractor.getKeys(entry.getKey(), value);
				if (keys != null)
				{
					for(String key : keys)
					{
						Set<T> servset = entry.getValue().get(key);
						if (servset != null)
						{
							servset.remove(value);
							if (servset.isEmpty())
								entry.getValue().remove(key);
						}
					}
				}
			}
		}
	}
	
	/**
	 *  Tests if the search specification matches a value.
	 *  
	 *  @param service The value.
	 *  @return True, if the value matches.
	 */
	public boolean match(List<Tuple2<String, String[]>> spec, T value)
	{
		for(Tuple2<String, String[]> tup: spec)
		{
			// Fetch the values of the cached element
			Set<String> keys = keyextractor.getKeys(tup.getFirstEntity(), value);
			
			if(keys == null)
				return false;
			
			for(String tag: tup.getSecondEntity())
			{
				if(!keys.contains(tag))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 *  Clears all contained values.
	 */
	public void clear()
	{
		for(Map<String, Set<T>> index : indexedvalues.values())
			index.clear();
		values.clear();
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		Indexer<ServiceQuery<?>> idx = new Indexer<ServiceQuery<?>>(new IKeyExtractor()
		{
			public Set<String> getKeys(String keytype, Object value)
			{
				ServiceQuery<?> query = (ServiceQuery<?>)value;
				Set<String> ret = null;
				if(JadexServiceKeyExtractor.KEY_TYPE_INTERFACE.equals(keytype))
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
				else if(JadexServiceKeyExtractor.KEY_TYPE_TAGS.equals(keytype))
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
				else if(JadexServiceKeyExtractor.KEY_TYPE_PROVIDER.equals(keytype))
				{
					if(query.getProvider()!=null)
						ret = new SetWrapper<String>(query.getProvider().toString());
				}
				else if(JadexServiceKeyExtractor.KEY_TYPE_PLATFORM.equals(keytype))
				{
					if(query.getProvider()!=null)
						ret = new SetWrapper<String>(query.getProvider().getRoot().toString());
				}
				else if("owner".equals(keytype))
				{
					if(query.getOwner()!=null)
						ret = new SetWrapper<String>(query.getOwner().toString());
				}
				return ret;
			}
		}, JadexServiceKeyExtractor.SERVICE_KEY_TYPES); // todo: change to query types
		
		ServiceQuery<IComponentManagementService> q1 = new ServiceQuery<IComponentManagementService>(IComponentManagementService.class, null, null, null, null);
		q1.setServiceTags(new String[]{"a", "b", "c"});
		idx.addValue(q1);
		ServiceQuery<IComponentManagementService> q2 = new ServiceQuery<IComponentManagementService>(IComponentManagementService.class, null, null, null, null);
		q2.setServiceTags(new String[]{"a", "b"});
		idx.addValue(q2);
		ServiceQuery<IComponentManagementService> q3 = new ServiceQuery<IComponentManagementService>(IComponentManagementService.class, null, null, null, null);
		q3.setServiceTags(new String[]{"a"});
		idx.addValue(q3);
		
		List<Tuple2<String, String[]>> spec = new ArrayList<Tuple2<String,String[]>>();
		Tuple2<String, String[]> s1 = new Tuple2<String, String[]>(JadexServiceKeyExtractor.KEY_TYPE_INTERFACE, new String[]{IComponentManagementService.class.getName()});
		spec.add(s1);
		Tuple2<String, String[]> s2 = new Tuple2<String, String[]>(JadexServiceKeyExtractor.KEY_TYPE_TAGS, new String[]{"a", "b"});
		spec.add(s2);
		System.out.println(idx.getValuesOr(spec));
	}
}
