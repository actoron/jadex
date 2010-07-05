package jadex.simulation.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="Data")
public class Data {

	//Hack: Needed for DataProvider extension
	private String value = null;
	private String name = null;
	private ObjectSource objectSource = null;
	private ElementSource elementSource = null;

	
	@XmlAttribute(name="value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@XmlElement(name="ObjectSource")
	public ObjectSource getObjectSource() {
		return objectSource;
	}
	
	public void setObjectSource(ObjectSource objectSource) {
		this.objectSource = objectSource;
	}
	
	@XmlElement(name="ElementSource")
	public ElementSource getElementSource() {
		return elementSource;
	}
	
	public void setElementSource(ElementSource elementSource) {
		this.elementSource = elementSource;
	}

	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
