package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;
import jadex.commons.collection.SCollection;

import java.util.List;
import java.util.Map;

/**
 *  Check correct operation of end states.
 */
public class EndStatePlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
		// Store report message from worker agent.
		getWaitqueue().addMessageEvent("inform_reports");
		
		// Create worker agent.
		IGoal	create	= createGoal("cmscap.cms_create_component");
		create.getParameter("type").setValue("/jadex/bdi/testcases/misc/EndStateWorker.agent.xml");
		Map args = SCollection.createHashMap();
		args.put("testagent", getComponentIdentifier());
		create.getParameter("arguments").setValue(args);
		create.getParameter("rid").setValue(getComponentDescription().getResourceIdentifier());
		dispatchSubgoalAndWait(create);
		IComponentIdentifier	worker	= (IComponentIdentifier)create.getParameter("componentidentifier").getValue();
		
		// Wait for reports from worker agent.
		IMessageEvent	msg	= waitForMessageEvent("inform_reports");
		getWaitqueue().removeMessageEvent("inform_reports");
		List	reports	= (List)msg.getParameter(SFipa.CONTENT).getValue();
		
		// Check if worker agent has been correctly removed.
		waitFor(1000);	// Hack!!! how to ensure that agent has time to remove itself?
		IGoal	search	= createGoal("cmscap.cms_search_components");
//		IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getServiceUpwards(
//			getComponentFeature(IRequiredServicesFeature.class), IComponentManagementService.class).get();
//		search.getParameter("description").setValue(cms.createComponentDescription(worker, null, null, null, null, null));
		search.getParameter("description").setValue(new CMSComponentDescription(worker, null, false, false, false, false, false, null, null, null, null, -1, null, null, false));
		dispatchSubgoalAndWait(search);
		TestReport	report	= new TestReport("termination", "Test if the worker agent has been terminated");
		if(search.getParameterSet("result").getValues().length==0)
		{
			report.setSucceeded(true);
		}
		else
		{
			report.setFailed("Worker agent still alive.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		// Add test results from worker.
		for(int i=0; i<reports.size(); i++)
		{
			getBeliefbase().getBeliefSet("testcap.reports").addFact(reports.get(i));
		}

		// Create deregister agent.
		report	= new TestReport("deregister", "Test if an agent can deregister on termination.");
		create	= createGoal("cmscap.cms_create_component");
		create.getParameter("type").setValue("/jadex/bdi/testcases/misc/EndStateDeregister.agent.xml");
		create.getParameter("rid").setValue(getComponentDescription().getResourceIdentifier());
		dispatchSubgoalAndWait(create);
		IComponentIdentifier deregister	= (IComponentIdentifier)create.getParameter("componentidentifier").getValue();

		// Check if deregister agent is registered.
		waitFor(100);	// Hack!!! how to ensure that agent has time to register itself?
		IDF df = (IDF)SServiceProvider.getLocalService(getInterpreter(), IDF.class, RequiredServiceInfo.SCOPE_PLATFORM);
		IDFServiceDescription sd = df.createDFServiceDescription(null, "endstate_testservice", null);
		IDFComponentDescription ad = df.createDFComponentDescription(null, sd);
		
		IGoal	dfsearch	= createGoal("dfcap.df_search");
		dfsearch.getParameter("description").setValue(ad);
		dispatchSubgoalAndWait(dfsearch);
		if(dfsearch.getParameterSet("result").getValues().length==0)
		{
			report.setFailed("Agent is not registered at DF.");
		}
		else
		{
			// Kill deregister agent.
			IGoal	destroy	= createGoal("cmscap.cms_destroy_component");
			destroy.getParameter("componentidentifier").setValue(deregister);
			dispatchSubgoalAndWait(destroy);
			
			// Check if deregister agent is deregistered.
			waitFor(100);	// Hack!!! how to ensure that agent has time to deregister itself?
			dfsearch	= createGoal("dfcap.df_search");
			dfsearch.getParameter("description").setValue(ad);
			dispatchSubgoalAndWait(dfsearch);
			if(dfsearch.getParameterSet("result").getValues().length!=0)
			{
				report.setFailed("Agent is still registered at DF.");
			}
			else
			{
				// Check if deregister agent has been correctly removed.
				search = createGoal("cmscap.cms_search_components");
//				search.getParameter("description").setValue(cms.createComponentDescription(deregister, null, null, null, null, null));
				search.getParameter("description").setValue(new CMSComponentDescription(deregister, null, false, false, false, false, false, null, null, null, null, -1, null, null, false));
				dispatchSubgoalAndWait(search);
				if(search.getParameterSet("result").getValues().length!=0)
				{
					report.setFailed("Deregister agent still alive.");
				}
				else
				{
					report.setSucceeded(true);
				}
			}
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
	}
}
