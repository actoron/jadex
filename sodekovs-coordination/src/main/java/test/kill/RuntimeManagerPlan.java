package test.kill;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.service.SServiceProvider;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;

public class RuntimeManagerPlan extends Plan {

	public void body() {
		System.out.println("## New Runtime Manager");
		waitFor(4000);
		IComponentManagementService ces = (IComponentManagementService) SServiceProvider
				.getService(getScope().getServiceContainer(),
						IComponentManagementService.class).get(this);
		AbstractEnvironmentSpace space = (AbstractEnvironmentSpace) getScope()
				.getParent().getExtension("mycoordspace");
		ces.destroyComponent(space.getExternalAccess().getComponentIdentifier());
	}
}
