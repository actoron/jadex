package deco4mas.distributed.annotation.agent;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Parameters that are to be passed between agents via the decentralized coordination. 
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="parametermapping")
public class ParameterMapping {

	//----------attributes----------

	/** The parameter name. */
	private String local_name;
	
	/** The name used by the coordination medium to (temporarily) store the value. */
	private String ref;
	
	/** The class of the parameter value */
	private String clazz;

	//----------constructors-------------
	
	public ParameterMapping(String local_name, String ref, String clazz) {
		super();
		this.local_name = local_name;
		this.ref = ref;
		this.clazz = clazz;
	}
	
	public ParameterMapping(String local_name, String ref) {
		super();
		this.local_name = local_name;
		this.ref = ref;
	}
	
	public ParameterMapping() {
		super();
		this.local_name = "";
		this.ref = "";
		this.clazz = "";
	}

	//----------methods-------------
	
	@XmlAttribute(name="name")
	public String getLocalName() {
		return local_name;
	}
	
	public void setLocalName(String name) {
		this.local_name = name;
	}

	@XmlAttribute(name="ref")
	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	@XmlAttribute(name="class")
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}	
	
}
