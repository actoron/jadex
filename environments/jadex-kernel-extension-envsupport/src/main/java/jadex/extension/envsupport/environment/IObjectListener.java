package jadex.extension.envsupport.environment;

/**
 * 
 */
public interface IObjectListener
{
	/**
	 * This event gets called when an environment object event is triggered.
	 */
	public void dispatchObjectEvent(ObjectEvent event);
}
