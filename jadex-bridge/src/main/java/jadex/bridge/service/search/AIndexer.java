package jadex.bridge.service.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.service.search.ServiceKeyExtractor.SetWrapper;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;

/**
 *  Base class for indexer. 
 */
public abstract class AIndexer<T>
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
	public AIndexer(IKeyExtractor keyextractor, String... indextypes)
	{
		this.keyextractor = keyextractor;
		for(String indextype: indextypes)
			indexedvalues.put(indextype, new HashMap<String, Set<T>>());
	}
	
	/**
	 *  Get values per specification.
	 *  @param spec The key values (first element is key name and array are values)
	 *  @return The values matching the spec.
	 */
	public abstract Set<T> getValues(List<Tuple2<String, String[]>> spec);
	
	/**
	 *  Tests if the search specification matches a value (spec=query).
	 *  @param value The value.
	 *  @return True, if the value matches.
	 */
	public abstract boolean match(List<Tuple2<String, String[]>> spec, T value);
	
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
				// Fetch all keys used 
				Set<String> keys = keyextractor.getKeyValues(entry.getKey(), value);
				
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
	public void removeValue(T value)
	{
		if(!values.remove(value))
			System.out.println("Could not remove value from indexer: "+value+", "+value.toString());
		
		if(indexedvalues != null)
		{
			for(Map.Entry<String, Map<String, Set<T>>> entry : indexedvalues.entrySet())
			{
				Set<String> keys = keyextractor.getKeyValues(entry.getKey(), value);
				if (keys != null)
				{
					for(String key : keys)
					{
						Set<T> vals = entry.getValue().get(key);
						if (vals != null)
						{
							vals.remove(value);
							if(vals.isEmpty())
								entry.getValue().remove(key);
						}
					}
				}
			}
		}
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
	
}
