package sodekovs.util.misc;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;

/**
 * Class for helper methods regarding BDI Agents
 * 
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

		for (IComponentDescription agentIdentifier : space.getComponents()) {
			if (agentIdentifier.getLocalType().equals(agentType)) {

			}
		}
		System.err.println("#AgentMethods:getICompIdent# Error on finding IComponentIdentifier for " + agentType);
		return null;
	}
}
