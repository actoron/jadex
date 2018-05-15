package jadex.bridge.service.component.interceptors;

import java.util.Collection;
import java.util.logging.Logger;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.bridge.service.ServiceIdentifier;
import jadex.bridge.service.component.IInternalServiceMonitoringFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.ServiceCallEvent;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.ICommand;
import jadex.commons.MethodInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;

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
		final IRequiredServicesFeature	feat	= caller!=null ? caller.getComponentFeature0(IRequiredServicesFeature.class) : null;
		if(feat instanceof IInternalServiceMonitoringFeature && ((IInternalServiceMonitoringFeature)feat).isMonitoring())
		{
			if(!ServiceIdentifier.isSystemService(sic.getServiceIdentifier().getServiceType().getType(caller.getClassLoader())))
			{
				((IInternalServiceMonitoringFeature)feat).postServiceEvent(
					new ServiceCallEvent(ServiceCallEvent.Type.CALL, sic.getServiceIdentifier(), new MethodInfo(sic.getMethod()), sic.getCaller(), sic.getArguments()));
			}
		}
				
		sic.invoke().addResultListener(new DelegationResultListener<Void>(fut)
		{
			public void customResultAvailable(Void result)
			{
//				if(sic.getMethod().getName().indexOf("Void")!=-1)
//					System.out.println("decouplingret: "+sic.getArguments());
				
				final Object	res	= sic.getResult();
				
				if(res instanceof IFuture)
				{
					FutureFunctionality func = new FutureFunctionality(caller!=null ? caller.getLogger() : (Logger)null)
					{
						@Override
						public void scheduleForward(final ICommand<Void> com)
						{
							// Don't reschedule if already on correct thread.
							if(caller==null || caller.getComponentFeature(IExecutionFeature.class).isComponentThread())
							{
								com.execute(null);
							}
							else if (caller.getComponentDescription().getState().equals(IComponentDescription.STATE_TERMINATED)
									&& sic.getMethod().getName().equals("destroyComponent")
									&& sic.getArguments().size()==1 && caller!=null && caller.getComponentIdentifier().equals(sic.getArguments().get(0))) 
							{
								// do not try to reschedule if component killed itself and is already terminated to allow passing results to the original caller.
								com.execute(null);
							}
							else
							{
								try
								{
									caller.getComponentFeature(IExecutionFeature.class).scheduleStep(new ImmediateComponentStep<Void>()
									{
										public IFuture<Void> execute(IInternalAccess ia)
										{
											com.execute(null);
											return IFuture.DONE;
										}
									}).addResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result) {}
										
										public void exceptionOccurred(Exception exception)
										{
											if(exception instanceof ComponentTerminatedException)
											{
												// pass exception back to future as receiver is already dead.
												if(res instanceof ITerminableFuture<?>)
												{
													((ITerminableFuture<?>)res).terminate(exception);
												}
												else
												{
													getLogger().warning("Future receiver already dead: "+exception+", "+com+", "+res);
												}
											}
											else
											{
												// shouldn't happen?
												System.err.println("Unexpected Exception"+", "+com);
												exception.printStackTrace();
											}
										}
									});
								}
								catch(Exception e)
								{
									// shouldn't happen?
									System.err.println("Unexpected Exception");
									e.printStackTrace();
								}
							}
						}
					};
					
					@SuppressWarnings({"unchecked"})
					Future<Object> fut = (Future<Object>)FutureFunctionality.getDelegationFuture((IFuture<?>)res, func);
					sic.setResult(fut);
					
					// Monitoring below.
					if(feat instanceof IInternalServiceMonitoringFeature && ((IInternalServiceMonitoringFeature)feat).isMonitoring())
					{
						if(!ServiceIdentifier.isSystemService(sic.getServiceIdentifier().getServiceType().getType(caller.getClassLoader())))
						{
							@SuppressWarnings({"rawtypes", "unchecked"})
							IResultListener<Object>	lis = new IIntermediateResultListener()
							{
	
								@Override
								public void exceptionOccurred(Exception exception)
								{
									((IInternalServiceMonitoringFeature)feat).postServiceEvent(
										new ServiceCallEvent(ServiceCallEvent.Type.EXCEPTION, sic.getServiceIdentifier(), new MethodInfo(sic.getMethod()), sic.getCaller(), exception));
								}
	
								@Override
								public void resultAvailable(Object result)
								{
									((IInternalServiceMonitoringFeature)feat).postServiceEvent(
										new ServiceCallEvent(ServiceCallEvent.Type.RESULT, sic.getServiceIdentifier(), new MethodInfo(sic.getMethod()), sic.getCaller(), result));
								}
	
								@Override
								public void intermediateResultAvailable(Object result)
								{
									((IInternalServiceMonitoringFeature)feat).postServiceEvent(
										new ServiceCallEvent(ServiceCallEvent.Type.INTERMEDIATE_RESULT, sic.getServiceIdentifier(), new MethodInfo(sic.getMethod()), sic.getCaller(), result));
								}
	
								@Override
								public void finished()
								{
									((IInternalServiceMonitoringFeature)feat).postServiceEvent(
										new ServiceCallEvent(ServiceCallEvent.Type.FINISHED, sic.getServiceIdentifier(), new MethodInfo(sic.getMethod()), sic.getCaller(), null));
								}
	
//								Not necessary?
//								@Override
//								public void resultAvailable(Collection<Object> result)
//								{
//									((IInternalServiceMonitoringFeature)feat).postServiceEvent(
//										new ServiceCallEvent(ServiceCallEvent.Type.RESULT, sic.getServiceIdentifier(), new MethodInfo(sic.getMethod()), sic.getCaller(), result));
//								}
								
							};
							fut.addResultListener(lis);
						}
					}
				}
				
				super.customResultAvailable(null);
			}
		});
		return fut; 
	}
}
