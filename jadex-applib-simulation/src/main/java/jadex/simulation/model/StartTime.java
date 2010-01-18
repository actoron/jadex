package jadex.simulation.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="StartTime")
public class StartTime {
	
	private long value;
	private String type;
	
	@XmlAttribute(name="value")
	public long getValue() {
		return value;
	}
	
	public void setValue(long value) {
		this.value = value;
	}
	
	@XmlAttribute(name="type")
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

}
