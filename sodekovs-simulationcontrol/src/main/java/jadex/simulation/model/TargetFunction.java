package jadex.simulation.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="TargetFunction")
public class TargetFunction {

	private String function = null;
	private ObjectSource objectSource = null;

	
	@XmlAttribute(name="function")
	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}
	
	@XmlElement(name="ObjectSource")
	public ObjectSource getObjectSource() {
		return objectSource;
	}
	
	public void setObjectSource(ObjectSource objectSource) {
		this.objectSource = objectSource;
	}
		
}
