/**
 * 
 */
package jadex.platform.service.cli;

/**
 *  The cli context that is passed to the commands.
 *  Provides access to the cli platform and a user context.
 */
public class CliContext
{
	//-------- attributes --------
	
	/** The cli shell. */
	protected CliShell clip;
	
	/** The user context. */
	protected Object usercontext;

	//-------- constructors --------
	
	/**
	 *  Create a new CliContext.
	 */
	public CliContext()
	{
	}

	/**
	 *  Create a new CliContext.
	 */
	public CliContext(CliShell clip, Object usercontext)
	{
		this.clip = clip;
		this.usercontext = usercontext;
	}

	/**
	 *  Get the clip.
	 *  @return The clip.
	 */
	public CliShell getClip()
	{
		return clip;
	}

	/**
	 *  Set the clip.
	 *  @param clip The clip to set.
	 */
	public void setClip(CliShell clip)
	{
		this.clip = clip;
	}

	/**
	 *  Get the userContext.
	 *  @return The userContext.
	 */
	public Object getUserContext()
	{
		return usercontext;
	}

	/**
	 *  Set the userContext.
	 *  @param userContext The userContext to set.
	 */
	public void setUserContext(Object userContext)
	{
		this.usercontext = userContext;
	}
	
}
