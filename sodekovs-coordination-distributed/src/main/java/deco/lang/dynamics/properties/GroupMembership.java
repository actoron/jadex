package deco.lang.dynamics.properties;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * MASDynamics property type.<br>
 * <br>
 * Group memberships describe the dynamics of joining and leaving organizational groups.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="group_membership_count")
public class GroupMembership extends SystemProperty {

	//----------constants-----------

	/** Agent elements. */
	private ArrayList<AgentReference> agent_elements = new ArrayList<AgentReference>();

	//----------methods-------------
	
	@XmlElementWrapper(name="members")
	@XmlElement(name="member")
	public ArrayList<AgentReference> getAgent_elements() {
		return agent_elements;
	}

	public void setAgent_elements(ArrayList<AgentReference> agent_elements) {
		this.agent_elements = agent_elements;
	}
	
	public void addAgentElement(AgentReference ae){
		this.agent_elements.add(ae);
	}
	
}
