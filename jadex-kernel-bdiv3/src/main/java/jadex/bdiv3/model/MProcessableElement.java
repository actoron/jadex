package jadex.bdiv3.model;

/**
 * 
 */
public class MProcessableElement
{
	/** Post to all flag. */
	protected boolean posttoall;
	
	/** Random selection flag. */
	protected boolean randomselection;
	
	/**
	 * 
	 */
	public MProcessableElement(boolean posttoall, boolean randomselection)
	{
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

}
