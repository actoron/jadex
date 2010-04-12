package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.IResultListener;

import java.util.List;

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
		
		// Internal subprocess.
		if(start!=null && file==null)
		{
//			thread.setWaitingState(ProcessThread.WAITING_FOR_SUBPROCESS);
			thread.setWaiting(true);
			ThreadContext	subcontext	= new ThreadContext(proc, thread);
			thread.getThreadContext().addSubcontext(subcontext);
			for(int i=0; i<start.size(); i++)
			{
				ProcessThread	newthread	= new ProcessThread((MActivity)start.get(i), subcontext, instance);
				subcontext.addThread(newthread);
			}
		}
		
		// External subprocess
		else if((start==null || start.isEmpty()) && file!=null)
		{
			thread.setWaiting(true);
			IComponentManagementService	cms	= (IComponentManagementService)instance.getComponentAdapter()
				.getServiceContainer().getService(IComponentManagementService.class);
			
			cms.createComponent(null, file,
				new CreationInfo(null, null, instance.getComponentIdentifier(), false, false, false, instance.getModelElement().getAllImports()), null, new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					instance.getComponentAdapter().invokeLater(new Runnable()
					{
						public void run()
						{
							// Todo: store results.
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
