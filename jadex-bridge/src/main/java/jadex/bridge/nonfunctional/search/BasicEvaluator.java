package jadex.bridge.nonfunctional.search;

import jadex.bridge.service.IService;
import jadex.commons.MethodInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

public abstract class BasicEvaluator<T> implements IServiceEvaluator
{
	protected String propertyname;
	
	protected MethodInfo methodinfo;
	
	protected Object unit;
	
	public BasicEvaluator(String propertyname)
	{
		this(propertyname, null, null);
	}
	
	public BasicEvaluator(String propertyname, Object unit)
	{
		this(propertyname, null, unit);
	}
	
	public BasicEvaluator(String propertyname, MethodInfo methodinfo, Object unit)
	{
		this.propertyname = propertyname;
		this.methodinfo = methodinfo;
		this.unit = unit;
	}
	
	public abstract double calculateEvaluation(T propertyvalue);
	
	public IFuture<Double> evaluate(IService service)
	{
		final Future<Double> ret = new Future<Double>();
		IResultListener<Object> listener = new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
				ret.setResult(calculateEvaluation((T) result));
			}

			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		};
		if (methodinfo != null)
		{
			if (unit != null)
			{
				service.getMethodNFPropertyValue(methodinfo, propertyname, unit).addResultListener(listener);
			}
			else
			{
				service.getMethodNFPropertyValue(methodinfo, propertyname).addResultListener(listener);
			}
		}
		else
		{
			if (unit != null)
			{
				service.getNFPropertyValue(propertyname, unit).addResultListener(listener);
			}
			else
			{
				service.getNFPropertyValue(propertyname).addResultListener(listener);
			}
		}
		return ret;
	}
}
