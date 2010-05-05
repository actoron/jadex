package jadex.bpmn.runtime.handler;

import java.util.List;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MNamedIdElement;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.IStepHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;

/**
 *  Handles the transition of steps.
 */
public class DefaultStepHandler implements IStepHandler
{
	/**
	 *  Make a process step, i.e. find the next edge or activity for a just executed thread.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void step(MActivity activity, BpmnInterpreter instance, ProcessThread thread, Object event)
	{
		// Hack!!! Should be in interpreter/thread?
		thread.updateParametersAfterStep(activity, instance);
		
		MNamedIdElement	next	= null;
		ThreadContext	remove	= null;	// Context that needs to be removed (if any).
		Exception ex = thread.getException();
		
		// Store event (if any).
		if(event!=null)
		{
			thread.setParameterValue("$event", event);
//			System.out.println("Event: "+activity+" "+thread+" "+event);
		}
		
		// Timer occurred flow
		if(event==null && ex==null && activity instanceof MSubProcess)
		{
			// Cancel subflows.
			remove = thread.getThreadContext();
			
			// Continue with timer edge.
			List	handlers	= activity.getEventHandlers();
			for(int i=0; handlers!=null && next==null && i<handlers.size(); i++)
			{
				MActivity handler	= (MActivity) handlers.get(i);
				if(handler.getActivityType().equals("EventIntermediateTimer"))
				{
					List outedges = handler.getOutgoingSequenceEdges();
					if(outedges==null || outedges.size()!=1)
					{
						throw new RuntimeException("Cannot determine outgoing edge: "+handler);
					}
					next = (MSequenceEdge)outedges.get(0);
				}
			}	
		}
		
		// Find next element and context(s) to be removed.
		else
		{
			boolean	outside	= false;
			ThreadContext	context	= thread.getThreadContext();
			while(next==null && !outside)
			{
				// Normal flow
				if(ex==null)
				{
					List	outgoing	= activity.getOutgoingSequenceEdges();
					if(outgoing!=null && outgoing.size()==1)
					{
						next	= (MSequenceEdge)outgoing.get(0);
					}
					else if(outgoing!=null && outgoing.size()>1)
					{
						throw new UnsupportedOperationException("Activity has more than one one outgoing edge. Please overridge step() for disambiguation: "+activity);
					}
					// else no outgoing edge -> check parent context, if any.
				}
			
				// Exception flow.
				else
				{
					List	handlers	= activity.getEventHandlers();
					for(int i=0; handlers!=null && next==null && i<handlers.size(); i++)
					{
						MActivity	handler	= (MActivity) handlers.get(i);
						if(handler.getActivityType().equals("EventIntermediateError"))
						{
							// Todo: match exception types.
	//						Class	clazz	= handler.getName()!=null ? SReflect.findClass0(clname, imports, classloader);
							next	= handler;
						}
					}
				}
			
				outside	= context.getParent()==null;
				if(next==null && !outside)
				{
					// When last thread or exception, mark current context for removal.
					if(context.getThreads().size()==1 || ex!=null)
					{
						activity	= (MActivity)context.getModelElement();
						remove	= context;
						context	= context.getParent();
					}
					
					// If more threads are available in current context just exit loop.
					else if(context.getThreads().size()>1)
					{
						outside	= true;
					}
				}
			}
		}

		// Remove inner context(s), if any.
		if(remove!=null)
		{
			thread	= remove.getInitiator();
			thread.setNonWaiting();
			// Todo: Callbacks for aborted threads (to abort external activities)
			thread.getThreadContext().removeSubcontext(remove);
		}

		if(next!=null)
		{

			// Todo: store exception as parameter!?
			if(ex!=null)
				thread.setException(null);
		}
		else if(ex!=null)
		{
			if(ex instanceof RuntimeException)
				throw (RuntimeException)ex;
			else
				throw new RuntimeException("Unhandled exception in process: "+activity, ex);
		}
		
		// Perform step settings, i.e. set next edge/activity or remove thread.
		if(next instanceof MSequenceEdge)
		{
			thread.setLastEdge((MSequenceEdge)next);
		}
		else if(next instanceof MActivity)
		{
			thread.setActivity((MActivity)next);
		}
		else if(next==null)
		{
			thread.getThreadContext().removeThread(thread);
		} 
		else
		{
			throw new UnsupportedOperationException("Unknown outgoing element type: "+next);
		}
	}
}
