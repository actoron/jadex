package jadex.bridge.service.component.interceptors;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

/**
 *  The decoupling return interceptor ensures that the result
 *  notifications of a future a delivered on the calling 
 *  component thread.
 */
public class DecouplingReturnInterceptor extends AbstractApplicableInterceptor
{
	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;	
		
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public DecouplingReturnInterceptor(IExternalAccess ea, IComponentAdapter adapter)
	{
		assert ea!=null;
		assert adapter!=null;
		this.ea = ea;
		this.adapter = adapter;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
		Future<Void> fut	= new Future<Void>();
		sic.invoke().addResultListener(new DelegationResultListener<Void>(fut)
		{
			public void customResultAvailable(Void result)
			{
				Object	res	= sic.getResult();
				
				if(res instanceof IFuture)
				{
					FutureFunctionality func = new FutureFunctionality()
					{
						public IFuture<Void> notifyListener(IResultListener listener) 
						{
							final Future<Void> ret = new Future<Void>();
							// Hack!!! Notify multiple listeners at once?
							if(adapter.isExternalThread())
							{
								try
								{
									ea.scheduleStep(new IComponentStep<Void>()
									{
										public IFuture<Void> execute(IInternalAccess ia)
										{
											ret.setResult(null);
//											ComponentIntermediateFuture.super.notifyListener(listener);
											return IFuture.DONE;
										}
									});
								}
								catch(ComponentTerminatedException e)
								{
//									ComponentIntermediateFuture.super.notifyListener(listener);
									ret.setResult(null);
								}
							}
							else
							{
								ret.setResult(null);
//								super.notifyListener(listener);
							}
							
							return ret;
						};
						
						public IFuture<Void> notifyIntermediateResult(IIntermediateResultListener<Object> listener, Object result)
						{
							final Future<Void> ret = new Future<Void>();
							// Hack!!! Notify multiple results at once?
							if(adapter.isExternalThread())
							{
								try
								{
									ea.scheduleStep(new IComponentStep<Void>()
									{
										public IFuture<Void> execute(IInternalAccess ia)
										{
											ret.setResult(null);
//											ComponentIntermediateFuture.super.notifyIntermediateResult(listener, result);
											return IFuture.DONE;
										}
									});
								}
								catch(ComponentTerminatedException e)
								{
									ret.setException(e);
//									ComponentIntermediateFuture.super.notifyListener(listener);
								}				
							}
							else
							{
								ret.setResult(null);
//								super.notifyIntermediateResult(listener, result);
							}
							
							return ret;
						}
					};
					
					Future<?> fut = FutureFunctionality.getDelegationFuture((IFuture)res, func);
					sic.setResult(fut);
//					if(res instanceof IIntermediateFuture)
//					{
//						sic.setResult(new ComponentIntermediateFuture(ea, adapter, (IIntermediateFuture)res));
//					}
//					else if(res instanceof IFuture)
//					{
//						sic.setResult(new ComponentFuture(ea, adapter, (IFuture)res));
//					}
				}
				super.customResultAvailable(null);
			}
		});
		return fut; 
	}
}
