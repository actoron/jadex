package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;

/**
 *  On error end propagate an exception.
 */
public class EventIntermediateErrorActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute the activity.
	 */
	protected void doExecute(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
		// Do catch exception when the activity is an event handler.
		if(activity.isEventHandler())
		{
			thread.setParameterValue("$exception", thread.getException());
			thread.setException(null);
			super.doExecute(activity, instance, thread);
		}
		// Otherwise throw an exception.
		else
		{
			Exception ex = (Exception)thread.getPropertyValue("exception", activity);
			if(ex==null)
				ex = new EventIntermediateErrorException(activity.getDescription());
			thread.setException(ex);
		}
	}
	
	/**
	 *  Runtime exception representing explicit process failure.
	 */
	public static class EventIntermediateErrorException	extends RuntimeException
	{
		/**
		 *  Create an empty end error.
		 */
		public EventIntermediateErrorException()
		{
		}
		
		/**
		 *  Create an end error with an error message.
		 */
		public EventIntermediateErrorException(String message)
		{
			super(message);
		}
	}
}