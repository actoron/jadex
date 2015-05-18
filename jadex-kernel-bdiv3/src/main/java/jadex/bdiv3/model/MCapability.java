package jadex.bdiv3.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *  The capability model.
 */
public class MCapability extends MElement
{
	/** The beliefs. */
	protected List<MBelief> beliefs;

	/** The goals. */
	protected List<MGoal> goals;
	
	/** The plans. */
	protected List<MPlan> plans;

	/** The message events. */
	protected List<MMessageEvent> messages;
	
	/** The services. */
	protected List<MServiceCall> services;
	
	/** The configurations. */
	protected List<MConfiguration> configurations;
	
	//-------- additional xml properties --------

	/** The subcapabilities. */
	protected List<MCapabilityReference>	subcapabilities;
	
	/**
	 *	Bean Constructor. 
	 */
	public MCapability()
	{
	}
	
	/**
	 *  Create a capability.
	 */
	public MCapability(String name)
	{
		super(name);
	}

	/**
	 *  Get the beliefs.
	 *  @return The beliefs.
	 */
	public List<MBelief> getBeliefs()
	{
		return beliefs==null? Collections.EMPTY_LIST: beliefs;
	}

	/**
	 *  Set the beliefs.
	 *  @param beliefs The beliefs to set.
	 */
	public void setBeliefs(List<MBelief> beliefs)
	{
		this.beliefs = beliefs;
	}

	/**
	 *  Add a belief.
	 */
	public void addBelief(MBelief belief)
	{
		if(beliefs==null)
			beliefs = new ArrayList<MBelief>();
		beliefs.add(belief);
	}
	
