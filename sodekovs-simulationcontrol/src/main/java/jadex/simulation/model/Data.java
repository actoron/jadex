package jadex.simulation.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Data")
public class Data {

	private String name = null;
	private ObjectSource objectSource = null;
	private ElementSource elementSource = null;
	
	
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
