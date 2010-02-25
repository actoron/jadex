package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IMessageService;
import jadex.bridge.MessageType;
import jadex.commons.IFilter;
import jadex.commons.SReflect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Handler for message events.
 */
public class EventIntermediateMessageActivityHandler	extends DefaultActivityHandler
{
	//-------- constants --------
	
	/** The mode property name (distinguishes send/receive events). */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	PROPERTY_MODE	= "mode";
	
	/** The 'send' mode property value. */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	MODE_SEND	= "send";
	
	/** The 'receive' mode property value (default). */
	// Hack!!! Required, because eclipse STP does not distinguish send/receive intermediate events.
	public static final String	MODE_RECEIVE	= "receive";
	
	
	/** The type property message type identifies the meta type (e.g. fipa). */
	public static final String	PROPERTY_MESSAGETYPE	= "messagetype";
	
	/** The filter property describes the filter for receiving a message. */
	public static final String	PROPERTY_FILTER	= "filter";
	
	/** The property message is the message to be sent. */
	public static final String	PROPERTY_MESSAGE = "message";
	
	//-------- methods --------
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
		String mode = thread.hasPropertyValue(PROPERTY_MODE)? (String)thread.getPropertyValue(PROPERTY_MODE): MODE_RECEIVE;
				
		if(MODE_SEND.equals(mode))
		{
			sendMessage(activity, instance, thread);
		}
		else if(MODE_RECEIVE.equals(mode))
		{
			receiveMessage(activity, instance, thread);
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
		
		Map msg;
		
		if(thread.hasPropertyValue(PROPERTY_MESSAGE))
		{
			msg = (Map)thread.getPropertyValue(PROPERTY_MESSAGE);
		}
		else
		{
			msg = new HashMap();
			
			// Convenience conversion of strings to component identifiers for receivers.
			String ri = mt.getReceiverIdentifier();
			Object recs = thread.getPropertyValue(ri);
			if(SReflect.isIterable(recs))
			{
				IComponentManagementService ces = (IComponentManagementService)instance.getComponentAdapter().getServiceContainer().getService(IComponentManagementService.class);
				List newrecs = new ArrayList();
				for(Iterator it=SReflect.getIterator(recs); it.hasNext(); )
				{
					Object rec = it.next();
					if(rec instanceof String)
					{
						newrecs.add(ces.createComponentIdentifier((String)rec, true, null));
					}
					else
					{
						newrecs.add(rec);
					}
				}
				recs = newrecs;
			}
			msg.put(ri, recs);
			
			String[] params	= mt.getParameterNames();
			for(int i=0; params!=null && i<params.length; i++)
			{
				if(thread.hasPropertyValue(params[i]) && !params[i].equals(ri))
				{
					msg.put(params[i], thread.getPropertyValue(params[i]));
				}
			}
			
			String[] paramsets	= mt.getParameterSetNames();
			for(int i=0; paramsets!=null && i<paramsets.length; i++)
			{
				if(thread.hasPropertyValue(paramsets[i]) && !paramsets[i].equals(ri))
				{
					msg.put(paramsets[i], thread.getPropertyValue(paramsets[i]));
				}
			}
		}
		
		ms.sendMessage(msg, mt, instance.getComponentAdapter().getComponentIdentifier(), instance.getClassLoader());
		instance.getStepHandler(activity).step(activity, instance, thread, null);
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
		
//		System.out.println("Waiting for message: "+filter);
	}
}
