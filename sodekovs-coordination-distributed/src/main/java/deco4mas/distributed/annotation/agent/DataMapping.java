package deco4mas.distributed.annotation.agent;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data element (agent intern). 
 * These data are to be passed between agents via the decentralized coordination. 
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="agent_data_mapping")
public class DataMapping {

	//----------attributes----------

	/** The name of the element. */
	private String element_name;
	
	/** The name of the data element (parameter name / content); content is a reserved word. */
	private String data_name;
	
	/** The name used by the coordination medium to (temporarily) store the value. */
	private String ref;

	/** The type of the element. */
	private String elementType; // currently only Belief supported.
	
	//-------- methods -------------
	
	@XmlAttribute(name="name")
	public String getElement_name() {
		return element_name;
	}

	public void setElement_name(String element_name) {
		this.element_name = element_name;
	}

	@XmlAttribute(name="data")
	public String getData_type() {
		return data_name;
	}

	public void setData_type(String data_type) {
		this.data_name = data_type;
	}

	@XmlAttribute(name="ref")
	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	@XmlAttribute(name="type")
	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

}
