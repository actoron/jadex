package jadex.bdiv3.model;

/**
 * 
 */
public class MProcessableElement extends MElement
{
	/** Never exclude plan candidates from apl. */
	public static final String EXCLUDE_NEVER = "never";

	/** Exclude tried plan candidates from apl. */ 
	public static final String EXCLUDE_WHEN_TRIED = "when_tried";
	
	/** Exclude failed plan candidates from apl. */
	public static final String EXCLUDE_WHEN_FAILED = "when_failed";

	/** Exclude succeeded plan candidates from apl. */
	public static final String EXCLUDE_WHEN_SUCCEEDED = "when_succeeded";

	
	/** Post to all flag. */
	protected boolean posttoall;
	
	/** Random selection flag. */
	protected boolean randomselection;
	
	/** The rebuild mode. */
	protected boolean rebuild;

	/** The exclude mode. */
	protected String excludemode;
	
	/**
	 *	Bean Constructor. 
	 */
	public MProcessableElement()
	{
		// used by xml reader
		this.excludemode = excludemode==null? EXCLUDE_WHEN_TRIED: excludemode;
	}
	
	/**
	 * 
	 */
	public MProcessableElement(String name, boolean posttoall, boolean randomselection, String excludemode)
	{
		super(name);
		this.posttoall = posttoall;
		this.randomselection = randomselection;
		this.excludemode = excludemode==null? EXCLUDE_WHEN_TRIED: excludemode;
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
