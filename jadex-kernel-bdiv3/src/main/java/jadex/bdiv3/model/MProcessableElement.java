package jadex.bdiv3.model;

/**
 * 
 */
public class MProcessableElement extends MElement
{
	/** Post to all flag. */
	protected boolean posttoall;
	
	/** Random selection flag. */
	protected boolean randomselection;
	
	/** The rebuild mode. */
	protected boolean rebuild;

	/** The exclude mode. */
	protected String excludemode;
	
	/**
	 * 
	 */
	public MProcessableElement(String name, boolean posttoall, boolean randomselection)
	{
		super(name);
		this.posttoall = posttoall;
		this.randomselection = randomselection;
	}
	
	/**
	 *  Test if is posttoall.
	 *  @return True, if posttoaall.
	 */
	public boolean isPostToAll()
	{
		return posttoall;
	}
	
	/**
	 *  Test if is random selection.
	 *  @return True, if is random selection.
	 */
	public boolean isRandomSelection()
	{
		return randomselection;
	}

	/**
	 *  Set the posttoall.
	 *  @param posttoall The posttoall to set.
	 */
	public void setPostToAll(boolean posttoall)
	{
		this.posttoall = posttoall;
	}

	/**
	 *  Set the randomselection.
	 *  @param randomselection The randomselection to set.
	 */
	public void setRandomSelection(boolean randomselection)
	{
		this.randomselection = randomselection;
	}
	
	/**
	 *  Test if rebuild APL.
	 *  @return True, if rebuild.
	 */
	public boolean isRebuild()
	{
		return rebuild;
	}
	
	/**
	 *  Set the rebuild.
	 *  @param rebuild The rebuild to set.
	 */
	public void setRebuild(boolean rebuild)
	{
		this.rebuild = rebuild;
	}

	/**
	 *  Get the excludemode.
	 *  @return The excludemode.
	 */
	public String getExcludeMode()
	{
		return excludemode;
	}

	/**
	 *  Set the excludemode.
	 *  @param excludemode The excludemode to set.
	 */
	public void setExcludeMode(String excludemode)
	{
		this.excludemode = excludemode;
	}
	
}
