package jadex.bdi.testcases;

import jadex.bdi.planlib.test.TestReport;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.collection.SCollection;

import java.util.List;
import java.util.Map;

/**
 *  Base class for test plans that create other agents.
 */
public abstract class AbstractMultipleAgentsPlan extends Plan
{
	//-------- attributes --------
	
	/** The list of agents. */
	protected List agents;
	
	/** The intended number of agents. */
	protected int agent_cnt;
	
	//-------- methods --------

	/**
	 *  Create agents of a given type with given arguments.
	 *  @param type The type.
	 *  @param args The args.
	 */
	protected List createAgents(String type, Map[] args)
	{
		return createAgents(type, null, args);
	}
	
	/**
	 *  Create agents of a given type with given arguments.
	 *  @param type The type.
	 *  @param config The configuration.
	 *  @param args The args.
	 */
	protected List createAgents(String type, String config, Map[] args)
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
//				IGoal ca = createGoal("ams_create_agent");
//				ca.getParameter("type").setValue(type);
//				if(config!=null)
//					ca.getParameter("configuration").setValue(config);
//				if(args[i]!=null)
//					ca.getParameter("arguments").setValue(args[i]);
//				dispatchSubgoalAndWait(ca);
//				agents.add(ca.getParameter("agentidentifier").getValue());
				
				SyncResultListener	listener	= new SyncResultListener();
				IComponentExecutionService ces = (IComponentExecutionService)getScope().getServiceContainer().getService(IComponentExecutionService.class);
				ces.createComponent(null, type, config, args[i], false, listener, getComponentIdentifier(), null);
				IComponentIdentifier aid = (IComponentIdentifier)listener.waitForResult();
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
//				IGoal da = createGoal("amscap.ams_destroy_agent");
//				da.getParameter("agentidentifier").setValue(agents.get(i));
//				dispatchSubgoalAndWait(da);
				
				SyncResultListener	listener	= new SyncResultListener();
				IComponentExecutionService ces	= (IComponentExecutionService)getScope().getServiceContainer().getService(IComponentExecutionService.class);
				ces.destroyComponent((IComponentIdentifier)agents.get(i), listener);
				IComponentIdentifier	aid	= (IComponentIdentifier)listener.waitForResult();
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
