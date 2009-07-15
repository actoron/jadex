package jadex.bdi.bpmn;

import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;
import jadex.bpmn.runtime.handler.DefaultActivityHandler;
import jadex.commons.IFilter;
import jadex.rules.state.IOAVState;

/**
 *  Handler for message events.
 */
public class EventIntermediateMessageActivityHandler	extends DefaultActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param context	The thread context.
	 */
	public void execute(final MActivity activity, final BpmnInstance instance, ProcessThread thread, ThreadContext context)
	{
		// Just set thread to waiting.
		System.out.println("Waiting for message");
		thread.setWaitingState(ProcessThread.WAITING_FOR_MESSAGE);
		thread.setWaitInfo(activity.getPropertyValue("type"));
		
		// Does currently only match message type name.
		thread.setWaitFilter(new IFilter()
		{
			public boolean filter(Object msg)
			{
				BpmnPlanBodyInstance inst = (BpmnPlanBodyInstance)instance;
				IOAVState state = inst.getState();
				Object mmsg = state.getAttributeValue(msg, OAVBDIRuntimeModel.element_has_model);
				String msgtype = (String)state.getAttributeValue(mmsg, OAVBDIMetaModel.modelelement_has_name);
				return activity.getPropertyValue("type").equals(msgtype);
			}
		});
	}
}
