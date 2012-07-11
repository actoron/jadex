package jadex.gpmn.editor.model.gpmn.impl;

import jadex.gpmn.editor.model.gpmn.IGoal;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.gpmn.ModelConstants;

/**
 *  Node representing a goal.
 *
 */
public class Goal extends AbstractNode implements IGoal
{
	/** The goal type. */
	protected String goaltype = ModelConstants.ACHIEVE_GOAL_TYPE;
	
	/** The creation condition. */
	protected String creationcondition;
	
	/** The creation condition language. */
	protected String creationconditionlanguage;
	
	/** The context condition. */
	protected String contextcondition;
	
	/** The context condition language. */
	protected String contextconditionlanguage;
	
	/** The drop condition. */
	protected String dropcondition;
	
	/** The drop condition language. */
	protected String dropconditionlanguage;
	
	/** The target condition. */
	protected String targetcondition;
	
	/** The target condition language. */
	protected String targetconditionlanguage;
	
	/** The failure condition. */
	protected String failurecondition;
	
	/** The failure condition language. */
	protected String failureconditionlanguage;
	
	/** The maintain condition. */
	protected String maintaincondition;
	
	/** The maintain condition language. */
	protected String maintainconditionlanguage;
	
	/** Easy deliberation strategy setting. */
	protected String deliberation;
	
	/** The exclude rule. */
	protected String exclude;
	
	/** Flag if the goal is post-to-all. */
	protected boolean posttoall;
	
	/** Flag if the goal uses random selection. */
	protected boolean randomselection;
	
	/** Flag if the goal recalculates. */
	protected boolean recalculate;
	
	/** Flag if the goal recurs. */
	protected boolean recur;
	
	/** The recur delay. */
	protected int recurdelay;
	
	/** Flag if the goal should be retried. */
	protected boolean retry = true;
	
	/** The retry delay. */
	protected int retrydelay;
	
	/**
	 *  Creates a new goal.
	 */
	public Goal(IGpmnModel model)
	{
		super(model);
	}

	/**
	 *  Gets the goal type.
	 *
	 *  @return The goal type.
	 */
	public String getGoalType()
	{
		return goaltype;
	}

	/**
	 *  Sets the goal type.
	 *
	 *  @param goaltype The goal type.
	 */
	public void setGoalType(String goaltype)
	{
		this.goaltype = goaltype;
	}

	/**
	 *  Gets the creation condition.
	 *
	 *  @return The creation condition.
	 */
	public String getCreationCondition()
	{
		return creationcondition;
	}

	/**
	 *  Sets the creation condition.
	 *
	 *  @param creationcondition The creation condition.
	 */
	public void setCreationCondition(String creationcondition)
	{
		this.creationcondition = creationcondition;
	}

	/**
	 *  Gets the creation condition language.
	 *
	 *  @return The creation condition language.
	 */
	public String getCreationConditionLanguage()
	{
		return creationconditionlanguage;
	}

	/**
	 *  Sets the creation condition language.
	 *
	 *  @param creationconditionlanguage The creation condition language.
	 */
	public void setCreationConditionLanguage(String creationconditionlanguage)
	{
		this.creationconditionlanguage = creationconditionlanguage;
	}

	/**
	 *  Gets the context condition.
	 *
	 *  @return The context condition.
	 */
	public String getContextCondition()
	{
		return contextcondition;
	}

	/**
	 *  Sets the context condition.
	 *
	 *  @param contextcondition The context condition.
	 */
	public void setContextCondition(String contextcondition)
	{
		this.contextcondition = contextcondition;
	}

	/**
	 *  Gets the context condition language.
	 *
	 *  @return The context condition language.
	 */
	public String getContextConditionLanguage()
	{
		return contextconditionlanguage;
	}

	/**
	 *  Sets the context condition language.
	 *
	 *  @param contextconditionlanguage The context condition language.
	 */
	public void setContextConditionLanguage(String contextconditionlanguage)
	{
		this.contextconditionlanguage = contextconditionlanguage;
	}

	/**
	 *  Gets the drop condition.
	 *
	 *  @return The drop condition.
	 */
	public String getDropCondition()
	{
		return dropcondition;
	}

	/**
	 *  Sets the drop condition.
	 *
	 *  @param dropcondition The drop condition.
	 */
	public void setDropCondition(String dropcondition)
	{
		this.dropcondition = dropcondition;
	}

	/**
	 *  Gets the drop condition language.
	 *
	 *  @return The drop condition language.
	 */
	public String getDropConditionLanguage()
	{
		return dropconditionlanguage;
	}

	/**
	 *  Sets the drop condition language.
	 *
	 *  @param dropconditionlanguage The drop condition language.
	 */
	public void setDropConditionLanguage(String dropconditionlanguage)
	{
		this.dropconditionlanguage = dropconditionlanguage;
	}

	/**
	 *  Gets the target condition.
	 *
	 *  @return The target condition.
	 */
	public String getTargetCondition()
	{
		return targetcondition;
	}

	/**
	 *  Sets the target condition.
	 *
	 *  @param targetcondition The target condition.
	 */
	public void setTargetCondition(String targetcondition)
	{
		this.targetcondition = targetcondition;
	}

	/**
	 *  Gets the target condition language.
	 *
	 *  @return The target condition language.
	 */
	public String getTargetConditionLanguage()
	{
		return targetconditionlanguage;
	}

