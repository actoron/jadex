package test.kill;

import jadex.bdi.runtime.Plan;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;

public class RuntimeManagerPlan extends Plan {

	public void body() {
		System.out.println("## New Runtime Manager");
		waitFor(4000);
		IComponentManagementService ces = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceContainer(), IComponentManagementService.class).get(this);
		AbstractEnvironmentSpace space = (AbstractEnvironmentSpace) getScope().getParentAccess().getExtension("mycoordspace");
		ces.destroyComponent(space.getExternalAccess().getComponentIdentifier());
	}
}
