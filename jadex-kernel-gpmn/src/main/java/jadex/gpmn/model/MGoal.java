package jadex.gpmn.model;


/**
 *  Base class for all kinds of goals.
 */
public class MGoal
{
	//-------- goal types --------
	public static final class Types
	{
		public static final String PERFORM_GOAL  = "PerformGoal";
		public static final String ACHIEVE_GOAL  = "AchieveGoal";
		public static final String MAINTAIN_GOAL = "MaintainGoal";
	}
	
	//-------- attributes --------
	
	/** The id. */
	protected String id;
	
	/** The name. */
	protected String name;
	
	/** The goal type */
	protected String goaltype;
	
	/** The creation condition. */
	protected String creationcondition;
	
	/** The context condition. */
	protected String contextcondition;
	
	/** The drop condition. */
	protected String dropcondition;
	
	/** The target condition. */
	protected String targetcondition;
	
	/** The maintain condition */
	protected String maintaincondition;

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
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 *  Set the id.
	 *  @param id the id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		name = name.replaceAll("\r", " ");
		name = name.replaceAll("\n", " ");
		this.name = name;
	}
	
	/**
	 *  Get the goal type.
	 *  @return The goal type.
	 */
	public String getGoalType()
	{
		return goaltype;
	}

	/**
	 *  Set the goal type.
	 *  @param type The goal type to set.
	 */
	public void setGoalType(String goaltype)
	{
		this.goaltype = goaltype;
	}
	
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
	
	/**
	 *  Get the target condition.
	 *  @return The targetcondition.
	 */
	public String getTargetCondition()
	{
		return this.targetcondition;
	}

	/**
	 *  Set the target condition.
	 *  @param targetcondition The targetcondition to set.
	 */
	public void setTargetCondition(String targetcondition)
	{
		this.targetcondition = targetcondition;
	}
	
	/**
	 *  Get the maintaincondition.
	 *  @return The maintaincondition.
	 */
	public String getMaintainCondition()
	{
		return this.maintaincondition;
	}

	/**
	 *  Set the maintaincondition.
	 *  @param dropcondition The maintaincondition to set.
	 */
	public void setMaintainCondition(String maintaincondition)
	{
		this.maintaincondition = maintaincondition;
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
