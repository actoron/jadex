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
	protected List<MInitialParameterElement> initialgoals;

	/** The initial plans. */
	protected List<MInitialParameterElement> initialplans;

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
	public List<MInitialParameterElement> getInitialGoals()
	{
		return initialgoals;
	}

	/**
	 *  Set the initialgoals.
	 *  @param initialgoals The initialgoals to set.
	 */
	public void setInitialGoals(List<MInitialParameterElement> initialgoals)
	{
		this.initialgoals = initialgoals;
	}

	/**
	 *  Get the initialplans.
	 *  @return The initialplans.
	 */
	public List<MInitialParameterElement> getInitialPlans()
	{
		return initialplans;
	}

	/**
	 *  Set the initialplans.
	 *  @param initialplans The initialplans to set.
	 */
	public void setInitialPlans(List<MInitialParameterElement> initialplans)
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
	public void	addInitialGoal(MInitialParameterElement upex)
	{
		if(initialgoals==null)
		{
			initialgoals	= new ArrayList<MInitialParameterElement>();
		}
		initialgoals.add(upex);
	}
	
	/**
	 *  Add an initial plan.
	 *  @param upex	The expression.
	 */
	public void	addInitialPlan(MInitialParameterElement upex)
	{
		if(initialplans==null)
		{
			initialplans	= new ArrayList<MInitialParameterElement>();
		}
		initialplans.add(upex);
	}
}
