package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.IInternalProcessEngineService;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.ICommand;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.kernelbase.InterpreterFetcher;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  Wait for an external notification (could be a signal or a fired rule).
 *  Makes available a notfier object as "notifier" property.
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

		String[]	eventtypes	= (String[]) thread.getActivity().getParsedPropertyValue(MBpmnModel.PROPERTY_EVENT_RULE_EVENTTYPES);
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
		
		// Todo: allow injecting service binding from outside?
		instance.getServiceContainer().searchService(IInternalProcessEngineService.class, RequiredServiceInfo.SCOPE_APPLICATION)
			.addResultListener(new IResultListener<IInternalProcessEngineService>()
		{
			public void resultAvailable(IInternalProcessEngineService ipes)
			{
				final IExternalAccess	exta	= instance.getExternalAccess(); 
//				IFuture<String>	fut	= ipes.addEventMatcher(eventtypes, upex, params, instance.getModel().getAllImports(), new ICommand<Object>()
//				{
//					public void execute(final Object event)
//					{
//						exta.scheduleStep(step);
//					}
//				});
//				thread.setWaitInfo(fut);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				thread.setNonWaiting();
				thread.setException(exception);
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
