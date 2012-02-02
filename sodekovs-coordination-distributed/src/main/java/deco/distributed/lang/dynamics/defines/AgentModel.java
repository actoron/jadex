package deco.lang.dynamics.defines;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Description of an individual agent model.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="agent_model")
public class AgentModel {
	//----------constants-----------

	/** The supported agent types. */
	public enum AgentModelType {JADEX,JADE};

	//----------attributes----------
	
	/** Identifier. */
	String id;
	
	/** Agent model (e.g. ADF) location. */
	String filename;
	
	/** The agent model type. */
	AgentModelType type;
	
	/** The read-in agent model. */
	Object model;

	//----------constructors--------
	
	public AgentModel() {
		super();
	}
	
	public AgentModel(String id, String filename, AgentModelType type) {
		super();
		this.id = id;
		this.filename = filename;
		this.type = type;
	}

	//----------methods-------------
	
	@XmlAttribute(name="id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(name="file_name")
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@XmlAttribute(name="type")
	public AgentModelType getType() {
		return type;
	}

	public void setType(AgentModelType type) {
		this.type = type;
	}

	@XmlTransient
	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

}