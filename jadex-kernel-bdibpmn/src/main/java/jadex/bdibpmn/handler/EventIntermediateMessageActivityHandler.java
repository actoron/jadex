package jadex.bdibpmn.handler;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.impl.MessageEventFlyweight;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdibpmn.BpmnPlanBodyInstance;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.handler.DefaultActivityHandler;
import jadex.commons.IFilter;
import jadex.rules.state.IOAVState;

/**
 *  Handler for message events.
 */
public class EventIntermediateMessageActivityHandler	extends DefaultActivityHandler
{
	//-------- constants --------
	
	/** The type property name (identifies message type). */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	PROPERTY_TYPE	= "type";
	
	/** The mode property name (distinguishes send/receive events). */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	PROPERTY_MODE	= "mode";
	
	/** The 'send' mode property value. */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	MODE_SEND	= "send";
	
	/** The 'receive' mode property value (default). */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	MODE_RECEIVE	= "receive";
	
	
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
		if(thread.hasPropertyValue(PROPERTY_MODE) && MODE_SEND.equals(thread.getPropertyValue(PROPERTY_MODE)))
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
			instance.getStepHandler(activity).step(activity, instance, thread, null);
		}
		else if(!thread.hasPropertyValue(PROPERTY_MODE) || MODE_RECEIVE.equals(thread.getPropertyValue(PROPERTY_MODE)))
		{
			// Just set thread to waiting.
	//		thread.setWaitingState(ProcessThread.WAITING_FOR_MESSAGE);
			final String	type	= (String)thread.getPropertyValue("type", activity);
			thread.setWaiting(true);
			thread.setWaitInfo(type);
//			System.out.println("Waiting for message: "+type);
			
			// Does currently only match message type name.
			thread.setWaitFilter(new IFilter()
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
		else
		{
			throw new RuntimeException("Invalid mode: "+thread.getPropertyValue(PROPERTY_MODE)+", "+thread);
		}
		
	}
}
