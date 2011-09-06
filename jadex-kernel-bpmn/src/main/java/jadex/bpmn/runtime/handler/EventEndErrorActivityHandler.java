package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessServiceInvocationHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.handler.EventIntermediateErrorActivityHandler.EventIntermediateErrorException;
import jadex.commons.future.Future;

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
		Exception ex = (Exception)thread.getPropertyValue("exception", activity);
		if(ex==null)
			ex = new EventIntermediateErrorException(activity.getDescription());
		
		// If service call: propagate exception.
		Future	ret	= (Future)thread.getParameterValue(ProcessServiceInvocationHandler.THREAD_PARAMETER_SERVICE_RESULT);
		if(ret!=null)
		{
			ret.setException(ex);
		}
		else
		{
			thread.setException(ex);
		}
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
