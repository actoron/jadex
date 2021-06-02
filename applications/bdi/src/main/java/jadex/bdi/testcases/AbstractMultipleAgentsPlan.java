package jadex.bdi.testcases;

import java.util.List;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.collection.SCollection;
import jadex.commons.future.IFuture;

/**
 *  Base class for test plans that create other agents.
 */
public abstract class AbstractMultipleAgentsPlan extends Plan
{
	//-------- attributes --------
	
	/** The list of agents. */
	protected List<IComponentIdentifier> agents;
	
	/** The intended number of agents. */
	protected int agent_cnt;
	
	//-------- methods --------

	/**
	 *  Create agents of a given type with given arguments.
	 *  @param type The type.
	 *  @param args The args.
	 */
	protected List<IComponentIdentifier> createAgents(String type, Map<String, Object>[] args)
	{
		return createAgents(type, null, args);
	}
	
	/**
	 *  Create agents of a given type with given arguments.
	 *  @param type The type.
	 *  @param config The configuration.
	 *  @param args The args.
	 */
	protected List<IComponentIdentifier> createAgents(String type, String config, Map<String, Object>[] args)
	{
		if(agents!=null)
			throw new RuntimeException("Create agents is intended to be called only " +
				"when previous agents have been killed");
		
		this.agent_cnt = args.length;
		this.agents = SCollection.createArrayList();
		
		try
		{
			for(int i=0; i<args.length; i++)
			{
				IComponentIdentifier aid = getAgent().createComponent(new CreationInfo(config, args[i]).setFilename(type)).get().getId();
				agents.add(aid);
			}
		}
		catch(GoalFailureException e)
		{
			getLogger().severe("Exception while creating the agents of testcase: "+ e);
		}
		return agents;
	}
	
	/**
	 *  Destroy the agents.
	 */
	protected void destroyAgents()
	{
		if(agents==null)
			throw new RuntimeException("Destroy agents is intended to be " +
				"called only when agents have been created");
		
		for(int i=0; i<agents.size(); i++)
		{
//			System.out.println("Killing " + ((IComponentIdentifier)agents.get(i)).getName());
			try
			{
				IFuture<Map<String, Object>> ret = getAgent().getExternalAccess((IComponentIdentifier)agents.get(i)).killComponent();
				ret.get();
			}
			catch(GoalFailureException e)
			{
				e.printStackTrace();
				getLogger().severe("Exception while destroying agent: "+agents.get(i));
			}
//			System.out.println("Killed " + ((IComponentIdentifier)agents.get(i)).getName());
		}
		agents = null;
	}
	
	/**
	 *  Assure that all agents needed for test are there.
	 *  @param tr The test report.
	 *  @return True, if test can be performed.
	 */
	protected boolean assureTest(TestReport tr)
	{
		boolean ret = agents.size()==agent_cnt;
		if(!ret)
			tr.setFailed("Not all agents could be created");
		return ret;
	}

	/**
	 *  The passed method is called on plan success.
	 */
	public void	passed()
	{
		//System.out.println("passed");
		if(agents!=null)
			destroyAgents();
		//System.out.println("passed end");
	}

	/**
	 *  The failed method is called on plan failure/abort.
	 */
	public void	failed()
	{
		//System.out.println("failed");
		if(agents!=null)
			destroyAgents();
		//System.out.println("failed end");
	}

	/**
	 *  The plan was aborted (because of conditional goal
	 *  success or termination from outside).
	 */
	public void aborted()
	{
		//System.out.println("aborted");
		if(agents!=null)
			destroyAgents();
		//System.out.println("aborted end");
	}
}
