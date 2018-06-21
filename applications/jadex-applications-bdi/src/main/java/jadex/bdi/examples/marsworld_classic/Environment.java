package jadex.bdi.examples.marsworld_classic;

import java.util.ArrayList;

import jadex.bridge.service.types.clock.IClockService;

/**
 *  The environment as singleton.
 */
public class Environment
{
	//-------- constants --------

	/** The agent types. */

	/** The carry agent type. */
	public static final String CARRY_AGENT = "carry_agent";

	/** The production agent type. */
	public static final String PRODUCTION_AGENT = "production_agent";

	/** The sentry agent type. */
	public static final String SENTRY_AGENT = "sentry_agent";

	//-------- attributes --------

	/** The hashtable containing all agent infos. */
	protected ArrayList agentinfos;

	/** The target locations. */
	protected ArrayList targets;

	/** The enviroment insstance. */
	protected static Environment instance;

	/** The agents homebase. */
	protected Homebase homebase;

	//-------- constructors --------

	/**
	 *  Create a new environment
	 */
	private Environment(IClockService clock)
	{
		this.agentinfos = new ArrayList();
		this.targets = new ArrayList();

		targets.add(new Target(new Location(0.1, 0.2), 0));
		targets.add(new Target(new Location(0.05, 0.7), 200));
		targets.add(new Target(new Location(0.5, 0.6), 0));
		targets.add(new Target(new Location(0.8, 0.1), 50));
		targets.add(new Target(new Location(0.7, 0.4), 100));
		targets.add(new Target(new Location(0.8, 0.8), 25));

//		targets.add(new Target(new Location(0.28, 0.28), 0));
//		targets.add(new Target(new Location(0.35, 0.35), 0));
//		targets.add(new Target(new Location(0.28, 0.32), 0));

//		Random	rand	= new Random();
//		for(int i=0; i<200; i++)
//		{
//			targets.add(new Target(new Location(rand.nextDouble(),
//					rand.nextDouble()), rand.nextInt(250)));
//		}
	
		this.homebase = new Homebase(new Location(0.3, 0.3), 90000, clock);
	}

	/**
	 *  Get the environment.
	 *  @return The environment.
	 */
	public static Environment getInstance()
	{
		if(instance==null)
			throw new RuntimeException("No instance available (use createInstance() first).");
		return instance;
	}

	/**
	 *  Create a new instance.
	 */
	public static Environment createInstance(IClockService clock)
	{
		instance = new Environment(clock);
		return instance;
	}

	/**
	 *  Clear the singleton instance.
	 */
	public static void clearInstance()
	{
		instance = null;
	}

	//-------- methods --------

	/**
	 *  The the info for an agent.
	 * /
	public void addTarget(Location target)
	{
		this.targets.put(target.getLocation(), target);
	}

	/**
	 *  Get all targets.
	 */
	public synchronized Target[] getTargets()
	{
		return (Target[])targets.toArray(new Target[targets.size()]);
	}

	/**
	 *  Get all targets near a position.
	 */
	public synchronized Target[] getTargetsNear(Location loc, double tolerance)
	{
		ArrayList ret = new ArrayList();
		for(int i=0; i<targets.size(); i++)
		{
			Target tmp = (Target)targets.get(i);
			if(tmp.getLocation().isNear(loc, tolerance))
				ret.add(tmp);
		}
		return (Target[])ret.toArray(new Target[ret.size()]);
	}

	/**
	 *  Get the homebase.
	 */
	public synchronized Homebase getHomebase()
	{
		return homebase;
	}

	/**
	 *  The the info for an agent.
	 */
	public synchronized void setAgentInfo(AgentInfo agentinfo)
	{
		this.agentinfos.remove(agentinfo);
		this.agentinfos.add(agentinfo);
	}

	/**
	 *  Get all agent infos.
	 */
	public synchronized AgentInfo[] getAgentInfos()
	{
		return (AgentInfo[])agentinfos.toArray(new AgentInfo[agentinfos.size()]);
	}

	/**
	 *  Get a target for a location.
	 *  WARNING: Method does not check if more
	 *  than one target is near.
	 */
	public synchronized Target getTarget(Location loc)
	{
		Target ret = null;
		for(int i=0; i<targets.size(); i++)
		{
			if(((Target)targets.get(i)).getLocation().isNear(loc))
			{
				ret = (Target)targets.get(i);
			}
		}
		if(ret==null)
			throw new RuntimeException("No target found for: "+loc+" "+targets);
		return ret;
	}

	/**
	 *  Get the target for the target-id.
	 */
	public synchronized Target getTarget(String id)
	{
		Target ret = null;
		for(int i=0; i<targets.size(); i++)
		{
			if(((Target)targets.get(i)).getId().equals(id))
			{
				ret = (Target)targets.get(i);
			}
		}
		if(ret==null)
			throw new RuntimeException("No target found for: "+id+" "+targets);
		return ret;
	}
}