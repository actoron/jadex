package jadex.bpmn.runtime.handler;

import java.util.List;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MDataEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessServiceInvocationHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ProcessThreadValueFetcher;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.IValueFetcher;
import jadex.commons.future.Future;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;

/**
 * 
 */
public class EventIntermediateServiceActivityHandler extends EventIntermediateMessageActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
		//boolean	send = thread.hasPropertyValue(PROPERTY_THROWING)? ((Boolean)thread.getPropertyValue(PROPERTY_THROWING)).booleanValue() : false;
		
//		boolean service = thread.hasPropertyValue("iface") || thread.hasPropertyValue("returnparam");
		boolean service = activity.hasProperty("iface") || activity.hasParameter("returnparam");
		
		if(!service)
		{
			super.execute(activity, instance, thread);
		}
		else
		{
			if(activity.isThrowing())
			{
				sendReturnValue(activity, instance, thread);
				instance.step(activity, instance, thread, null);
			}
			else
			{
				// Top level event -> just move forward to next activity.
				// Or start event of event subprocess -> just move forward.
				if(MBpmnModel.EVENT_START_MESSAGE.equals(activity.getActivityType()) &&
					thread.getParent().getParent()==null	// check that parent thread is the top thread.
					|| (thread.getParent().getModelElement() instanceof MSubProcess
					&& MSubProcess.SUBPROCESSTYPE_EVENT.equals(((MSubProcess)thread.getParent().getModelElement()).getSubprocessType())))
				{
					doExecute(activity, instance, thread);
					instance.step(activity, instance, thread, null);
				}
				// Internal subprocess -> treat like intermediate event.
				else
				{
					// todo: waitForCall()
					System.out.println("todo: waitfor incoming call");
	//				super.execute(activity, instance, thread);
				}
			}
		}
	}
	
	/**
	 *  Wait for a service call.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	protected void sendReturnValue(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
		Future<Object> ret	= (Future<Object>)thread.getParameterValue(ProcessServiceInvocationHandler.THREAD_PARAMETER_SERVICE_RESULT);
		
		Object res = thread.getParameterValue("returnparam");
		
//		UnparsedExpression uexp = activity.getPropertyValue("returnparam");
//		IParsedExpression exp = SJavaParser.parseExpression(uexp, instance.getModel().getAllImports(), instance.getClassLoader());
//		IValueFetcher fetcher = new ProcessThreadValueFetcher(thread, false, instance.getFetcher());
//		Object res = exp.getValue(fetcher);
		
		ret.setResult(res);
	}
}
