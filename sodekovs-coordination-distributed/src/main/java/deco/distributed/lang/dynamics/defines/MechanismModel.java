package deco.distributed.lang.dynamics.defines;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/** 
 * Decentralized coordination mechanism definition.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="mechanism_model")
public class MechanismModel {

	//----------attributes----------

	/** Identifier. */
	String id;

	/** Mechanism configuration file (Implementation). */
	String configuration_file;

	//----------constructors--------
	
	public MechanismModel() {
		super();
	}
	
	public MechanismModel(String id, String configuration_file) {
		super();
		this.id = id;
		this.configuration_file = configuration_file;
	}

	//----------methods-------------
	
	@XmlAttribute(name="id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(name="configuration_file")
	public String getConfiguration_file() {
		return configuration_file;
	}

	public void setConfiguration_file(String configuration_file) {
		this.configuration_file = configuration_file;
	}

}