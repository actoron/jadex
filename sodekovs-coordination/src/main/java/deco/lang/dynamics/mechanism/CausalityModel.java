package deco.lang.dynamics.mechanism;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;

/**
 * Detailed model of a behavior interdependency.
 * 
 * @author Jan Sudeikat
 *
 */
public class CausalityModel {

	@XmlEnum(String.class)
	public enum CoordinationDirection {PUBLICATION, PERCEPTION}

	/** Identifier. */
	String id;
	/** The agent element name that is under influence. */
	ArrayList<AgentElement> from_agents;
	/** The agent element name that is under influence. */
	ArrayList<AgentElement> to_agents;

	/**
	 * 
	 */
	public CausalityModel() {
		super();
	}

	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElementWrapper(name = "from")
	@XmlElement(name = "agent_element")
	public ArrayList<AgentElement> getFrom_agents() {
		return from_agents;
	}

	public void setFrom_agents(ArrayList<AgentElement> from_agent_id) {
		this.from_agents = from_agent_id;
	}

	public void addFrom_agents(AgentElement elem) {
		if (this.from_agents == null){
			this.from_agents = new ArrayList<AgentElement>();
		}
		this.from_agents.add(elem);
	}

	@XmlElementWrapper(name = "to")
	@XmlElement(name = "agent_element")
	public ArrayList<AgentElement> getTo_agents() {
		return to_agents;
	}

	public void setTo_agents(ArrayList<AgentElement> to_agent_id) {
		this.to_agents = to_agent_id;
	}

	public void addTo_agents(AgentElement elem) {
		if (this.to_agents == null){
			this.to_agents = new ArrayList<AgentElement>();
		}
		this.to_agents.add(elem);
	}

}