package jadex.simulation.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Vilenica
 * 
 */
@XmlRootElement(name="Dataconsumer")
public class Dataconsumer {

	/* The name. */
	private String name;
	
	/* The class. */
	private String clazz;

	/* List of properties */
	private ArrayList<Property> propertiesList;


	@XmlElementWrapper(name = "Properties")
	@XmlElement(name = "Property")
	public ArrayList<Property> getPropertyList() {
		return propertiesList;
	}

	public void setPropertyList(ArrayList<Property> propertyList) {
		this.propertiesList = propertyList;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "clazz")
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
}
