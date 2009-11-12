package jadex.bpmn.runtime.handler;

import java.util.HashMap;
import java.util.Map;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IMessageService;
import jadex.bridge.MessageType;
import jadex.commons.IFilter;

/**
 *  Handler for message events.
 */
public class EventIntermediateMessageActivityHandler	extends DefaultActivityHandler
{
	//-------- constants --------
	
	/** The type property message type identifies the meta type (e.g. fipa). */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	PROPERTY_MESSAGETYPE	= "messagetype";
	
	/** The type property identifies the application level type. */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	PROPERTY_TYPE	= "type";
	
	/** The filter property describes the filter for receiving a message. */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	PROPERTY_FILTER	= "filter";
	
	/** The mode property name (distinguishes send/receive events). */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	PROPERTY_MODE	= "mode";
	
	/** The 'send' mode property value. */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	MODE_SEND	= "send";
	
	/** The 'receive' mode property value (default). */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	MODE_RECEIVE	= "receive";
	
	/** The property message is the message to be sent. */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	PROPERTY_MESSAGE = "message";

	
	
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
			sendMessage(activity, instance, thread);
		}
		else if(!thread.hasPropertyValue(PROPERTY_MODE) || MODE_RECEIVE.equals(thread.getPropertyValue(PROPERTY_MODE)))
		{
			receiveMessage(activity, instance, thread);
		}
		else
		{
			throw new RuntimeException("Invalid mode: "+thread.getPropertyValue(PROPERTY_MODE)+", "+thread);
		}
	}
	
	/**
	 *  Send a message.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	protected void sendMessage(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
		IMessageService ms = (IMessageService)instance.getComponentAdapter().getServiceContainer().getService(IMessageService.class);
		String mtname = (String)thread.getPropertyValue(PROPERTY_MESSAGETYPE, activity);
		MessageType mt = mtname!=null? ms.getMessageType(mtname): ms.getMessageType("fipa");

		Map msg = new HashMap();
		String[] params	= mt.getParameterNames();
		for(int i=0; params!=null && i<params.length; i++)
		{
			if(thread.hasPropertyValue(params[i]))
			{
				msg.put(params[i], thread.getPropertyValue(params[i]));
			}
		}
		
		String[] paramsets	= mt.getParameterSetNames();
		for(int i=0; paramsets!=null && i<paramsets.length; i++)
		{
			if(thread.hasPropertyValue(paramsets[i]))
			{
				msg.put(params[i], thread.getPropertyValue(paramsets[i]));
			}
		}
		ms.sendMessage(msg, mt, instance.getComponentAdapter().getComponentIdentifier(), instance.getClassLoader());
		step(activity, instance, thread, null);
	}
	
	/**
	 *  Receive a message.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	protected void receiveMessage(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
		thread.setWaiting(true);
//		thread.setWaitInfo(type);
		IFilter filter = (IFilter)thread.getPropertyValue(PROPERTY_FILTER, activity);
		thread.setWaitFilter(filter);
		
		System.out.println("Waiting for message: "+filter);
	}
}
