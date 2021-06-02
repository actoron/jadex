package jadex.bridge;

/**
 *  Bean containing a Jadex version.
 *
 */
public class JadexVersion
{
	/** The major version if available. */
	protected int majorversion = -1;
	
	/** The minor version if available. */
	protected int minorversion = -1;
	
	/**
	 *  Returns the major version of Jadex.
	 * 
	 *  @return Version of Jadex, -1 if version cannot be determined.
	 */
	public int getMajorVersion()
	{
		return majorversion;
	}
	
	/**
	 *  Sets the major version.
	 *  @param majorversion The version.
	 */
	public void setMajorVersion(int majorversion)
	{
		this.majorversion = majorversion;
	}
	
	/**
	 *  Returns the minor version of Jadex.
	 * 
	 *  @return Version of Jadex, -1 if version cannot be determined.
	 */
	public int getMinorVersion()
	{
		return minorversion;
	}
	
	/**
	 *  Sets the minor version.
	 *  @param majorversion The version.
	 */
	public void setMinorVersion(int minorversion)
	{
		this.minorversion = minorversion;
	}
	
	/**
	 *  Checks if the version is unknown.
	 *  @return True, if unknown.
	 */
	public boolean isUnknown()
	{
		return majorversion < 0 || minorversion < 0;
	}
}
