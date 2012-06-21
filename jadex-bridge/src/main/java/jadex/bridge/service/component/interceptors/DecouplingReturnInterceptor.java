package jadex.bridge.service.component.interceptors;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
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
//		assert adapter!=null;
		this.ea = ea;
		this.adapter = adapter;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
//	static int cnt;
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
		Future<Void> fut	= new Future<Void>();
//		final int mycnt=cnt++;
//		System.out.println("in:"+mycnt+" "+sic.getMethod().getName()+" "+sic.getArguments());
		
		final IComponentIdentifier caller = IComponentIdentifier.LOCAL.get();
		
		sic.invoke().addResultListener(new DelegationResultListener<Void>(fut)
		{
			public void customResultAvailable(Void result)
			{
				Object	res	= sic.getResult();
				
				if(res instanceof IFuture)
				{
					FutureFunctionality func = new FutureFunctionality()
					{
						public void notifyListener(final IResultListener<Void> listener)
						{
							// Do not reschedule remotely
							if(adapter!=null || caller!=null && caller.getPlatformName().equals(ea.getComponentIdentifier().getPlatformName()))
							{
								getAdapter(caller, sic).addResultListener(new IResultListener<IComponentAdapter>()
								{
									public void resultAvailable(IComponentAdapter adapter) 
									{
										// Hack!!! Notify multiple listeners at once?
										if(adapter.isExternalThread())
										{
											try
											{
												adapter.invokeLater(new Runnable()
												{
													public void run()
													{
//														System.out.println("out re: "+mycnt);
														listener.resultAvailable(null);
													}
												});
//												getExternalAccess(caller).addResultListener(new IResultListener<IExternalAccess>()
//												{
//													public void resultAvailable(IExternalAccess ea)
//													{
//														ea.scheduleStep(new IComponentStep<Void>()
//														{
//															public IFuture<Void> execute(IInternalAccess ia)
//															{
//																System.out.println("out re: "+mycnt);
//																ret.setResult(null);
//																return IFuture.DONE;
//															}
//														});
//													}
//													
//													public void exceptionOccurred(Exception exception)
//													{
//														System.out.println("out ex1: "+mycnt);
//														ret.setResult(null);
//													}
//												});
												
											}
											catch(Exception e)
											{
//												System.out.println("out ex2: "+mycnt);
												listener.exceptionOccurred(e);
											}
										}
										else
										{
//											System.out.println("out nore: "+mycnt);
											listener.resultAvailable(null);
										}
									};
									
									public void exceptionOccurred(Exception exception) 
									{
//										System.out.println("out norepos: "+mycnt);
										// No thread conversion possible
										listener.exceptionOccurred(exception);
									};
								});
							}
							else
							{
								listener.resultAvailable(null);
							}
						};
						
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
//					if(res instanceof IIntermediateFuture)
//					{
//						sic.setResult(new ComponentIntermediateFuture(ea, adapter, (IIntermediateFuture)res));
//					}
//					else if(res instanceof IFuture)
//					{
//						sic.setResult(new ComponentFuture(ea, adapter, (IFuture)res));
//					}
				}
//				else
//				{
//					System.out.println("out nofut: "+mycnt);
//				}
				
				super.customResultAvailable(null);
			}
		});
		return fut; 
	}
	
	/**
	 *  Get or find the component adapter.
	 */
	public IFuture<IComponentAdapter> getAdapter(final IComponentIdentifier caller, final ServiceInvocationContext sic)
	{
		final Future<IComponentAdapter> ret = new Future<IComponentAdapter>();
		
		if(adapter==null)
		{
			if(caller!=null)
			{
				SServiceProvider.getServiceUpwards(ea.getServiceProvider(), IComponentManagementService.class)
					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentAdapter>(ret)
				{
					public void customResultAvailable(IComponentManagementService cms)
					{
//						if(sic.getMethod().getName().indexOf("createComponent")!=-1)
//							System.out.println("back to: "+caller+" for: "+sic.getMethod()+" "+sic.getArguments());
						
						IComponentAdapter adap = cms.getComponentAdapter(caller);
						if(adap!=null)
						{
							ret.setResult(adap);
						}
						else
						{
							ret.setException(new RuntimeException("No adapter"));
						}
					}
				});
			}
			else
			{
				ret.setException(new RuntimeException("No adapter"));
			}
		}
		else
		{
			ret.setResult(adapter);
		}
		
		return ret;
	}
	
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
