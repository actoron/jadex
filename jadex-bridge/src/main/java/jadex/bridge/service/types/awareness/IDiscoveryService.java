package jadex.bridge.service.types.awareness;

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
	 *  Set the fast awareness flag.
	 *  @param fast The fast flag.
	 */
	public void setFast(boolean fast);
	
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
	
	/**
	 *  Republish the awareness info.
	 *  Called when some important property has changed, e.g. platform addresses.
	 */
	public void republish();
}
