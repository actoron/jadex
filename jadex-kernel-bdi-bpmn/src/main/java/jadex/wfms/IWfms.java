package jadex.wfms;

/**
 * The Workflow Management System interface.
 */
public interface IWfms
{
	/**
	 *  Get a Wfms-service.
	 *  @param type The service interface/type.
	 *  @return The corresponding Wfms-service.
	 */
	public Object getService(Class type);
}
