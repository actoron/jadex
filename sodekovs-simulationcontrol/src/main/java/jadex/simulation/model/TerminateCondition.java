package jadex.simulation.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="TerminateCondition")
public class TerminateCondition {
	
	private Time time;
	private TargetFunction targetFunction;

	@XmlElement(name="Time")
	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	@XmlElement(name="TargetFunction")
	public TargetFunction getTargetFunction() {
		return targetFunction;
	}

	public void setTargetFunction(TargetFunction targetFunction) {
		this.targetFunction = targetFunction;
	}
	
	

}
