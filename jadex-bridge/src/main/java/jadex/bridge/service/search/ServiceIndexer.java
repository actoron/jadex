package jadex.bridge.service.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import jadex.bridge.service.IService;
import jadex.commons.Tuple2;

public class ServiceIndexer<T>
{
	/** Cutoff value for building index set intersections. */
	public static final int INTERSECT_CUTOFF = 32;
	
	/** Service key extractor. */
	protected IKeyExtractor keyextractor;
	
	/** All services. */
	protected Set<T> services = new LinkedHashSet<T>();
	
	/** The index of published services. */
	protected Map<String, Map<String, Set<T>>> indexedservices = new HashMap<String, Map<String, Set<T>>>();
	
	public ServiceIndexer(IKeyExtractor keyextractor, String... indextypes)
	{
		this.keyextractor = keyextractor;
		for (String indextype : indextypes)
			indexedservices.put(indextype, new HashMap<String, Set<T>>());
	}
	
	/**
	 *  Get services per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return First matching service or null.
	 */
	public Set<T> getServices(List<Tuple2<String, String[]>> spec)
	{
		Set<T> ret = null;
		if (spec == null || spec.size() == 0)
			ret = new LinkedHashSet<T>(services);
		// Only pass copies.
//		else if (spec.size() == 1 && spec.get(0).getSecondEntity().length == 1)
//		{
//			Tuple2<String, String[]> tup = spec.get(0);
//			ret = getServices(tup.getFirstEntity(), tup.getSecondEntity()[0]);
//		}
		else
		{
			List<Set<T>> servicesets = null;
			int speccount = 0;
			for (Iterator<Tuple2<String, String[]>> it = spec.iterator(); it.hasNext();)
			{
				Tuple2<String, String[]> tup = it.next();
				speccount += tup.getSecondEntity().length;
				Map<String, Set<T>> index = indexedservices.get(tup.getFirstEntity());
				if (index != null)
				{
					it.remove();
					
					if (servicesets == null)
						servicesets = new ArrayList<Set<T>>();
					
					for (String key : tup.getSecondEntity())
					{
						Set<T> iset = index.get(key);
						
						if (iset == null || iset.isEmpty())
							return null;
						
						servicesets.add(iset);
					}
				}
			}
			
			if (servicesets != null)
			{
				servicesets.sort(new Comparator<Set<T>>()
				{
					public int compare(Set<T> o1, Set<T> o2)
					{
						return o1.size() - o2.size();
					}
				});
				
				int i = 0;
				for (i = 0; i < servicesets.size() && (ret == null || ret.size() < INTERSECT_CUTOFF); ++i)
				{
					if (ret == null)
						ret = new LinkedHashSet<T>(servicesets.get(i));
					else
					{
						Set<T> iset = servicesets.get(i);
						for (Iterator<T> it = ret.iterator(); it.hasNext(); )
						{
							T serv = it.next();
							if (!iset.contains(serv))
								it.remove();
						}
					}
				}
				
				if (ret != null && i == speccount)
					return ret;
			}
			
			if (ret == null)
				ret = new LinkedHashSet<T>(services);
			
			for (Iterator<T> it = ret.iterator(); it.hasNext(); )
			{
				T serv = it.next();
				if (!match(spec, serv))
					it.remove();
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get services per type.
	 *  @param type The interface type. If type is null all services are returned.
	 *  @return First matching service or null.
	 */
	public Set<T> getServices(String keytype, String key)
	{
		Set<T> ret = null;
		Map<String, Set<T>> index = indexedservices.get(keytype);
		if (index != null)
			 ret = index.get(key);
		else
		{
			for (T serv : services)
			{
				Set<String> keys = keyextractor.getKeys(keytype, serv);
				if (keys != null && keys.contains(key))
				{
					if (ret == null)
						ret = new LinkedHashSet<T>();
					ret.add(serv);
				}
			}
		}
		return ret;
	}
	
	/** 
	 *  Returns all services. 
	 *  @return All services.
	 */
	public Set<T> getAllServices()
	{
		return services;
	}
	
	public void addService(T service)
	{
		services.add(service);
		if (indexedservices != null)
		{
			for (Map.Entry<String, Map<String, Set<T>>> entry : indexedservices.entrySet())
			{
				Set<String> keys = keyextractor.getKeys(entry.getKey(), service);
				for (String key : keys)
				{
					Set<T> servset = entry.getValue().get(key);
					if (servset == null)
					{
						servset = new HashSet<T>();
						entry.getValue().put(key, servset);
					}
					servset.add(service);
				}
			}
		}
	}
	
	public void removeService(T service)
	{
		if (!services.remove(service))
		{
			// Print warning?
			System.out.println("Could not remove service from registry: "+service+", "+service.toString());
		}
		
		if (indexedservices != null)
		{
			for (Map.Entry<String, Map<String, Set<T>>> entry : indexedservices.entrySet())
			{
				Set<String> keys = keyextractor.getKeys(entry.getKey(), service);
				for (String key : keys)
				{
					Set<T> servset = entry.getValue().get(key);
					if (servset != null)
					{
						servset.remove(service);
						if (servset.isEmpty())
							entry.getValue().remove(key);
					}
				}
			}
		}
	}
	
	/**
	 *  Tests if the search specification matches a service.
	 *  
	 *  @param service The service.
	 *  @return True, if the service matches.
	 */
	public boolean match(List<Tuple2<String, String[]>> spec, T service)
	{
		for (Tuple2<String, String[]> tup : spec)
		{
			Set<String> servicekeys = keyextractor.getKeys(tup.getFirstEntity(), service);
			
			if (servicekeys == null)
				return false;
			
			for (String tag : tup.getSecondEntity())
			{
				if (!servicekeys.contains(tag))
				{
					return false;
				}
			}
		}
		
		return true;
	}
}
