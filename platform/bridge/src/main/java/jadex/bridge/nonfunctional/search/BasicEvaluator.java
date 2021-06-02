package jadex.bridge.nonfunctional.search;

import jadex.bridge.IExternalAccess;
import jadex.bridge.nonfunctional.SNFPropertyProvider;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.MethodInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Basic evaluator implementation for service and methods.
 */
public abstract class BasicEvaluator<T> implements IServiceEvaluator
{
	/** The component. */
	protected IExternalAccess component;
	
	/** The property name. */
	protected String propertyname;
	
	/** The method info. */
	protected MethodInfo methodinfo;
	
	/** The unit. */
	protected Object unit;
	
	/** The required flag. */
	protected boolean required;
	
	/**
	 *  Create a new evaluator.
	 *  @param propertyname The property name.
	 */
	public BasicEvaluator(IExternalAccess component, String propertyname)
	{
		this(component, propertyname, null, null, false);
	}
	
	/**
	 *  Create a new evaluator.
	 *  @param propertyname The property name.
	 */
	public BasicEvaluator(IExternalAccess component, String propertyname, Object unit)
	{
		this(component, propertyname, null, unit, false);
	}
	
	/**
	 *  Create a new evaluator.
	 *  @param propertyname The property name.
	 */
	public BasicEvaluator(IExternalAccess component, String propertyname, MethodInfo mi)
	{
		this(component, propertyname, mi, null, false);
	}
	
	/**
	 *  Create a new evaluator.
	 *  @param propertyname The property name.
	 *  @param methodinfo The method.
	 *  @param unit The unit.
	 */
	public BasicEvaluator(IExternalAccess component, String propertyname, MethodInfo methodinfo, Object unit, boolean required)
	{
		this.component = component;
		this.propertyname = propertyname;
		this.methodinfo = methodinfo;
		this.unit = unit;
		this.required = required;
	}
	
	/**
	 * 
	 * @param propertyvalue
	 * @return
	 */
	public abstract double calculateEvaluation(T propertyvalue);
	
	/**
	 *  Evaluate the service of method.
	 */
	public IFuture<Double> evaluate(IService service)
	{
		final Future<Double> ret = new Future<Double>();
		final IResultListener<T> listener = new ExceptionDelegationResultListener<T, Double>(ret)
		{
			public void customResultAvailable(T result)
			{
				ret.setResult(calculateEvaluation(result));
			}
		};
		
		getPropertyValue(((IService)service).getServiceId()).addResultListener(listener);
		
		return ret;
	}
	
	/**
	 *  Get the property value based on the provider.
	 */
	protected IFuture<T> getPropertyValue(final IServiceIdentifier sid)
	{
		final Future<T> ret = new Future<T>();
		
		if(required)
		{
			if(methodinfo!=null)
			{
				if(unit!=null)
				{
					IFuture<T> res = component.getRequiredMethodNFPropertyValue(sid, methodinfo, propertyname, unit);
					res.addResultListener(new DelegationResultListener<T>(ret));
//								return provider.getMethodNFPropertyValue(methodinfo, propertyname, unit);
				}
				else
				{
					IFuture<T> res = component.getRequiredMethodNFPropertyValue(sid, methodinfo, propertyname);
					res.addResultListener(new DelegationResultListener<T>(ret));
//								return provider.getMethodNFPropertyValue(methodinfo, propertyname);
				}
			}
			else
			{
				if(unit!=null)
				{
					IFuture<T> res = component.getRequiredNFPropertyValue(sid, propertyname, unit);
					res.addResultListener(new DelegationResultListener<T>(ret));
//								return provider.getNFPropertyValue(propertyname, unit);
				}
				else
				{
					IFuture<T> res = component.getRequiredNFPropertyValue(sid, propertyname);
					res.addResultListener(new DelegationResultListener<T>(ret));
//								return provider.getNFPropertyValue(propertyname);
				}
			}
		}
		else
		{
			component.getExternalAccessAsync(sid.getProviderId()).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					if(methodinfo!=null)
					{
						if(unit!=null)
						{
							IFuture<T> res = result.getMethodNFPropertyValue(sid, methodinfo, propertyname, unit);
							res.addResultListener(new DelegationResultListener<T>(ret));
//								return provider.getMethodNFPropertyValue(methodinfo, propertyname, unit);
						}
						else
						{
							IFuture<T> res = result.getMethodNFPropertyValue(sid, methodinfo, propertyname);
							res.addResultListener(new DelegationResultListener<T>(ret));
//								return provider.getMethodNFPropertyValue(methodinfo, propertyname);
						}
					}
					else
					{
						if(unit!=null)
						{
							IFuture<T> res = result.getNFPropertyValue(sid, propertyname, unit);
							res.addResultListener(new DelegationResultListener<T>(ret));
//								return provider.getNFPropertyValue(propertyname, unit);
						}
						else
						{
							IFuture<T> res = result.getNFPropertyValue(sid, propertyname);
							res.addResultListener(new DelegationResultListener<T>(ret));
//								return provider.getNFPropertyValue(propertyname);
						}
					}
				}
			});
		}

		return ret;
	}
}
