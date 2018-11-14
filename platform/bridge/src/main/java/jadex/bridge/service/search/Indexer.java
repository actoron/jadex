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
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceKeyExtractor.SetWrapper;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;

/**
 *  Indexer for values.
 */
public class Indexer<T>
{
	/** Cutoff value for building index set intersections. */
	public static final int INTERSECT_CUTOFF = 32;
	
	/** Service key extractor. */
	protected IKeyExtractor<T> keyextractor;
	
	/** All values. */
	protected Set<T> values = new LinkedHashSet<T>();
	
	/** The index of published values. First string is the index key name. */
	protected Map<String, Map<String, Set<T>>> indexedvalues = new HashMap<String, Map<String, Set<T>>>();
	
	/** Include null values during indexing. */
	protected boolean includenull;
	
	/**
	 *  Create a new ServiceIndexer.
	 */
	public Indexer(IKeyExtractor<T> keyextractor, boolean includenull, String... indextypes)
	{
		this.keyextractor = keyextractor;
		this.includenull = includenull;
		for(String indextype: indextypes)
			indexedvalues.put(indextype, new HashMap<String, Set<T>>());
	}
	
	/**
	 *  Get values per specification. 'And' relates to inter-term, i.e. example
	 *  type=ICMS, tags=a,b means an object must have both fulfilled. For multi-valued
	 *  intra-term values it can be 'and' or 'or' as well.
	 *  @param spec The key values (first element is key name and array are values)
	 *  @return The values matching the spec.
	 */
	public Set<T> getValues(List<Tuple3<String, String[], Boolean>> spec)
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
			for(Iterator<Tuple3<String, String[], Boolean>> it = spec.iterator(); it.hasNext();)
			{
				Tuple3<String, String[], Boolean> tup = it.next();
				speccount += tup.getSecondEntity().length;
				
				// Fetch index service map per key
				Map<String, Set<T>> index = indexedvalues.get(tup.getFirstEntity());
				if(index != null)
				{
					// Remove this part from spec because index was found
					it.remove();
					
					if(valuesets == null)
						valuesets = new ArrayList<Set<T>>();
					
					if(tup.getThirdEntity()==null || tup.getThirdEntity())
					{
						// AND treatment. All sets are added to the set
						for(String key: tup.getSecondEntity())
						{
							Set<T> iset = index.get(key);
							
							if(iset == null || iset.isEmpty())
								return null;
							
							valuesets.add(iset);
						}
					}
					else // or
					{
						Set<T> tmp = new HashSet<T>();
						
						for(String key: tup.getSecondEntity())
						{
							Set<T> iset = index.get(key);
							
							if(iset != null)
								tmp.addAll(iset);
						}
						if(!tmp.isEmpty())
							valuesets.add(tmp);
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
	 *  Get values per specification (multivalues considered as OR match, when queries are the values).
	 *  @param spec The key values (first element is key name and array are values)
	 *  @return The values matching the spec.
	 */
	public Set<T> getValuesInverted(List<Tuple2<String, String[]>> spec)
	{
		Set<T> ret = null;
		if(spec == null || spec.size() == 0)
		{
			ret = new LinkedHashSet<T>(values);
		}
		else
		{
			List<Set<T>> valuesets = null;
			for(Iterator<Tuple2<String, String[]>> it = spec.iterator(); it.hasNext();)
			{
				Tuple2<String, String[]> tup = it.next();
				
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
					Set<T> iset = index.get("null");
					if(iset!=null)
						vals.addAll(iset);
					
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
			}
			
			for(Iterator<T> it = ret.iterator(); it.hasNext(); )
			{
				T val = it.next();
				if(!matchOr(spec, val))
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
				Set<String> keys = keyextractor.getKeyValues(keytype, val);
				if(keys != null && keys.contains(key))
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
				// Fetch all key values used 
				Set<String> keys = keyextractor.getKeyValues(entry.getKey(), value);
				
				if(includenull && keys==null)
				{
					keys = new HashSet<String>();
					keys.add("null");
				}	
				
				for(String key: SUtil.notNull(keys))
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
	
	/**
	 *  Remove a value from the indexer.
	 *  @param value The value.
	 */
	public void removeValue(T value)
	{
//		if(!values.remove(value))
//			System.out.println("Could not remove value from indexer: "+value+", "+value.toString());
		
		if(indexedvalues != null)
		{
			for(Map.Entry<String, Map<String, Set<T>>> entry : indexedvalues.entrySet())
			{
				Set<String> keys = keyextractor.getKeyValues(entry.getKey(), value);
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
		
		values.remove(value);
	}
	
	/**
	 *  Tests if the search specification matches a value (spec=query).
	 *  @param value The value.
	 *  @return True, if the value matches.
	 */
	public boolean match(List<Tuple3<String, String[], Boolean>> spec, T value)
	{
		for(Tuple3<String, String[], Boolean> tup: spec)
		{
			// Fetch the values of the cached element
			Set<String> keyvals = keyextractor.getKeyValues(tup.getFirstEntity(), value);
			
			if(keyvals == null)
				return false;
			
			// All vals of query must be contained in object
			if(tup.getThirdEntity())
			{
				for(String val: tup.getSecondEntity())
				{
					if(!keyvals.contains(val))
						return false;
				}
			}
			else // or
			{
				boolean found = false;
				for(String val: tup.getSecondEntity())
				{
					if(keyvals.contains(val))
					{
						found = true;
						break;
					}
				}
				if(!found)
					return false;
			}
		}
		
		return true;
	}
	
	/**
	 *  Tests if the search specification matches a value (spec=service, value=query).
	 *  
	 *  @param value The value.
	 *  @return True, if the value matches.
	 */
	public boolean matchOr(List<Tuple2<String, String[]>> spec, T value)
	{
		Map<String, Set<String>> totest = new HashMap<String, Set<String>>();
		for(Tuple2<String, String[]> tup: spec)
		{
			if(tup.getSecondEntity()!=null)
			{
				Set<String> vals = new HashSet<String>();
				for(String val: tup.getSecondEntity())
				{
					vals.add(val);
				}
				totest.put(tup.getFirstEntity(), vals);
			}
		}
		
		// Get keys from value (query) 
		for(String keyname: keyextractor.getKeyNames())
		{
			Set<String> vs = keyextractor.getKeyValues(keyname, value);
			if(vs!=null && vs.size()>0)
			{
				Boolean mode = keyextractor.getKeyMatchingMode(keyname, value);
				Set<String> svals = totest.get(keyname);
				
				// All tags of query must be contained in service
				if(mode==null || mode)
				{
					for(String val: vs)
					{
						if(svals==null || !svals.contains(val))
							return false;
					}
				}
				else if(mode!=null && !mode)
				{
					boolean found = false;
					for(String val: vs)
					{
						if(svals.contains(val))
						{
							found = true;
							break;
						}
						if(!found)
							return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 *  Gets the key extractor used by the service.
	 * 
	 *  @return The key extractor.
	 */
	public IKeyExtractor<T> getKeyExtractor()
	{
		return keyextractor;
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
	 *  Update an index for all values.
	 */
	public void updateIndex(String indexname)
	{
		Map<String, Set<T>> index = new HashMap<String, Set<T>>();
		for(T value: values)
		{
			Set<String> keys = keyextractor.getKeyValues(indexname, value);
		
			if(includenull && keys==null)
			{
				keys = new HashSet<String>();
				keys.add("null");
			}	
			
			if(keys!=null)
			{
				for(String key: keys)
				{
					Set<T> valset = index.get(key);
					if(valset == null)
					{
						valset = new HashSet<T>();
						index.put(key, valset);
					}
					valset.add(value);
				}
			}
		}
		indexedvalues.put(indexname, index);
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		Indexer<ServiceQuery<IService>> idx = new Indexer<ServiceQuery<IService>>(new IKeyExtractor<ServiceQuery<IService>>()
		{
			public Set<String> getKeyValues(String keytype, ServiceQuery<IService> query)
			{
				Set<String> ret = null;
				if(QueryInfoExtractor.KEY_TYPE_INTERFACE.equals(keytype))
				{
					if(query.getServiceType()!=null)
					{
						ret = new HashSet<String>();
						ret.add(query.getServiceType().toString());
					}
					
					// todo:
//					ClassInfo[] supertypes = service.getId().getServiceSuperTypes();
//					if (supertypes != null)
//					{
//						for (ClassInfo supertype : supertypes)
//							ret.add(supertype.toString());
//					}
				}
				else if(QueryInfoExtractor.KEY_TYPE_TAGS.equals(keytype))
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
				else if(QueryInfoExtractor.KEY_TYPE_PROVIDER.equals(keytype))
				{
					if(ServiceScope.COMPONENT_ONLY.equals(query.getScope()))
						ret = new SetWrapper<String>(query.getSearchStart() != null ? query.getSearchStart().toString() : query.getOwner().toString());
				}
				else if(QueryInfoExtractor.KEY_TYPE_PLATFORM.equals(keytype))
				{
					//if(query.getProvider()!=null)
						ret = new SetWrapper<String>(query.getPlatform().toString());
				}
				else if("owner".equals(keytype))
				{
					if(query.getOwner()!=null)
						ret = new SetWrapper<String>(query.getOwner().toString());
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
			public Boolean getKeyMatchingMode(String keytype, ServiceQuery<IService> query)
			{
				if(QueryInfoExtractor.KEY_TYPE_TAGS.equals(keytype))
					return Boolean.TRUE;
				return null;
			}
			
			/**
			 *  Get the key names for this type of extractor.
			 *  @return The key names.
			 */
			public String[] getKeyNames()
			{
				return ServiceKeyExtractor.SERVICE_KEY_TYPES;
			}
		
		}, true, ServiceKeyExtractor.SERVICE_KEY_TYPES); // todo: change to query types
		
		ServiceQuery<IService> q0 = new ServiceQuery<>((Class<IService>)null, null, null);
		idx.addValue(q0);

		ServiceQuery<IService> q1 = new ServiceQuery<>(new ClassInfo(ILibraryService.class), null, null);
		q1.setServiceTags(new String[]{"a", "b", "c"});
		idx.addValue(q1);
		ServiceQuery<IService> q2 = new ServiceQuery<>(new ClassInfo(ILibraryService.class), null, null);
		q2.setServiceTags(new String[]{"a", "b"});
		idx.addValue(q2);
		ServiceQuery<IService> q3 = new ServiceQuery<>(new ClassInfo(ILibraryService.class), null, null);

		q3.setServiceTags(new String[]{"a"});
		idx.addValue(q3);
		
		List<Tuple2<String, String[]>> spec = new ArrayList<Tuple2<String,String[]>>();
		Tuple2<String, String[]> s1 = new Tuple2<String, String[]>(ServiceKeyExtractor.KEY_TYPE_INTERFACE, new String[]{ILibraryService.class.getName()});
		spec.add(s1);
		Tuple2<String, String[]> s2 = new Tuple2<String, String[]>(ServiceKeyExtractor.KEY_TYPE_TAGS, new String[]{"a", "b"});
		spec.add(s2);
		
		Set<ServiceQuery<IService>> res = idx.getValuesInverted(spec);
		if(res!=null)
		{
			for(ServiceQuery<IService> r: res)
				System.out.println(r);
		}
	}
}
