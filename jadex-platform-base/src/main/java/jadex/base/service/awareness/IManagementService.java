package jadex.base.service.awareness;

/**
 *  Service for managing discovery infos.
 */
public interface IManagementService
{
	/**
	 *  Announce an awareness info.
	 *  @param info The info.
	 */
	public void addAwarenessInfo(AwarenessInfo info);
}
