package jadex.bpmnbdi.handler;

import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.handler.DefaultActivityHandler;
import jadex.bpmnbdi.BpmnPlanBodyInstance;
import jadex.commons.IFilter;
import jadex.rules.state.IOAVState;

/**
 *  Handler for signal (internal) events.
 */
public class EventIntermediateSignalActivityHandler	extends DefaultActivityHandler
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
//		thread.setWaitingState(ProcessThread.WAITING_FOR_MESSAGE);
		final String type = (String)thread.getPropertyValue("type", activity);
		thread.setWaiting(true);
		thread.setWaitInfo(type);
		System.out.println("Waiting for internal event: "+type);
		
		// Does currently only match message type name.
		thread.setWaitFilter(new IFilter()
		{
			public boolean filter(Object event)
			{
				boolean ret = false;
				BpmnPlanBodyInstance inst = (BpmnPlanBodyInstance)instance;
				IOAVState state = inst.getState();
				if(OAVBDIRuntimeModel.internalevent_type.equals(state.getType(event)))
				{
					Object mmsg = state.getAttributeValue(event, OAVBDIRuntimeModel.element_has_model);
					String eventtype = (String)state.getAttributeValue(mmsg, OAVBDIMetaModel.modelelement_has_name);
					ret = type.equals(eventtype);
				}
				return ret; 
			}
		});
	}
}