	/**
	 *  Test if a belief is contained.
	 */
	public boolean hasBelief(String name)
	{
		boolean ret = false;
		
		if(beliefs!=null && name!=null)
		{
			for(MBelief bel: beliefs)
			{
				ret = name.equals(bel.getName());
				if(ret)
					break;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get a belief.
	 */
	public MBelief getBelief(String name)
	{
		MBelief ret = null;
		
		if(beliefs!=null && name!=null)
		{
			for(MBelief bel: beliefs)
			{
				if(name.equals(bel.getName()))
				{
					ret = bel;
					break;
				}
			}
		}
		
		return ret;
	}
	
	
	/**
	 *  Get the goals.
	 *  @return The goals.
	 */
	public List<MGoal> getGoals()
	{
		return goals==null? Collections.EMPTY_LIST: goals;
	}

	/**
	 *  Set the goals.
	 *  @param goals The goals to set.
	 */
	public void setGoals(List<MGoal> goals)
	{
		this.goals = goals;
	}
	
	/**
	 *  Add a goal.
	 */
	public void addGoal(MGoal goal)
	{
		if(goals==null)
			goals = new ArrayList<MGoal>();
		goals.add(goal);
	}
	
	/**
	 *  Get the goal for its name.
	 *  @return The goal.
	 */
	public MGoal getGoal(String name)
	{
		MGoal ret = null;
		if(goals!=null)
		{
			for(MGoal goal: goals)
			{
				if(goal.getName().endsWith(name))
				{
					ret = goal;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 *  Get the plans.
	 *  @return The plans.
	 */
	public List<MPlan> getPlans()
	{
		return plans==null? Collections.EMPTY_LIST: plans;
	}

	/**
	 *  Set the plans.
	 *  @param plans The plans to set.
	 */
	public void setPlans(List<MPlan> plans)
	{
		this.plans = plans;
	}
	
	/**
	 *  Add a plan.
	 */
	public void addPlan(MPlan plan)
	{
		if(plans==null)
			plans = new ArrayList<MPlan>();
		plans.add(plan);
	}
	
	/**
	 *  Get the plan for its name.
	 *  @return The plan.
	 */
	public MPlan getPlan(String name)
	{
		MPlan ret = null;
		if(plans!=null)
		{
			for(MPlan plan: plans)
			{
				if(plan.getName().equals(name))
				{
					ret = plan;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Sorts plans according to their line numbers in the source
	 *  to guarantee their natural declaration order.
	 */
	public void sortPlans(final ClassLoader cl)
	{
		if(plans!=null)
		{
			Collections.sort(plans, new Comparator<MPlan>()
			{
				public int compare(MPlan p1, MPlan p2)
				{
					int ln1 = p1.getBody().getLineNumber(cl);
					int ln2 = p2.getBody().getLineNumber(cl);
					return ln1-ln2;
				}
			});
		}
	}
	
	/**
	 *  Get the messages.
	 *  @return The messages.
	 */
	public List<MMessageEvent> getMessageEvents()
	{
		return messages==null? Collections.EMPTY_LIST: messages;
	}

	/**
	 *  Set the messages.
	 *  @param messages The messages to set.
	 */
	public void setMessageEvents(List<MMessageEvent> messages)
	{
		this.messages = messages;
	}

	/**
	 *  Add a message.
	 */
	public void addMessageEvent(MMessageEvent message)
	{
		if(messages==null)
			messages = new ArrayList<MMessageEvent>();
		messages.add(message);
	}
	
	/**
	 *  Test if a message is contained.
	 */
	public boolean hasMessageEvent(String name)
	{
		boolean ret = false;
		
		if(messages!=null && name!=null)
		{
			for(MMessageEvent bel: messages)
			{
				ret = name.equals(bel.getName());
				if(ret)
					break;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get a message.
	 */
	public MMessageEvent getMessageEvent(String name)
	{
		MMessageEvent ret = null;
		
		if(messages!=null && name!=null)
		{
			for(MMessageEvent bel: messages)
			{
				if(name.equals(bel.getName()))
				{
					ret = bel;
					break;
				}
			}
		}
		
		return ret;
	}

	/**
	 *  Get the configurations.
	 *  @return The configurations.
	 */
	public List<MConfiguration> getConfigurations()
	{
		return configurations==null? Collections.EMPTY_LIST: configurations;
	}

	/**
	 *  Set the configurations.
	 *  @param configurations The configurations to set.
	 */
	public void setConfigurations(List<MConfiguration> configurations)
	{
		this.configurations = configurations;
	}
	
	/**
	 *  Add a configuration.
	 */
	public void addConfiguration(MConfiguration config)
	{
		if(configurations==null)
			configurations = new ArrayList<MConfiguration>();
		configurations.add(config);
	}
	
	/**
	 *  Get the configurations.
	 *  @return The configurations.
	 */
	public MConfiguration getConfiguration(String name)
	{
		MConfiguration ret = null;
		if(configurations!=null)
		{
			for(MConfiguration conf: configurations)
			{
				if(conf.getName().equals(name))
				{
					ret = conf;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 *  Get the services.
	 *  @return The services.
	 */
	public List<MServiceCall> getServices()
	{
		return services;
	}

	/**
	 *  Set the services.
	 *  @param services The services to set.
	 */
	public void setServices(List<MServiceCall> services)
	{
		this.services = services;
	}

	/**
	 *  Get the plan for its name.
	 *  @return The plan.
	 */
	public MServiceCall getService(String name)
	{
		MServiceCall ret = null;
		if(services!=null)
		{
			for(MServiceCall ser: services)
			{
				if(ser.getName().equals(name))
				{
					ret = ser;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Add a service.
	 */
	public void addservice(MServiceCall ser)
	{
		if(services==null)
			services = new ArrayList<MServiceCall>();
		services.add(ser);
	}
	
	/**
	 *  Get the sub capabilities.
	 *  @return The sub capabilities.
	 */
	public List<MCapabilityReference> getCapabilities()
	{
		return subcapabilities==null? Collections.EMPTY_LIST: subcapabilities;
	}

	/**
	 *  Set the sub capabilities.
	 *  @param subcapabilities The sub capabilities to set.
	 */
	public void setCapabilities(List<MCapabilityReference> subcapabilities)
	{
		this.subcapabilities = subcapabilities;
	}

	/**
	 *  Add a sub capability.
	 */
	public void addCapability(MCapabilityReference subcapability)
	{
		if(subcapabilities==null)
			subcapabilities = new ArrayList<MCapabilityReference>();
		subcapabilities.add(subcapability);
	}

}
