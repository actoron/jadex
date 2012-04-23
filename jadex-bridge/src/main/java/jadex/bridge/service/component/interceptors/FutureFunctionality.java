package jadex.bridge.service.component.interceptors;

import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.TerminableDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationFuture;

import java.util.Collection;

/**
 * 
 */
class FutureFunctionality
{
	/**
	 * 
	 */
	public FutureFunctionality()
	{
	}
	
	/**
	 * 
	 * @param result
	 * @return
	 */
	public Object intermediateResultAvailable(Object result)
	{
		return result;
	}
	
//	/**
//	 * 
//	 */
//	public Collection<Object> finished(Collection<Object> results)
//	{
//		return results;
//	}
	
	/**
	 * 
	 */
	public void finished(Collection<Object> results)
	{
	}
	
	/**
	 * 
	 * @param result
	 * @return
	 */
	public Object resultAvailable(Object result)
	{
		return result;
	}
	
	/**
	 * 
	 * @param exception
	 * @return
	 */
	public Exception exceptionOccurred(Exception exception)
	{
		return exception;
	}

	/**
	 * 
	 */
	public static Future getDelegationFuture(IFuture<?> orig, final FutureFunctionality func)
	{
		Future ret = null;
		
		if(orig instanceof ISubscriptionIntermediateFuture)
		{
			SubscriptionIntermediateDelegationFuture<Object> fut = new SubscriptionIntermediateDelegationFuture<Object>((ISubscriptionIntermediateFuture)orig)
			{
				public void	setResult(Collection<Object> result)
				{
					try
					{
						super.setResult((Collection<Object>)func.resultAvailable(result));
					}
					catch(Exception e)
					{
						super.setException(e);
					}
				}
				public void addIntermediateResult(Object result)
				{
					try
					{
						super.addIntermediateResult(func.intermediateResultAvailable(result));
					}
					catch(Exception e)
					{
						super.setException(e);
					}
				}
				public void setFinished()
				{
					try
					{
						func.finished((Collection<Object>)getIntermediateResults());
						super.setFinished();
					}
					catch(Exception e)
					{
						super.setException(e);
					}
				}
				public void setException(Exception exception)
				{
					super.setException(func.exceptionOccurred(exception));
				}
			};
			ret	= fut;
		}
		else if(orig instanceof ITerminableIntermediateFuture)
		{
			TerminableIntermediateDelegationFuture<Object> fut = new TerminableIntermediateDelegationFuture<Object>((ITerminableIntermediateFuture)orig)
			{
				public void	setResult(Collection<Object> result)
				{
					try
					{
						super.setResult((Collection<Object>)func.resultAvailable(result));
					}
					catch(Exception e)
					{
						super.setException(e);
					}
				}
				public void addIntermediateResult(Object result)
				{
					try
					{
						super.addIntermediateResult(func.intermediateResultAvailable(result));
					}
					catch(Exception e)
					{
						super.setException(e);
					}
				}
				public void setFinished()
				{
					try
					{
						func.finished((Collection<Object>)getIntermediateResults());
						super.setFinished();
					}
					catch(Exception e)
					{
						super.setException(e);
					}
				}
				public void setException(Exception exception)
				{
					super.setException(func.exceptionOccurred(exception));
				}
			};
//			((Future<Object>)res).addResultListener(new TerminableDelegationResultListener<Object>(fut, (ITerminableFuture)res));
			ret	= fut;
		}
		else if(orig instanceof ITerminableFuture)
		{
			TerminableDelegationFuture<Object> fut = new TerminableDelegationFuture<Object>((ITerminableFuture)orig)
			{
				public void	setResult(Object result)
				{
					try
					{
						super.setResult(func.resultAvailable(result));
					}
					catch(Exception e)
					{
						super.setException(e);
					}
				}
				
				public void setException(Exception exception)
				{
					super.setException(func.exceptionOccurred(exception));
				}
			};
//			((Future<Object>)res).addResultListener(new TerminableDelegationResultListener<Object>(fut, (ITerminableFuture)res));
			ret	= fut;
		}
		else if(orig instanceof IIntermediateFuture)
		{
			IntermediateFuture<Object>	fut	= new IntermediateFuture<Object>()
			{
				public void	setResult(Collection<Object> result)
				{
					try
					{
						super.setResult((Collection<Object>)func.resultAvailable(result));
					}
					catch(Exception e)
					{
						super.setException(e);
					}
				}
				public void addIntermediateResult(Object result)
				{
					try
					{
						super.addIntermediateResult(func.intermediateResultAvailable(result));
					}
					catch(Exception e)
					{
						super.setException(e);
					}
				}
				public void setFinished()
				{
					try
					{
						func.finished((Collection<Object>)getIntermediateResults());
						super.setFinished();
					}
					catch(Exception e)
					{
						super.setException(e);
					}
				}
				public void setException(Exception exception)
				{
					super.setException(func.exceptionOccurred(exception));
				}
			};
//			((IntermediateFuture<Object>)res).addResultListener(new IntermediateDelegationResultListener<Object>(fut));
			ret	= fut;
		}
		else
		{
			Future<Object>	fut	= new Future<Object>()
			{
				public void	setResult(Object result)
				{
					try
					{
						super.setResult(func.resultAvailable(result));
					}
					catch(Exception e)
					{
						super.setException(e);
					}
				}
				public void setException(Exception exception)
				{
					super.setException(func.exceptionOccurred(exception));
				}
			};							
//			((Future<Object>)res).addResultListener(new DelegationResultListener<Object>(fut));
			ret	= fut;
		}
		
		return ret;
	}
	
//	/**
//	 * 
//	 */
//	public static IFuture getDelegationFuture(Class<?> type, final FutureFunctionality func)
//	{
//		IFuture ret = null;
//		
//		if(SReflect.isSupertype(ISubscriptionIntermediateFuture.class, type))
//		{
//			SubscriptionIntermediateDelegationFuture<Object> fut = new SubscriptionIntermediateDelegationFuture<Object>()
//			{
//				public void	setResult(Collection<Object> result)
//				{
//					try
//					{
//						super.setResult((Collection<Object>)func.resultAvailable(result));
//					}
//					catch(Exception e)
//					{
//						super.setException(e);
//					}
//				}
//				public void addIntermediateResult(Object result)
//				{
//					try
//					{
//						super.addIntermediateResult(func.intermediateResultAvailable(result));
//					}
//					catch(Exception e)
//					{
//						super.setException(e);
//					}
//				}
//				public void setFinished()
//				{
//					try
//					{
//						func.finished();
//						super.setFinished();
//					}
//					catch(Exception e)
//					{
//						super.setException(e);
//					}
//				}
//				public void setException(Exception exception)
//				{
//					super.setException(func.exceptionOccurred(exception));
//				}
//			};
//			ret	= fut;
//		}
//		else if(SReflect.isSupertype(ITerminableIntermediateFuture.class, type))
//		{
//			TerminableIntermediateDelegationFuture<Object> fut = new TerminableIntermediateDelegationFuture<Object>()
//			{
//				public void	setResult(Collection<Object> result)
//				{
//					try
//					{
//						super.setResult((Collection<Object>)func.resultAvailable(result));
//					}
//					catch(Exception e)
//					{
//						super.setException(e);
//					}
//				}
//				public void addIntermediateResult(Object result)
//				{
//					try
//					{
//						super.addIntermediateResult(func.intermediateResultAvailable(result));
//					}
//					catch(Exception e)
//					{
//						super.setException(e);
//					}
//				}
//				public void setFinished()
//				{
//					try
//					{
//						func.finished();
//						super.setFinished();
//					}
//					catch(Exception e)
//					{
//						super.setException(e);
//					}
//				}
//				public void setException(Exception exception)
//				{
//					super.setException(func.exceptionOccurred(exception));
//				}
//			};
////			((Future<Object>)res).addResultListener(new TerminableDelegationResultListener<Object>(fut, (ITerminableFuture)res));
//			ret	= fut;
//		}
//		else if(SReflect.isSupertype(ITerminableFuture.class, type))
//		{
//			TerminableDelegationFuture<Object> fut = new TerminableDelegationFuture<Object>()
//			{
//				public void	setResult(Object result)
//				{
//					try
//					{
//						super.setResult(func.resultAvailable(result));
//					}
//					catch(Exception e)
//					{
//						super.setException(e);
//					}
//				}
//				
//				public void setException(Exception exception)
//				{
//					super.setException(func.exceptionOccurred(exception));
//				}
//			};
////			((Future<Object>)res).addResultListener(new TerminableDelegationResultListener<Object>(fut, (ITerminableFuture)res));
//			ret	= fut;
//		}
//		else if(SReflect.isSupertype(IIntermediateFuture.class, type))
//		{
//			IntermediateFuture<Object>	fut	= new IntermediateFuture<Object>()
//			{
//				public void	setResult(Collection<Object> result)
//				{
//					try
//					{
//						super.setResult((Collection<Object>)func.resultAvailable(result));
//					}
//					catch(Exception e)
//					{
//						super.setException(e);
//					}
//				}
//				public void addIntermediateResult(Object result)
//				{
//					try
//					{
//						super.addIntermediateResult(func.intermediateResultAvailable(result));
//					}
//					catch(Exception e)
//					{
//						super.setException(e);
//					}
//				}
//				public void setFinished()
//				{
//					try
//					{
//						func.finished();
//						super.setFinished();
//					}
//					catch(Exception e)
//					{
//						super.setException(e);
//					}
//				}
//				public void setException(Exception exception)
//				{
//					super.setException(func.exceptionOccurred(exception));
//				}
//			};
////			((IntermediateFuture<Object>)res).addResultListener(new IntermediateDelegationResultListener<Object>(fut));
//			ret	= fut;
//		}
//		else
//		{
//			Future<Object>	fut	= new Future<Object>()
//			{
//				public void	setResult(Object result)
//				{
//					try
//					{
//						super.setResult(func.resultAvailable(result));
//					}
//					catch(Exception e)
//					{
//						super.setException(e);
//					}
//				}
//				public void setException(Exception exception)
//				{
//					super.setException(func.exceptionOccurred(exception));
//				}
//			};							
////			((Future<Object>)res).addResultListener(new DelegationResultListener<Object>(fut));
//			ret	= fut;
//		}
//		
//		return ret;
//	}
	
}