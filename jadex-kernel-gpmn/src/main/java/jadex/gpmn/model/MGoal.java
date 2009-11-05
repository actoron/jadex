package jadex.gpmn.model;


/**
 *  Base class for all kinds of goals.
 */
public class MGoal extends MProcessElement
{
	//-------- attributes --------
	
	/** The creation condition. */
	protected String creationcondition;
	
	/** The context condition. */
	protected String contextcondition;
	
	/** The drop condition. */
	protected String dropcondition;

	/** The exclude mode. */
	protected String excludemode;

	/** The retry flag. */
	protected Boolean retry;
	
	/** The retry delay. */
	protected Long retrydelay;
	
	/** The recur flag. */
	protected Boolean recur;
	
	/** The recur delay. */
	protected Long recurdelay;
	
	/** The exclude mode. */
	protected String exclude;
	
	/** The rebuild flag. */
	protected Boolean rebuild;
	
	/** The unique flag. */
	protected Boolean unique;
	
	//-------- methods --------
	
	/**
	 *  Get the creationcondition.
	 *  @return The creationcondition.
	 */
	public String getCreationCondition()
	{
		return this.creationcondition;
	}

	/**
	 *  Set the creationcondition.
	 *  @param creationcondition The creationcondition to set.
	 */
	public void setCreationCondition(String creationcondition)
	{
		this.creationcondition = creationcondition;
	}

	/**
	 *  Get the contextcondition.
	 *  @return The contextcondition.
	 */
	public String getContextCondition()
	{
		return this.contextcondition;
	}

	/**
	 *  Set the contextcondition.
	 *  @param contextcondition The contextcondition to set.
	 */
	public void setContextCondition(String contextcondition)
	{
		this.contextcondition = contextcondition;
	}

	/**
	 *  Get the dropcondition.
	 *  @return The dropcondition.
	 */
	public String getDropCondition()
	{
		return this.dropcondition;
	}

	/**
	 *  Set the dropcondition.
	 *  @param dropcondition The dropcondition to set.
	 */
	public void setDropCondition(String dropcondition)
	{
		this.dropcondition = dropcondition;
	}	

	//-------- flags --------
	
	/**
	 *  Get the exclude mode.
	 *  @return The exclude mode.
	 */
	public String getExcludeMode()
	{
		return this.excludemode;
	}
	
	/**
	 *  Set the exclude mode.
	 *  @param excludemode The exclude mode to set.
	 */
	public void setExcludeMode(String excludemode)
	{
		this.excludemode = excludemode;
	}

	/**
	 *  Get the retry.
	 *  @return The retry.
	 */
	public Boolean getRetry()
	{
		return this.retry;
	}

	/**
	 *  Set the retry.
	 *  @param retry The retry to set.
	 */
	public void setRetry(Boolean retry)
	{
		this.retry = retry;
	}

	/**
	 *  Get the retrydelay.
	 *  @return The retrydelay.
	 */
	public Long getRetryDelay()
	{
		return this.retrydelay;
	}

	/**
	 *  Set the retrydelay.
	 *  @param retrydelay The retrydelay to set.
	 */
	public void setRetryDelay(Long retrydelay)
	{
		this.retrydelay = retrydelay;
	}

	/**
	 *  Get the recur.
	 *  @return The recur.
	 */
	public Boolean getRecur()
	{
		return this.recur;
	}

	/**
	 *  Set the recur.
	 *  @param recur The recur to set.
	 */
	public void setRecur(Boolean recur)
	{
		this.recur = recur;
	}

	/**
	 *  Get the recurdelay.
	 *  @return The recurdelay.
	 */
	public Long getRecurDelay()
	{
		return this.recurdelay;
	}

	/**
	 *  Set the recurdelay.
	 *  @param recurdelay The recurdelay to set.
	 */
	public void setRecurDelay(Long recurdelay)
	{
		this.recurdelay = recurdelay;
	}

	/**
	 *  Get the rebuild.
	 *  @return The rebuild.
	 */
	public Boolean getRebuild()
	{
		return this.rebuild;
	}

	/**
	 *  Set the rebuild.
	 *  @param rebuild The rebuild to set.
	 */
	public void setRebuild(Boolean rebuild)
	{
		this.rebuild = rebuild;
	}

	/**
	 *  Get the unique.
	 *  @return The unique.
	 */
	public Boolean getUnique()
	{
		return this.unique;
	}

	/**
	 *  Set the unique.
	 *  @param unique The unique to set.
	 */
	public void setUnique(Boolean unique)
	{
		this.unique = unique;
	}
}
