package jadex.adapter.base.envsupport.environment;

/**
 * 
 */
public interface IEnvironmentListener
{
	/**
	 * 
	 */
	public boolean isRelevant(EnvironmentEvent event);
	
	/**
	 * 
	 */
	public void dispatchEnvironmentEvent(EnvironmentEvent event);
}
