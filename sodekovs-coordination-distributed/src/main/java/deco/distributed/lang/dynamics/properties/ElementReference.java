package deco.lang.dynamics.properties;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import deco.lang.dynamics.AgentElementType;

@XmlRootElement(name="element")
public class ElementReference {

	//----------attributes----------

	/** Element identifier. */
	String element_id;
	
	/** The type of agent element that is under influence. */
	AgentElementType agent_element_type;

	//----------constructors--------
	
	public ElementReference(String element_id,
			AgentElementType agent_element_type) {
		super();
		this.element_id = element_id;
		this.agent_element_type = agent_element_type;
	}
	
	public ElementReference(){
		super();
		this.element_id = "";
		this.agent_element_type = AgentElementType.GENERIC_ACTIVITY;
	}

	//----------methods-------------
	
	@XmlAttribute(name="id")
	public String getElement_id() {
		return element_id;
	}

	public void setElement_id(String element_id) {
		this.element_id = element_id;
	}

	@XmlAttribute(name="type")
	public AgentElementType getAgent_element_type() {
		return agent_element_type;
	}

	public void setAgent_element_type(AgentElementType agent_element_type) {
		this.agent_element_type = agent_element_type;
	}
	
}
