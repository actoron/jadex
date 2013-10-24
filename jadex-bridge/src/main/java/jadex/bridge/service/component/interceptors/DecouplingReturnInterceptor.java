package jadex.bridge.service.component.interceptors;

import jadex.base.Starter;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  The decoupling return interceptor ensures that the result
 *  notifications of a future a delivered on the calling 
 *  component thread.
 */
public class DecouplingReturnInterceptor extends AbstractApplicableInterceptor
{
	//-------- attributes --------
	
//	/** The external access. */
//	protected IExternalAccess ea;	
//		
//	/** The component adapter. */
//	protected IComponentAdapter adapter;
	
//	/** The thread pool. */
//	protected IThreadPoolService tp;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 *  @param tp the rescue thread pool (if any).
	 */
//	public DecouplingReturnInterceptor(/*IExternalAccess ea, IComponentAdapter adapter,*/ IThreadPoolService tp)
	public DecouplingReturnInterceptor()
	{
//		assert ea!=null;
//		this.ea = ea;
//		this.adapter = adapter;
//		this.tp = tp;
	}
	
	//-------- methods --------

	/**
	 *  Execute the interceptor.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
		Future<Void> fut	= new Future<Void>();
		
		final IComponentAdapter	ada	= /*adapter!=null ? adapter :*/ sic.getCallerAdapter();
		final IComponentIdentifier caller = IComponentIdentifier.LOCAL.get();
		
//		// Todo: disallow component methods being called from wrong thread and afterwards remove adapter attribute.
//		if(adapter!=null &&! adapter.equals(sic.getCallerAdapter()))
//		{
//			System.out.println("sdifzgio sd");
//		}
//		assert adapter==null || adapter.equals(sic.getCallerAdapter()): adapter+", "+sic.getCallerAdapter();
//		if(ada!=null && caller!=null && !ada.getComponentIdentifier().equals(caller))
//		{
//			System.out.println("klsdj gkl: "+ada+", "+caller);
//		}
		assert ada==null || caller==null || ada.getComponentIdentifier().equals(caller): ada+", "+caller;

//		if(ada!=null && caller!=null && !caller.getPlatformName().equals(ea.getComponentIdentifier().getPlatformName()))
//		{
//			System.out.println("sdilufsgi: "+ada+", "+caller+", "+ea.getComponentIdentifier());
//		}
//		assert	ada==null || caller==null || caller.getPlatformName().equals(ea.getComponentIdentifier().getPlatformName()) : caller+", "+ea.getComponentIdentifier();  

