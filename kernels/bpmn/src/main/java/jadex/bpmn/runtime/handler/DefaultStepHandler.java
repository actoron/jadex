package jadex.bpmn.runtime.handler;

import java.util.List;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MNamedIdElement;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.IStepHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.commons.IResultCommand;
import jadex.commons.SReflect;

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
	public void step(MActivity activity, IInternalAccess instance, ProcessThread thread, Object event)
	{
		assert instance.getComponentFeature(IExecutionFeature.class).isComponentThread();
		
//		System.out.println("stephandler: "+thread.getId()+" "+instance.getComponentIdentifier().getLocalName()+": step "+activity+", data "+thread.getData());
		
//		if("Participant_1".equals(instance.getComponentIdentifier().getLocalName()))
//		{
//			Thread.dumpStack();
//		}
		
//		// Hack!!! Should be in interpreter/thread?
		thread.updateParametersAfterStep(activity, instance);
		
		MNamedIdElement	next	= null;
		ProcessThread remove = null; // Thread that needs to be removed (if any).
		Exception ex = thread.getException();
		
		// Store event (if any).
		if(event!=null)
		{
			thread.setOrCreateParameterValue("$event", event);
//			System.out.println("Event: "+activity+" "+thread+" "+event);
		}
		
		// For subprocesses not called
//		// Cancel handlers
//		if(activity instanceof MSubProcess)
//		{
//			ICancelable cancelable = (CompositeCancelable)thread.getWaitInfo();
//			if(cancelable!=null)
//			{
//				cancelable.cancel();
//				thread.setWaitInfo(null);
//			}
//		}
		
		// Timer occurred flow
		if(AbstractEventIntermediateTimerActivityHandler.TIMER_EVENT.equals(event))
		{
			// Cancel subflows.
			remove = thread;
			
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
			ProcessThread parent = thread.getParent();
			
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
				
				if(parent!=null)
				{
					outside	= parent.getParent()==null;
					if(next==null && !outside)
					{
						// Create new subthreads if it is a looping subprocess
						if(parent.getActivity() instanceof MSubProcess)
						{
							MSubProcess subp = (MSubProcess)parent.getActivity();
							if(MSubProcess.SUBPROCESSTYPE_SEQUENTIAL.equals(subp.getSubprocessType())
								&& thread.getParent().getLoopCommand()!=null)
							{
								IResultCommand<Boolean, Void> cmd = thread.getParent().getLoopCommand();
								if(!cmd.execute(null))
								{
									parent.setLoopCommand(null);
								}
							}
						}
						
						// When last thread or exception, mark current context for removal.
						if((parent.getSubthreads()!=null && parent.getSubthreads().size()==1) || ex!=null)
						{
							activity = (MActivity)parent.getModelElement();
							remove = parent;
							parent.updateParametersAfterStep(activity, instance);
							parent = parent.getParent();
							
							// Cancel subprocess handlers.
							if(activity instanceof MSubProcess)
							{
								List<MActivity>	handlers	= activity.getEventHandlers();
								for(int i=0; handlers!=null && i<handlers.size(); i++)
								{
									MActivity handler = (MActivity)handlers.get(i);
									DefaultActivityHandler.getBpmnFeature(instance).getActivityHandler(handler).cancel(handler, instance, remove.getParent());
								}
							}
						}
						
						// If more threads are available in current context just exit loop.
						else if(parent.getSubthreads()!=null && parent.getSubthreads().size()>1)
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
			thread = remove;
			thread.setNonWaiting();
			if(ex!=null)
				thread.setException(ex);
			// Todo: Callbacks for aborted threads (to abort external activities)
			thread.removeSubcontext();
			
//			for(Iterator<ProcessThread> it=remove.getAllThreads().iterator(); it.hasNext(); )
//			{
//				if(instance.hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
//				{
//					instance.publishEvent(instance.createThreadEvent(IMonitoringEvent.EVENT_TYPE_DISPOSAL, thread), PublishTarget.TOALL);
//				}
//			}
			if(instance.getComponentFeature0(IMonitoringComponentFeature.class)!=null && thread.getActivity()!=null && instance.getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
			{
				instance.getComponentFeature(IMonitoringComponentFeature.class).publishEvent(DefaultActivityHandler.getBpmnFeature(instance).createThreadEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION, thread), PublishTarget.TOALL);
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
				// If component scope and exception terminate the component
				instance.killComponent(ex);
				
				// Does not work because components now tolerate exceptions in steps
//				if(ex instanceof RuntimeException)
//				{
//					throw (RuntimeException)ex;
//				}
//				else
//				{
//					throw new RuntimeException("Unhandled exception in process: "+activity, ex);
//				}
			}
		}
			
		// Perform step settings, i.e. set next edge/activity or remove thread.
		if(next instanceof MSequenceEdge)
		{
			// Sets the edge as well as the next activity
			thread.setLastEdge((MSequenceEdge)next);
			if(instance.getComponentFeature0(IMonitoringComponentFeature.class)!=null && thread.getActivity()!=null && instance.getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
			{
				instance.getComponentFeature(IMonitoringComponentFeature.class).publishEvent(DefaultActivityHandler.getBpmnFeature(instance).createThreadEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION, thread), PublishTarget.TOALL);
			}
		}
		else if(next instanceof MActivity)
		{
			// Set the activity and the last edge to null
			thread.setActivity((MActivity)next);
			if(instance.getComponentFeature0(IMonitoringComponentFeature.class)!=null && thread.getActivity()!=null && instance.getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
			{
				instance.getComponentFeature(IMonitoringComponentFeature.class).publishEvent(DefaultActivityHandler.getBpmnFeature(instance).createThreadEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION, thread), PublishTarget.TOALL);
			}
		}
		else if(next==null)
		{
			thread.setActivity(null);
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
