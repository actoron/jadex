package jadex.bdi.simulation.model;

import javax.xml.bind.annotation.XmlAttribute;

public class ObserverData {

	private String name;

	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
