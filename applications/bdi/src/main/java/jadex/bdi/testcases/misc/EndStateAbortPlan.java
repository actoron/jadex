package jadex.bdi.testcases.misc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.TimeoutException;

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
		IComponentIdentifier	worker	= getAgent()
			.createComponent(null,
			new CreationInfo(getComponentIdentifier()).setFilename("/jadex/bdi/testcases/misc/EndStateAbortWorker.agent.xml")).getFirstResult();
		
		// Wait to allow worker to start plan
		waitFor(100);

		// Kill worker and wait for result.
		TestReport	report	= new TestReport("termination", "Test if the worker agent terminates with timeout.");
		try
		{
//			System.out.println("destroying worker: "+worker);
			agent.killComponent(worker).get();
//			System.out.println("destroyed worker: "+worker);
			report.setFailed("Worker agent terminated without timeout.");
		}
		catch(TimeoutException e)
		{
//			report.setFailed("Worker agent terminated with timeout.");
			report.setSucceeded(true);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		// Wait to allow worker to exit plan passed()
//		try
//		{
//			System.out.println("waiting for worker exit: "+worker);
			waitFor(100);
//			System.out.println("waited for worker exit: "+worker);
//		}
//		catch(RuntimeException e)
//		{
//			System.out.println("failed waiting for worker exit: "+worker+", "+e);
//			e.printStackTrace();
//			throw e;
//		}
//		catch(Error e)
//		{
//			System.out.println("failed waiting for worker exit: "+worker+", "+e);
//			e.printStackTrace();
//			throw e;
//		}
		
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
