package jadex.bridge.service.component.interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.CheckIndex;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.CheckState;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.IValueFetcher;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.javaccimpl.ExpressionNode;
import jadex.javaparser.javaccimpl.ParameterNode;

/**
 *  Interceptor that checks annotated pre- and postconditions.
 */
public class PrePostConditionInterceptor extends AbstractLRUApplicableInterceptor
{
	/**
	 *  Create a new AbstractLRUApplicableInterceptor. 
	 */
	public PrePostConditionInterceptor(IInternalAccess ia)
	{
		super(ia);
	}
	
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean customIsApplicable(ServiceInvocationContext context)
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
	 *  Check if an annotation belongs to the supported
	 *  types of pre/postconditions.
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
	 *  Check the precondition.
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
						ret = new IllegalArgumentException("Argument must not null: "+i+", "+context.getMethod());
					}
				}
				else if(paramannos[i][j] instanceof CheckIndex)
				{
					if(args[i]!=null)
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
							int size = -1;
							if(args[idx] instanceof Collection)
							{
								size = ((Collection<?>)args[idx]).size();
							}
							else if(args[idx]!=null && args[idx].getClass().isArray())
							{
								size = Array.getLength(args[idx]);
							}
							else if(args[idx] instanceof Map)
							{
								size = ((Map<?, ?>)args[idx]).size();
							}
							else
							{
								ret = new RuntimeException("Unknown collection type: "+args[idx]);
							}
							
							if(size!=-1 && test>=size)
							{
								ret = new IndexOutOfBoundsException("Index="+idx+" , Size="+size);
							}
						}
					}
				}
				else if(paramannos[i][j] instanceof CheckState)
				{
					CheckState ci = (CheckState)paramannos[i][j];
					try
					{
						Object resu = SJavaParser.evaluateExpression(ci.value(), new PrePostConditionFetcher(args, args[i], null, null));
					
						if(!(resu instanceof Boolean) || !((Boolean)resu).booleanValue())
						{
							ret = new IllegalStateException("Precondition violated: "+ci.value()+" arg: "+args[i]);
						}
					}
					catch(Exception e)
					{
						ret = e instanceof RuntimeException? (RuntimeException)e: new RuntimeException(e);
					}
				}	
			}
		}
		
		return ret;
	}
	
	/**
	 *  Check the postconditions.
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
						ret = new IllegalArgumentException("Result must not null.");
					}
				}
			}
			else if(annos[i] instanceof CheckState)
			{
				CheckState ci = (CheckState)annos[i];
				if(intermediate && ci.intermediate() || (!intermediate && !ci.intermediate()))
				{
					Object[] args = context.getArgumentArray();
					try
					{
						Object resu = SJavaParser.evaluateExpression(ci.value(), new PrePostConditionFetcher(args, null, res, ires));
						
						if(!(resu instanceof Boolean) || !((Boolean)resu).booleanValue())
						{
							ret = new IllegalStateException("Postcondition violated: "+ci.value());
						}
					}
					catch(IntermediateResultUnavailableException e)
					{
//						System.out.println("Unavailable: "+context.getMethod().getName());
						// no error if intermediate is not available, e.g. could be first call so that [-1] is not available
					}
					catch(Exception e)
					{
						ret = e instanceof RuntimeException? (RuntimeException)e: new RuntimeException(e);
					}
				}
			}	
		}
		
		return ret;
	}
	
	/**
	 *  Get the number of intermediate results that should be explicitly
	 *  kept by the interceptor (only necessary for subscription futures).
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
					if(ci.keep()>0)
					{
						ret = Math.max(ret, ci.keep());
					}
					else
					{
						try
						{
							ExpressionNode expr = (ExpressionNode)SJavaParser.parseExpression(ci.value(), null, null);
							List<ExpressionNode> nodes = new ArrayList<ExpressionNode>();
							nodes.add(expr);
							
							
							while(nodes.size()>0)
							{
								ExpressionNode node = nodes.remove(0);
								// add all child nodes
								for(int k=0; k<node.jjtGetNumChildren(); k++)
								{
									nodes.add((ExpressionNode)node.jjtGetChild(k));
								}
								if(node instanceof ParameterNode)
								{
									String txt = ((ParameterNode)node).getText();
									if(node instanceof ParameterNode && txt.startsWith("$res[-"))
									{
										String numtxt = txt.substring(6, txt.length()-1);
										int kp = Integer.parseInt(numtxt);
										if(kp>ret)
											ret = kp;
									}
								}
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}	
		}
		
//		System.out.println("keep: "+ret);
		
		return ret;
	}
	
	/**
	 *  Fetcher for pre and post condition.
	 *  Supports $arg, $res and $res[-1], ... 
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
			if("$res".equals(name) || name.startsWith("$res[0]"))
			{
				ret = result;
			}
			else if(name.startsWith("$res["))
			{
				String numtext = name.substring(5, name.length()-1);
				int num = Integer.parseInt(numtext);
				if(ires!=null)
				{
					int idx = ires.size()+num;
					if(idx>=0 && idx<ires.size())
					{
						ret = ires.get(idx);
					}
					else
					{
						throw new IntermediateResultUnavailableException();
					}
				}
				else
				{
					throw new IntermediateResultUnavailableException();
				}
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
			final Object	res	= sic.getResult();
			
//			if(sic.getMethod().getName().equals("getInputStream"))
//				System.out.println("heererrere");
			
			if(res instanceof IFuture)
			{
//				final IIntermediateFuture<?> iresfut = res instanceof IIntermediateFuture? (IIntermediateFuture<?>)res: null;
				
				FutureFunctionality func = new FutureFunctionality(/*sic.getCallerAdapter()!=null ? sic.getCallerAdapter().getLogger() :*/(Logger) null)
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
						RuntimeException ex = checkPostConditions(sic, result, true, ires);
						
						int keep = getKeepForPostConditions(sic);
						addIntermediateResultToStore(result, keep);
						
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
				
				Future<?> fut = FutureFunctionality.getDelegationFuture((IFuture<?>)res, func);
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

	/**
	 * 
	 */
	public static class IntermediateResultUnavailableException extends RuntimeException
	{
		/**
		 *  Create a new exception.
		 */
	    public IntermediateResultUnavailableException() 
	    {
	    }

	    /**
	     *  Create a new exception.
	     */
	    public IntermediateResultUnavailableException(String message) 
	    {
	    	super(message);
	    }
	}
}
