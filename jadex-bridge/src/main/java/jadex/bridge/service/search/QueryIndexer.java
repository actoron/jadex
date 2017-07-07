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

import jadex.bridge.service.search.ServiceKeyExtractor.SetWrapper;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;

/**
 *  Indexer for queries. Matching is here different:
 *  - multivalue results are ORed to form one big intersection set 
 *    (at least one must match to later on test subsets)
 *  - indexing only delivers potentially matching queries
 *    - multivaluesets have to retested
 *    - the query could have additional constraints
 *  Thus all queries found in index matching need to rechecked.
 */
public class QueryIndexer<T> extends Indexer<T>
{	
	/**
	 *  Create a new Indexer.
	 */
	public QueryIndexer(IKeyExtractor keyextractor, String... indextypes)
	{
		super(keyextractor, indextypes);
	}
	
	/**
	 *  Get values per specification (multivalues considered as OR match, when queries are the values).
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
	 *  Tests if the search specification matches a value.
	 *  @param value The value.
	 *  @return True, if the value matches.
	 */
	public boolean match(List<Tuple2<String, String[]>> spec, T value)
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
		
		// Get keys from value (query) here
		for(String keyname: keyextractor.getKeyNames())
		{
			Set<String> vs = keyextractor.getKeys(keyname, value);
			if(vs!=null && vs.size()>0)
			{
				Set<String> keys = totest.get(keyname);
				
				// All tags of query must be contained in service
				for(String tag: vs)
				{
					if(keys==null || !keys.contains(tag))
					{
						return false;
					}
				}
			}
		}
		
		return true;
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
				if(ServiceKeyExtractor.KEY_TYPE_INTERFACE.equals(keytype))
				{
					if(query.getServiceType()!=null)
					{
						ret = new HashSet<String>();
						ret.add(query.getServiceType().toString());
					}
					
					// todo:
//						ClassInfo[] supertypes = service.getServiceIdentifier().getServiceSuperTypes();
//						if (supertypes != null)
//						{
//							for (ClassInfo supertype : supertypes)
//								ret.add(supertype.toString());
//						}
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
				else if(ServiceKeyExtractor.KEY_TYPE_PROVIDER.equals(keytype))
				{
					if(query.getProvider()!=null)
						ret = new SetWrapper<String>(query.getProvider().toString());
				}
				else if(ServiceKeyExtractor.KEY_TYPE_PLATFORM.equals(keytype))
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
		
			
			/**
			 *  Get the key names for this type of extractor.
			 *  @return The key names.
			 */
			public String[] getKeyNames()
			{
				return ServiceKeyExtractor.SERVICE_KEY_TYPES;
			}
		
		}, ServiceKeyExtractor.SERVICE_KEY_TYPES); // todo: change to query types
		
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
		Tuple2<String, String[]> s1 = new Tuple2<String, String[]>(ServiceKeyExtractor.KEY_TYPE_INTERFACE, new String[]{IComponentManagementService.class.getName()});
		spec.add(s1);
		Tuple2<String, String[]> s2 = new Tuple2<String, String[]>(ServiceKeyExtractor.KEY_TYPE_TAGS, new String[]{"a", "b"});
		spec.add(s2);
		System.out.println(idx.getValuesOr(spec));
	}
}
