package jadex.gpmn.editor.model.gpmn;

public interface IGoal extends INode
{
	/**
	 *  Gets the goal type.
	 *
	 *  @return The goal type.
	 */
	public String getGoalType();

	/**
	 *  Sets the goal type.
	 *
	 *  @param goaltype The goal type.
	 */
	public void setGoalType(String goaltype);

	/**
	 *  Gets the creation condition.
	 *
	 *  @return The creation condition.
	 */
	public String getCreationCondition();

	/**
	 *  Sets the creation condition.
	 *
	 *  @param creationcondition The creation condition.
	 */
	public void setCreationCondition(String creationcondition);

	/**
	 *  Gets the creation condition language.
	 *
	 *  @return The creation condition language.
	 */
	public String getCreationConditionLanguage();

	/**
	 *  Sets the creation condition language.
	 *
	 *  @param creationconditionlanguage The creation condition language.
	 */
	public void setCreationConditionLanguage(String creationconditionlanguage);

	/**
	 *  Gets the context condition.
	 *
	 *  @return The context condition.
	 */
	public String getContextCondition();

	/**
	 *  Sets the context condition.
	 *
	 *  @param contextcondition The context condition.
	 */
	public void setContextCondition(String contextcondition);

	/**
	 *  Gets the context condition language.
	 *
	 *  @return The context condition language.
	 */
	public String getContextConditionLanguage();

	/**
	 *  Sets the context condition language.
	 *
	 *  @param contextconditionlanguage The context condition language.
	 */
	public void setContextConditionLanguage(String contextconditionlanguage);

	/**
	 *  Gets the drop condition.
	 *
	 *  @return The drop condition.
	 */
	public String getDropCondition();

	/**
	 *  Sets the drop condition.
	 *
	 *  @param dropcondition The drop condition.
	 */
	public void setDropCondition(String dropcondition);

	/**
	 *  Gets the drop condition language.
	 *
	 *  @return The drop condition language.
	 */
	public String getDropConditionLanguage();

	/**
	 *  Sets the drop condition language.
	 *
	 *  @param dropconditionlanguage The drop condition language.
	 */
	public void setDropConditionLanguage(String dropconditionlanguage);

	/**
	 *  Gets the target condition.
	 *
	 *  @return The target condition.
	 */
	public String getTargetCondition();

	/**
	 *  Sets the target condition.
	 *
	 *  @param targetcondition The target condition.
	 */
	public void setTargetCondition(String targetcondition);

	/**
	 *  Gets the target condition language.
	 *
	 *  @return The target condition language.
	 */
	public String getTargetConditionLanguage();

	/**
	 *  Sets the target condition language.
	 *
	 *  @param targetconditionlanguage The target condition language.
	 */
	public void setTargetConditionLanguage(String targetconditionlanguage);

	/**
	 *  Gets the failure condition.
	 *
	 *  @return The failure condition.
	 */
	public String getFailureCondition();

	/**
	 *  Sets the failure condition.
	 *
	 *  @param failurecondition The failure condition.
	 */
	public void setFailureCondition(String failurecondition);

	/**
	 *  Gets the failure condition language.
	 *
	 *  @return The failure condition language.
	 */
	public String getFailureConditionLanguage();

	/**
	 *  Sets the failure condition language.
	 *
	 *  @param failureconditionlanguage The failure condition language.
	 */
	public void setFailureConditionLanguage(String failureconditionlanguage);

	/**
	 *  Gets the maintain condition.
	 *
	 *  @return The maintain condition.
	 */
	public String getMaintainCondition();

	/**
	 *  Sets the maintain condition.
	 *
	 *  @param maintaincondition The maintain condition.
	 */
	public void setMaintainCondition(String maintaincondition);

	/**
	 *  Gets the maintain condition language.
	 *
	 *  @return The maintain condition language.
	 */
	public String getMaintainConditionLanguage();

	/**
	 *  Sets the maintain condition language.
	 *
	 *  @param maintainconditionlanguage The maintain condition language.
	 */
	public void setMaintainConditionLanguage(String maintainconditionlanguage);

	/**
	 *  Gets the deliberation.
	 *
	 *  @return The deliberation.
	 */
	public String getDeliberation();

	/**
	 *  Sets the deliberation.
	 *
	 *  @param deliberation The deliberation.
	 */
	public void setDeliberation(String deliberation);

	/**
	 *  Gets the exclude.
	 *
	 *  @return The exclude.
	 */
	public String getExclude();

	/**
	 *  Sets the exclude.
	 *
	 *  @param exclude The exclude.
	 */
	public void setExclude(String exclude);

	/**
	 *  Returns if the goal is post-to-all.
	 *
	 *  @return True, if post-to-all.
	 */
	public boolean isPostToAll();

	/**
	 *  Sets the post-to-all setting.
	 *
	 *  @param posttoall The post-to-all setting.
	 */
	public void setPostToAll(boolean posttoall);

	/**
	 *  Tests if the goal uses random selection.
	 *
	 *  @return True if the goal uses random selection.
	 */
	public boolean isRandomSelection();

	/**
	 *  Sets the random selection setting.
	 *
	 *  @param randomselection The random selection setting.
	 */
	public void setRandomSelection(boolean randomselection);

	/**
	 *  Gets the recalculate setting.
	 *
	 *  @return The recalculate setting.
	 */
	public boolean isRecalculate();

	/**
	 *  Sets the recalculate setting.
	 *
	 *  @param recalculate The recalculate setting.
	 */
	public void setRecalculate(boolean recalculate);

	/**
	 *  Gets the recur setting.
	 *
	 *  @return The recur setting.
	 */
	public boolean isRecur();

	/**
	 *  Sets the recur setting.
	 *
	 *  @param recur The recur setting.
	 */
	public void setRecur(boolean recur);

	/**
	 *  Gets the recur delay.
	 *
	 *  @return The recur delay.
	 */
	public int getRecurDelay();

	/**
	 *  Sets the recur delay.
	 *
	 *  @param recurdelay The recur delay.
	 */
	public void setRecurDelay(int recurdelay);

	/**
	 *  Gets the retry setting.
	 *
	 *  @return The retry setting.
	 */
	public boolean isRetry();

	/**
	 *  Sets the retry.
	 *
	 *  @param retry The retry.
	 */
	public void setRetry(boolean retry);

	/**
	 *  Gets the retry delay.
	 *
	 *  @return The retry delay.
	 */
	public int getRetryDelay();

	/**
	 *  Sets the retry delay.
	 *
	 *  @param retrydelay The retry delay.
	 */
	public void setRetryDelay(int retrydelay);
}
