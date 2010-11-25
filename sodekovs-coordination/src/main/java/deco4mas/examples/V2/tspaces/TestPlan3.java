package deco4mas.examples.V2.tspaces;

import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.service.SServiceProvider;


/**
 * This Plan is used to update the environment. Right now it is used as an
 * observer to show observed values of agents / the application.
 * 
 * 
 */
@SuppressWarnings("serial")
public class TestPlan3 extends Plan {


	public void body() {

		waitFor(7000);
		
		AbstractEnvironmentSpace space = (AbstractEnvironmentSpace) getBeliefbase().getBelief("env").getFact();
		IComponentManagementService ces = (IComponentManagementService)SServiceProvider.getService(getScope().getServiceProvider(), IComponentManagementService.class).get(this);
		
//		IComponentIdentifier id1  = space.getContext().getComponentIdentifier();
		
		System.out.println("Killing appl ...");
		ces.destroyComponent(space.getContext().getComponentIdentifier()).get(this);
		System.out.println("Killed appl ...");
		
	}
}
