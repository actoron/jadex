package deco4mas.examples.agentNegotiation.deco.media;

import java.util.HashMap;
import deco.lang.dynamics.AgentElementType;
import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.coordinate.environment.CoordinationSpaceObject;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInfo;
import deco4mas.mechanism.CoordinationInformation;
import deco4mas.mechanism.ICoordinationMechanism;

/**
 * Implements a coordination mechanism, witch initialize a InternalEvent with
 * the same name as the element name of the coordination information
 */
public class DirectInternalEventMechanism extends ICoordinationMechanism
{
	/** name for the medium */
	public static String realisationName = "by_directInternalEvent";

	public DirectInternalEventMechanism(CoordinationSpace space)
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
			publishInternalEvent(ci);
		}

	}

	/**
	 * Just publish a Internal Event
	 * 
	 * @param ci
	 */
	private void publishInternalEvent(CoordinationInformation ci)
	{
		CoordinationSpace env = (CoordinationSpace) space;
		// set parameter
		CoordinationInfo coordInfo = new CoordinationInfo();

		coordInfo.setName("directInformation");
		HashMap<String, Object> parameter = ci.getValues();
		parameter.put("value", ci.getValueByName("value"));
		coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
		coordInfo.addValue(Constants.PARAMETER_DATA_MAPPING, parameter);
		coordInfo.addValue("value", ci.getValueByName("value"));
		coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, ci.getValueByName(CoordinationSpaceObject.AGENT_ARCHITECTURE));
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_NAME, ci.getValueByName(CoordinationInfo.AGENT_ELEMENT_NAME));
		coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_TYPE, AgentElementType.INTERNAL_EVENT.toString());
		coordInfo.addValue(Constants.DML_REALIZATION_NAME, ci.getValueByName(Constants.DML_REALIZATION_NAME));
		env.publishCoordinationEvent(coordInfo);
	}

	/**
	 * Start Medium
	 */
	public void start()
	{
		System.out.println("#Start Selfdirected InternalEvent");
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
