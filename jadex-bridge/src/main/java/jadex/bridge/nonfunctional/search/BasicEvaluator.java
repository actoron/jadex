package jadex.bridge.nonfunctional.search;

import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.INFRPropertyProvider;
import jadex.bridge.service.IService;
import jadex.commons.MethodInfo;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Basic evaluator implementation for service and methods.
 */
public abstract class BasicEvaluator<T> implements IServiceEvaluator
{
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
	public BasicEvaluator(String propertyname)
	{
		this(propertyname, null, null, false);
	}
	
	/**
	 *  Create a new evaluator.
	 *  @param propertyname The property name.
	 */
	public BasicEvaluator(String propertyname, Object unit)
	{
		this(propertyname, null, unit, false);
	}
	
	/**
	 *  Create a new evaluator.
	 *  @param propertyname The property name.
	 */
	public BasicEvaluator(String propertyname, MethodInfo mi)
	{
		this(propertyname, mi, null, false);
	}
	
	/**
	 *  Create a new evaluator.
	 *  @param propertyname The property name.
	 *  @param methodinfo The method.
	 *  @param unit The unit.
	 */
	public BasicEvaluator(String propertyname, MethodInfo methodinfo, Object unit, boolean required)
	{
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
		
		if(required)
		{
//			System.out.println("test: "+(service instanceof INFRPropertyProvider));
			((INFRPropertyProvider)service).getRequiredServicePropertyProvider()
				.addResultListener(new ExceptionDelegationResultListener<INFMixedPropertyProvider, Double>(ret)
			{
				public void customResultAvailable(INFMixedPropertyProvider result)
				{
					getPropertyValue(result).addResultListener(listener);
				}
			});
		}
		else
		{
			getPropertyValue(service).addResultListener(listener);
		}
		
		return ret;
	}
	
	/**
	 *  Get the property value based on the provider.
	 */
	protected IFuture<T> getPropertyValue(INFMixedPropertyProvider provider)
	{
		if(methodinfo!=null)
		{
			if(unit!=null)
			{
				return provider.getMethodNFPropertyValue(methodinfo, propertyname, unit);
			}
			else
			{
				return provider.getMethodNFPropertyValue(methodinfo, propertyname);
			}
		}
		else
		{
			if(unit!=null)
			{
				return provider.getNFPropertyValue(propertyname, unit);
			}
			else
			{
				return provider.getNFPropertyValue(propertyname);
			}
		}
	}
}
