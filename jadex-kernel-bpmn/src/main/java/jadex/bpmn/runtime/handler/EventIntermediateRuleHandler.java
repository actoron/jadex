package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.IInternalProcessEngineService;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.IResultCommand;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *  Wait for an external notification (could be a signal or a fired rule).
 *  The event is registered at the process engine service of the application.
 */
public class EventIntermediateRuleHandler extends DefaultActivityHandler
{
	//-------- methods --------
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
		thread.setWaiting(true);

		final String[]	eventtypes	= (String[]) thread.getActivity().getParsedPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES);
		UnparsedExpression	upex	= thread.getActivity().getPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_CONDITION);
		Map<String, Object>	params	= null; 
		if(upex!=null)
		{
			IParsedExpression	exp	= SJavaParser.parseExpression(upex, instance.getModel().getAllImports(), instance.getClassLoader());
			for(String param: exp.getParameters())
			{
				Object	val	= thread.getParameterValue(param);
				if(val!=null)	// omit null values (also excludes '$event')
				{
					if(params==null)
					{
						params	= new LinkedHashMap<String, Object>();
					}
					params.put(param, thread.getParameterValue(param));
				}
			}
		}
		
		final UnparsedExpression	fupex	= upex;
		final Map<String, Object>	fparams	= params;
		
		// Todo: allow injecting service binding from outside?
		IInternalProcessEngineService ipes = SServiceProvider.getLocalService(instance, IInternalProcessEngineService.class, RequiredServiceInfo.SCOPE_APPLICATION);
		
		final IExternalAccess	exta	= instance.getExternalAccess();
		final String	actid	= activity.getId();
		final String	procid	= thread.getId();
		
//				System.out.println("Adding event matcher: "+instance.getComponentIdentifier());
		
		final IComponentIdentifier	cid	= instance.getComponentIdentifier();
		final IFuture<String>	fut	= ipes.addEventMatcher(eventtypes, fupex, instance.getModel().getAllImports(), fparams, true, new IResultCommand<IFuture<Void>, Object>()
		{
			public IFuture<Void> execute(final Object event)
			{
//						System.out.println("Triggered event matcher: "+cid);
				
				return exta.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						IFuture<Void>	ret	= IFuture.DONE;
						
						MActivity	activity	= ((MBpmnModel)ia.getModel().getRawModel()).getAllActivities().get(actid);
						StringTokenizer	stok	= new StringTokenizer(procid, ":");
						ProcessThread	thread	= null;
//								ThreadContext	context	= instance.getThreadContext();
						ProcessThread context = getBpmnFeature(ia).getTopLevelThread();
						String	pid	= null;
						while(stok.hasMoreTokens() && context!=null)
						{
							thread	= null;
							pid	= pid!=null ? pid+":"+stok.nextToken() : stok.nextToken();
							for(ProcessThread pt: context.getSubthreads())
							{
								if(pt.getId().equals(pid))
								{
									thread	= pt;
									context	= thread;
									break;
								}
							}
						}
						
						if(activity==null)
						{
							ret	= new Future<Void>(new RuntimeException("Activity not found: "+actid));
						}
						else if(thread==null)
						{
							ret	= new Future<Void>(new RuntimeException("Process thread not found: "+procid));
						}
						
						getBpmnFeature(ia).notify(activity, thread, event);
						return ret;
					}
				});
			}
		});
		
		ICancelable ca = new ICancelable()
		{
			public IFuture<Void> cancel()
			{
				final Future<Void> ret = new Future<Void>();
				fut.addResultListener(new ExceptionDelegationResultListener<String, Void>(ret)
				{
					public void customResultAvailable(final String id)
					{
						IInternalProcessEngineService ipes = SServiceProvider.getLocalService(instance, IInternalProcessEngineService.class, RequiredServiceInfo.SCOPE_APPLICATION);
						
						System.out.println("Cancel event matcher1: "+instance.getComponentIdentifier());
						
						ipes.removeEventMatcher(id).addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								// done.
							}
							
							public void exceptionOccurred(Exception exception)
							{
								instance.getLogger().warning("Event deregistration failed: "+exception);
							}
						});
					}
				});
				return ret;
			}
		};
		
		thread.setWaitInfo(ca);
	}
	
//	/**
//	 *  Called from execute when service is not available.
//	 */
//	protected void processEngineNotFound(MActivity activity, BpmnInterpreter instance, ProcessThread thread, Exception exception)
//	{
//		thread.setException(exception);
//		instance.notify(activity, thread, null);
//	}
	
//	/**
//	 *  Called when the process thread is aborted and waiting is no longer wanted.
//	 */
//	public void cancel(MActivity activity, final BpmnInterpreter instance, ProcessThread thread)
//	{
//		System.out.println("Cancel event matcher: "+instance.getComponentIdentifier());
//		
//		IFuture<String> fut = (IFuture<String>)thread.getWaitInfo();
//		if(fut!=null)
//		{			
//			thread.setWaitInfo(null);
//			
//			fut.addResultListener(new IResultListener<String>()
//			{
//				public void resultAvailable(final String id)
//				{
//					instance.getServiceContainer().searchService(IInternalProcessEngineService.class, RequiredServiceInfo.SCOPE_APPLICATION)
//						.addResultListener(new IResultListener<IInternalProcessEngineService>()
//					{
//						public void resultAvailable(IInternalProcessEngineService ipes)
//						{
//							System.out.println("Cancel event matcher1: "+instance.getComponentIdentifier());
//							
//							ipes.removeEventMatcher(id).addResultListener(new IResultListener<Void>()
//							{
//								public void resultAvailable(Void result)
//								{
//									// done.
//								}
//								
//								public void exceptionOccurred(Exception exception)
//								{
//									instance.getLogger().warning("Event deregistration failed: "+exception);
//								}
//							});
//						}
//						
//						public void exceptionOccurred(Exception exception)
//						{
//							instance.getLogger().warning("Event deregistration failed: "+exception);
//						}
//					});			
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					instance.getLogger().warning("Event registration failed: "+exception);
//				}
//			});
//		}		
//	}
}
