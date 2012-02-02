package deco4mas.mechanism;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Coordination Information element. <br>
 * These elements are exchanged between agent instances 
 * to indicate the occurrences of agent-internal events 
 * as well as to transfer agent-internal data.
 * 
 * This class can be serialized / de-serialized in XML Documents (using JAXB).
 * 
 * @author Jan Sudeikat & Ante Vilenica
 *
 */
@XmlRootElement(name="coordination_information")
public class CoordinationInfo implements CoordinationInformation {
	
	//----------constants----------
	
	/** The agent type. */
	public final static String AGENT_TYPE = "agent_type";
	
	/** The type of the agent element. */
	public final static String AGENT_ELEMENT_TYPE = "agent_element_type";
	
	/** The name of the agent element. */
	public final static String AGENT_ELEMENT_NAME = "agent_element_name";
	
	/** The collection of the "toAgents", i.e. the collection of agents that belong to a "fromAgent" within the dynamics.xml-file. */
	public final static String COLLECTION_OF_TOAGENTS = "collection_of_to-agents";
	
	
	//----------attributes----------

	/** The type of subject to be transmitted. */
	private String type;
	
	/** The name of the subject. */
	private String name;
	
	/** The value of the subject. */
	private HashMap<String, Object> parameters;

	//----------constructors--------
	
	public CoordinationInfo() {
		super();
		this.parameters = new HashMap<String, Object>();
	}

	public CoordinationInfo(String type, String name) {
		super();
		this.type = type;
		this.name = name;		// TODO name equals subject...
		this.parameters = new HashMap<String, Object>();
	}

	//----------methods-------------
	
	@XmlAttribute(name="type")	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, Object> getValues() {
		return parameters;
	}

	public void setValues(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	public Object getValueByName(String key){
		return this.parameters.get(key);
	}
	
	public void addValue(String key, Object value){
		this.parameters.put(key, value);
	}
	
}