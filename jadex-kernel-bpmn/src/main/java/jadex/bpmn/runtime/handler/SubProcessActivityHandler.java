package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.IResultListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Handler for (embedded) sub processes.
 */
public class SubProcessActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
		MSubProcess	proc	= (MSubProcess) activity;
		List start = proc.getStartActivities();
		String	file	= (String)thread.getPropertyValue("file");
		
		IActivityHandler timerhandler = null;
		List handlers = activity.getEventHandlers();
		for(int i=0; handlers!=null && i<handlers.size(); i++)
		{
			MActivity	handler	= (MActivity)handlers.get(i);
			if(handler.getActivityType().equals("EventIntermediateTimer"))
			{
				final IActivityHandler th = instance.getActivityHandler(handler);
				break; // todo: support more than one timer?
			}
		}
		
		// Internal subprocess.
		if(start!=null && file==null)
		{
//			thread.setWaitingState(ProcessThread.WAITING_FOR_SUBPROCESS);
//			thread.setWaiting(true);
			ThreadContext subcontext = new ThreadContext(proc, thread);
			thread.getThreadContext().addSubcontext(subcontext);
			for(int i=0; i<start.size(); i++)
			{
				ProcessThread subthread = new ProcessThread((MActivity)start.get(i), subcontext, instance);
				subcontext.addThread(subthread);
			}
			
			if(timerhandler!=null)
			{
				timerhandler.execute(activity, instance, thread);
			}
			else
			{
				thread.setWaiting(true);
			}
		}
		
		// External subprocess
		else if((start==null || start.isEmpty()) && file!=null)
		{
			// Extract arguments from in/inout parameters.
			Map	args	= null;
			List params	= activity.getParameters(new String[]{MParameter.DIRECTION_IN, MParameter.DIRECTION_INOUT});
			if(params!=null && !params.isEmpty())
			{
				args	= new HashMap();
				for(int i=0; i<params.size(); i++)
				{
					MParameter	param	= (MParameter)params.get(i);
					args.put(param.getName(), thread.getParameterValue(param.getName()));
				}
			}

			IComponentManagementService cms = (IComponentManagementService)instance.getComponentAdapter()
				.getServiceContainer().getService(IComponentManagementService.class);
			
//			thread.setWaiting(true);
			cms.createComponent(null, file,
				new CreationInfo(null, args, instance.getComponentIdentifier(), false, false, false, instance.getModelElement().getAllImports()), 
			new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					// todo: save component id
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
				}
			} , 
			new IResultListener()
			{
				public void resultAvailable(Object source, final Object result)
				{
					instance.getComponentAdapter().invokeLater(new Runnable()
					{
						public void run()
						{
							// Store results in out parameters.
							Map	results	= (Map)result;
							List	params	= activity.getParameters(new String[]{MParameter.DIRECTION_OUT, MParameter.DIRECTION_INOUT});
							if(params!=null && !params.isEmpty())
							{
								for(int i=0; i<params.size(); i++)
								{
									MParameter	param	= (MParameter)params.get(i);
									if(results.containsKey(param.getName()))
									{
										thread.setParameterValue(param.getName(), results.get(param.getName()));
									}
								}
							}
							thread.setNonWaiting();
							instance.getStepHandler(activity).step(activity, instance, thread, null);
						}
					});
				}
				
				public void exceptionOccurred(Object source, final Exception exception)
				{
					instance.getComponentAdapter().invokeLater(new Runnable()
					{
						public void run()
						{
							thread.setNonWaiting();
							thread.setException(exception);
							instance.getStepHandler(activity).step(activity, instance, thread, null);
						}
					});
				}
			});
		}
		
		// Empty subprocess.
		else if((start==null || start.isEmpty()) && file==null)
		{
			// If no activity in sub process, step immediately. 
			instance.getStepHandler(activity).step(activity, instance, thread, null);
		}
		
		// Inconsistent subprocess.
		else
		{
			throw new RuntimeException("External subprocess may not have inner activities: "+activity+", "+instance);
		}
		
	}
}
