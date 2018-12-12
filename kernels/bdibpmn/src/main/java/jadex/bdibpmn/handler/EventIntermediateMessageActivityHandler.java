package jadex.bdibpmn.handler;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.impl.flyweights.MessageEventFlyweight;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdibpmn.BpmnPlanBodyInstance;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.handler.DefaultActivityHandler;
import jadex.bpmn.runtime.handler.ICancelable;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.rules.state.IOAVState;

/**
 *  Handler for message events.
 */
public class EventIntermediateMessageActivityHandler	extends DefaultActivityHandler
{
	//-------- constants --------
		
	/** The isThrowing property name (distinguishes send/receive events). */
	public static final String	PROPERTY_THROWING	= "isThrowing";
	
	//-------- methods --------
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
		boolean	send = thread.hasPropertyValue(PROPERTY_THROWING)? ((Boolean)thread.getPropertyValue(PROPERTY_THROWING)).booleanValue() : false;
//		System.out.println("message: "+instance+", "+send+", "+activity);
		
		if(send)
		{
			String	type	= (String)thread.getPropertyValue("type", activity);
			BpmnPlanBodyInstance inst = (BpmnPlanBodyInstance)instance;
			IMessageEvent	me	= inst.createMessageEvent(type);
			String[]	params	= me.getMessageType().getParameterNames();
			for(int i=0; params!=null && i<params.length; i++)
			{
				if(thread.hasPropertyValue(params[i]))
				{
					me.getParameter(params[i]).setValue(thread.getPropertyValue(params[i]));
				}
			}
			String[]	paramsets	= me.getMessageType().getParameterSetNames();
			for(int i=0; paramsets!=null && i<paramsets.length; i++)
			{
				if(thread.hasPropertyValue(paramsets[i]))
				{
					me.getParameterSet(paramsets[i]).removeValues();
					me.getParameterSet(paramsets[i]).addValues((Object[])thread.getPropertyValue(paramsets[i]));
				}
			}
			inst.sendMessage(me);
			instance.step(activity, instance, thread, null);
		}
		else 
		{
			// Just set thread to waiting.
	//		thread.setWaitingState(ProcessThread.WAITING_FOR_MESSAGE);
			final String	type	= (String)thread.getPropertyValue("type", activity);
			thread.setWaiting(true);
			thread.setWaitInfo(new DummyCancelable(type));
//			System.out.println("Waiting for message: "+type);
			
			// Does currently only match message type name.
			thread.setWaitFilter(new IFilter<Object>()
			{
				public boolean filter(Object event)
				{
					boolean ret = false;
					BpmnPlanBodyInstance inst = (BpmnPlanBodyInstance)instance;
					IOAVState state = inst.getState();
					if(event instanceof MessageEventFlyweight)
						event = ((MessageEventFlyweight)event).getHandle();
					if(OAVBDIRuntimeModel.messageevent_type.equals(state.getType(event)))
					{
						Object mmsg = state.getAttributeValue(event, OAVBDIRuntimeModel.element_has_model);
						String msgtype = (String)state.getAttributeValue(mmsg, OAVBDIMetaModel.modelelement_has_name);
						ret = type.equals(msgtype);
					}
					return ret; 
				}
			});
		}		
	}
}



