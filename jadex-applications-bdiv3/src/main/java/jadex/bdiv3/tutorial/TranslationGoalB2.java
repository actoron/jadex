package jadex.bdiv3.tutorial;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalResult;

/**
 * 
 */
@Goal
public class TranslationGoalB2
{
	protected String gword;
	
	protected String eword;

	/**
	 *  Create a new TranslateGoal. 
	 */
	public TranslationGoalB2(String eword)
	{
		this.eword = eword;
	}

	@GoalResult
	public String getGWord()
	{
		return gword;
	}
	
	public void setGWord(String gword)
	{
		this.gword = gword;
	}
	
	public String getEWord()
	{
		return eword;
	}
	
	public void setEword(String eword)
	{
		this.eword = eword;
	}
}
