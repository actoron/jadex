package jadex.simulation.controlcenter;

import jadex.bdi.runtime.Plan;
import jadex.simulation.model.SimulationConfiguration;


public class StartControlCenterPlan extends Plan{


	//start control center
	public void body() {
		
		System.out.println("Starting control center: " + ((SimulationConfiguration)getBeliefbase().getBelief("simulationConf").getFact()).getName());
		
		ControlCenter gui = new ControlCenter(this.getExternalAccess());
	
		getBeliefbase().getBelief("tmpGUI").setFact(gui);
	}

}
