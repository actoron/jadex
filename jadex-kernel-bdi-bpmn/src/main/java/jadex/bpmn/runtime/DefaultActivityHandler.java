package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MNamedIdElement;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;

import java.util.List;


/**
 *  Default activity handler, which provides some
 *  useful helper methods.
 */
public class DefaultActivityHandler implements IActivityHandler
{	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		doExecute(activity, instance, thread);
		step(activity, instance, thread);
	}
	
	/**
	 *  Execute an activity. Empty default implementation.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	protected void doExecute(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		System.out.println("Executed: "+activity+", "+instance);
	}
	
	/**
	 *  Make a process step.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void step(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		MNamedIdElement	next	= getOutgoingElement(activity, instance, thread);
		if(next instanceof MSequenceEdge)
		{
			thread.setLastEdge((MSequenceEdge) next);
		}
		else if(next instanceof MActivity)
		{
			thread.setNextActivity((MActivity) next);
		}
		else if(next!=null)
		{
			throw new UnsupportedOperationException("Unknown outgoing element type: "+next);
		}
		else
		{
			ThreadContext	context	= thread.getThreadContext();
			context.removeThread(thread);
			
			if(context.isFinished() && context.getParent()!=null)
			{
				context.getInitiator().setWaiting(false);
				context.getParent().removeSubcontext(context);
			}
		}
	}
	
	/**
	 *  Get the outgoing edge or activity.
	 *  @param activity	The current activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @return The outgoing edge or activity or null if the thread is finished.
	 */
	protected MNamedIdElement	getOutgoingElement(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		MNamedIdElement	ret;
		
		// Normal flow
		if(thread.getException()==null)
		{
			List	outgoing	= activity.getOutgoingSequenceEdges();
			if(outgoing==null || outgoing.size()==0)
			{
				ret	= null;
			}
			else if(outgoing.size()==1)
			{
				ret	= (MSequenceEdge)outgoing.get(0);
			}
			else
			{
				throw new UnsupportedOperationException("Activity has more than one one outgoing edge. Please overridge getOutgoingEdge() for disambiguation: "+activity);
			}
		}
		
		// Exception flow.
		else if(activity instanceof MSubProcess)
		{
			// Todo: save stack frames (i.e. nested sub processes) in process thread.
			List	handlers	= ((MSubProcess)activity).getEventHandlers();
			MActivity	match	= null;
			for(int i=0; handlers!=null && match==null && i<handlers.size(); i++)
			{
				MActivity	handler	= (MActivity) handlers.get(i);
				if(handler.getActivityType().equals("EventIntermediateError"))
				{
					// Todo: match exception types.
//					Class	clazz	= handler.getName()!=null ? SReflect.findClass0(clname, imports, classloader);
					match	= handler;
				}
			}

			if(match!=null)
			{
				// Todo: store exception as parameter!?
				thread.setException(null);
				ret	= match;
			}
			else
			{
				throw new UnsupportedOperationException("Subsequent activity after exception cannot be determined: "+activity);
			}
		}

		else
		{
			throw new UnsupportedOperationException("Subsequent activity after exception cannot be determined: "+activity, thread.getException());
		}

		return ret; 
	}
	
	/**
	 *  Method that should be called, when an activity is finished and the following activity should be scheduled.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void	notify(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		thread.setWaiting(false);
		step(activity, instance, thread);
		instance.wakeUp();
	}
}
