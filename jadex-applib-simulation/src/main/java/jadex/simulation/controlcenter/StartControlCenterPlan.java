package jadex.simulation.controlcenter;

import jadex.bdi.runtime.Plan;


public class StartControlCenterPlan extends Plan{


	//start control center
	public void body() {
		ControlCenter gui = new ControlCenter(this.getExternalAccess());
				
	}

}
