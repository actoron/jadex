package jadex.bdiv3.model;

import jadex.bridge.modelinfo.UnparsedExpression;

import java.util.ArrayList;
import java.util.List;

/**
 *  BDI configuration. The name is used to connect
 *  it to the existing component configuration.
 */
public class MConfiguration	extends MElement
{
	/** The initial beliefs. */
	protected List<UnparsedExpression> initialbeliefs;

	/** The initial goals. */
	protected List<UnparsedExpression> initialgoals;

	/** The initial plans. */
	protected List<UnparsedExpression> initialplans;

	/**
	 *	Bean Constructor. 
	 */
	public MConfiguration()
	{
	}
	
	/**
	 * 
	 */
	public MConfiguration(String name)
	{
		super(name);
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
	
	/**
	 *  Add an initial belief.
	 *  @param upex	The expression.
	 */
	public void	addInitialBelief(UnparsedExpression upex)
	{
		if(initialbeliefs==null)
		{
			initialbeliefs	= new ArrayList<UnparsedExpression>();
		}
		initialbeliefs.add(upex);
	}
	
	/**
	 *  Add an initial goal.
	 *  @param upex	The expression.
	 */
	public void	addInitialGoal(UnparsedExpression upex)
	{
		if(initialgoals==null)
		{
			initialgoals	= new ArrayList<UnparsedExpression>();
		}
		initialgoals.add(upex);
	}
	
	/**
	 *  Add an initial plan.
	 *  @param upex	The expression.
	 */
	public void	addInitialPlan(UnparsedExpression upex)
	{
		if(initialplans==null)
		{
			initialplans	= new ArrayList<UnparsedExpression>();
		}
		initialplans.add(upex);
	}
}
