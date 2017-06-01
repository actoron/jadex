package jadex.bridge.service.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;

/**
 *  Container for persistent service queries.
 */
public class QueryInfoContainer
{
	/** Queries by owner */
	protected Map<IComponentIdentifier, Set<ServiceQueryInfo<?>>> queriesbyowner;
	
	/** Queries by service type. */
	protected Map<ClassInfo, Set<ServiceQueryInfo<?>>> queriesbyservicetype;
	
	/** Event Queries by service type. */
	protected Map<ClassInfo, Set<ServiceQueryInfo<?>>> eventqueriesbyservicetype;
	
	/**
	 *  Creates the container.
	 */
	public QueryInfoContainer()
	{
		queriesbyowner = new HashMap<IComponentIdentifier, Set<ServiceQueryInfo<?>>>();
		queriesbyservicetype = new HashMap<ClassInfo, Set<ServiceQueryInfo<?>>>();
		eventqueriesbyservicetype = new HashMap<ClassInfo, Set<ServiceQueryInfo<?>>>();
	}
	
	/**
	 *  Adds a query info.
	 *  @param queryinfo The query info.
	 */
	public void addQueryInfo(ServiceQueryInfo<?> queryinfo)
	{
		createQueryInfoSet(queryinfo.getQuery().getOwner(), queriesbyowner).add(queryinfo);
		createQueryInfoSet(queryinfo.getQuery().getServiceType(), queriesbyservicetype).add(queryinfo);
		if(ServiceEvent.CLASSINFO.equals(queryinfo.getQuery().getReturnType()))
			createQueryInfoSet(queryinfo.getQuery().getServiceType(), eventqueriesbyservicetype).add(queryinfo);
	}
	
	/**
	 *  Removes a query info.
	 *  @param queryinfo The query info.
	 */
	public void removeQueryInfo(ServiceQueryInfo<?> queryinfo)
	{
		Set<ServiceQueryInfo<?>> set = queriesbyowner.get(queryinfo.getQuery().getOwner());
		if(set != null)
		{
			set.remove(queryinfo);
			if (set.size() == 0)
				queriesbyowner.remove(queryinfo.getQuery().getOwner());
		}
		
		set = queriesbyservicetype.get(queryinfo.getQuery().getServiceType());
		if(set != null)
		{
			set.remove(queryinfo);
			if (set.size() == 0)
				queriesbyservicetype.remove(queryinfo.getQuery().getServiceType());
		}
		
		set = eventqueriesbyservicetype.get(queryinfo.getQuery().getServiceType());
		if(set != null)
		{
			set.remove(queryinfo);
			if (set.size() == 0)
				eventqueriesbyservicetype.remove(queryinfo.getQuery().getServiceType());
		}
	}
	
	/**
	 *  Removes a query.
	 *  @param query The query.
	 */
	public ServiceQueryInfo<?> removeQuery(ServiceQuery<?> query)
	{
		Set<ServiceQueryInfo<?>> queries = queriesbyowner.get(query.getOwner());
		if (queries != null)
		{
			for (ServiceQueryInfo<?> qinfo : queries)
			{
				if (query.equals(qinfo.getQuery()))
				{
					removeQueryInfo(qinfo);
					return qinfo;
				}
			}
		}
		return null;
	}
	
	/**
	 *  Removes all queries of an owner.
	 *  @param owner Owner of the queries.
	 *  @return Removed query infos.
	 */
	public Set<ServiceQueryInfo<?>> removeQueries(IComponentIdentifier owner)
	{
		Set<ServiceQueryInfo<?>> queries = queriesbyowner.remove(owner);
		if (queries != null)
		{
			for (ServiceQueryInfo<?> qinfo : queries)
				removeQueryInfo(qinfo);
		}
		return queries;
	}
	
	/**
	 *  Returns all queries.
	 *  @return All queries.
	 */
	public Set<ServiceQueryInfo<?>> getAllQueries()
	{
		Set<ServiceQueryInfo<?>> ret = new HashSet<ServiceQueryInfo<?>>();
		Collection<Set<ServiceQueryInfo<?>>> coll = null;
		if (queriesbyservicetype.size() < queriesbyowner.size())
			coll = queriesbyservicetype.values();
		else
			coll = queriesbyowner.values();
		for (Set<ServiceQueryInfo<?>> subset : coll)
			ret.addAll(subset);
		return ret;
	}
	
	/**
	 *  Returns all queries matching a specific service type.
	 *  @param servicetype The service type.
	 *  @return All queries matching a specific service type.
	 */
	public Set<ServiceQueryInfo<?>> getQueries(ClassInfo servicetype)
	{
		Set<ServiceQueryInfo<?>> ret = new LinkedHashSet<ServiceQueryInfo<?>>();
		Set<ServiceQueryInfo<?>> set = queriesbyservicetype.get(null);
		if (set != null)
			ret.addAll(set);
		set = queriesbyservicetype.get(servicetype);
		if (set != null)
			ret.addAll(set);
		return ret;
	}
	
	/**
	 *  Returns all event queries matching a specific service type.
	 *  @param servicetype The service type.
	 *  @return All event queries matching a specific service type.
	 */
	public Set<ServiceQueryInfo<?>> getEventQueries(ClassInfo servicetype)
	{
		Set<ServiceQueryInfo<?>> ret = new LinkedHashSet<ServiceQueryInfo<?>>();
		Set<ServiceQueryInfo<?>> set = eventqueriesbyservicetype.get(null);
		if (set != null)
			ret.addAll(set);
		set = eventqueriesbyservicetype.get(servicetype);
		if (set != null)
			ret.addAll(set);
		return ret;
	}
	
	/**
	 *  Creates an query info set for a key if needed.
	 *  @param key The key.
	 *  @param map The map to use.
	 *  @return Created or found set.
	 */
	protected <T> Set<ServiceQueryInfo<?>> createQueryInfoSet(T key, Map<T, Set<ServiceQueryInfo<?>>> map)
	{
		Set<ServiceQueryInfo<?>> ret = map.get(key);
		if (ret == null)
		{
			ret = new LinkedHashSet<ServiceQueryInfo<?>>();
			map.put(key, ret);
		}
		return ret;
	}
}
