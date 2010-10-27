package jadex.simulation.remote.test;

import jadex.bdi.runtime.Plan;
import jadex.commons.service.SServiceProvider;
import jadex.simulation.remote.IRemoteSimulationExecutionService;

import java.util.ArrayList;

public class FindRemoteServicePlan extends Plan{

	/**
	 * 
	 */
	private static final long serialVersionUID = -659084597244368398L;

	public void body() {
		
		System.out.println("Started Agent to find remote component.");
		waitFor(4500);		
		ArrayList<IRemoteSimulationExecutionService> res = (ArrayList<IRemoteSimulationExecutionService>) SServiceProvider.getServices(getScope().getServiceProvider(), IRemoteSimulationExecutionService.class, true, true).get(this);
		System.out.println("Nr. of found services: " + res.size());
		String myRes = (String) res.get(0).executeExperiment("simA").get(this);
		
		System.out.println("Result for caller: " + myRes);
		
	}

}
