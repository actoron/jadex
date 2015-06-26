package jadex.bpmn.runtime;

import jadex.bpmn.features.IBpmnComponentFeature;
import jadex.bpmn.features.impl.BpmnComponentFeature;
import jadex.bpmn.model.MActivity;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;

import java.util.List;

/**
 *  Execute the next step of a process thread.
 */
public class ExecuteProcessThread implements IConditionalComponentStep<Void>
{
	/** The process thread. */
	protected ProcessThread thread;

	/** The thread id. Needed for bpmn debugger. */
	protected String threadid;
	
	/**
	 *  Create a new step.
	 *  @param thread The thread.
	 */
	public ExecuteProcessThread(ProcessThread thread)
	{
		this.thread = thread;
		this.threadid = thread.getId();
		
//		System.out.println("created step for: "+thread.getId()+" "+thread.getActivity());
	}

	/**
	 *  Test if the action is valid.
	 *  @return True, if action is valid.
	 */
	public boolean isValid()
	{
		// Can be set to null when thread is removed (e.g. termination of component)
		boolean ret = thread.getActivity()!=null && !thread.isWaiting();
//		if(!ret)
//			System.out.println("not exe: "+thread.getInstance().getComponentIdentifier().getLocalName()+" "+thread);
		return ret;
	}
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<Void> execute(IInternalAccess ia)
	{
		BpmnComponentFeature bcf = (BpmnComponentFeature)thread.getInstance().getComponentFeature(IBpmnComponentFeature.class);

		// Update parameters based on edge inscriptions and initial values.
		thread.updateParametersBeforeStep(thread.getInstance());
		
		// Find handler and execute activity.
		IActivityHandler handler = (IActivityHandler)bcf.getActivityHandler(thread.getActivity());
//		IActivityHandler handler = (IActivityHandler)activityhandlers.get(thread.getActivity().getActivityType());
		if(handler==null)
			throw new UnsupportedOperationException("No handler for activity: "+thread);

//		System.out.println("step: "+getComponentIdentifier()+" "+thread.getId()+" "+thread.getActivity()+" "+thread.getActivity().getId());
		MActivity act = thread.getActivity();
		
//		notifyListeners(createActivityEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, thread, thread.getActivity()));
//		if(thread.getInstance().getComponentFeature0(IMonitoringComponentFeature.class)!=null && thread.getInstance().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
//			thread.getInstance().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(bcf.createActivityEvent(IMonitoringEvent.EVENT_TYPE_CREATION, thread, thread.getActivity()), PublishTarget.TOALL);
		
//		thread = handler.execute(act, this, thread);
		handler.execute(act, thread.getInstance(), thread);

		// Moved to StepHandler
//		thread.updateParametersAfterStep(act, this);
		
		// Check if thread now waits for a message and there is at least one in the message queue.
		// Todo: check if thread directly or indirectly (multiple events!) waits for a message event before checking waitqueue
		List<Object> messages = bcf.getMessages();
//		System.out.println("messsages: "+messages);
		if(thread.isWaiting() && messages.size()>0 /*&& MBpmnModel.EVENT_INTERMEDIATE_MESSAGE.equals(thread.getActivity().getActivityType()) 
			&& (thread.getPropertyValue(EventIntermediateMessageActivityHandler.PROPERTY_MODE)==null 
				|| EventIntermediateMessageActivityHandler.MODE_RECEIVE.equals(thread.getPropertyValue(EventIntermediateMessageActivityHandler.PROPERTY_MODE)))*/)
		{
			boolean processed = false;
			for(int i=0; i<messages.size() && !processed; i++)
			{
				Object message = messages.get(i);
				IFilter<Object> filter = thread.getWaitFilter();
				if(filter!=null && filter.filter(message))
				{
					processed = true;
					messages.remove(i);
					System.out.println("Dispatched from waitqueue: "+messages.size()+" "+System.identityHashCode(message)+", "+message);
					bcf.notify(thread.getActivity(), thread, message);
				}
			}
		}
		List<IConnection> streams = bcf.getStreams();
		if(thread.isWaiting() && streams.size()>0) 
		{
			boolean processed = false;
			for(int i=0; i<streams.size() && !processed; i++)
			{
				Object stream = streams.get(i);
				IFilter<Object> filter = thread.getWaitFilter();
				if(filter!=null && filter.filter(stream))
				{
					processed = true;
					streams.remove(i);
					bcf.notify(thread.getActivity(), thread, stream);
//					System.out.println("Dispatched from stream: "+messages.size()+" "+message);
				}
			}
		}
				
		return IFuture.DONE;
	}

	/**
	 *  Get the thread.
	 *  @return The thread
	 */
	public ProcessThread getThread()
	{
		return thread;
	}
	
}