		sic.invoke().addResultListener(new DelegationResultListener<Void>(fut)
		{
			public void customResultAvailable(Void result)
			{
//				final boolean	destroy	= sic.getMethod().getName().toString().indexOf("destroyComponent")!=-1;
				
				Object	res	= sic.getResult();
				
				if(res instanceof IFuture)
				{
					FutureFunctionality func = new FutureFunctionality(ada!=null ? ada.getLogger() : null)
					{
						public void terminate(Exception reason, IResultListener<Void> terminate)
						{
							// As termination is done in listener, can use same decoupling code as for listener notification.
							notifyListener(terminate);
						}
						
						public void notifyListener(final IResultListener<Void> listener)
						{
//							if(sic.getMethod().getName().indexOf("method")!=-1)
//								System.out.println("sjdjfhsdfhj");
							
							// Don't reschedule if already on correct thread or called from remote.
							if(ada==null || !ada.isExternalThread())
//								|| !caller.getPlatformName().equals(ea.getComponentIdentifier().getPlatformName()) )
							{
//								if(sic.getMethod().getName().indexOf("test")!=-1)
//									System.out.println("setting to: "+sic.getLastServiceCall());
								CallAccess.setServiceCall(sic.getLastServiceCall());
								listener.resultAvailable(null);
							}
							else
							{
								try
								{
									ada.invokeLater(new Runnable()
									{
										public void run()
										{
//											if(ada.getComponentIdentifier().getName().indexOf("rms")!=-1)
////												ada.getDescription().getModelName().indexOf("testcases.threading")!=-1)
//											{
//												System.out.println("resched: "+sic.getMethod().getName()+", "+System.currentTimeMillis());
//											}
//											if(sic.getMethod().getName().indexOf("test")!=-1)
//												System.out.println("setting to d: "+sic.getLastServiceCall());
											CallAccess.setServiceCall(sic.getLastServiceCall());
											listener.resultAvailable(null);
										}
									});
								}
								catch(ComponentTerminatedException e)
								{
									// Special case: ignore reschedule failure when component has called cms.destroyComponent() for itself
									if(sic.getMethod().getName().equals("destroyComponent")
										&& sic.getArguments().size()==1 && caller!=null && caller.equals(sic.getArguments().get(0)))
									{
										Runnable run = new Runnable()
										{
											public void run() 
											{
												listener.resultAvailable(null);
											}
										};
										
										if(caller.getParent()==null)
										{
											// If destroy of platform, run directly as rescue thread already shut down.
											run.run();
										}
										else
										{
											Starter.scheduleRescueStep(sic.getCallerAdapter().getComponentIdentifier(), run);
										}
									}
									else
									{
										// pass exception back to result provider as receiver is already dead.
										throw e;
//										listener.exceptionOccurred(new ComponentTerminatedException(ada.getComponentIdentifier(), "Cannot reschedule "+sic+": "+e));
									}
								}
//								catch(final Exception e)
//								{
//									Runnable run = new Runnable()
//									{
//										public void run() 
//										{
////											System.out.println("out ex2: "+mycnt);
//											
//											// Special case: ignore reschedule failure when component has called cms.destroyComponent() for itself
//											if(sic.getMethod().getName().equals("destroyComponent")
//												&& sic.getArguments().size()==1 && caller!=null && caller.equals(sic.getArguments().get(0)))
//											{
////												System.out.println("Rescheduled to rescue thread1: "+e+", "+sic);
//												listener.resultAvailable(null);
//											}
//											else
//											{
////												System.out.println("Rescheduled to rescue thread2: "+e+", "+sic+", "+this);
////												Thread.dumpStack();
//												listener.exceptionOccurred(new ComponentTerminatedException(ada.getComponentIdentifier(), "Cannot reschedule "+sic+": "+e));
//											}
//										}
//									};
//									Starter.scheduleRescueStep(sic.getCallerAdapter().getComponentIdentifier(), run);
////									if(tp!=null)
////									{
////										// Hack!!! Thread pool service should be asynchronous.
////										try
////										{
////											tp.execute(run);
////										}
////										catch(RuntimeException re)
////										{
////											// Happens when thread pool already terminated.
////											Thread t = new Thread(run);
////											t.start();																
////										}
////									}
////									else
////									{
////										Thread t = new Thread(run);
////										t.start();
////									}
//								}
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
	
//	/**
//	 *  Get or find the component adapter.
//	 */
//	public IFuture<IComponentAdapter> getAdapter(final IComponentIdentifier caller, final ServiceInvocationContext sic)
//	{
//		final Future<IComponentAdapter> ret = new Future<IComponentAdapter>();
//		
//		if(adapter==null)
//		{
//			if(caller!=null)
//			{
////				if(caller.toString().indexOf("ServiceCall")!=-1)
////					System.out.println("sdfkl j");
//				
//				SServiceProvider.getServiceUpwards(ea.getServiceProvider(), IComponentManagementService.class)
//					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentAdapter>(ret)
//				{
//					public void customResultAvailable(IComponentManagementService cms)
//					{
////						if(sic.getMethod().getName().indexOf("getComponentAdapter")!=-1)
////							System.out.println("back to: "+caller+" from "+IComponentIdentifier.LOCAL.get()+" for: "+sic.getMethod()+" "+sic.getArguments());
//						
//						cms.getComponentAdapter(caller)
//							.addResultListener(new DelegationResultListener<IComponentAdapter>(ret)
//						{
//							public void customResultAvailable(IComponentAdapter result)
//							{
//								if(result!=null)
//								{
//									super.customResultAvailable(result);
//								}
//								else
//								{
//									ret.setException(new RuntimeException("No adapter"));									
//								}
//							}
//						});
//					}
//				});
//			}
//			else
//			{
//				ret.setException(new RuntimeException("No adapter"));
//			}
//		}
//		else
//		{
//			ret.setResult(adapter);
//		}
//		
//		return ret;
//	}
	
//	/**
//	 *  Get or find the external access.
//	 */
//	public IFuture<IExternalAccess> getExternalAccess(final IComponentIdentifier caller)
//	{
//		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
//		
//		if(adapter==null)
//		{
//			if(caller!=null)
//			{
//				SServiceProvider.getServiceUpwards(ea.getServiceProvider(), IComponentManagementService.class)
//					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IExternalAccess>(ret)
//				{
//					public void customResultAvailable(IComponentManagementService cms)
//					{
//						System.out.println("found: "+cms.getComponentAdapter(caller)+" for: "+caller);
//						cms.getExternalAccess(caller).addResultListener(new DelegationResultListener<IExternalAccess>(ret));
//					}
//				});
//			}
//			else
//			{
//				ret.setException(new RuntimeException("No external access"));
//			}
//		}
//		else
//		{
//			ret.setResult(ea);
//		}
//		
//		return ret;
//	}
}
