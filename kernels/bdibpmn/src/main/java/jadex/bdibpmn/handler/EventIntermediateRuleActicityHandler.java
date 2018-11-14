package jadex.bdibpmn.handler;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdibpmn.BpmnPlanBodyInstance;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
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
	public void execute(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
		// Just set thread to waiting.
		final String wtype	= (String)thread.getPropertyValue("type", activity);
//		thread.setWaitingState(ProcessThread.WAITING_FOR_RULE);
		thread.setWaiting(true);
		thread.setWaitInfo(new DummyCancelable(wtype));
//		System.out.println("Waiting for rule: "+type);
		
		// Does currently only match message type name.
		thread.setWaitFilter(new IFilter<Object>()
		{
			public boolean filter(Object event)
			{
				boolean ret = false;
				BpmnPlanBodyInstance inst = (BpmnPlanBodyInstance)instance;
				IOAVState state = inst.getState();
				if(event instanceof ElementFlyweight)
					event = ((ElementFlyweight)event).getHandle();
				if(OAVBDIMetaModel.condition_type.equals(state.getType(event)))
				{
					String type = (String)state.getAttributeValue(event, OAVBDIMetaModel.modelelement_has_name);
					ret = type.equals(wtype);
				}
				
				return ret;
			}
		});
		
	}
}
