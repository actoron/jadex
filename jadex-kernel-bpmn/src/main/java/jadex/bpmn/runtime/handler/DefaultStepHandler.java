package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MNamedIdElement;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.IStepHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;
import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.IComponentChangeEvent;

import java.util.Iterator;
import java.util.List;

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
		assert !instance.getComponentAdapter().isExternalThread();
		
//		System.out.println(instance.getComponentIdentifier().getLocalName()+": step "+activity+", data "+thread.getData());
		
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
		if(AbstractEventIntermediateTimerActivityHandler.TIMER_EVENT.equals(event))
		{
			// Cancel subflows.
			remove = thread.getThreadContext().getSubcontext(thread);
			
			// Continue with timer edge.
			List outedges = activity.getOutgoingSequenceEdges();
			if(outedges!=null && outedges.size()==1)
			{
				next = (MSequenceEdge)outedges.get(0);
			}
			else if(outedges!=null && outedges.size()>1)
			{
				throw new RuntimeException("Cannot determine outgoing edge: "+activity);
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
						if(handler.getActivityType().equals(MBpmnModel.EVENT_INTERMEDIATE_ERROR))//"EventIntermediateError"))
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
						activity = (MActivity)context.getModelElement();
						remove	= context;
						context	= context.getParent();
						
						// Cancel subprocess handlers.
						if(activity instanceof MSubProcess)
						{
							List	handlers	= activity.getEventHandlers();
							for(int i=0; handlers!=null && i<handlers.size(); i++)
							{
								MActivity handler = (MActivity)handlers.get(i);
								instance.getActivityHandler(handler).cancel(handler, instance, remove.getInitiator());
							}
						}
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
			if(ex!=null)
				thread.setException(ex);
			// Todo: Callbacks for aborted threads (to abort external activities)
			thread.getThreadContext().removeSubcontext(remove);
			
			for(Iterator it=remove.getAllThreads().iterator(); it.hasNext(); )
			{
				ProcessThread	pt	= (ProcessThread)it.next();
				ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL, BpmnInterpreter.TYPE_THREAD, pt.getClass().getName(), 
					pt.getId(), instance.getComponentIdentifier(), instance.getComponentDescription().getCreationTime(), instance.createProcessThreadInfo(pt));
				instance.notifyListeners(cce);
//				instance.notifyListeners(BpmnInterpreter.EVENT_THREAD_REMOVED, (ProcessThread)it.next());
			}
			ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_MODIFICATION, BpmnInterpreter.TYPE_THREAD, thread.getClass().getName(), 
				thread.getId(), instance.getComponentIdentifier(), instance.getComponentDescription().getCreationTime(), instance.createProcessThreadInfo(thread));
			instance.notifyListeners(cce);
//			instance.notifyListeners(BpmnInterpreter.EVENT_THREAD_CHANGED, thread);
		}

		if(ex!=null && next==null)
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
			thread.setActivity(null);
			thread.getThreadContext().removeThread(thread);
		} 
		else
		{
			throw new UnsupportedOperationException("Unknown outgoing element type: "+next);
		}
	}
}
