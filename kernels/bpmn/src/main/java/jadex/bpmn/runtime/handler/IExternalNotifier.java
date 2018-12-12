package jadex.bpmn.runtime.handler;

import java.util.Map;

/**
 *  An external system, in which a waiting can be initiated.
 */
public interface IExternalNotifier
{
	/**
	 *  Activate a wait action on an external source.
	 *  @param properties The properties.
	 *  @param notifier The notifier.
	 */
	public void activateWait(Map properties, Notifier notifier);
	
	/**
	 *  Cancel the wait action.
	 */
	public void cancel();
}
