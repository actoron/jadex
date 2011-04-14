package test.kill;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.service.SServiceProvider;

public class RuntimeManagerPlan extends Plan {

	public void body() {
		System.out.println("## New Runtime Manager");
		waitFor(4000);
		IComponentManagementService ces = (IComponentManagementService)SServiceProvider.getService(getScope().getServiceContainer(), IComponentManagementService.class).get(this);
		AbstractEnvironmentSpace space = (AbstractEnvironmentSpace) ((IApplicationExternalAccess) getScope().getParent()).getSpace("mycoordspace");		
		ces.destroyComponent(space.getContext().getComponentIdentifier()).get(this);
	}	
	}
