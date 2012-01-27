package deco.lang.dynamics.mechanism;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Configuration of direct interaction link.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="configuration")
public class DirectLinkConfiguration {
	
	//-------- attributes ----------

	/** The the delay value. */
	private Long delay;
	
	/** Mechanism dependent properties. */
	private HashMap<String,String> properties;
	
	/** Parameter mapping (to mechanism dependent attributes). */
	private ArrayList<MechanismParameterMapping> parameter_mappings;

	//-------- methods -------------
	
	@XmlAttribute(name="delay")
	public Long getDelay() {
		return delay;
	}

	public void setDelay(Long delay) {
		this.delay = delay;
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

}
