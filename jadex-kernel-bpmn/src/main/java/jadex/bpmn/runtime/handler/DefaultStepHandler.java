package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MNamedIdElement;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.IStepHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.commons.SReflect;

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
		
//		// Hack!!! Should be in interpreter/thread?
		thread.updateParametersAfterStep(activity, instance);
		
		MNamedIdElement	next	= null;
//		ThreadContext	remove	= null;	// Context that needs to be removed (if any).
		ProcessThread remove = null;
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
//			remove = thread.getSubcontext();
			remove = thread;
//			remove = thread.getThreadContext().getSubcontext(thread);
			
			// Continue with timer edge.
			List<MSequenceEdge> outedges = activity.getOutgoingSequenceEdges();
			if(outedges!=null && outedges.size()==1)
			{
				next = outedges.get(0);
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
//			ThreadContext	context	= thread.getThreadContext();
			ProcessThread context = thread.getParent();
			
			while(next==null && !outside)
			{
				// Normal flow
				if(ex==null)
				{
					List<MSequenceEdge>	outgoing	= activity.getOutgoingSequenceEdges();
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
					List<MActivity>	handlers	= activity.getEventHandlers();
					for(int i=0; handlers!=null && next==null && i<handlers.size(); i++)
					{
						MActivity	handler	= handlers.get(i);
						if(handler.getActivityType().equals(MBpmnModel.EVENT_INTERMEDIATE_ERROR))//"EventIntermediateError"))
						{
							// Attempt at "closest match" handler behavior
							
	//						Class	clazz	= handler.getName()!=null ? SReflect.findClass0(clname, imports, classloader);
//							if (handler.getClazz() == null)
//							{
//								if (nexthandler == null)
//								{
//									nexthandler = handler;
//								}
//							}
//							else if (handler.getClazz().getType(instance.getClassLoader()).equals(ex.getClass()))
//							{
//								nexthandler = handler;
//								break;
//							}
//							else if (SReflect.isSupertype(handler.getClazz().getType(instance.getClassLoader()), ex.getClass()))
//							{
//								nexthandler = handler;
//							}
							
							// Java-style "first matching handler" behavior
							if(handler.getClazz() == null || SReflect.isSupertype(handler.getClazz().getType(instance.getClassLoader(), instance.getModel().getAllImports()), ex.getClass()))
							{
								next = handler;
								break;
							}
						}
					}
				}
				
				if(context!=null)
				{
					outside	= context.getParent()==null;
					if(next==null && !outside)
					{
						// When last thread or exception, mark current context for removal.
						if(context.getSubthreads().size()==1 || ex!=null)
						{
							activity = (MActivity)context.getModelElement();
							remove	= context;
							context.updateParametersAfterStep(activity, instance);
							context	= context.getParent();
							
							// Cancel subprocess handlers.
							if(activity instanceof MSubProcess)
							{
								List<MActivity>	handlers	= activity.getEventHandlers();
								for(int i=0; handlers!=null && i<handlers.size(); i++)
								{
									MActivity handler = (MActivity)handlers.get(i);
									instance.getActivityHandler(handler).cancel(handler, instance, remove.getParent());
								}
							}
						}
						
						// If more threads are available in current context just exit loop.
						else if(context.getSubthreads().size()>1)
						{
							outside	= true;
						}
					}
				}
				else
				{
					break;
				}
//				else
//				{
//					throw new RuntimeException("Thread context nulls in process: "+activity, ex);
//				}
			}
		}
		
		// Remove inner context(s), if any.
		if(remove!=null)
		{
			thread	= remove;
			thread.setNonWaiting();
			if(ex!=null)
				thread.setException(ex);
			// Todo: Callbacks for aborted threads (to abort external activities)
//			thread.getThreadContext().removeSubcontext(remove);
			thread.removeSubcontext();
			
			for(Iterator<ProcessThread> it=remove.getAllThreads().iterator(); it.hasNext(); )
			{
//				ProcessThread	pt	= it.next();
//				ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_DISPOSAL, BpmnInterpreter.TYPE_THREAD, pt.getClass().getName(), 
//					pt.getId(), instance.getComponentIdentifier(), instance.getComponentDescription().getCreationTime(), instance.createProcessThreadInfo(pt));
//				instance.notifyListeners(cce);
//				instance.notifyListeners(BpmnInterpreter.EVENT_THREAD_REMOVED, (ProcessThread)it.next());
				
				if(instance.hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
				{
					instance.publishEvent(instance.createThreadEvent(IMonitoringEvent.EVENT_TYPE_DISPOSAL, thread), PublishTarget.TOALL);
				}

			}
//			ComponentChangeEvent cce = new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_MODIFICATION, BpmnInterpreter.TYPE_THREAD, thread.getClass().getName(), 
//				thread.getId(), instance.getComponentIdentifier(), instance.getComponentDescription().getCreationTime(), instance.createProcessThreadInfo(thread));
//			instance.notifyListeners(cce);
//			instance.notifyListeners(BpmnInterpreter.EVENT_THREAD_CHANGED, thread);
			if(thread.getActivity()!=null && instance.hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
			{
				instance.publishEvent(instance.createThreadEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION, thread), PublishTarget.TOALL);
			}
		}

		if(ex!=null && next==null)
		{
			// Hack! Special case of terminated exception of itself e.g. during killing.
			if(ex instanceof ComponentTerminatedException && instance.getComponentIdentifier().equals(((ComponentTerminatedException)ex).getComponentIdentifier()))
			{
				instance.getLogger().warning("Component terminated exception: "+ex);
			}
			else
			{
				if(ex instanceof RuntimeException)
				{
					throw (RuntimeException)ex;
				}
				else
				{
					throw new RuntimeException("Unhandled exception in process: "+activity, ex);
				}
			}
		}
			
		// Perform step settings, i.e. set next edge/activity or remove thread.
		if(next instanceof MSequenceEdge)
		{
			thread.setLastEdge((MSequenceEdge)next);
			if(thread.getActivity()!=null && instance.hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
			{
				instance.publishEvent(instance.createThreadEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION, thread), PublishTarget.TOALL);
			}
		}
		else if(next instanceof MActivity)
		{
			thread.setActivity((MActivity)next);
			if(thread.getActivity()!=null && instance.hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
			{
				instance.publishEvent(instance.createThreadEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION, thread), PublishTarget.TOALL);
			}
		}
		else if(next==null)
		{
			thread.setActivity(null);
//			thread.getThreadContext().removeThread(thread);
			if(thread.getParent()!=null)
			{
				thread.getParent().removeThread(thread);
			}
		} 
		else
		{
			throw new UnsupportedOperationException("Unknown outgoing element type: "+next);
		}
	}
}
