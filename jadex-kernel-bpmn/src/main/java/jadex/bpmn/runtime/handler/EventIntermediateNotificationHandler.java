package jadex.bpmn.runtime.handler;

import java.util.HashMap;
import java.util.Iterator;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;
import jadex.commons.collection.IndexMap;
import jadex.commons.future.IFuture;

/**
 *  Wait for an external notification (could be a signal or a fired rule).
 *  Makes available a notfier object as "notifier" property.
 */
public class EventIntermediateNotificationHandler extends DefaultActivityHandler
{
	//-------- constants --------
	
	/** The property for the external notifier (system). */
	public static final String	PROPERTY_EXTERNALNOTIFIER = "externalnotifier";

	//-------- methods --------
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
		thread.setWaiting(true);
		
		// Create a shallow copy of properties.
		HashMap<String, Object> props = new HashMap<String, Object>();
		IndexMap<String, MParameter> params = thread.getActivity().getParameters();
		if(params!=null)
		{
			for(Iterator<MParameter> it=params.values().iterator(); it.hasNext(); )
			{
				MParameter param = (MParameter)it.next();
				props.put(param.getName(), thread.getParameterValue(param.getName()));
			}
		}
		
		final IExternalNotifier ext = (IExternalNotifier)thread.getPropertyValue(PROPERTY_EXTERNALNOTIFIER);
		if(ext!=null)
		{
			ext.activateWait(props, new Notifier(activity, instance, thread));
			thread.setWaitInfo(new ICancelable()
			{
				public IFuture<Void> cancel()
				{
					ext.cancel();
					thread.setWaitInfo(null);
					return IFuture.DONE;
				}
			});
		}
//		else
//			System.out.println("Warning, thread is waiting forever, no external notification system specified.");
		
//		System.out.println("Waiting for notification.");
	}
	
//	public void cancel(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
//	{
//		IExternalNotifier ext = (IExternalNotifier)thread.getWaitInfo();
//		if(ext!=null)
//		{
//			ext.cancel();
//			thread.setWaitInfo(null);
//		}		
//	}
}
