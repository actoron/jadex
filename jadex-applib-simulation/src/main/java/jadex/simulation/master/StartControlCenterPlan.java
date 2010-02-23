package jadex.simulation.master;

import jadex.bdi.runtime.Plan;
import jadex.simulation.controlcenter.Gui;

public class StartControlCenterPlan extends Plan{


	//start control center
	public void body() {
		Gui gui = new Gui(this.getExternalAccess());
	}

}
