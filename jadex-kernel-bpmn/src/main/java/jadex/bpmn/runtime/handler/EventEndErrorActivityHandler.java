package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;

/**
 *  On error end propagate an exception.
 */
public class EventEndErrorActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute the activity.
	 */
	protected void doExecute(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
		thread.setException(new EventEndErrorException(activity.getDescription()));
		instance.getStepHandler(activity).step(activity, instance, thread, null);
	}
	
	/**
	 *  Runtime exception representing explicit process failure.
	 */
	public static class EventEndErrorException	extends RuntimeException
	{
		/**
		 *  Create an empty end error.
		 */
		public EventEndErrorException()
		{
		}
		
		/**
		 *  Create an end error with an error message.
		 */
		public EventEndErrorException(String message)
		{
			super(message);
		}
	}
}
