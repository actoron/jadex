package jadex.bridge.nonfunctional.hardconstraints;

import jadex.bridge.service.IService;
import jadex.commons.ComposedRemoteFilter;
import jadex.commons.IRemoteFilter;
import jadex.commons.MethodInfo;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Class defining runtime hard constraints.
 *
 */
public class RHardConstraints
{
	protected static final Map<String, Class<?>> CONSTRAINT_OPERATOR_MAP = new HashMap<String, Class<?>>();
	static
	{
		CONSTRAINT_OPERATOR_MAP.put(MHardConstraint.CONSTANT, ConstantValueFilter.class);
	}
	
	/** The basic hard constraints filter */
	protected List<IRemoteFilter<IService>> filters = new ArrayList<IRemoteFilter<IService>>();
	
	/** Unbound constant value filters */
	protected List<ConstantValueFilter> unboundconstantfilters = new ArrayList<ConstantValueFilter>();
	
	/**
	 *  Creates the runtime hard constraints.
	 * 
	 *  @param mhc The declared model hard constraints.
	 */
	public RHardConstraints(Collection<MHardConstraint> mhc)
	{
		for (MHardConstraint hc : mhc)
		{
			Class<?> opclazz = CONSTRAINT_OPERATOR_MAP.get(hc.getOperator());
			try
			{
				Constructor<IRemoteFilter<IService>> c = (Constructor<IRemoteFilter<IService>>) opclazz.getConstructor(String.class, Object.class);
				IRemoteFilter<IService> filter = c.newInstance(hc.getPropertyName(), hc.getValue());
				addFilter(filter);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 *  Adds a filter.
	 *  
	 *  @param filter The filter.
	 */
	protected void addFilter(IRemoteFilter<IService> filter)
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
	
	/**
	 *  Gets the filter that is remotable.
	 * 
	 *  @return Remotable filter.
	 */
	public IRemoteFilter<IService> getRemotableFilter()
	{
		IRemoteFilter<?> ret = null;
		
		if (filters.isEmpty())
		{
			ret = IRemoteFilter.ALWAYS;
		} 
		else
		{
			ret = new ComposedRemoteFilter(filters.toArray(new IRemoteFilter[filters.size()]));
		}
		
		return (IRemoteFilter<IService>) ret;
	}
	
	/**
	 *  Gets the filter for local filtering.
	 *  
	 *  @return Filter for local filtering.
	 */
	public IRemoteFilter<IService> getLocalFilter()
	{
		return getLocalFilter(null);
	}
	
	/**
	 *  Gets the filter for local filtering.
	 *  
	 *  @return Filter for local filtering.
	 */
	public IRemoteFilter<IService> getLocalFilter(final MethodInfo method)
	{
		IRemoteFilter<IService> ret = null;
		
		if (unboundconstantfilters.isEmpty())
		{
			ret = IRemoteFilter.ALWAYS;
		}
		else
		{
			ret = new IRemoteFilter<IService>()
			{
				public IFuture<Boolean> filter(final IService service)
				{
					final Future<Boolean> filterret = new Future<Boolean>();
					
					final List<ConstantValueFilter> boundconstantfilters = new ArrayList<ConstantValueFilter>();
					
					final CollectionResultListener<Boolean> constantrl = new CollectionResultListener<Boolean>(unboundconstantfilters.size(), false, new IResultListener<Collection<Boolean>>()
					{
						public void resultAvailable(Collection<Boolean> result)
						{
							Boolean[] results = result.toArray(new Boolean[result.size()]);
							boolean filterresult = true;
							for (int i = 0; i < results.length && filterresult; ++i)
							{
								filterresult &= results[i];
							}
							
							if (!filterresult)
							{
								for (ConstantValueFilter bfil : boundconstantfilters)
								{
									bfil.unbind();
								}
							}
							
							filterret.setResult(filterresult);
						};
						
						public void exceptionOccurred(Exception exception)
						{
							resultAvailable(null);
						}
					});
					
					for (int i = 0; i < unboundconstantfilters.size(); ++i)
					{
						final ConstantValueFilter filter = unboundconstantfilters.get(i);
						service.getMethodNFPropertyValue(method, filter.getValueName()).addResultListener(new IResultListener<Object>()
						{
							public void resultAvailable(Object result)
							{
								if (filter.getValue() == null)
								{
									filter.bind(result);
									boundconstantfilters.add(filter);
								}
								filter.filter(service).addResultListener(constantrl);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								constantrl.exceptionOccurred(exception);
							}
						});
					}
					
					return filterret;
				}
			};
		}
		
		return ret;
	}
	
	/**
	 *  Used after searches to make bound filters remotable.
	 */
	public void optimizeFilters()
	{
		List<ConstantValueFilter> newunboundconstantfilters = new ArrayList<ConstantValueFilter>();
		for (ConstantValueFilter fil : unboundconstantfilters)
		{
			if (fil.getValue() != null)
			{
				filters.add(fil);
			}
			else
			{
				newunboundconstantfilters.add(fil);
			}
		}
		unboundconstantfilters = newunboundconstantfilters;
	}
}
