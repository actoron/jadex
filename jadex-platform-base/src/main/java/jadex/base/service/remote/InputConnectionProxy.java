package jadex.base.service.remote;

/**
 * 
 */
public class InputConnectionProxy
{
	protected int connectionid;

	/**
	 * 
	 */
	public InputConnectionProxy()
	{
	}
	
	/**
	 * 
	 */
	public InputConnectionProxy(int connectionid)
	{
		this.connectionid = connectionid;
	}

	/**
	 *  Get the connectionid.
	 *  @return The connectionid.
	 */
	public int getConnectionId()
	{
		return connectionid;
	}

	/**
	 *  Set the connectionid.
	 *  @param connectionid The connectionid to set.
	 */
	public void setConnectionId(int connectionid)
	{
		this.connectionid = connectionid;
	}
}
