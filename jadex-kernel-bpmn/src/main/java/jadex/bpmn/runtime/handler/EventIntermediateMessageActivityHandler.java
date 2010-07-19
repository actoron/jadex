package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IMessageService;
import jadex.bridge.MessageType;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.service.SServiceProvider;

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
	
	/** The isThrowing property name (distinguishes send/receive events). */
	public static final String	PROPERTY_THROWING	= "isThrowing";	
	
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
		boolean	send = thread.hasPropertyValue(PROPERTY_THROWING)? ((Boolean)thread.getPropertyValue(PROPERTY_THROWING)).booleanValue() : false;
				
		if(send)
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
	protected void sendMessage(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
		SServiceProvider.getService(instance.getServiceProvider(), IMessageService.class)
			.addResultListener(instance.createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IMessageService	ms	= (IMessageService)result;
				SServiceProvider.getService(instance.getServiceProvider(), IComponentManagementService.class)
					.addResultListener(instance.createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IComponentManagementService cms = (IComponentManagementService)result;
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
						}
							
						// Convenience conversion of strings to component identifiers for receivers.
						String ri = mt.getReceiverIdentifier();
						if(thread.hasPropertyValue(ri))
						{
							Object recs = thread.getPropertyValue(ri);
							if(SReflect.isIterable(recs))
							{
								List newrecs = new ArrayList();
								for(Iterator it=SReflect.getIterator(recs); it.hasNext(); )
								{
									Object rec = it.next();
									if(rec instanceof String)
									{
										newrecs.add(cms.createComponentIdentifier((String)rec, true, null));
									}
									else
									{
										newrecs.add(rec);
									}
								}
								recs = newrecs;
							}
							msg.put(ri, recs);
						}
						
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
						
//						ms.sendMessage(msg, mt, instance.getComponentAdapter().getComponentIdentifier(), instance.getClassLoader());
						ms.sendMessage(msg, mt, instance.getComponentAdapter(), instance.getClassLoader());
						instance.getStepHandler(activity).step(activity, instance, thread, null);
					}
				}));
			}
		}));
	}
	
	/**
	 *  Receive a message.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	protected void receiveMessage(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
		thread.setWaiting(true);
//		thread.setWaitInfo(type);
		IFilter filter = (IFilter)thread.getPropertyValue(PROPERTY_FILTER, activity);
		if(filter==null)
		{
			filter	= new IFilter()
			{
				public boolean filter(Object obj)
				{
					boolean	ret	= obj instanceof IMessageAdapter;
					if(ret)
					{
						try
						{
							IMessageAdapter	msg	= (IMessageAdapter)obj;
							String[]	params	= activity.getPropertyNames();
							for(int i=0; ret && params!=null && i<params.length; i++)
							{
								// Fetch property from message event activity, because current activity might be mutliple event.
								ret	= SUtil.equals(thread.getPropertyValue(params[i], activity), msg.getValue(params[i]));
							}
						}
						catch(RuntimeException e)
						{
							instance.getLogger().warning("Error during message matching: "+instance+", "+thread+", "+obj+", "+e);
							ret	= false;
						}
					}
					return ret;
				}
			};
		}
		thread.setWaitFilter(filter);
		
//		System.out.println("Waiting for message: "+filter);
	}
}
