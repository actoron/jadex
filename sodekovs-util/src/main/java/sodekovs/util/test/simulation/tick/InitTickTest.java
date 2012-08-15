package sodekovs.util.test.simulation.tick;

import jadex.bdi.runtime.Plan;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.simulation.ISimulationService;

public class InitTickTest extends Plan{

	public void body() {			
		System.out.println("Changing TICK SIZE");
		
//		IComponentManagementService cms = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		IClockService clockservice = (IClockService) getScope().getServiceContainer().getRequiredService("clockservice").get(this);
		
		ISimulationService simServ = (ISimulationService) getScope().getServiceContainer().getRequiredService("simulationservice").get(this);


		//change clock type
		simServ.setClockType(IClock.TYPE_TIME_DRIVEN);
		
		//change tick size
		clockservice.setDelta(1234);
		

		
											
	}
}
