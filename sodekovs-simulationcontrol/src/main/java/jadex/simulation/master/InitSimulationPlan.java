package jadex.simulation.master;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.collection.SCollection;
import jadex.simulation.helper.Constants;
import jadex.simulation.model.SimulationConfiguration;
import jadex.simulation.model.StartTime;
import jadex.simulation.model.result.IntermediateResult;

import java.util.HashMap;
import java.util.Map;

import sodekovs.benchmarking.logger.ScheduleLogger;
import sodekovs.util.misc.TimeConverter;
import sodekovs.util.misc.XMLHandler;

public class InitSimulationPlan extends Plan{

	public void body() {			
		System.out.println("#InitSim# Init Master Simulation Agent with configuration file: " + (String) getBeliefbase().getBelief("simulationDescriptionFile").getFact());		
		String simulationDescription = (String) getBeliefbase().getBelief("simulationDescriptionFile").getFact();
		SimulationConfiguration simConf = (SimulationConfiguration) XMLHandler.parseXMLFromXMLFile(simulationDescription, SimulationConfiguration.class);
		

		//Start Simulation Control Center
		IComponentManagementService ces = (IComponentManagementService)SServiceProvider.getService(getScope().getServiceContainer(), IComponentManagementService.class,RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		Map args = SCollection.createHashMap();
		args.put("simulationConf", simConf);		
//		IFuture ret = ces.createComponent(null, "/jadex/simulation/controlcenter/ControlCenter.agent.xml", new CreationInfo(args), null);
//		IComponentIdentifier aid = (IComponentIdentifier)ret.get(this);
		
//		IFuture ret = ces.createComponent(name, "/jadex/bdi/benchmarks/AgentCreation.agent.xml", new CreationInfo(args), null);
		
		
		//Postpone start of simulation?
		if(simConf.getRunConfiguration().getGeneral().getStartTime() != null){
			StartTime sTime = simConf.getRunConfiguration().getGeneral().getStartTime();
			if(sTime.getType().equalsIgnoreCase(Constants.RELATIVE_TIME_EXPRESSION)){
				System.out.println("#InitSim# Start of simulation postponed till: " + TimeConverter.longTime2DateString(System.currentTimeMillis() + Long.valueOf(sTime.getValue())) + " due relative start time condition.");
				waitFor(Long.valueOf(sTime.getValue()));
			}else if(sTime.getType().equalsIgnoreCase(Constants.ABSOLUTE_TIME_EXPRESSION)){
				long absoluteStartTime = TimeConverter.dateString2LongTime(sTime.getValue());
				System.out.println("#InitSim# Start of simulation postponed till: " + TimeConverter.longTime2DateString(absoluteStartTime) + " due absolute start time condition.");
				waitFor(absoluteStartTime - System.currentTimeMillis());
			}
		}
		
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
		
//		ControlCenter tmpGui = new ControlCenter(this.getExternalAccess());
//		getBeliefbase().getBelief("tmpGUI").setFact(tmpGui);
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
