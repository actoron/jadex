package jadex.bdi.testcases;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.collection.SCollection;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;

import java.util.List;
import java.util.Map;

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
//				IGoal ca = createGoal("cms_create_component");
//				ca.getParameter("type").setValue(type);
//				if(config!=null)
//					ca.getParameter("configuration").setValue(config);
//				if(args[i]!=null)
//					ca.getParameter("arguments").setValue(args[i]);
//				dispatchSubgoalAndWait(ca);
//				agents.add(ca.getParameter("componentidentifier").getValue());
				
//				SyncResultListener	listener	= new SyncResultListener();
				IComponentManagementService ces = (IComponentManagementService)SServiceProvider.getLocalService(getAgent(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
				ITuple2Future<IComponentIdentifier, Map<String, Object>>	ret = ces.createComponent(null, type, new CreationInfo(config, args[i], getComponentDescription().getResourceIdentifier()));
				IComponentIdentifier aid = (IComponentIdentifier)ret.getFirstResult();
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
//				IGoal da = createGoal("cmscap.cms_destroy_component");
//				da.getParameter("componentidentifier").setValue(agents.get(i));
//				dispatchSubgoalAndWait(da);
				
//				SyncResultListener	listener	= new SyncResultListener();
				IComponentManagementService ces = (IComponentManagementService)SServiceProvider.getLocalService(getAgent(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
				IFuture<Map<String, Object>> ret = ces.destroyComponent((IComponentIdentifier)agents.get(i));
				ret.get();
//				listener.waitForResult();
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
