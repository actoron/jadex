package jadex.bridge.service.component.interceptors;

import jadex.bridge.service.annotation.CheckIndex;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.CheckState;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.IValueFetcher;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.javaparser.SJavaParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class PrePostConditionInterceptor implements IServiceInvocationInterceptor
{
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		boolean ret = false;
		Annotation[] methodannos = context.getMethod().getAnnotations();
		for(int i=0; !ret && i<methodannos.length; i++)
		{
			ret = isPrePostCondition(methodannos[i]);
		}
		if(!ret)
		{
			Annotation[][] paramannos = context.getMethod().getParameterAnnotations();
			for(int i=0; !ret && i<paramannos.length; i++)
			{
				for(int j=0; !ret && j<paramannos[i].length; j++)
				{
					ret = isPrePostCondition(paramannos[i][j]);
				}
			}
		}
		return ret;
	}
	
	/**
	 * 
	 */
	protected boolean isPrePostCondition(Annotation anno)
	{
		return anno instanceof CheckNotNull 
			|| anno instanceof CheckState
			|| anno instanceof CheckIndex;
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext context)
	{
		final Future<Void> ret = new Future<Void>();
		
		Exception ex = checkPreConditions(context);
		
		if(ex==null)
		{
			context.invoke().addResultListener(new CheckReturnValueResultListener(ret, context));
		}
		else
		{
			ret.setException(ex);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected RuntimeException checkPreConditions(ServiceInvocationContext context)
	{
		RuntimeException ret = null;
		final Object[] args = context.getArgumentArray();

		Annotation[][] paramannos = context.getMethod().getParameterAnnotations();
		for(int i=0; ret==null && i<paramannos.length; i++)
		{
			for(int j=0; ret==null && j<paramannos[i].length; j++)
			{
				if(paramannos[i][j] instanceof CheckNotNull)
				{
					if(args[i]==null)
					{
						ret = new NullPointerException();
					}
				}
				else if(paramannos[i][j] instanceof CheckIndex)
				{
					CheckIndex ci = (CheckIndex)paramannos[i][j];
					int test = ((Number)args[i]).intValue();
					if(test<0)
					{
						ret = new IndexOutOfBoundsException();
					}
					else
					{
						int idx = ci.value();
						if(args[idx] instanceof Collection)
						{
							if(test>=((Collection)args[idx]).size())
							{
								ret = new IndexOutOfBoundsException();
							}
						}
						else if(args[idx]!=null && args[idx].getClass().isArray())
						{
							if(test>=Array.getLength(args[idx]))
							{
								ret = new IndexOutOfBoundsException();
							}
						}
						else if(args[idx] instanceof Map)
						{
							if(test>=((Map)args[idx]).size())
							{
								ret = new IndexOutOfBoundsException();
							}
						}
					}
				}
				else if(paramannos[i][j] instanceof CheckState)
				{
					CheckState ci = (CheckState)paramannos[i][j];
					Object resu = SJavaParser.evaluateExpression(ci.value(), new PrePostConditionFetcher(args, 
						args[i], null, null));
					if(!(resu instanceof Boolean) || !((Boolean)resu).booleanValue())
					{
						ret = new IllegalStateException("Precondition violated: "+ci.value()+" arg: "+args[i]);
					}
				}	
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected RuntimeException checkPostConditions(ServiceInvocationContext context, Object res, 
		boolean intermediate, List<Object> ires)
	{
		RuntimeException ret = null;

		Annotation[] annos = context.getMethod().getAnnotations();
		for(int i=0; ret==null && i<annos.length; i++)
		{
			if(annos[i] instanceof CheckNotNull)
			{
				CheckNotNull cnn = (CheckNotNull)annos[i];
				if(intermediate && cnn.intermediate() || (!intermediate && !cnn.intermediate()))
				{
					if(res==null)
					{
						ret = new NullPointerException();
					}
				}
			}
			else if(annos[i] instanceof CheckState)
			{
				CheckState ci = (CheckState)annos[i];
				if(intermediate && ci.intermediate() || (!intermediate && !ci.intermediate()))
				{
					Object[] args = context.getArgumentArray();
					Object resu = SJavaParser.evaluateExpression(ci.value(), new PrePostConditionFetcher(args, 
						null, res, ires));
					if(!(resu instanceof Boolean) || !((Boolean)resu).booleanValue())
					{
						ret = new IllegalStateException("Precondition violated: "+ci.value());
					}
				}
			}	
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected int getKeepForPostConditions(ServiceInvocationContext context)
	{
		int ret = 0;

		Annotation[] annos = context.getMethod().getAnnotations();
		for(int i=0; i<annos.length; i++)
		{
			if(annos[i] instanceof CheckState)
			{
				CheckState ci = (CheckState)annos[i];
				if(ci.intermediate())
				{
					ret = Math.max(ret, ci.keep());
				}
			}	
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static class PrePostConditionFetcher implements IValueFetcher
	{
		/** The arguments. */
		protected Object[] args;
		
		/** The current arguments. */
		protected Object currentarg;
		
		/** The result. */
		protected Object result;
		
		/** The intermediate results. */
		protected List<Object> ires;
		
		/**
		 * 
		 */
		public PrePostConditionFetcher(Object[] args, Object currentarg, Object result, List<Object> ires)
		{
			this.args = args;
			this.currentarg = currentarg;
			this.result = result;
			this.ires = ires;
		}
		
		/**
		 * 
		 */
		public Object fetchValue(String name)
		{
			Object ret = null;
			if("$res".equals(name))
			{
				ret = result;
			}
			else if(name.startsWith("$res["))
			{
				String numtext = name.substring(5, name.length()-1);
				int num = Integer.parseInt(numtext);
				ret = ires.get(ires.size()-num);
			}
			else
			{
				int idx = name.indexOf("$arg");
				if(idx>-1 && idx+4<name.length())
				{
					String numtext = name.substring(idx+4);
					int num = Integer.parseInt(numtext);
					ret = args[num];
				}
				else
				{
					ret = currentarg;
				}
			}
			return ret;
		}
		
		/**
		 * 
		 */
		public Object fetchValue(String name, Object object)
		{
			return null;
		}
	}
	
	/**
	 *  Check return value, when service call is finished.
	 */
	protected class CheckReturnValueResultListener extends DelegationResultListener<Void>
	{
		//-------- attributes --------
		
		/** The service invocation context. */
		protected ServiceInvocationContext	sic;
		
		//-------- constructors --------
		
		/**
		 *  Create a result listener.
		 */
		protected CheckReturnValueResultListener(Future<Void> future, ServiceInvocationContext sic)
		{
			super(future);
			this.sic = sic;
		}
		
		//-------- IResultListener interface --------

		/**
		 *  Called when the service call is finished.
		 */
		public void customResultAvailable(Void result)
		{
			Object	res	= sic.getResult();
			
//			if(sic.getMethod().getName().equals("getInputStream"))
//				System.out.println("heererrere");
			
			if(res instanceof IFuture)
			{
				FutureFunctionality func = new FutureFunctionality()
				{
					List<Object> ires;
					
					protected void addIntermediateResultToStore(Object result, int keep)
					{
						if(keep>0)
						{
							if(ires==null)
								ires = new ArrayList<Object>();
							ires.add(result);
							if(ires.size()>keep)
								ires.remove(0);
						}
					}
					
					public Object addIntermediateResult(Object result)
					{
						int keep = getKeepForPostConditions(sic);
						addIntermediateResultToStore(result, keep);
						
						RuntimeException ex = checkPostConditions(sic, result, true, ires);
						if(ex!=null)
							throw ex;
						else
							return result;
					}
					
					public Object addIntermediateResultIfUndone(Object result)
					{
						return addIntermediateResult(result);
					}
					
					public void setFinished(Collection<Object> results)
					{
						RuntimeException ex = checkPostConditions(sic, results, false, ires);
						if(ex!=null)
							throw ex;
					}
					
					public void setFinishedIfUndone(Collection<Object> results)
					{
						setFinished(results);
					}
					
					public Object setResult(Object result)
					{
						RuntimeException ex = checkPostConditions(sic, result, false, ires);
						if(ex!=null)
							throw ex;
						else
							return result;
					}
					
					public Object setResultIfUndone(Object result)
					{
						return setResult(result);
					}
				};
				
				Future<?> fut = FutureFunctionality.getDelegationFuture((IFuture)res, func);
				sic.setResult(fut);
			}
			else
			{
				Exception ex = checkPostConditions(sic, result, false, null);
		    	if(ex!=null)
		    		super.exceptionOccurred(ex);
			}
			
			super.customResultAvailable(null);
		}
	}

}
