package deco.distributed.lang.dynamics.properties;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A MAS property.<br>
 * <br>
 * Role occupations describe the role executing behavior of agents.
 * 
 * @author Jan Sudeikat 
 *
 */
@XmlRootElement(name="role_occupation")
public class RoleOccupation extends SystemProperty {

	//----------attributes----------

	/** Agent elements. */
	private ArrayList<AgentReference> agent_elements = new ArrayList<AgentReference>();
	
	//----------methods-------------

	@XmlElement(name="agent")
	public ArrayList<AgentReference> getAgentReferences() {
		return agent_elements;
	}

	public void setAgentReferences(ArrayList<AgentReference> agent_elements) {
		this.agent_elements = agent_elements;
	}
	
	public void addAgentReference(AgentReference ae){
		this.agent_elements.add(ae);
	}
	
}