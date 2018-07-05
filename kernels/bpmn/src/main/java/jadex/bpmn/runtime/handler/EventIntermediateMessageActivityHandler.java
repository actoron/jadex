package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.commons.IFilter;
import jadex.commons.future.IResultListener;

/**
 *  Handler for message events.
 */
public class EventIntermediateMessageActivityHandler extends DefaultActivityHandler
{
	//-------- constants --------
	
	/** The filter property describes the filter for receiving a message. */
	public static final String	PROPERTY_FILTER	= "filter";
	
	/** The property message is the message object to be sent. */
	public static final String	PROPERTY_MESSAGE = "message";
	
	/** The property receiver is the cid of the intended receiver (may be null if set in message object, e.g. in FIPA messages). */
	public static final String	PROPERTY_RECEIVER = "receiver";
	
	//-------- methods --------
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
//		System.out.println("send message acticity2: "+activity);
		
		if(activity.isThrowing())
		{
			sendMessage(activity, instance, thread);
		}
		else
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
	protected void sendMessage(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
		Object message	= thread.getPropertyValue(PROPERTY_MESSAGE);
		IComponentIdentifier receiver;
		Object rec	= thread.getPropertyValue(PROPERTY_RECEIVER);
		if(rec instanceof String)
		{
			// Special case -> string converted to sibling cid.
			receiver	= new BasicComponentIdentifier((String)rec, instance.getIdentifier().getParent());
		}
		else
		{
			receiver	= (IComponentIdentifier)rec;
		}

		thread.setWaiting(true);
//		System.out.println("sending message: "+msg.get(ri));
		
		instance.getFeature(IMessageFeature.class).sendMessage(message, receiver)
			.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				getBpmnFeature(instance).notify(activity, thread, null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				thread.setException(exception);
				getBpmnFeature(instance).notify(activity, thread, null);
			}
		});
	}
	
	/**
	 *  Receive a message.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	protected void receiveMessage(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
		thread.setWaiting(true);
		@SuppressWarnings("unchecked")
		IFilter<Object> filter = (IFilter<Object>)thread.getPropertyValue(PROPERTY_FILTER, activity);
		if(filter==null)
		{
			// TODO: distinguish between messages and other objects?
			filter	= IFilter.ALWAYS;
//			throw new NullPointerException("Message receiving event needs "+PROPERTY_FILTER+" property: "+thread);
		}
		thread.setWaitFilter(filter);
		
//		System.out.println("Waiting for message: "+filter);
	}
}
