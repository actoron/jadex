package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.ProcessServiceInvocationHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;

/**
 *  On end of service call process set result on future.
 */
public class EventEndSignalActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute the activity.
	 */
	protected void doExecute(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
		Future	ret	= (Future)thread.getParameterValue(ProcessServiceInvocationHandler.THREAD_PARAMETER_SERVICE_RESULT);
		Object	result	= thread.getPropertyValue(ProcessServiceInvocationHandler.EVENT_PARAMETER_SERVICE_RESULT);
		ret.setResult(result);
	}
}
