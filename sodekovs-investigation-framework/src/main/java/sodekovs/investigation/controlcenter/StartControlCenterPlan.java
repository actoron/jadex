package sodekovs.investigation.controlcenter;

import jadex.bdi.runtime.Plan;
import sodekovs.investigation.model.InvestigationConfiguration;


public class StartControlCenterPlan extends Plan{


	//start control center
	public void body() {
		
		System.out.println("Starting control center: " + ((InvestigationConfiguration)getBeliefbase().getBelief("investigationConf").getFact()).getName());
		
		ControlCenter gui = new ControlCenter(this.getExternalAccess());
	
//		getBeliefbase().getBelief("tmpGUI").setFact(gui);
	}

}
