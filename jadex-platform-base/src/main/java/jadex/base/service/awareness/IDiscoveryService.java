package jadex.base.service.awareness;

/**
 *  Service interface for discovering other platforms.
 */
public interface IDiscoveryService
{
	/**
	 *  Set the send delay.
	 *  @param delay The delay.
	 */
	public void setDelay(long delay);
	
	/**
	 *  Set the includes.
	 *  @param includes The includes.
	 */
	public void setIncludes(String[] includes);
	
	/**
	 *  Set the excludes.
	 *  @param excludes The excludes.
	 */
	public void setExcludes(String[] excludes);
}
