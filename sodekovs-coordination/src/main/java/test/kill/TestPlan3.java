package test.kill;

import jadex.bdi.runtime.Plan;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;

/**
 * This Plan is used to update the environment. Right now it is used as an observer to show observed values of agents / the application.
 * 
 * 
 */
@SuppressWarnings("serial")
public class TestPlan3 extends Plan {

	public void body() {

		waitFor(4000);

		// AbstractEnvironmentSpace space = (AbstractEnvironmentSpace) getBeliefbase().getBelief("env").getFact();
		// IComponentManagementService ces = (IComponentManagementService)SServiceProvider.getService(getScope().getServiceProvider(), IComponentManagementService.class).get(this);
		System.out.println("Killing appl ...");
		// ces.destroyComponent(space.getContext().getComponentIdentifier()).get(this);
		// System.out.println("Killed appl ...");

		IComponentManagementService ces = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceContainer(), IComponentManagementService.class).get(this);
		AbstractEnvironmentSpace space = (AbstractEnvironmentSpace) getScope().getParent().getExtension("mycoordspace");
		ces.destroyComponent(space.getExternalAccess().getComponentIdentifier());
	}
}
