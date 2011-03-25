package jadex.bridge.service.component;

import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 * 
 */
public class RecoverServiceInterceptor extends AbstractApplicableInterceptor
{
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture execute(final ServiceInvocationContext sic)
	{
		final Future ret = new Future();
//		sic.invoke().addResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				Object res = sic.getResult();
//				if(res instanceof IFuture)
//				{
//					((IFuture)res).addResultListener(new DelegationResultListener(ret)
//					{
//						public void exceptionOccurred(Exception exception)
//						{
//							fetcher.getService(info, binding, ia.getServiceProvider(), false)
//								.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
//							{
//								public void customResultAvailable(Object result) 
//								{
//									// Note: this will not be executed remotely but on the component 
//									sic.setObject(result);
//									sic.invoke().addResultListener(new DelegationResultListener(ret));
//								}
//							}));
//							super.exceptionOccurred(exception);
//						}
//					});
//				}
//				else
//				{
//					ret.setResult(null);
//				}
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				
//			}
//		});
		return ret;
	}
	
}
