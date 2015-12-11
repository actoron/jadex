package jadex.bridge.service.component.interceptors;

import java.util.logging.Logger;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IUndoneResultListener;

/**
 *  The decoupling return interceptor ensures that the result
 *  notifications of a future a delivered on the calling 
 *  component thread.
 */
public class DecouplingReturnInterceptor extends AbstractApplicableInterceptor
{
	//-------- methods --------

	/**
	 *  Execute the interceptor.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
		Future<Void> fut	= new Future<Void>();
		
		final IInternalAccess	caller	= IInternalExecutionFeature.LOCAL.get();
				
		sic.invoke().addResultListener(new DelegationResultListener<Void>(fut)
		{
			public void customResultAvailable(Void result)
			{
//				if(sic.getMethod().getName().equals("destroyComponent"))
//					System.out.println("decouplingret: "+sic.getArguments());
				
				final Object	res	= sic.getResult();
				
				if(res instanceof IFuture)
				{
					FutureFunctionality func = new FutureFunctionality(caller!=null ? caller.getLogger() : (Logger)null)
					{
						public void terminate(Exception reason, IResultListener<Void> terminate)
						{
							// As termination is done in listener, can use same decoupling code as for listener notification.
							notifyListener(terminate);
						}
						
						public void sendForwardCommand(Object info, IResultListener<Void> com)
						{
							notifyListener(com);
						}
						
						public void sendBackwardCommand(Object info, IResultListener<Void> com)
						{
							notifyListener(com);
						}
						
						public void notifyListener(final IResultListener<Void> listener)
						{
							// Don't reschedule if already on correct thread.
							if(caller==null || caller.getComponentFeature(IExecutionFeature.class).isComponentThread())
							{
								// Is now done in future resume
//								CallAccess.setCurrentInvocation(sic.getLastServiceCall());
//								CallAccess.setLastInvocation(sic.getServiceCall());
//								CallAccess.resetNextInvocation();
								
								if(isUndone() && listener instanceof IUndoneResultListener)
								{
									((IUndoneResultListener)listener).resultAvailableIfUndone(null);
								}
								else
								{
									listener.resultAvailable(null);
								}
							}
							else
							{
								caller.getComponentFeature(IExecutionFeature.class).scheduleStep(new ImmediateComponentStep<Void>()
								{
									public IFuture<Void> execute(IInternalAccess ia)
									{
										// Is now done in future resume
//										CallAccess.setCurrentInvocation(sic.getLastServiceCall());
//										CallAccess.setLastInvocation(sic.getServiceCall());
//										CallAccess.resetNextInvocation();
										
										if(isUndone() && listener instanceof IUndoneResultListener)
										{
											((IUndoneResultListener)listener).resultAvailableIfUndone(null);
										}
										else
										{
											listener.resultAvailable(null);
										}
										return IFuture.DONE;
									}
								}).addResultListener(new IResultListener<Void>()
								{
									public void resultAvailable(Void result) {}
									
									public void exceptionOccurred(Exception exception)
									{
										// Special case: ignore ComponentTerminatedException when component has called cms.destroyComponent() for itself
										if(exception instanceof ComponentTerminatedException)
										{
											if(sic.getMethod().getName().equals("destroyComponent")
												&& sic.getArguments().size()==1 && caller!=null && caller.getComponentIdentifier().equals(sic.getArguments().get(0)))
											{
												// ignore
											}
											else
											{
												// pass exception back to future functionality as receiver is already dead.
												listener.exceptionOccurred(exception);
											}
										}
										else
										{
											// shouldn't happen?
											exception.printStackTrace();
										}
									}
								});
							}
						}
						
						/**
						 *  For intermediate results this method is called.
						 */
						public void startScheduledNotifications(IResultListener<Void> notify)
						{
							notifyListener(notify);
						}
					};
					
					Future<?> fut = FutureFunctionality.getDelegationFuture((IFuture<?>)res, func);
					sic.setResult(fut);
				}
				
				super.customResultAvailable(null);
			}
		});
		return fut; 
	}
}
