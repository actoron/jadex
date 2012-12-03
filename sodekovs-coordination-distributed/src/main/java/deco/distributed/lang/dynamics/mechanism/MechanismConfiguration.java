package deco.distributed.lang.dynamics.mechanism;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import deco.distributed.lang.dynamics.defines.AgentModel.AgentModelType;

public class MechanismConfiguration {

	//----------constants----------
	
	/** The id of the applied mechanism (from define / this is the fully qualified capability name). */
	private String mechanism_id;
	
	/** The type of agent that implements the mechanism. */
	private  AgentModelType agent_type;
	
	/** Mechanism dependent properties. */
	private HashMap<String,String> properties;
	
	/** Parameter mapping (to mechanism dependent attributes). */
	private ArrayList<MechanismParameterMapping> parameter_mappings;
	
	//----------methods-------------
	
	@XmlAttribute(name="mechanism_id")
	public String getMechanism_id() {
		return mechanism_id;
	}

	public void setMechanism_id(String mechanism_id) {
		this.mechanism_id = mechanism_id;
	}
	
	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}

	public void addProperty(String key, String value){
		if (this.properties == null){
			this.properties = new HashMap<String, String>();
		}
		this.properties.put(key, value);
	}
	
	@XmlElementWrapper(name="parameter_mappings")
	@XmlElement(name="mapping")
	public ArrayList<MechanismParameterMapping> getParameter_mappings() {
		return parameter_mappings;
	}

	public void setParameter_mappings(ArrayList<MechanismParameterMapping> parameter_mappings) {
		this.parameter_mappings = parameter_mappings;
	}

	public void addParameterMapping(MechanismParameterMapping pm){
		if (this.parameter_mappings == null){
			this.parameter_mappings = new ArrayList<MechanismParameterMapping>();
		}
		this.parameter_mappings.add(pm);
	}

	@XmlAttribute(name="agent_type")
	public AgentModelType getAgent_type() {
		return agent_type;
	}

	public void setAgent_type(AgentModelType agent_type) {
		this.agent_type = agent_type;
	}
	
	//----------utility-------------
	
	/**
	 * Fetch a parameter by name (ignoreCase).
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key){
		for (String k : this.properties.keySet()){
			if (k.equalsIgnoreCase(key)){
				return this.properties.get(k);
			}
		}
		return null;
	}
	
	/**
	 * Fetch a parameter by name (ignoreCase).
	 * 
	 * @param key
	 * @return
	 */
	public Double getDoubleProperty(String key) {
		
		if (hasProperty(key)) {
			String property_value = getProperty(key);
			return new Double(property_value);
		}
		
		else return null;
	}
	
	/**
	 * Fetch a parameter by name (ignoreCase).
	 * 
	 * @param key
	 * @return
	 */
	public Long getLongProperty(String key) {
		
		if (hasProperty(key)) {
			String property_value = getProperty(key);
			return new Long(property_value);
		}
		
		else return null;
	}
	
	/**
	 * Fetch a parameter by name (ignoreCase).
	 * 
	 * @param key
	 * @return
	 */
	public Integer getIntegerProperty(String key) {
		
		if (hasProperty(key)) {
			String property_value = getProperty(key);
			return new Integer(property_value);
		}
		
		else return null;
	}
	
	/**
	 * Fetch a parameter by name (ignoreCase).
	 * 
	 * @param key
	 * @return
	 */
	public Boolean getBooleanProperty(String key) {
		
		if (hasProperty(key)) {
			String property_value = getProperty(key);
			return new Boolean(property_value);
		}
		
		else return null;
	}

	/**
	 * Check whether a property has been specified.
	 * 
	 * @param string
	 * @return
	 */
	public boolean hasProperty(String string) {
		for (String k : this.properties.keySet()){
			if (k.equalsIgnoreCase(string)){
				return true;
			}
		}
		return false;
	}
	
}
