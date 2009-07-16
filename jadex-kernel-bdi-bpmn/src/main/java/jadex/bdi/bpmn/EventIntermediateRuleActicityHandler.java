package jadex.bdi.bpmn;

import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.handler.DefaultActivityHandler;
import jadex.commons.IFilter;
import jadex.rules.state.IOAVState;

/**
 *  Handler for rule events.
 */
public class EventIntermediateRuleActicityHandler	extends DefaultActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final BpmnInstance instance, ProcessThread thread)
	{
		// Just set thread to waiting.
//		thread.setWaitingState(ProcessThread.WAITING_FOR_RULE);
		thread.setWaiting(true);
		thread.setWaitInfo(activity.getPropertyValue("type"));
		System.out.println("Waiting for rule: "+activity.getPropertyValue("type"));
		
		// Does currently only match message type name.
		thread.setWaitFilter(new IFilter()
		{
			public boolean filter(Object event)
			{
				boolean ret = false;
				BpmnPlanBodyInstance inst = (BpmnPlanBodyInstance)instance;
				IOAVState state = inst.getState();
				if(OAVBDIMetaModel.condition_type.equals(state.getType(event)))
				{
					String type = (String)state.getAttributeValue(event, OAVBDIMetaModel.modelelement_has_name);
					ret = activity.getPropertyValue("type").equals(type);
				}
				
				return ret;
			}
		});
	}
}
