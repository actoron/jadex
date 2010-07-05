package jadex.simulation.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

public class Source {

	/** The name of the variable.*/
	private String name;
	
	/** The content of the element.*/
	private String value;
	
	/** The type of this object.*/
	private String objecttype;
	
	/** Should the data be aggregated?.*/
	private boolean aggregate = false;
	
	/** The include condition.*/
	private String includecondition;
	

	@XmlAttribute(name="includecondition")
	public String getIncludecondition() {
		return includecondition;
	}

	public void setIncludecondition(String includecondition) {
		this.includecondition = includecondition;
	}

	@XmlAttribute(name="aggregate")
	public boolean isAggregate() {
		return aggregate;
	}

	public void setAggregate(boolean aggregate) {
		this.aggregate = aggregate;
	}

	@XmlAttribute(name="objecttype")
	public String getObjecttype() {
		return objecttype;
	}

	public void setObjecttype(String objecttype) {
		this.objecttype = objecttype;
	}

	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	//Hack: this should be an element instead of an attribute
	@XmlAttribute(name="value")	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
