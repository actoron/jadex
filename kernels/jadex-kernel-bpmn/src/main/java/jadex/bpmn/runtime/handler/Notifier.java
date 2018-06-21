package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;

/**
 *  Notifier for initiating external notifications.  
 */
public class Notifier 
{
	//-------- attributes --------
	
	/** The activity. */
	protected MActivity activity;
	
	/** The instance. */
	protected IInternalAccess instance;
	
	/** The process thread. */
	protected ProcessThread thread;
	
	//-------- constructors --------
	
	/**
	 *  Create a notifier.
	 */
	public Notifier(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
		this.activity = activity;
		this.instance = instance;
		this.thread = thread;
	}

	//-------- methods --------
	
	/**
	 *  Initiate the notification.
	 *  @param event The event.
	 */
	public void notify(final Object event)
	{
		DefaultActivityHandler.getBpmnFeature(instance).notify(activity, thread, event);
	}
}
