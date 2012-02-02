package deco.distributed.lang.dynamics.mechanism;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import deco4mas.distributed.annotation.agent.DataMapping;
import deco4mas.distributed.annotation.agent.ParameterMapping;

/** 
 * An agent element reference.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="agent_element")
public class AgentElement {

	//----------attributes----------

	/** Agent identifier. */
	String agent_id;
	
	/** Element identifier. */
	String element_id;
	
	/** The type of agent element that is under influence. */
	String agent_element_type;
	
	/** Parameter mappings. */
	ArrayList<ParameterMapping> parameter_mappings = new ArrayList<ParameterMapping>();
	
	/** Data mappings. */
	ArrayList<DataMapping> data_mappings = new ArrayList<DataMapping>();
	
	//----------constructors--------
	
	public AgentElement() {
		super();
	}
	
	public AgentElement(String agent_id, String element_id,
			String agent_element_type) {
		super();
		this.agent_id = agent_id;
		this.element_id = element_id;
		this.agent_element_type = agent_element_type;
	}

	//----------methods-------------
	
	@XmlAttribute(name="agent_id")
	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	@XmlAttribute(name="element")
	public String getElement_id() {
		return element_id;
	}

	public void setElement_id(String element_id) {
		this.element_id = element_id;
	}

	@XmlAttribute(name="type")
	public String getAgentElementType() {
		return agent_element_type;
	}

	public void setAgentElementType(String agent_element_type) {
		this.agent_element_type = agent_element_type;
	}
	
	@XmlElementWrapper(name="parameter_mappings")
	@XmlElement(name="mapping")
	public ArrayList<ParameterMapping> getParameter_mappings() {
		return parameter_mappings;
	}

	public void setParameter_mappings(ArrayList<ParameterMapping> parameter_mappings) {
		this.parameter_mappings = parameter_mappings;
	}
	
	
	public void addParameterMapping(ParameterMapping pm){
		if (this.parameter_mappings == null){
			this.parameter_mappings = new ArrayList<ParameterMapping>();
		}
		this.parameter_mappings.add(pm);
	}

	@XmlElementWrapper(name="agent_data_mappings")
	@XmlElement(name="mapping")
	public ArrayList<DataMapping> getData_mappings() {
		return data_mappings;
	}

	public void setData_mappings(ArrayList<DataMapping> data_mappings) {
		this.data_mappings = data_mappings;
	}

}