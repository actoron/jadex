package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.ProcessServiceInvocationHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ProcessThreadValueFetcher;
import jadex.bpmn.runtime.handler.EventIntermediateErrorActivityHandler.EventIntermediateErrorException;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.IValueFetcher;
import jadex.commons.future.Future;
import jadex.javaparser.IParsedExpression;

/**
 *  On error end propagate an exception.
 */
public class EventEndErrorActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute the activity.
	 */
	protected void doExecute(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
		Exception ex = null;
		if(thread.getPropertyValue("exception", activity) instanceof Exception)
		{
			ex = (Exception)thread.getPropertyValue("exception", activity);
		}
		else if(thread.getPropertyValue("exception", activity) instanceof UnparsedExpression)
		{
			UnparsedExpression excexp = (UnparsedExpression) thread.getPropertyValue("exception", activity);
			IValueFetcher fetcher	= new ProcessThreadValueFetcher(thread, false, instance.getFetcher());
			ex = (Exception) ((IParsedExpression) excexp.getParsed()).getValue(fetcher);
		}
		
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
