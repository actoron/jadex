package jadex.bdiv3.tutorial.f3;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;

/**
 * 
 */
@Goal
public class TranslationGoal
{
	/** The German word. */
	@GoalResult
	protected String gword;
	
	/** The English word. */
	@GoalParameter
	protected String eword;

	/**
	 *  Create a new TranslateGoal. 
	 */
	public TranslationGoal(String eword)
	{
		this.eword = eword;
	}

	/**
	 *  Get the gword.
	 *  @return The gword.
	 */
	public String getGWord()
	{
		return gword;
	}

	/**
	 *  Set the gword.
	 *  @param gword The gword to set.
	 */
	public void setGWord(String gword)
	{
		this.gword = gword;
	}

	/**
	 *  Get the eword.
	 *  @return The eword.
	 */
	public String getEWord()
	{
		return eword;
	}

	/**
	 *  Set the eword.
	 *  @param eword The eword to set.
	 */
	public void setEWord(String eword)
	{
		this.eword = eword;
	}
	
//	/**
//	 *  Create service parameters.
//	 */
//	@GoalServiceParameterMapping//(name="transser")
//	public Object[] createServiceParameters(Method m)
//	{
//		return new Object[]{getEWord()};
//	}
//	
//	/**
//	 *  Create service result.
//	 */
//	@GoalServiceResultMapping//(name="transser")
//	public void handleServiceResult(TranslationGoal obj, Method m, Object result)
//	{
//		setGWord((String)result);
//	}
}
