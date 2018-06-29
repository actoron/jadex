package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ProcessThreadValueFetcher;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.IValueFetcher;
import jadex.javaparser.IParsedExpression;

/**
 *  On error end propagate an exception.
 */
public class EventIntermediateErrorActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute the activity.
	 */
	protected void doExecute(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
		// Do catch exception when the activity is an event handler.
		if(activity.isEventHandler() || !activity.isThrowing())
		{
			thread.setOrCreateParameterValue("$exception", thread.getException());
			thread.setException(null);
			super.doExecute(activity, instance, thread);
		}
		// Otherwise throw an exception.
		else
		{
			Exception ex = null;
			if (thread.getPropertyValue(MBpmnModel.PROPERTY_EVENT_ERROR, activity) instanceof UnparsedExpression)
			{
				UnparsedExpression excexp = (UnparsedExpression) thread.getPropertyValue(MBpmnModel.PROPERTY_EVENT_ERROR, activity);
				IValueFetcher fetcher	= new ProcessThreadValueFetcher(thread, false, instance.getFetcher());
				ex = (Exception) ((IParsedExpression) excexp.getParsed()).getValue(fetcher);
			}
			else if (thread.getPropertyValue(MBpmnModel.PROPERTY_EVENT_ERROR, activity) instanceof Exception)
			{
				ex = (Exception)thread.getPropertyValue(MBpmnModel.PROPERTY_EVENT_ERROR, activity);
			}
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