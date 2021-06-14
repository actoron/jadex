package jadex.bdiv3.model;

import jadex.bdiv3.annotation.ExcludeMode;

/**
 *  Base class for all elements that can be processed with means-end reasoning.
 */
public class MProcessableElement extends MParameterElement
{
	/** Post to all flag. */
	protected boolean posttoall = false;
	
	/** Random selection flag. */
	protected boolean randomselection = false;
	
	/** The rebuild mode. */
	protected boolean rebuild = false;

	/** The exclude mode. */
	protected ExcludeMode excludemode = ExcludeMode.WhenTried;
	
	// additional xml attributes
	
	/** The exported flag. */
	protected boolean exported;
	
	/**
	 *	Bean Constructor. 
	 */
	public MProcessableElement()
	{
	}
	
	/**
	 *  Create a new element.
	 */
	// todo: add rebuild?
	public MProcessableElement(String name, boolean posttoall, boolean rebuild, boolean randomselection, ExcludeMode excludemode)
	{
		super(name);
		this.posttoall = posttoall;
		this.rebuild = rebuild;
		this.randomselection = randomselection;
		this.excludemode = excludemode==null? ExcludeMode.WhenTried: excludemode;
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
	public ExcludeMode getExcludeMode()
	{
		return excludemode;
	}

	/**
	 *  Set the excludemode.
	 *  @param excludemode The excludemode to set.
	 */
	public void setExcludeMode(ExcludeMode excludemode)
	{
		this.excludemode = excludemode;
	}
	
	
	/**
	 *  Get the exported flag.
	 *  @return The exported flag.
	 */
	public boolean isExported()
	{
		return exported;
	}

	/**
	 *  Set the exported flag.
	 *  @param exported The exported to set.
	 */
	public void setExported(boolean exported)
	{
		this.exported = exported;
	}
}
