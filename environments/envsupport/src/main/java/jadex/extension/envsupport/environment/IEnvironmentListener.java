package jadex.extension.envsupport.environment;

/**
 *  Interface for environment listener.
 */
public interface IEnvironmentListener
{
	/**
	 *  Dispatch an environment event to this listener.
	 *  @param event The event.
	 */
	public void dispatchEnvironmentEvent(EnvironmentEvent event);
}
