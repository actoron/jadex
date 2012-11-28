package jadex.bdiv3.model;

import jadex.bridge.modelinfo.UnparsedExpression;

import java.util.List;

/**
 *  BDI configuration. The name is used to connect
 *  it to the existing component configuration.
 */
public class MConfiguration
{
	/** The configuration name. */
	protected String name;
	
	/** The initial beliefs. */
	protected List<UnparsedExpression> initialbeliefs;

	/** The initial goals. */
	protected List<UnparsedExpression> initialgoals;

	/** The initial plans. */
	protected List<UnparsedExpression> initialplans;

	/**
	 * 
	 */
	public MConfiguration()
	{
	}
	
	/**
	 * 
	 */
	public MConfiguration(String name)
	{
		this.name = name;
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
		this.name = name;
	}

	/**
	 *  Get the initialgoals.
	 *  @return The initialgoals.
	 */
	public List<UnparsedExpression> getInitialGoals()
	{
		return initialgoals;
	}

	/**
	 *  Set the initialgoals.
	 *  @param initialgoals The initialgoals to set.
	 */
	public void setInitialGoals(List<UnparsedExpression> initialgoals)
	{
		this.initialgoals = initialgoals;
	}

	/**
	 *  Get the initialplans.
	 *  @return The initialplans.
	 */
	public List<UnparsedExpression> getInitialPlans()
	{
		return initialplans;
	}

	/**
	 *  Set the initialplans.
	 *  @param initialplans The initialplans to set.
	 */
	public void setInitialPlans(List<UnparsedExpression> initialplans)
	{
		this.initialplans = initialplans;
	}

	/**
	 *  Get the initialbeliefs.
	 *  @return The initialbeliefs.
	 */
	public List<UnparsedExpression> getInitialBeliefs()
	{
		return initialbeliefs;
	}

	/**
	 *  Set the initialbeliefs.
	 *  @param initialbeliefs The initialbeliefs to set.
	 */
	public void setInitialBeliefs(List<UnparsedExpression> initialbeliefs)
	{
		this.initialbeliefs = initialbeliefs;
	}
	
	
}
