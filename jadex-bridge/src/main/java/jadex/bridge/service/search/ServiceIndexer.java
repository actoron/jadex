package jadex.bridge.service.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.Tuple2;

/**
 *  Indexer for services.
 */
public class ServiceIndexer<T> extends Indexer<T>
{	
	/**
	 *  Create a new Indexer.
	 */
	public ServiceIndexer(IKeyExtractor keyextractor, String... indextypes)
	{
		super(keyextractor, indextypes);
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
	 *  Tests if the search specification matches a value (spec=query).
	 *  @param value The value.
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
			
			// All tags of query must be contained in service
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
}
