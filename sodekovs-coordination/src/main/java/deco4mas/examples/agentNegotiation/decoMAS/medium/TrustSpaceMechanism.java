package deco4mas.examples.agentNegotiation.decoMAS.medium;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.coordinate.environment.CoordinationSpaceObject;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.Execution;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInfo;
import deco4mas.mechanism.CoordinationInformation;
import deco4mas.mechanism.ICoordinationMechanism;

/**
 * Implements a negotiation coordination mechanism.
 */
public class TrustSpaceMechanism extends ICoordinationMechanism
{
	private Logger mediumLogger = AgentLogger.getTimeEvent("TrustSpaceMedium");

	private String realisationName = "by_trust";

	/** name for the medium */
	public static String NAME = "by_trust";

	public TrustSpaceMechanism(CoordinationSpace space)
	{
		super(space);
	}

	/**
	 * called by every CoordinationInformation
	 */
	public void perceiveCoordinationEvent(Object obj)
	{
		if (obj instanceof CoordinationInformation)
		{
			CoordinationInformation ci = (CoordinationInformation) obj;
			String task = (String) ((Map) ci.getValueByName("parameterDataMapping")).get("task");
			if (task.equals("executionOccur"))
				executionOccur(ci);
		}

	}

	/**
	 * Just publish it to smas
	 * 
	 * @param ci
	 */
	private void executionOccur(CoordinationInformation ci)
	{
		// get Execution
		Execution execution = (Execution) ((Map) ci.getValueByName("parameterDataMapping")).get("execution");
		System.out.println("#perceiveCoordinationEvent " + (String) ((Map) ci.getValueByName("parameterDataMapping")).get("task"));
		mediumLogger.info("execution (" + execution.getEvent().toString() + ") from " + execution.getSa() + " for " + execution.getSma());

		// get Space
		CoordinationSpace env = (CoordinationSpace) space;

		CoordinationInfo coordInfo = new CoordinationInfo();

		HashMap<String, Object> parameterDataMappings = new HashMap<String, Object>();
		parameterDataMappings.put("execution", execution);

		// set parameter
		coordInfo.setName("Execution-" + execution.getServiceType().getName() + "@" + execution.getTime());
		coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
		coordInfo.addValue(Constants.PARAMETER_DATA_MAPPING, parameterDataMappings);
		coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, "BDI");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_NAME, "trustEvent");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_TYPE, "INTERNAL_EVENT");
		coordInfo.addValue(Constants.DML_REALIZATION_NAME, "by_trust");

		System.out.println("#publish TrustEvent for " + execution.getServiceType().getName());
		env.publishCoordinationEvent(coordInfo);
	}

	/**
	 * Start Medium
	 */
	public void start()
	{
		System.out.println("#Start Mechanism Trust");
	}

	public void stop()
	{
	}

	public void suspend()
	{
	}

	public void restart()
	{
	}

	public String getRealisationName()
	{
		return realisationName;
	}

}
