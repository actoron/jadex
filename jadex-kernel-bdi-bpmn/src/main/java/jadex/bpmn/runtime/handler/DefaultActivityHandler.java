package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MNamedIdElement;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.IActivityHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;

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
	 *  @param context	The thread context.
	 */
	public void execute(MActivity activity, BpmnInstance instance, ProcessThread thread, ThreadContext context)
	{
		doExecute(activity, instance, thread, context);
		step(activity, instance, thread, context);
	}
	
	/**
	 *  Execute an activity. Empty default implementation.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param context	The thread context.
	 */
	protected void doExecute(MActivity activity, BpmnInstance instance, ProcessThread thread, ThreadContext context)
	{
		System.out.println("Executed: "+activity+", "+instance);
	}
	
	/**
	 *  Make a process step, i.e. find the next edge or activity for a just executed thread.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param context	The thread context.
	 */
	public void step(MActivity activity, BpmnInstance instance, ProcessThread thread, ThreadContext context)
	{
		MNamedIdElement	next;

		Exception	ex	= thread.getException();
		
		// Normal flow
		if(ex==null)
		{
			List	outgoing	= activity.getOutgoingSequenceEdges();
			if(outgoing==null || outgoing.size()==0)
			{
				next	= null;
			}
			else if(outgoing.size()==1)
			{
				next	= (MSequenceEdge)outgoing.get(0);
			}
			else
			{
				throw new UnsupportedOperationException("Activity has more than one one outgoing edge. Please overridge getOutgoingEdge() for disambiguation: "+activity);
			}
		}
		
		// Exception flow.
		else
		{
			MActivity	match	= null;
			boolean	outside	= false;
			ThreadContext	abort	= null;	// Context that needs to be aborted (if any).
			while(match==null && !outside)
			{
				List	handlers	= activity.getEventHandlers();
				for(int i=0; handlers!=null && match==null && i<handlers.size(); i++)
				{
					MActivity	handler	= (MActivity) handlers.get(i);
					if(handler.getActivityType().equals("EventIntermediateError"))
					{
						// Todo: match exception types.
//							Class	clazz	= handler.getName()!=null ? SReflect.findClass0(clname, imports, classloader);
						match	= handler;
					}
				}
				
				outside	= context.getParent()==null;	// No event handlers for top-level context (i.e. process).
				if(match==null && !outside)
				{
					activity	= (MActivity)context.getModelElement();
					abort	= context;
					context	= context.getParent();
				}
			}

			if(match!=null)
			{
				if(abort!=null)
				{
					thread	= abort.getInitiator();
					thread.setWaiting(false);
					// Todo: Callbacks for aborted threads (to abort external activities)
					context.removeSubcontext(abort);
				}

				// Todo: store exception as parameter!?
				thread.setException(null);
				next	= match;
			}
			else
			{
				throw new RuntimeException("Unhandled exception in process: "+activity, ex);
			}
		}
		
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
			context.removeThread(thread);
			
			if(context.isFinished() && context.getParent()!=null)
			{
				context.getInitiator().setWaiting(false);
				context.getParent().removeSubcontext(context);
			}
		}
	}
	
	/**
	 *  Method that should be called, when an activity is finished and the following activity should be scheduled.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param context	The thread context.
	 */
	public void	notify(MActivity activity, BpmnInstance instance, ProcessThread thread, ThreadContext context)
	{
		thread.setWaiting(false);
		step(activity, instance, thread, context);
		instance.wakeUp();
	}
}
