/**
 * 
 */
package jadex.platform.service.cli;

/**
 *
 */
public class CliContext
{
	/** The cli platform. */
	protected CliPlatform clip;
	
	/** The user context. */
	protected Object userContext;

	/**
	 *  Create a new CliContext.
	 */
	public CliContext()
	{
	}

	/**
	 *  Create a new CliContext.
	 */
	public CliContext(CliPlatform clip, Object userContext)
	{
		this.clip = clip;
		this.userContext = userContext;
	}

	/**
	 *  Get the clip.
	 *  @return The clip.
	 */
	public CliPlatform getClip()
	{
		return clip;
	}

	/**
	 *  Set the clip.
	 *  @param clip The clip to set.
	 */
	public void setClip(CliPlatform clip)
	{
		this.clip = clip;
	}

	/**
	 *  Get the userContext.
	 *  @return The userContext.
	 */
	public Object getUserContext()
	{
		return userContext;
	}

	/**
	 *  Set the userContext.
	 *  @param userContext The userContext to set.
	 */
	public void setUserContext(Object userContext)
	{
		this.userContext = userContext;
	}
	
	
}
