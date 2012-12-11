/**
 * 
 */
package deco.distributed.lang.dynamics.convergence;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Description of possible convergence behavior.
 * 
 * @author Thomas Preisler
 */
@XmlRootElement(name="convergence")
public class Convergence {

	private List<Agent> agents = new ArrayList<Agent>();
	
	private List<Adaption> adaptions = new ArrayList<Adaption>();

	/**
	 * @return the agents
	 */
	@XmlElementWrapper(name = "agents")
	@XmlElement(name = "agent")
	public List<Agent> getAgents() {
		return agents;
	}

	/**
	 * @param agents the agents to set
	 */
	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}

	/**
	 * @return the adaptions
	 */
	@XmlElementWrapper(name = "adaptions")
	@XmlElement(name = "adaption")
	public List<Adaption> getAdaptions() {
		return adaptions;
	}

	/**
	 * @param adaptions the adaptions to set
	 */
	public void setAdaptions(List<Adaption> adaptions) {
		this.adaptions = adaptions;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Convergence [agents=" + (agents != null ? agents.subList(0, Math.min(agents.size(), maxLen)) : null) + ", adaptions="
				+ (adaptions != null ? adaptions.subList(0, Math.min(adaptions.size(), maxLen)) : null) + "]";
	}
	
	/**
	 * Returns all {@link Adaption}s where the given agentId is referenced in one of the {@link Constraint}s.
	 * 
	 * @param agentId the given agent id
	 * @return the affected {@link Adaption}s
	 */
	public List<Adaption> getAffectedAdaptions(String agentId) {
		List<Adaption> affectedAdaptions = new ArrayList<Adaption>();
		
		for (Adaption adaption : this.adaptions) {
			for (Constraint constraint : adaption.getConstraints()) {
				if (constraint.getAgentId().equals(agentId)) {
					affectedAdaptions.add(adaption);
					break;
				}
			}
		}
		
		return affectedAdaptions;
	}
}