	/**
	 *  Sets the target condition language.
	 *
	 *  @param targetconditionlanguage The target condition language.
	 */
	public void setTargetConditionLanguage(String targetconditionlanguage)
	{
		this.targetconditionlanguage = targetconditionlanguage;
	}

	/**
	 *  Gets the failure condition.
	 *
	 *  @return The failure condition.
	 */
	public String getFailureCondition()
	{
		return failurecondition;
	}

	/**
	 *  Sets the failure condition.
	 *
	 *  @param failurecondition The failure condition.
	 */
	public void setFailureCondition(String failurecondition)
	{
		this.failurecondition = failurecondition;
	}

	/**
	 *  Gets the failure condition language.
	 *
	 *  @return The failure condition language.
	 */
	public String getFailureConditionLanguage()
	{
		return failureconditionlanguage;
	}

	/**
	 *  Sets the failure condition language.
	 *
	 *  @param failureconditionlanguage The failure condition language.
	 */
	public void setFailureConditionLanguage(String failureconditionlanguage)
	{
		this.failureconditionlanguage = failureconditionlanguage;
	}

	/**
	 *  Gets the maintain condition.
	 *
	 *  @return The maintain condition.
	 */
	public String getMaintainCondition()
	{
		return maintaincondition;
	}

	/**
	 *  Sets the maintain condition.
	 *
	 *  @param maintaincondition The maintain condition.
	 */
	public void setMaintainCondition(String maintaincondition)
	{
		this.maintaincondition = maintaincondition;
	}

	/**
	 *  Gets the maintain condition language.
	 *
	 *  @return The maintain condition language.
	 */
	public String getMaintainConditionLanguage()
	{
		return maintainconditionlanguage;
	}

	/**
	 *  Sets the maintain condition language.
	 *
	 *  @param maintainconditionlanguage The maintain condition language.
	 */
	public void setMaintainConditionLanguage(String maintainconditionlanguage)
	{
		this.maintainconditionlanguage = maintainconditionlanguage;
	}

	/**
	 *  Gets the deliberation.
	 *
	 *  @return The deliberation.
	 */
	public String getDeliberation()
	{
		return deliberation;
	}

	/**
	 *  Sets the deliberation.
	 *
	 *  @param deliberation The deliberation.
	 */
	public void setDeliberation(String deliberation)
	{
		this.deliberation = deliberation;
	}

	/**
	 *  Gets the exclude.
	 *
	 *  @return The exclude.
	 */
	public String getExclude()
	{
		return exclude;
	}

	/**
	 *  Sets the exclude.
	 *
	 *  @param exclude The exclude.
	 */
	public void setExclude(String exclude)
	{
		this.exclude = exclude;
	}

	/**
	 *  Returns if the goal is post-to-all.
	 *
	 *  @return True, if post-to-all.
	 */
	public boolean isPostToAll()
	{
		return posttoall;
	}

	/**
	 *  Sets the post-to-all setting.
	 *
	 *  @param posttoall The post-to-all setting.
	 */
	public void setPostToAll(boolean posttoall)
	{
		this.posttoall = posttoall;
	}

	/**
	 *  Tests if the goal uses random selection.
	 *
	 *  @return True if the goal uses random selection.
	 */
	public boolean isRandomSelection()
	{
		return randomselection;
	}

	/**
	 *  Sets the random selection setting.
	 *
	 *  @param randomselection The random selection setting.
	 */
	public void setRandomSelection(boolean randomselection)
	{
		this.randomselection = randomselection;
	}

	/**
	 *  Gets the recalculate setting.
	 *
	 *  @return The recalculate setting.
	 */
	public boolean isRecalculate()
	{
		return recalculate;
	}

	/**
	 *  Sets the recalculate setting.
	 *
	 *  @param recalculate The recalculate setting.
	 */
	public void setRecalculate(boolean recalculate)
	{
		this.recalculate = recalculate;
	}

	/**
	 *  Gets the recur setting.
	 *
	 *  @return The recur setting.
	 */
	public boolean isRecur()
	{
		return recur;
	}

	/**
	 *  Sets the recur setting.
	 *
	 *  @param recur The recur setting.
	 */
	public void setRecur(boolean recur)
	{
		this.recur = recur;
	}

	/**
	 *  Gets the recur delay.
	 *
	 *  @return The recur delay.
	 */
	public int getRecurDelay()
	{
		return recurdelay;
	}

	/**
	 *  Sets the recur delay.
	 *
	 *  @param recurdelay The recur delay.
	 */
	public void setRecurDelay(int recurdelay)
	{
		this.recurdelay = recurdelay;
	}

	/**
	 *  Gets the retry setting.
	 *
	 *  @return The retry setting.
	 */
	public boolean isRetry()
	{
		return retry;
	}

	/**
	 *  Sets the retry.
	 *
	 *  @param retry The retry.
	 */
	public void setRetry(boolean retry)
	{
		this.retry = retry;
	}

	/**
	 *  Gets the retry delay.
	 *
	 *  @return The retry delay.
	 */
	public int getRetryDelay()
	{
		return retrydelay;
	}

	/**
	 *  Sets the retry delay.
	 *
	 *  @param retrydelay The retry delay.
	 */
	public void setRetryDelay(int retrydelay)
	{
		this.retrydelay = retrydelay;
	}
}
