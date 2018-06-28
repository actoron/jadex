package jadex.bdiv3.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  BDI configuration. The name is used to connect
 *  it to the existing component configuration.
 */
public class MConfiguration	extends MElement
{
	/** The initial beliefs. */
	protected List<MConfigBeliefElement> initialbeliefs;

	/** The initial goals. */
	protected List<MConfigParameterElement> initialgoals;

	/** The initial plans. */
	protected List<MConfigParameterElement> initialplans;

	/** The initial events. */
	protected List<MConfigParameterElement> initialevents;

	/** The end beliefs. */
	protected List<MConfigBeliefElement> endbeliefs;

	/** The end goals. */
	protected List<MConfigParameterElement> endgoals;

	/** The end plans. */
	protected List<MConfigParameterElement> endplans;

	/** The end events. */
	protected List<MConfigParameterElement> endevents;
	
	/** The initial capabilities (capability name->initial configuration name). */
	protected Map<String, String>	initialcapabilities;

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
	 *  Get the initial beliefs.
	 *  @return The initial beliefs.
	 */
	public List<MConfigBeliefElement> getInitialBeliefs()
	{
		return initialbeliefs;
	}

	/**
	 *  Set the initial beliefs.
	 *  @param initialbeliefs The initial beliefs to set.
	 */
	public void setInitialBeliefs(List<MConfigBeliefElement> initialbeliefs)
	{
		this.initialbeliefs = initialbeliefs;
	}
	
	/**
	 *  Add an initial belief.
	 *  @param upex	The expression.
	 */
	public void	addInitialBelief(MConfigBeliefElement upex)
	{
		if(initialbeliefs==null)
		{
			initialbeliefs	= new ArrayList<MConfigBeliefElement>();
		}
		initialbeliefs.add(upex);
	}
	
	/**
	 *  Get the initial goals.
	 *  @return The initial goals.
	 */
	public List<MConfigParameterElement> getInitialGoals()
	{
		return initialgoals;
	}

	/**
	 *  Set the initial goals.
	 *  @param initialgoals The initial goals to set.
	 */
	public void setInitialGoals(List<MConfigParameterElement> initialgoals)
	{
		this.initialgoals = initialgoals;
	}

	/**
	 *  Add an initial goal.
	 *  @param upex	The expression.
	 */
	public void	addInitialGoal(MConfigParameterElement upex)
	{
		if(initialgoals==null)
		{
			initialgoals	= new ArrayList<MConfigParameterElement>();
		}
		initialgoals.add(upex);
	}
	
	/**
	 *  Get the initial plans.
	 *  @return The initial plans.
	 */
	public List<MConfigParameterElement> getInitialPlans()
	{
		return initialplans;
	}

	/**
	 *  Set the initial plans.
	 *  @param initialplans The initial plans to set.
	 */
	public void setInitialPlans(List<MConfigParameterElement> initialplans)
	{
		this.initialplans = initialplans;
	}

	/**
	 *  Add an initial plan.
	 *  @param upex	The expression.
	 */
	public void	addInitialPlan(MConfigParameterElement upex)
	{
		if(initialplans==null)
		{
			initialplans	= new ArrayList<MConfigParameterElement>();
		}
		initialplans.add(upex);
	}

	/**
	 *  Get the initial events.
	 *  @return The initial events.
	 */
	public List<MConfigParameterElement> getInitialEvents()
	{
		return initialevents;
	}

	/**
	 *  Set the initial events.
	 *  @param initialevents The initial events to set.
	 */
	public void setInitialEvents(List<MConfigParameterElement> initialevents)
	{
		this.initialevents = initialevents;
	}

	/**
	 *  Add an initial event.
	 *  @param upex	The expression.
	 */
	public void	addInitialEvent(MConfigParameterElement upex)
	{
		if(initialevents==null)
		{
			initialevents	= new ArrayList<MConfigParameterElement>();
		}
		initialevents.add(upex);
	}
	
	/**
	 *  Get the end beliefs.
	 *  @return The end beliefs.
	 */
	public List<MConfigBeliefElement> getEndBeliefs()
	{
		return endbeliefs;
	}

	/**
	 *  Set the end beliefs.
	 *  @param endbeliefs The end beliefs to set.
	 */
	public void setEndBeliefs(List<MConfigBeliefElement> endbeliefs)
	{
		this.endbeliefs = endbeliefs;
	}
	
	/**
	 *  Add an end belief.
	 *  @param upex	The expression.
	 */
	public void	addEndBelief(MConfigBeliefElement upex)
	{
		if(endbeliefs==null)
		{
			endbeliefs	= new ArrayList<MConfigBeliefElement>();
		}
		endbeliefs.add(upex);
	}
	
	/**
	 *  Get the end goals.
	 *  @return The end goals.
	 */
	public List<MConfigParameterElement> getEndGoals()
	{
		return endgoals;
	}

	/**
	 *  Set the end goals.
	 *  @param endgoals The end goals to set.
	 */
	public void setEndGoals(List<MConfigParameterElement> endgoals)
	{
		this.endgoals = endgoals;
	}

	/**
	 *  Add an end goal.
	 *  @param upex	The expression.
	 */
	public void	addEndGoal(MConfigParameterElement upex)
	{
		if(endgoals==null)
		{
			endgoals	= new ArrayList<MConfigParameterElement>();
		}
		endgoals.add(upex);
	}
	
	/**
	 *  Get the end plans.
	 *  @return The end plans.
	 */
	public List<MConfigParameterElement> getEndPlans()
	{
		return endplans;
	}

	/**
	 *  Set the end plans.
	 *  @param endplans The end plans to set.
	 */
	public void setEndPlans(List<MConfigParameterElement> endplans)
	{
		this.endplans = endplans;
	}

	/**
	 *  Add an end plan.
	 *  @param upex	The expression.
	 */
	public void	addEndPlan(MConfigParameterElement upex)
	{
		if(endplans==null)
		{
			endplans	= new ArrayList<MConfigParameterElement>();
		}
		endplans.add(upex);
	}

	/**
	 *  Get the end events.
	 *  @return The end events.
	 */
	public List<MConfigParameterElement> getEndEvents()
	{
		return endevents;
	}

	/**
	 *  Set the end events.
	 *  @param endevents The end events to set.
	 */
	public void setEndEvents(List<MConfigParameterElement> endevents)
	{
		this.endevents = endevents;
	}

	/**
	 *  Add an end event.
	 *  @param upex	The expression.
	 */
	public void	addEndEvent(MConfigParameterElement upex)
	{
		if(endevents==null)
		{
			endevents	= new ArrayList<MConfigParameterElement>();
		}
		endevents.add(upex);
	}
	
	/**
	 *  Get the initial capabilities.
	 */
	public Map<String, String>	getInitialCapabilities()
	{
		return initialcapabilities;
	}
	
	/**
	 *  Add an initial capability.
	 */
	public void	addInitialCapability(String name, String configuration)
	{
		if(initialcapabilities==null)
		{
			initialcapabilities	= new LinkedHashMap<String, String>();
		}
		initialcapabilities.put(name, configuration);
	}
}
