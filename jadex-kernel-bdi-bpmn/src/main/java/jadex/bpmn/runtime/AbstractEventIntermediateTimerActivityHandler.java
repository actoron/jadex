package jadex.bpmn.runtime;

import java.util.StringTokenizer;

import jadex.bpmn.model.MActivity;

/**
 *  Abstract handler for timing events.
 *  Can be subclassed by platform-specific implementations.
 */
public abstract class AbstractEventIntermediateTimerActivityHandler	extends DefaultActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		long	duration	= -1; 
		StringTokenizer	stok	= new StringTokenizer(activity.getName(), "\r\n");
		stok.nextToken();	// Skip first token (-> name).
		while(stok.hasMoreTokens())
		{
			String	prop	= stok.nextToken();
			if(prop.indexOf("=")!=-1)
			{
				String	name	= prop.substring(0, prop.indexOf("=")).trim();
				String	value	= prop.substring(prop.indexOf("=")+1).trim();
				if(name.equals("duration"))
					duration	= Long.parseLong(value);
			}
			
		}
		thread.setWaiting(true);
		doWait(activity, instance, thread, duration);
	}
	
	/**
	 *  Template method to be implemented by platform-specific subclasses.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param duration	The duration to wait.
	 */
	public abstract void doWait(MActivity activity, BpmnInstance instance, ProcessThread thread, long duration);
	
	/**
	 *  Method that should be called, when the timer event occurs in the platform.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void	notify(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
		thread.setWaiting(false);
		super.execute(activity, instance, thread);
		instance.wakeUp();
	}
}
