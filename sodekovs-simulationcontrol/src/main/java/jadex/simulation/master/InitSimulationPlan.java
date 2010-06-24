package jadex.simulation.master;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.commons.collection.SCollection;
import jadex.simulation.controlcenter.ControlCenter;
import jadex.simulation.helper.Constants;
import jadex.simulation.helper.XMLHandler;
import jadex.simulation.model.SimulationConfiguration;
import jadex.simulation.model.result.IntermediateResult;

import java.util.HashMap;
import java.util.Map;

public class InitSimulationPlan extends Plan{

	public void body() {			
		System.out.println("#InitSim# Init Master Simulation Agent with configuration file: " + (String) getBeliefbase().getBelief("simulationDescriptionFile").getFact());		
		String simulationDescription = (String) getBeliefbase().getBelief("simulationDescriptionFile").getFact();
		SimulationConfiguration simConf = (SimulationConfiguration) XMLHandler.parseXML(simulationDescription, SimulationConfiguration.class);
		initSettings(simConf);
		
		IGoal goal = createGoal("StartSimulationExperiments");
		System.out.println("#InitSim# Starting first round of Simulation Experiments.");
		dispatchTopLevelGoal(goal);
		
		//trigger the start of the simulation control center
//		IGoal ca = createGoal("cmscap.cms_create_component");
//		ca.getParameter("type").setValue("/jadex/simulation/client/ControlCenter.agent.xml");
//		Map args = SCollection.createHashMap();
//		args.put("simulationConf", getBeliefbase().getBelief("simulationConf").getFact());
//		ca.getParameter("arguments").setValue(args);
//		dispatchTopLevelGoal(ca);				
		
		ControlCenter tmpGui = new ControlCenter(this.getExternalAccess());
		getBeliefbase().getBelief("tmpGUI").setFact(tmpGui);
	}

	/**
	 * Init Beliefs according to parsed Simulation description.
	 * @param simConf
	 */
	private void initSettings(SimulationConfiguration simConf){		
	
		HashMap facts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
		facts.put(Constants.SIMULATION_START_TIME, getClock().getTime());
		facts.put(Constants.SIMULATION_NAME, simConf.getName());
		facts.put(Constants.TOTAL_EXPERIMENT_COUNTER, new Integer(0));
		facts.put(Constants.ROW_EXPERIMENT_COUNTER, new Integer(0));
		facts.put(Constants.EXPERIMENT_ROW_COUNTER, new Integer(0));
		facts.put(Constants.EXPERIMENTS_PER_ROW_TO_DO, new Long(simConf.getRunConfiguration().getRows().getExperiments()));
		facts.put(Constants.ROWS_TO_DO, new Integer(simConf.getRunConfiguration().getGeneral().getRows()));
		
		getBeliefbase().getBelief("generalSimulationFacts").setFact(facts);
		getBeliefbase().getBelief("simulationConf").setFact(simConf);
		getBeliefbase().getBelief("intermediateResults").setFact(new IntermediateResult(simConf));		
	};
}
