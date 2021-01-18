package jadex.bpmn.runtime.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
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
	 *  Convert a string to component identifier
	 *  @param cid The component identifier.
	 *  @return parent The parent.
	 */
	public IComponentIdentifier getCid(Object cid, IComponentIdentifier parent)
	{
		IComponentIdentifier ret = null;
		if(cid instanceof String)
		{
			// Special case -> string converted to sibling cid.
			ret = new ComponentIdentifier((String)cid, parent);
		}
		else
		{
			ret = (IComponentIdentifier)ret;
		}
		return ret;
	}
	
	/**
	 * 
	 * @return
	 */
	protected Object assembleMessage(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
		// read complete message
		Object message	= thread.getPropertyValue(PROPERTY_MESSAGE);
		
		// or assemble from parts
		if(message==null)
			message = new HashMap<>();

		if(message instanceof Map)
		{
			String[] props = activity.getPropertyNames();
			for(String prop: props)
			{
				if(!(PROPERTY_MESSAGE.equals(prop)))
					((Map<String, Object>)message).put(prop, thread.getPropertyValue(prop));
			}
			
			// HACK!!! remove FIPA specific code
			if(((Map<String, Object>)message).get(SFipa.SENDER)==null)
				((Map<String, Object>)message).put(SFipa.SENDER, instance.getId());
		}
		
		return message;
	}
	
	/**
	 *  Send a message.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	protected void sendMessage(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
		Object message	= assembleMessage(activity, instance, thread);
		IComponentIdentifier[] receivers = null;
		Object rec	= thread.getPropertyValue(PROPERTY_RECEIVER);

		if(SReflect.isIterable(rec))
		{
			List<IComponentIdentifier> tmp = new ArrayList<>();
			for(Object r: SReflect.getIterable(rec))
			{
				tmp.add(getCid(r, instance.getId().getParent()));
			}
			receivers = tmp.toArray(new IComponentIdentifier[tmp.size()]);
		}
		else if(rec!=null)
		{
			receivers = new IComponentIdentifier[]{getCid(rec, instance.getId().getParent())};
		}
		
		// HACK! fix receivers, could be set only in message (fipa) when createReply was used
		if(message instanceof Map)
		{
			if(receivers!=null)
			{
				((Map)message).put(PROPERTY_RECEIVER, receivers);
			}
			else 
			{
				Object r = ((Map)message).get(SFipa.RECEIVERS);
				if(r instanceof IComponentIdentifier)
					receivers = new IComponentIdentifier[]{(IComponentIdentifier)r};
			}
			
			//if("refuse".equals(((Map)message).get(SFipa.PERFORMATIVE)))
			//	System.out.println("sdg");
		}

		thread.setWaiting(true);
		System.out.println("sending message: "+message);
		
		instance.getFeature(IMessageFeature.class).sendMessage(message, receivers)
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
			// read out before because matching could be done in multiple event
			/*Map<String, Object> targetvals = new HashMap<String, Object>();
			String[] props = activity.getPropertyNames();
			for(String prop: props)
			{
				if(!(PROPERTY_MESSAGE.equals(prop)) && !(PROPERTY_FILTER.equals(prop))
					&& !(PROPERTY_RECEIVER.equals(prop)))
				{
					Object propval = thread.getPropertyValue(prop, activity);
					targetvals.put(prop, propval);
				}
			}*/
			
			// todo: hack for message maps?!
			filter = new IFilter<Object>() 
			{
				public boolean filter(Object obj) 
				{
					boolean ret = true;
					// only map filter supported currently
					if(obj instanceof Map)
					{
						Map<String, Object> msg = (Map<String, Object>)obj;
						
						String[] props = activity.getPropertyNames();
						for(String prop: props)
						{
							if(!(PROPERTY_MESSAGE.equals(prop)) && !(PROPERTY_FILTER.equals(prop))
								&& !(PROPERTY_RECEIVER.equals(prop)))
							{
								Object propval = thread.getPropertyValue(prop, activity);
								ret = SUtil.equals(msg.get(prop), propval);
								if(!ret)
									break;
							}
						}
						
						/*for(String prop: targetvals.keySet())
						{
							ret = SUtil.equals(msg.get(prop), targetvals.get(prop));
							if(!ret)
								break;
						}*/
					}
					return ret;
				}
			};
		
			// TODO: distinguish between messages and other objects?
			//filter	= IFilter.ALWAYS;
//			throw new NullPointerException("Message receiving event needs "+PROPERTY_FILTER+" property: "+thread);
		}
		thread.setWaitFilter(filter);
		
//		System.out.println("Waiting for message: "+filter);
	}
}
