package jadex.bridge.nonfunctional.hardconstraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.SNFPropertyProvider;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.ComposedRemoteFilter;
import jadex.commons.IAsyncFilter;
import jadex.commons.MethodInfo;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;

/**
 *  Class defining runtime hard constraints.
 *
 */
public class RHardConstraints
{
	/** The component. */
	protected IExternalAccess component;
	
	/** Hard constraint model */
	protected Collection<MHardConstraint> constraintmodel;
	
	/** The basic hard constraints filter */
	protected List<IAsyncFilter<?>> filters = new ArrayList<IAsyncFilter<?>>();
	
	/** Unbound constant value filters */
	protected List<ConstantValueFilter> unboundconstantfilters = new ArrayList<ConstantValueFilter>();
	
	/**
	 *  Creates the runtime hard constraints.
	 * 
	 *  @param mhc The declared model hard constraints.
	 */
	public RHardConstraints(IExternalAccess component, Collection<MHardConstraint> constraintmodel)
	{
		this.component = component;
		this.constraintmodel = constraintmodel;
		for(MHardConstraint hc : constraintmodel)
		{
			if (MHardConstraint.CONSTANT.equals(hc.getOperator()))
			{
				addFilter(new ConstantValueFilter(hc.getPropertyName(), hc.getValue()));
			}
			else if (MHardConstraint.GREATER.equals(hc.getOperator()))
			{
				addFilter(new StrictInequalityFilter(false));
			}
			else if (MHardConstraint.LESS.equals(hc.getOperator()))
			{
				addFilter(new StrictInequalityFilter(true));
			}
			else if (MHardConstraint.GREATER_OR_EQUAL.equals(hc.getOperator()))
			{
				addFilter(new InequalityFilter(false));
			}
			else if (MHardConstraint.LESS_OR_EQUAL.equals(hc.getOperator()))
			{
				addFilter(new StrictInequalityFilter(true));
			}
			else
			{
				throw new RuntimeException("Unknown hard constraint type: " + hc.getOperator());
			}
		}
	}
	
	/**
	 *  Adds a filter.
	 *  
	 *  @param filter The filter.
	 */
	protected void addFilter(IAsyncFilter<IService> filter)
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
	public IAsyncFilter<?> getRemotableFilter()
	{
		IAsyncFilter<?> ret = null;
		
		if (filters.isEmpty())
		{
			ret = IAsyncFilter.ALWAYS;
		} 
		else
		{
			ret = new ComposedRemoteFilter(filters.toArray(new IAsyncFilter[filters.size()]));
		}
		
		return (IAsyncFilter<?>) ret;
	}
	
	/**
	 *  Gets the filter for local filtering.
	 *  
	 *  @return Filter for local filtering.
	 */
	public IAsyncFilter<?> getLocalFilter()
	{
		return getLocalFilter(null);
	}
	
	/**
	 *  Gets the filter for local filtering.
	 *  
	 *  @return Filter for local filtering.
	 */
	public IAsyncFilter<IService> getLocalFilter(final MethodInfo method)
	{
		IAsyncFilter<IService> ret = null;
		
		if(unboundconstantfilters.isEmpty())
		{
			ret = (IAsyncFilter)IAsyncFilter.ALWAYS;
		}
		else
		{
			ret = new IAsyncFilter<IService>()
			{
				public IFuture<Boolean> filter(final IService service)
				{
					final Future<Boolean> filterret = new Future<Boolean>();
					
					final List<ConstantValueFilter> boundconstantfilters = new ArrayList<ConstantValueFilter>();
					
					final CollectionResultListener<Boolean> constantrl = new CollectionResultListener<Boolean>(unboundconstantfilters.size(), false, new IResultListener<Collection<Boolean>>()
					{
						public void resultAvailable(Collection<Boolean> result)
						{
							boolean filterresult = false;
							if(result!=null)
							{
								Boolean[] results = result.toArray(new Boolean[result.size()]);
								filterresult = true;
								for (int i = 0; i < results.length && filterresult; ++i)
								{
									filterresult &= results[i];
								}
							}
							
							if(!filterresult)
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
						
						component.getMethodNFPropertyValue(((IService)service).getId(), method, filter.getValueName())
							.addResultListener(new IResultListener<Object>()
						{
							public void resultAvailable(Object result)
							{
								if(filter.getValue() == null)
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
						
//						INFMixedPropertyProvider prov = ((INFMixedPropertyProvider)((IService)service).getExternalComponentFeature(INFPropertyComponentFeature.class));
//						((IService)service).getNFPropertyValue(propname).addResultListener(new IResultListener<Object>()
//						prov.getMethodNFPropertyValue(method, filter.getValueName()).addResultListener(new IResultListener<Object>()
////						service.getMethodNFPropertyValue(method, filter.getValueName()).addResultListener(new IResultListener<Object>()
//						{
//							public void resultAvailable(Object result)
//							{
//								if (filter.getValue() == null)
//								{
//									filter.bind(result);
//									boundconstantfilters.add(filter);
//								}
//								filter.filter(service).addResultListener(constantrl);
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
//								constantrl.exceptionOccurred(exception);
//							}
//						});
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
	
	public static <T> ITerminableIntermediateFuture<T> getServices(final IInternalAccess ia, final Class<T> type, final String scope, final MethodInfo method, final RHardConstraints hardconstraints)
	{
		if(hardconstraints == null)
		{
			return ia.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(type, scope));
		}
		else
		{
			final TerminableIntermediateFuture<T> ret = new TerminableIntermediateFuture<T>();
			// dropped for v4???
//			SServiceProvider.getServices(ea, type, scope, (IAsyncFilter<T>) hardconstraints.getRemotableFilter()).addResultListener(new IResultListener<Collection<T>>()
//			{
//				public void resultAvailable(Collection<T> results)
//				{
////					List<T> filteredresults = new ArrayList<T>();
//					IAsyncFilter<T> filter = (IAsyncFilter<T>) hardconstraints.getLocalFilter();
//					
////					CollectionResultListener<T> crl = new CollectionResultListener<T>(results.size(), true, new DelegationResultListener<T>(new IResultListener<T>()
////					{
////						
////					}));
//					
//					for (T result : results)
//					{
//					}
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					ret.setException(exception);
//				}
//			});
		}
		return null;
	}
}
