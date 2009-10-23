package jadex.bdi.testcases.misc;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.planlib.test.TestReport;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ISearchConstraints;

/**
 *  Test the AMS plans.
 */
public class AMSTestPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		int num=1;
		num = performTests(num, null); // test locally
		IAMS ams = (IAMS)getScope().getPlatform().getService(IAMS.class);
		IComponentIdentifier aa = ams.createAgentIdentifier(SFipa.AMS_AGENT, true);
		performTests(num, aa); // test remotely
	}

	/**
	 *  The failed method is called on plan failure/abort.
	 */
	public void	failed()
	{
		System.err.println("\nSome tests failed!");
	}

	/**
	 *  Perform the basic ams tests.
	 */
	public int performTests(int num, IComponentIdentifier ams)
	{
//		String agenttype = "jadex/bdi/examples/ping/Ping.agent.xml";
		String agenttype = "jadex/bdi/tutorial/TranslationA1.agent.xml";
//		String agenttype = "jadex/bdi/examples/puzzle/Sokrates.agent.xml";
		
		// Try to search the AMS.
		TestReport tr = new TestReport("#"+num++, "Searching for all agents");
		getLogger().info("\nSearching for all agents.");
		IAMS amsservice = (IAMS)getScope().getPlatform().getService(IAMS.class);
		IComponentDescription desc = amsservice.createAMSAgentDescription(null);
		ISearchConstraints constraints = amsservice.createSearchConstraints(-1, 0);
		/*AMSAgentDescription	desc	= new AMSAgentDescription();
		SearchConstraints	constraints	= new SearchConstraints();
		constraints.setMaxResults(-1);*/
		
		IGoal	search	= createGoal("amscap.ams_search_agents");
		search.getParameter("description").setValue(desc);
		search.getParameter("constraints").setValue(constraints);
		search.getParameter("ams").setValue(ams);
		try
		{
			dispatchSubgoalAndWait(search);
			IComponentDescription[]	result	= (IComponentDescription[])search
				.getParameterSet("result").getValues();
			getLogger().info("Success! Found agents: "+result.length);
			for(int i=0; i< result.length; i++)
				getLogger().info("Agent "+i+": "+result[i].getName());
			tr.setSucceeded(true);
		}
		catch(GoalFailureException e)
		{
			e.printStackTrace();
			tr.setReason("Search subgoal failed.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		// Try to create agent.
		tr = new TestReport("#"+num++, "Trying to create agent.");
		getLogger().info("\nTrying to create agent.");
		IGoal	create	= createGoal("amscap.ams_create_agent");
		create.getParameter("type").setValue(agenttype);
		create.getParameter("ams").setValue(ams);
		IComponentIdentifier agent = null;
		try
		{
			dispatchSubgoalAndWait(create);
			//getLogger().info("Success: Created "+create.getResult());
			agent = (IComponentIdentifier)create.getParameter("agentidentifier").getValue();
			getLogger().info("Success: Created "+agent);
			tr.setSucceeded(true);
		}
		catch(GoalFailureException e)
		{
			e.printStackTrace();
			tr.setReason("Create agent subgoal failed.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		// Try to find agent.
		tr = new TestReport("#"+num++, "Searching for agent.");
		if(create.isSucceeded())
		{
			getLogger().info("\nSearching for agent.");
			
			desc = amsservice.createAMSAgentDescription((IComponentIdentifier)create.getParameter("agentidentifier").getValue());
			constraints = amsservice.createSearchConstraints(-1, 0);
		
			/*desc	= new AMSAgentDescription();
			//desc.setName((AgentIdentifier)create.getResult());
			desc.setName((AgentIdentifier)create.getParameter("agentidentifier").getValue());
			constraints	= new SearchConstraints();
			constraints.setMaxResults(-1);*/
			
			search	= createGoal("amscap.ams_search_agents");
			search.getParameter("description").setValue(desc);
			search.getParameter("constraints").setValue(constraints);
			search.getParameter("ams").setValue(ams);
			try
			{
				dispatchSubgoalAndWait(search);
				IComponentDescription[] result	= (IComponentDescription[])search.getParameterSet("result").getValues();
				if(result.length==1)
				{
					getLogger().info("Success! Found agent:"+result[0].getName());
					tr.setSucceeded(true);
				}
				else
				{
					//failure	= true;
					System.err.println("Failure! Found "+result.length+" agents.");
					for(int i=0; i< result.length; i++)
						System.err.println("Agent "+i+": "+result[i].getName());
					tr.setReason("Could not find agent.");
				}
			}
			catch(GoalFailureException e)
			{
				tr.setReason("Search subgoal failed.");
			}
		}
		else
		{
			tr.setReason("Cannot search because creation already failed.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		if(ams==null && agent!=null)
		{
			tr = new TestReport("#"+num++, "Get an external access.");
			if(create.isSucceeded())
			{
				getLogger().info("\nTry to get an external access.");
				
				IGoal gext = createGoal("amscap.ams_get_externalaccess");
				gext.getParameter("agentidentifier").setValue(agent);
				try
				{
					dispatchSubgoalAndWait(gext);
					IExternalAccess ext = (IExternalAccess)gext.getParameter("result").getValue();
					if(ext!=null)
						tr.setSucceeded(true);
				}
				catch(GoalFailureException e)
				{
					tr.setReason("Search subgoal failed.");
				}
			}
			else
			{
				tr.setReason("Cannot search because creation already failed.");
			}
			getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		}

		// Try to destroy created agent.
		tr = new TestReport("#"+num++, "Trying to destroy agent.");
		if(create.isSucceeded())
		{
			getLogger().info("\nTrying to destroy agent.");
			IGoal	destroy	= createGoal("amscap.ams_destroy_agent");
			//destroy.getParameter("agentidentifier").setValue(create.getResult());
			destroy.getParameter("agentidentifier").setValue(create.getParameter("agentidentifier").getValue());
			destroy.getParameter("ams").setValue(ams);
			try
			{
				dispatchSubgoalAndWait(destroy);
				getLogger().info("Success: Agent was destroyed.");
				tr.setSucceeded(true);
			}
			catch(GoalFailureException e)
			{
				tr.setReason("Destroy subgoal failed.");
			}
		}
		else
		{
			tr.setReason("Cannot destroy because creation already failed.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);


		//waitFor(200);
		tr = new TestReport("#"+num++, "Searching for agent again.");
		if(create.isSucceeded())
		{
			getLogger().info("\nSearching for agent again.");
			
			desc = amsservice.createAMSAgentDescription((IComponentIdentifier)create.getParameter("agentidentifier").getValue());
			constraints = amsservice.createSearchConstraints(-1, 0);
	
			/*desc	= new AMSAgentDescription();
			//desc.setName((AgentIdentifier)create.getResult());
			desc.setName((AgentIdentifier)create.getParameter("agentidentifier").getValue());
			constraints	= new SearchConstraints();
			constraints.setMaxResults(-1);*/
			
			search	= createGoal("amscap.ams_search_agents");
			search.getParameter("description").setValue(desc);
			search.getParameter("constraints").setValue(constraints);
			search.getParameter("ams").setValue(ams);
			try
			{
				dispatchSubgoalAndWait(search);
				IComponentDescription[] result	= (IComponentDescription[])search.getParameterSet("result").getValues();
				if(result.length==0)
				{
					getLogger().info("Success! Found 0 agents.");
					tr.setSucceeded(true);
				}
				else
				{
					//failure	= true;
					System.err.println("Failure! Found "+result.length+" agents.");
					for(int i=0; i< result.length; i++)
						System.err.println("Agent "+i+": "+result[i].getName());
					tr.setReason("Found an agent that should not be there.");
				}
			}
			catch(GoalFailureException e)
			{
				tr.setReason("Search subgoal failed.");
			}
		}
		else
		{
			tr.setReason("Cannot search because creation already failed.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		return num;
		
		// Test summary.
		/*if(failure)
			System.err.println("\nSome tests failed!");
		else
			getLogger().info("\nAll tests succeded.");*/

		/*IGoal shutdown = createGoal("ams_shutdown_platform");
		shutdown.getParameter("ams").setValue(ams);
		dispatchSubgoalAndWait(shutdown);*/

		/*getLogger().info("Test 1: Creating an agent per message!");
		IGoal ca = createGoal("ams_create_agent");
		ca.getParameter("type").setValue("jadex.bdi.testcases.benchmarks.AgentCreation");
		ca.getParameter("name").setValue("Creator");
		ca.getParameterSet("arguments").addValue(new Integer(5));
		ca.getParameter("ams").setValue(SFipa.AMS);
		dispatchSubgoalAndWait(ca);
		CreateAgent ca = new CreateAgent();
		ca.setType("jadex.bdi.testcases.benchmarks.AgentCreation");
		ca.setName("Creator");
		ca.addArgument(new Integer(5));
		IMessageEvent rca = createMessageEvent("request_create_agent");
		rca.getParameterSet(SFipa.RECEIVERS).addValue(SFipa.AMS);
		rca.setContent(ca);
		IMessageEvent reply = sendMessageAndWait(rca, 10000);
		getLogger().info("Test 1 succeeded.");

		/*getLogger().info("Test 2: Destroying an agent per message!");
		DestroyAgent da = new DestroyAgent();
		da.setAgentIdentifier(((CreateAgent)((Done)reply.getContent()).getAction()).getAgentIdentifier());
		IMessageEvent rda = createMessageEvent("request_destroy_agent");
		rda.getParameterSet(SFipa.RECEIVERS).addValue(SFipa.AMS);
		rda.setContent(da);
		sendMessageAndWait(rda, 10000);
		getLogger().info("Test 2 succeeded.");

		getLogger().info("Test 3: Searching for agents per message!");
		SearchAgents sa = new SearchAgents();
		AMSAgentDescription	desc	= new AMSAgentDescription();
		SearchConstraints	constraints	= new SearchConstraints();
		constraints.setMaxResults(-1);
		sa.setAgentDescription(desc);
		sa.setSearchConstraints(constraints);
		IMessageEvent sda = createMessageEvent("request_search_agents");
		sda.getParameterSet(SFipa.RECEIVERS).addValue(SFipa.AMS);
		sda.setContent(sa);
		IMessageEvent rep = sendMessageAndWait(sda, 10000);
		getLogger().info("Test 3 succeeded: "+rep);

		getLogger().info("Test 4: Shutdown platform per message!");
		ShutdownPlatform sp = new ShutdownPlatform();
		IMessageEvent spa = createMessageEvent("request_shutdown_platform");
		spa.getParameterSet(SFipa.RECEIVERS).addValue(SFipa.AMS);
		spa.setContent(sp);
		IMessageEvent re = sendMessageAndWait(spa, 10000);
		getLogger().info("Test 4 succeeded: "+re);*/
	}
}
