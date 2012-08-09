package jadex.base.service.autoupdate;

/**
 * 
 */
public class UpdateInfo
{
	/** The version found. */
	protected long version;
	
	/** The version access. */
	protected Object access;

	/**
	 *  Get the version.
	 *  @return The version.
	 */
	public long getVersion()
	{
		return version;
	}

	/**
	 *  Set the version.
	 *  @param version The version to set.
	 */
	public void setVersion(long version)
	{
		this.version = version;
	}

	/**
	 *  Get the access.
	 *  @return The access.
	 */
	public Object getAccess()
	{
		return access;
	}

	/**
	 *  Set the access.
	 *  @param access The access to set.
	 */
	public void setAccess(Object access)
	{
		this.access = access;
	}
}
