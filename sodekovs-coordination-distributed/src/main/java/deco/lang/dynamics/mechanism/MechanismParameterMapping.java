package deco.lang.dynamics.mechanism;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Mechanism internal parameters that are set at runtime from agent internal elements. 
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="parametermapping")
public class MechanismParameterMapping {

	//----------attributes----------

	/** The parameter name (origin). */
	private String from_id;
	
	/** The parameter name (destination). */
	private String to_id;
	
	/** The class of the parameter value */
	private String clazz;

	/** Agent identifier. */
	private String agent_id;
	
	/** The element of the agent (agent_id) that provides the parameter (from_id). */
	private String element_id;
	
	//----------constructors-------------
	
	public MechanismParameterMapping(String from_name, String to_id, String clazz,String agent_id) {
		super();
		this.from_id = from_name;
		this.to_id = to_id;
		this.clazz = clazz;
		this.agent_id = agent_id;
	}
	
	public MechanismParameterMapping(String from_name, String to_id, String clazz) {
		super();
		this.from_id = from_name;
		this.to_id = to_id;
		this.clazz = clazz;
	}
	
	public MechanismParameterMapping(String from_name, String to_id) {
		super();
		this.from_id = from_name;
		this.to_id = to_id;
	}
	
	public MechanismParameterMapping() {
		super();
		this.from_id = "";
		this.to_id = "";
		this.clazz = "";
	}

	//----------methods-------------
	
	@XmlAttribute(name="from_id")
	public String getFromId() {
		return from_id;
	}
	
	public void setFromId(String name) {
		this.from_id = name;
	}

	@XmlAttribute(name="to_id")
	public String getToID() {
		return to_id;
	}

	public void setToId(String to_id) {
		this.to_id = to_id;
	}

	@XmlAttribute(name="class")
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	@XmlAttribute(name="agent")
	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	public String getFrom_id() {
		return from_id;
	}

	public void setFrom_id(String from_id) {
		this.from_id = from_id;
	}

	public String getTo_id() {
		return to_id;
	}

	public void setTo_id(String to_id) {
		this.to_id = to_id;
	}

	public String getElement_id() {
		return element_id;
	}

	public void setElement_id(String element_id) {
		this.element_id = element_id;
	}
}