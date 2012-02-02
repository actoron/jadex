package deco.lang.dynamics.properties;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/** 
 * An agent reference that contains references to a set of agent-elements.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="agent")
public class AgentReference {

	//----------attributes----------

	/** Agent identifier. */
	private String agent_id;
	
	/** Agent element reference(s) */
	private ArrayList<ElementReference> elements;
	
	/** Constraint model. */
	private PropertyConstraints constraints;
	
	//----------constructors--------

	public AgentReference() {
		super();
		this.elements = new ArrayList<ElementReference>();
	}
	
	public AgentReference(String agent_id) {
		super();
		this.agent_id = agent_id;
		this.elements = new ArrayList<ElementReference>();
	}

	//----------methods-------------
	
	@XmlAttribute(name="id")
	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	@XmlElementWrapper(name="elements")
	@XmlElement(name="element")
	public ArrayList<ElementReference> getElements() {
		return elements;
	}

	public void setElements(ArrayList<ElementReference> elements) {
		this.elements = elements;
	}
	
	public void addElement(ElementReference ele){
		this.elements.add(ele);
	}
	
	public PropertyConstraints getContraints() {
		return constraints;
	}

	@XmlElement(name="constraints")
	public void setContraints(PropertyConstraints contraints) {
		this.constraints = contraints;
	}
	
	public boolean hasConstraints() {
		if (this.constraints == null) return false;
		else return true;
	}
	
}