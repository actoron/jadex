package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.IInternalProcessEngineService;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.ICommand;
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
	public void execute(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
		thread.setWaiting(true);

		final String[]	eventtypes	= (String[]) thread.getActivity().getParsedPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES);
		String	condition	= (String)thread.getActivity().getParsedPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_CONDITION);
		UnparsedExpression	upex	= null;
		Map<String, Object>	params	= null; 
		if(condition!=null)
		{
			upex	= new UnparsedExpression(null, condition);
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
		instance.getServiceContainer().searchService(IInternalProcessEngineService.class, RequiredServiceInfo.SCOPE_APPLICATION)
			.addResultListener(new IResultListener<IInternalProcessEngineService>()
		{
			public void resultAvailable(IInternalProcessEngineService ipes)
			{
				final IExternalAccess	exta	= instance.getExternalAccess();
				final String	actid	= activity.getId();
				final String	procid	= thread.getId();
				IFuture<String>	fut	= ipes.addEventMatcher(eventtypes, fupex, instance.getModel().getAllImports(), fparams, new ICommand<Object>()
				{
					public void execute(final Object event)
					{
						exta.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								IFuture<Void>	ret	= IFuture.DONE;
								
								BpmnInterpreter	instance	= (BpmnInterpreter)ia;
								MActivity	activity	= instance.getModelElement().getAllActivities().get(actid);
								StringTokenizer	stok	= new StringTokenizer(procid, ":");
								ProcessThread	thread	= null;
								ThreadContext	context	= instance.getThreadContext();
								while(stok.hasMoreTokens() && context!=null)
								{
									thread	= null;
									String	pid	= stok.nextToken();
									for(ProcessThread pt: context.getThreads())
									{
										if(pt.getId().equals(pid))
										{
											thread	= pt;
											context	= thread.getSubcontext();
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
								
								instance.notify(activity, thread, null);
								return ret;
							}
						}).addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								// done.
							}
							
							public void exceptionOccurred(Exception exception)
							{
								System.err.println("Could not notify process: "+exta.getComponentIdentifier());
								exception.printStackTrace();
							}
						});
					}
				});
				thread.setWaitInfo(fut);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				thread.setException(exception);
				instance.notify(activity, thread, null);
			}
		});
	}
	
	/**
	 *  Called when the process thread is aborted and waiting is no longer wanted.
	 */
	public void cancel(MActivity activity, final BpmnInterpreter instance, ProcessThread thread)
	{
		IFuture<String> fut = (IFuture<String>)thread.getWaitInfo();
		if(fut!=null)
		{			
			thread.setWaitInfo(null);
			
			fut.addResultListener(new IResultListener<String>()
			{
				public void resultAvailable(final String id)
				{
					instance.getServiceContainer().searchService(IInternalProcessEngineService.class, RequiredServiceInfo.SCOPE_APPLICATION)
						.addResultListener(new IResultListener<IInternalProcessEngineService>()
					{
						public void resultAvailable(IInternalProcessEngineService ipes)
						{
							ipes.removeEventMatcher(id)
								.addResultListener(new IResultListener<Void>()
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
						
						public void exceptionOccurred(Exception exception)
						{
							instance.getLogger().warning("Event deregistration failed: "+exception);
						}
					});			
				}
				
				public void exceptionOccurred(Exception exception)
				{
					instance.getLogger().warning("Event registration failed: "+exception);
				}
			});
		}		
	}
}
