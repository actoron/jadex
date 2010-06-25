package jadex.simulation.helper;

import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.bridge.IComponentIdentifier;

/**
 * Class for helper methods regarding BDI Agents
 * @author Ante Vilenica
 *
 */
public class AgentMethods {

	/**
	 * Returns the IComponentIdentifier for an AgentType. Returns only the first instance of an agent type if there are more than one.
	 * 
	 * @param space
	 * @param agentType
	 * @return
	 */
	public static IComponentIdentifier getIComponentIdentifier(AbstractEnvironmentSpace space, String agentType) {

		for (IComponentIdentifier agentIdentifier : space.getComponents()) {
			if (space.getContext().getComponentType(agentIdentifier).equals(agentType)) {
				return agentIdentifier;
			}
		}
		System.err.println("#AgentMethods:getICompIdent# Error on finding IComponentIdentifier for " + agentType);
		return null;
	}
}
