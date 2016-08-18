package jadex.bdi.testcases.misc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.concurrent.TimeoutException;

/**
 *  Check correct operation of end state abort.
 */
public class EndStateAbortPlan extends Plan
{
	protected static Set<IComponentIdentifier>	TERMINATED	= Collections.synchronizedSet(new HashSet<IComponentIdentifier>());
	
	/**
	 *  Plan body.
	 */
	public void body()
	{
		// Create worker agent.
		IComponentManagementService	cms	= getAgent().getComponentFeature(IRequiredServicesFeature.class)
			.searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		IComponentIdentifier	worker	= cms.createComponent("/jadex/bdi/testcases/misc/EndStateAbortWorker.agent.xml",
			new CreationInfo(getComponentIdentifier())).getFirstResult();

		// Kill worker and wait for result.
		TestReport	report	= new TestReport("termination", "Test if the worker agent terminates with timeout.");
		try
		{
			cms.destroyComponent(worker).get();
			report.setFailed("Worker agent terminated without timeout.");
		}
		catch(TimeoutException e)
		{
			report.setSucceeded(true);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		// Check if worker agent thread has been correctly removed.
		report	= new TestReport("cleanup", "Test if the worker agent thread has been terminated");
		if(TERMINATED.contains(worker))
		{
			TERMINATED.remove(worker);
			report.setSucceeded(true);
		}
		else
		{
			report.setFailed("Worker agent thread did not finish.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
	}
}
