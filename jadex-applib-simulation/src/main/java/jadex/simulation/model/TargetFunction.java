package jadex.simulation.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="TargetFunction")
public class TargetFunction {

	private String function;

	@XmlAttribute(name="function")
	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}
}
