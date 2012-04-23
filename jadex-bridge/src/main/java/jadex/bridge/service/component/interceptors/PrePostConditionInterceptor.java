package jadex.bridge.service.component.interceptors;

import jadex.bridge.service.annotation.PostCondition;
import jadex.bridge.service.annotation.PostConditions;
import jadex.bridge.service.annotation.PreCondition;
import jadex.bridge.service.annotation.PreConditions;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.IValueFetcher;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;

import java.util.Collection;

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
		return context.getMethod().isAnnotationPresent(PreConditions.class);
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext context)
	{
		final Future<Void> ret = new Future<Void>();
		
		Exception ex = null;
		PreConditions pcons = context.getMethod().getAnnotation(PreConditions.class);
		PreCondition[] pcs = pcons.value();
		final Object[] args = context.getArgumentArray();
		for(int i=0; ex ==null && i<pcs.length; i++)
		{
			if(pcs[i].value()==PreCondition.Type.NOTNULL)
			{
				int[] argnos = pcs[i].argno();
				for(int j=0; ex==null && j<argnos.length; j++)
				{
					if(args[argnos[j]]==null)
					{
						ex = new ConditionException("Precondition violated, argument null: "+argnos[j]);
					}
				}
			}
			else if(pcs[i].value()==PreCondition.Type.EXPRESSION)
			{
				Object res = SJavaParser.evaluateExpression(pcs[i].expression(), new PrePostConditionFetcher(context.getArgumentArray(), null, null));
				if(!(res instanceof Boolean) || !((Boolean)res).booleanValue())
				{
					ex = new ConditionException("Precondition violated: "+pcs[i].expression());
				}
			}
		}
		
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
	protected ConditionException checkPostConditions(ServiceInvocationContext context, Object res, Object ires)
	{
		ConditionException ex = null;
		PostConditions pcons = context.getMethod().getAnnotation(PostConditions.class);
		PostCondition[] pcs = pcons.value();
		for(int i=0; ex==null && i<pcs.length; i++)
		{
			if(pcs[i].value()==PostCondition.Type.NOTNULL)
			{
				if(res==null)// || ires==null) ?
				{
					throw new ConditionException("Postcondition violated, result nulls.");
				}
			}
			else if(pcs[i].value()==PostCondition.Type.EXPRESSION)
			{
				Object val = SJavaParser.evaluateExpression(pcs[i].expression(), new PrePostConditionFetcher(context.getArgumentArray(), res, ires));
				if(!(val instanceof Boolean) || !((Boolean)val).booleanValue())
					throw new ConditionException("Postcondition violated: "+pcs[i].expression());
			}
		}
		
		return ex;
	}
	
	/**
	 * 
	 */
	public static class PrePostConditionFetcher implements IValueFetcher
	{
		/** The arguments. */
		protected Object[] args;
		
		/** The result. */
		protected Object result;
		
		/** The intermediate result. */
		protected Object iresult;
		
		/**
		 * 
		 */
		public PrePostConditionFetcher(Object[] args, Object result, Object iresult)
		{
			this.args = args;
			this.result = result;
			this.iresult = iresult;
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
			else if("$ires".equals(name))
			{
				ret = iresult;
			}
			else
			{
				int idx = name.indexOf("$arg");
				if(idx>-1)
				{
					String numtext = name.substring(idx+4);
					int num = Integer.parseInt(numtext);
					return args[num];
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
					public Object intermediateResultAvailable(Object result)
					{
						checkPostConditions(sic, null, result);
						return result;
					}
					
					public void finished(Collection<Object> results)
					{
						 checkPostConditions(sic, results, null);
					}
					
					public Object resultAvailable(Object result)
					{
						checkPostConditions(sic, result, null);
						return result;
					}
				};
				
				Future<?> fut = FutureFunctionality.getDelegationFuture((IFuture)res, func);
				((IFuture)res).addResultListener(new DelegationResultListener(fut));
				sic.setResult(fut);
				
//				if(res instanceof ISubscriptionIntermediateFuture)
//				{
//					SubscriptionIntermediateDelegationFuture<Object> fut = new SubscriptionIntermediateDelegationFuture<Object>((ISubscriptionIntermediateFuture)res)
//					{
//						public void	setResult(Collection<Object> result)
//						{
//							Exception ex = checkPostConditions(sic, result, null);
//					    	if(ex==null)
//					    		super.setResult(result);
//					    	else
//					    		super.setException(ex);
//					    }
//						
//						public void addIntermediateResult(Object result)
//						{
//							Exception ex = checkPostConditions(sic, null, result);
//					    	if(ex==null)
//					    		super.addIntermediateResult(result);
//					    	else
//					    		super.setExceptionIfUndone(ex);
//						}	
//						
//						public void setFinished()
//						{
//							Exception ex = checkPostConditions(sic, getIntermediateResults(), null);
//					    	if(ex==null)
//					    		super.setResult(result);
//					    	else
//					    		super.setException(ex);
//						}
//					};
////					((Future<Object>)res).addResultListener(new TerminableDelegationResultListener<Object>(fut, (ITerminableFuture)res));
//					res	= fut;
//				}
//				else if(res instanceof ITerminableIntermediateFuture)
//				{
//					TerminableIntermediateDelegationFuture<Object> fut = new TerminableIntermediateDelegationFuture<Object>((ITerminableIntermediateFuture)res)
//					{
//						public void	setResult(Collection<Object> result)
//						{
//							Exception ex = checkPostConditions(sic, result, null);
//					    	if(ex==null)
//					    		super.setResult(result);
//					    	else
//					    		super.setException(ex);
//					    }
//						
//						public void addIntermediateResult(Object result)
//						{
//							Exception ex = checkPostConditions(sic, null, result);
//					    	if(ex==null)
//					    		super.addIntermediateResult(result);
//					    	else
//					    		super.setExceptionIfUndone(ex);
//						}	
//						
//						public void setFinished()
//						{
//							Exception ex = checkPostConditions(sic, getIntermediateResults(), null);
//					    	if(ex==null)
//					    		super.setResult(result);
//					    	else
//					    		super.setException(ex);
//						}
//					};
////					((Future<Object>)res).addResultListener(new TerminableDelegationResultListener<Object>(fut, (ITerminableFuture)res));
//					res	= fut;
//				}
//				else if(res instanceof ITerminableFuture)
//				{
//					TerminableDelegationFuture<Object> fut = new TerminableDelegationFuture<Object>((ITerminableFuture)res)
//					{
//						public void	setResult(Object result)
//						{
//					    	Exception ex = checkPostConditions(sic, result, null);
//					    	if(ex==null)
//					    		super.setResult(result);
//					    	else
//					    		super.setException(ex);
//					    }	
//					};
////					((Future<Object>)res).addResultListener(new TerminableDelegationResultListener<Object>(fut, (ITerminableFuture)res));
//					res	= fut;
//				}
//				else if(res instanceof IIntermediateFuture)
//				{
//					IntermediateFuture<Object>	fut	= new IntermediateFuture<Object>()
//					{
//						public void	setResult(Collection<Object> result)
//						{
//							Exception ex = checkPostConditions(sic, result, null);
//					    	if(ex==null)
//					    		super.setResult(result);
//					    	else
//					    		super.setException(ex);
//					    }
//						
//						public void addIntermediateResult(Object result)
//						{
//							Exception ex = checkPostConditions(sic, null, result);
//					    	if(ex==null)
//					    		super.addIntermediateResult(result);
//					    	else
//					    		super.setExceptionIfUndone(ex);
//						}
//						
//						public void setFinished()
//						{
//							Exception ex = checkPostConditions(sic, getIntermediateResults(), null);
//					    	if(ex==null)
//					    		super.setResult(result);
//					    	else
//					    		super.setException(ex);
//						}
//					};
//					((IntermediateFuture<Object>)res).addResultListener(new IntermediateDelegationResultListener<Object>(fut));
//					res	= fut;
//				}
//				else
//				{
//					Future<Object>	fut	= new Future<Object>()
//					{
//					    public void	setResult(Object result)
//					    {
//					    	Exception ex = checkPostConditions(sic, result, null);
//					    	if(ex==null)
//					    		super.setResult(result);
//					    	else
//					    		super.setException(ex);
//					    }							
//					};							
//					((Future<Object>)res).addResultListener(new DelegationResultListener<Object>(fut));
//					res	= fut;
//				}
//				sic.setResult(res);
			}
			else
			{
				Exception ex = checkPostConditions(sic, result, null);
		    	if(ex!=null)
		    		super.exceptionOccurred(ex);
			}
			
			super.customResultAvailable(null);
		}
	}

}
