package jadex.bdiv3.tutorial.e4;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalResult;

/**
 * 
 */
@Goal
public class TranslationGoal
{
	protected String gword;
	
	protected String eword;

	/**
	 *  Create a new TranslateGoal. 
	 */
	public TranslationGoal(String eword)
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
	
	public void setEWord(String eword)
	{
		this.eword = eword;
	}
}
