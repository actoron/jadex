package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.ProcessServiceInvocationHandler;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IntermediateFuture;

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
	public void execute(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
		//boolean	send = thread.hasPropertyValue(PROPERTY_THROWING)? ((Boolean)thread.getPropertyValue(PROPERTY_THROWING)).booleanValue() : false;
//		System.out.println("send message acticity: "+instance.getComponentIdentifier().getLocalName()+" "+thread.getId()+" "+activity);
		
//		boolean service = thread.hasPropertyValue("iface") || thread.hasPropertyValue("returnparam");
		boolean service = activity.hasProperty(MActivity.ISSERVICE);// || activity.hasParameter(MActivity.RETURNPARAM);
		
		if(!service)
		{
			super.execute(activity, instance, thread);
		}
		else
		{
			if(activity.isThrowing())
			{
				sendReturnValue(activity, instance, thread);
				getBpmnFeature(instance).step(activity, instance, thread, null);
			}
			else
			{
				// Top level event -> just move forward to next activity.
				// Or start event of event subprocess -> just move forward.
				if(MBpmnModel.EVENT_START_MESSAGE.equals(activity.getActivityType()) &&
					thread.getParent().getParent()==null	// check that parent thread is the top thread.
					|| (thread.getParent().getModelElement() instanceof MSubProcess
					&& MSubProcess.SUBPROCESSTYPE_EVENT.equals(((MSubProcess)thread.getParent().getModelElement()).getSubprocessType()))
					|| (activity.isEventHandler()))
				{
					doExecute(activity, instance, thread);
					getBpmnFeature(instance).step(activity, instance, thread, null);
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
	protected void sendReturnValue(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
		Future<Object> ret	= (Future<Object>)thread.getParameterValue(ProcessServiceInvocationHandler.THREAD_PARAMETER_SERVICE_RESULT);
		
		boolean hasret = activity.getIncomingDataEdges()!=null && activity.getIncomingDataEdges().size()>0;
		Object res = hasret? thread.getParameterValue(MActivity.RETURNPARAM): null;
		
		if(ret instanceof IntermediateFuture)
		{
			if(hasret)
				((IntermediateFuture)ret).addIntermediateResult(res);
			if(activity.isEndEvent())
			{
				((IntermediateFuture)ret).setFinished();
			}
		}
		else if(ret!=null)
		{
			ret.setResult(res);
		}
		else
		{
			thread.getInstance().getLogger().warning("Cannot return value from service call, no future found: "+activity);
		}
	}
}
