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
	protected ACliShell shell;
	
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
	public CliContext(ACliShell clip, Object usercontext)
	{
		this.shell = clip;
		this.usercontext = usercontext;
	}

	/**
	 *  Get the clip.
	 *  @return The clip.
	 */
	public ACliShell getShell()
	{
		return shell;
	}

	/**
	 *  Set the clip.
	 *  @param clip The clip to set.
	 */
	public void setShell(ACliShell clip)
	{
		this.shell = clip;
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
	public void setUserContext(Object usercontext)
	{
		this.usercontext = usercontext;
	}
	
}
