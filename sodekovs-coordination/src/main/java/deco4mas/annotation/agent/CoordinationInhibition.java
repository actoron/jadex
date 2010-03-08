package deco4mas.annotation.agent;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Inhibit the instantiation of a coordinated element.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="by")
public class CoordinationInhibition {
	
	//----------attributes----------
	
	/** The name of the inhibiting element. */
	private String element_name;
	
	/** The type of the inhibiting element. */
	private String element_type;

	//----------constructors----------
	
	public CoordinationInhibition(String element_name, String element_type) {
		super();
		this.element_name = element_name;
		this.element_type = element_type;
	}
	
	//----------methods----------
	
	public CoordinationInhibition() {
		super();
	}

	@XmlAttribute(name="name")
	public String getElement_name() {
		return element_name;
	}

	public void setElement_name(String element_name) {
		this.element_name = element_name;
	}

	@XmlAttribute(name="type")
	public String getElement_type() {
		return element_type;
	}

	public void setElement_type(String element_type) {
		this.element_type = element_type;
	}

}
