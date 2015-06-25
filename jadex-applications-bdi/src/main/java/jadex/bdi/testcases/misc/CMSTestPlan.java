package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.fipa.SearchConstraints;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.IComponentDescription;

/**
 *  Test the CMS plans.
 */
public class CMSTestPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		int num=1;
		num = performTests(num, null); // test locally
		
		// Todo: support remote CMS agent!?
//		IComponentManagementService ces = (IComponentManagementService)SServiceProvider.getServiceUpwards(
//			getScope().getComponentFeature(IRequiredServicesFeature.class), IComponentManagementService.class).get();
//		IComponentIdentifier aa = ces.createComponentIdentifier(SFipa.CMS_COMPONENT, getComponentIdentifier(), null);
		IComponentIdentifier aa = new BasicComponentIdentifier(SFipa.CMS_COMPONENT, getComponentIdentifier());
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
		
		// Try to search the CMS.
		TestReport tr = new TestReport("#"+num++, "Searching for all agents");
		getLogger().info("\nSearching for all agents.");
		IComponentDescription desc = new CMSComponentDescription(null, null, false, false, false, false, false, null, null, null, null, -1, null, null, false);
		ISearchConstraints constraints = new SearchConstraints(-1, 0);
		
		IGoal	search	= createGoal("cmscap.cms_search_components");
		search.getParameter("description").setValue(desc);
		search.getParameter("constraints").setValue(constraints);
		search.getParameter("cms").setValue(ams);
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
//			e.printStackTrace();
			tr.setReason("Search subgoal failed.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		// Try to create agent.
		tr = new TestReport("#"+num++, "Trying to create agent.");
		getLogger().info("\nTrying to create agent.");
		IGoal	create	= createGoal("cmscap.cms_create_component");
		create.getParameter("type").setValue(agenttype);
		create.getParameter("cms").setValue(ams);
		create.getParameter("rid").setValue(getComponentDescription().getResourceIdentifier());
//		System.out.println("rid: "+getComponentDescription().getResourceIdentifier().getLocalIdentifier());
		IComponentIdentifier agent = null;
		try
		{
			dispatchSubgoalAndWait(create);
			//getLogger().info("Success: Created "+create.getResult());
			agent = (IComponentIdentifier)create.getParameter("componentidentifier").getValue();
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
			
//			desc = amsservice.createComponentDescription((IComponentIdentifier)create.getParameter("componentidentifier").getValue(), null, null, null, null, null);
			desc = new CMSComponentDescription((IComponentIdentifier)create.getParameter("componentidentifier").getValue(), null, false, false, false, false, false, null, null, null, null, -1, null, null, false);
//			constraints = amsservice.createSearchConstraints(-1, 0);
			constraints = new SearchConstraints(-1, 0);
		
			search	= createGoal("cmscap.cms_search_components");
			search.getParameter("description").setValue(desc);
			search.getParameter("constraints").setValue(constraints);
			search.getParameter("cms").setValue(ams);
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

		if(agent!=null)
		{
			tr = new TestReport("#"+num++, "Get an external access.");
			if(create.isSucceeded())
			{
				getLogger().info("\nTry to get an external access.");
				
				IGoal gext = createGoal("cmscap.cms_get_externalaccess");
				gext.getParameter("componentidentifier").setValue(agent);
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
			IGoal	destroy	= createGoal("cmscap.cms_destroy_component");
			//destroy.getParameter("componentidentifier").setValue(create.getResult());
			destroy.getParameter("componentidentifier").setValue(create.getParameter("componentidentifier").getValue());
			destroy.getParameter("cms").setValue(ams);
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
			
//			desc = amsservice.createComponentDescription((IComponentIdentifier)create.getParameter("componentidentifier").getValue(), null, null, null, null, null);
//			constraints = amsservice.createSearchConstraints(-1, 0);
			desc = new CMSComponentDescription((IComponentIdentifier)create.getParameter("componentidentifier").getValue(), null, false, false, false, false, false, null, null, null, null, -1, null, null, false);
			constraints = new SearchConstraints(-1, 0);
			
			search	= createGoal("cmscap.cms_search_components");
			search.getParameter("description").setValue(desc);
			search.getParameter("constraints").setValue(constraints);
			search.getParameter("cms").setValue(ams);
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

		/*IGoal shutdown = createGoal("cms_shutdown_platform");
		shutdown.getParameter("cms").setValue(ams);
		dispatchSubgoalAndWait(shutdown);*/

		/*getLogger().info("Test 1: Creating an agent per message!");
		IGoal ca = createGoal("cms_create_component");
		ca.getParameter("type").setValue("jadex.bdi.testcases.benchmarks.AgentCreation");
		ca.getParameter("name").setValue("Creator");
		ca.getParameterSet("arguments").addValue(Integer.valueOf(5));
		ca.getParameter("cms").setValue(SFipa.CMS);
		dispatchSubgoalAndWait(ca);
		CreateAgent ca = new CreateAgent();
		ca.setType("jadex.bdi.testcases.benchmarks.AgentCreation");
		ca.setName("Creator");
		ca.addArgument(Integer.valueOf(5));
		IMessageEvent rca = createMessageEvent("request_create_component");
		rca.getParameterSet(SFipa.RECEIVERS).addValue(SFipa.CMS);
		rca.setContent(ca);
		IMessageEvent reply = sendMessageAndWait(rca, 10000);
		getLogger().info("Test 1 succeeded.");

		/*getLogger().info("Test 2: Destroying an agent per message!");
		DestroyAgent da = new DestroyAgent();
		da.setAgentIdentifier(((CreateAgent)((Done)reply.getContent()).getAction()).getAgentIdentifier());
		IMessageEvent rda = createMessageEvent("request_destroy_component");
		rda.getParameterSet(SFipa.RECEIVERS).addValue(SFipa.CMS);
		rda.setContent(da);
		sendMessageAndWait(rda, 10000);
		getLogger().info("Test 2 succeeded.");

		getLogger().info("Test 3: Searching for agents per message!");
		SearchAgents sa = new SearchAgents();
		CMSAgentDescription	desc	= new CMSAgentDescription();
		SearchConstraints	constraints	= new SearchConstraints();
		constraints.setMaxResults(-1);
		sa.setAgentDescription(desc);
		sa.setSearchConstraints(constraints);
		IMessageEvent sda = createMessageEvent("request_search_components");
		sda.getParameterSet(SFipa.RECEIVERS).addValue(SFipa.CMS);
		sda.setContent(sa);
		IMessageEvent rep = sendMessageAndWait(sda, 10000);
		getLogger().info("Test 3 succeeded: "+rep);

		getLogger().info("Test 4: Shutdown platform per message!");
		ShutdownPlatform sp = new ShutdownPlatform();
		IMessageEvent spa = createMessageEvent("request_shutdown_platform");
		spa.getParameterSet(SFipa.RECEIVERS).addValue(SFipa.CMS);
		spa.setContent(sp);
		IMessageEvent re = sendMessageAndWait(spa, 10000);
		getLogger().info("Test 4 succeeded: "+re);*/
	}
}
