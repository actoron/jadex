package jadex.rules.rulesystem.rete.builder;

import jadex.rules.rulesystem.IRule;

/**
 *  The build info per rule.
 */
public class BuildInfo
{
	//-------- attributes --------
	
	/** The rule. */
	protected IRule rule;
	
	/** The buildtime. */
	protected long time;
	
	//-------- constructors --------

	/**
	 *  Create a new build info.
	 */
	public BuildInfo(IRule rule, long time)
	{
		this.rule = rule;
		this.time = time;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the rule.
	 *  @return The rule.
	 */
	public IRule getRule()
	{
		return rule;
	}

	/**
	 *  Get the time.
	 *  @return The time.
	 */
	public long getTime()
	{
		return time;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return rule.getName()+" needed:  "+time;
	}
}


