package deco4mas.examples.agentNegotiation.deco.media;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.coordinate.environment.CoordinationSpaceObject;
import deco4mas.examples.agentNegotiation.common.trustInformation.TrustExecutionInformation;
import deco4mas.examples.agentNegotiation.common.trustInformation.TrustInformation;
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
			TrustInformation info = (TrustInformation) ((Map) ci.getValueByName("parameterDataMapping")).get("information");
			if (info instanceof TrustExecutionInformation)
				executionOccur(info);
		}

	}

	/**
	 * Just publish it to trust owner
	 * 
	 * @param info
	 *            the trust information, which occur
	 */
	private void executionOccur(TrustInformation info)
	{
		TrustExecutionInformation executionInfo = (TrustExecutionInformation) info;
		System.out.println("#perceiveCoordinationEvent " + executionInfo);
		mediumLogger.info("execution: " + executionInfo);

		// get Space
		CoordinationSpace env = (CoordinationSpace) space;

		CoordinationInfo coordInfo = new CoordinationInfo();

		HashMap<String, Object> parameterDataMappings = new HashMap<String, Object>();
		parameterDataMappings.put("information", executionInfo);

		// set parameter
		coordInfo.setName("Execution-" + executionInfo.getServiceType().getName() + "@" + executionInfo.getTime());
		coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
		coordInfo.addValue(Constants.PARAMETER_DATA_MAPPING, parameterDataMappings);
		coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, "BDI");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_NAME, "trustEvent");
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_TYPE, "INTERNAL_EVENT");
		coordInfo.addValue(Constants.DML_REALIZATION_NAME, "by_trust");

		System.out.println("#publish TrustEvent " + executionInfo);
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
		return NAME;
	}

}
