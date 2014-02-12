package jadex.rules.eca;

/**
 *  Information about execution of a rule.
 */
public class RuleEvent
{
	/** The rule that was fired. */
	protected String rulename;
	
	/** The action result. */
	protected Object result;

	/**
	 *  Create a new RuleEvent. 
	 */
	public RuleEvent()
	{
	}
	
	/**
	 *  Create a new RuleEvent. 
	 */
	public RuleEvent(String rulename, Object result)
	{
		this.rulename = rulename;
		this.result = result;
	}

	/**
	 *  Get the ruleName.
	 *  @return The ruleName.
	 */
	public String getRuleName()
	{
		return rulename;
	}

	/**
	 *  Set the ruleName.
	 *  @param ruleName The ruleName to set.
	 */
	public void setRuleName(String rulename)
	{
		this.rulename = rulename;
	}

	/**
	 *  Get the result.
	 *  @return The result.
	 */
	public Object getResult()
	{
		return result;
	}

	/**
	 *  Set the result.
	 *  @param result The result to set.
	 */
	public void setResult(Object result)
	{
		this.result = result;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "RuleEvent(rulename=" + rulename + ", result=" + result + ")";
	}	
	
}
