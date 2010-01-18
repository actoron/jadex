package jadex.simulation.master;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.simulation.helper.Constants;
import jadex.simulation.helper.XMLHandler;
import jadex.simulation.model.SimulationConfiguration;

import java.util.HashMap;

public class InitSimulationPlan extends Plan{

	public void body() {
		System.out.println("#InitSim# Init Master Simulation Agent.");		
		
		SimulationConfiguration simConf = (SimulationConfiguration) XMLHandler.parseXML("../jadex-applications-bdi/src/main/java/jadex/bdi/simulation/persist/TestXML.xml", SimulationConfiguration.class);
		initSettings(simConf);
		
		IGoal goal = createGoal("StartSimulationExperiments");
		System.out.println("#InitSim# Starting first round of Simulation Experiments.");
		dispatchTopLevelGoal(goal);
		
	}

	/**
	 * Init Beliefs according to parsed Simulation description.
	 * @param simConf
	 */
	private void initSettings(SimulationConfiguration simConf){		
	
		HashMap facts = (HashMap) getBeliefbase().getBelief("generalSimulationFacts").getFact();
		facts.put(Constants.SIMULATION_START_TIME, new Long(System.currentTimeMillis()));
		facts.put(Constants.SIMULATION_NAME, simConf.getName());
		facts.put(Constants.TOTAL_EXPERIMENT_COUNTER, new Integer(0));
		facts.put(Constants.ROW_EXPERIMENT_COUNTER, new Integer(0));
		facts.put(Constants.EXPERIMENT_ROW_COUNTER, new Integer(0));
		facts.put(Constants.EXPERIMENTS_PER_ROW_TO_DO, new Long(simConf.getRunConfiguration().getRows().getExperiments()));
		facts.put(Constants.ROWS_TO_DO, new Integer(simConf.getRunConfiguration().getGeneral().getRows()));
//		facts.put(new String("HSV"), new Integer(1887));
		
		getBeliefbase().getBelief("generalSimulationFacts").setFact(facts);
		getBeliefbase().getBelief("simulationConf").setFact(simConf);
	};
}
