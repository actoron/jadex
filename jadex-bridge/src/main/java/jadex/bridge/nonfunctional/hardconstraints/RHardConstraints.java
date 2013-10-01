package jadex.bridge.nonfunctional.hardconstraints;

import jadex.bridge.service.IService;
import jadex.commons.ComposedFilter;
import jadex.commons.IFilter;
import jadex.commons.MethodInfo;
import jadex.commons.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Class defining runtime hard constraints.
 *
 */
public class RHardConstraints
{
	/** The basic hard constraints filter */
	protected List<IFilter<IService>> filters = new ArrayList<IFilter<IService>>();
	
	/** Unbound constant value filters */
	protected List<ConstantValueFilter> unboundconstantfilters = new ArrayList<ConstantValueFilter>();
	
	public void addFilter(IFilter<IService> filter)
	{
		if (filter instanceof ConstantValueFilter &&
			((ConstantValueFilter) filter).getValue() == null)
		{
			unboundconstantfilters.add((ConstantValueFilter) filter);
		}
		else
		{
			filters.add(filter);
		}
	}
	
	public IFilter<IService> getRemotableFilter()
	{
		IFilter<?> ret = null;
		
		if (filters.isEmpty())
		{
			ret = IFilter.ALWAYS;
		} 
		else
		{
			ret = new ComposedFilter<Tuple2<IService,Map<String,Object>>>(filters.toArray(new IFilter[filters.size()]));
		}
		
		return (IFilter<IService>) ret;
	}
	
	/**
	 *  Gets the filter for local filtering.
	 *  
	 *  @return Filter for local filtering.
	 */
	public IFilter<IService> getLocalFilter()
	{
		return getLocalFilter(null);
	}
	
	/**
	 *  Gets the filter for local filtering.
	 *  
	 *  @return Filter for local filtering.
	 */
	public IFilter<IService> getLocalFilter(final MethodInfo method)
	{
		IFilter<?> ret = null;
		
		if (unboundconstantfilters.isEmpty())
		{
			ret = IFilter.ALWAYS;
		}
		else
		{
			ret = new IFilter<IService>()
			{
				public boolean filter(IService service)
				{
					boolean ret = true;
					int i;
					List<ConstantValueFilter> boundconstantfilters = new ArrayList<ConstantValueFilter>();
					for (i = 0; i < unboundconstantfilters.size() && ret; ++i)
					{
						ConstantValueFilter filter = unboundconstantfilters.get(i);
						Object val = service.getMethodNFPropertyValue(method, filter.getValueName());
						filter.bind(val);
						ret &= filter.filter(service);
					}
//					ArrayList.removeAll
//					unboundconstantfilters.removeAll(c)
					int max = i + 1;
					for (i = 0; i < max; ++i)
					{
						ConstantValueFilter filter = unboundconstantfilters.get(i);
						if (ret && filter.getValue() != null)
						{
							
						}
					}
					
					return ret;
				}
			};
		}
		
		return (IFilter<IService>) ret;
	}
}
