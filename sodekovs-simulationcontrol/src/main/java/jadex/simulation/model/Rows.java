package jadex.simulation.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Rows")
public class Rows {

	private long experiments;
	private TerminateCondition terminateCondition;

	@XmlAttribute(name="experiments")
	public long getExperiments() {
		return experiments;
	}

	public void setExperiments(long experiments) {
		this.experiments = experiments;
	}

	@XmlElement(name="TerminateCondition")
	public TerminateCondition getTerminateCondition() {
		return terminateCondition;
	}

	public void setTerminateCondition(TerminateCondition terminateCondition) {
		this.terminateCondition = terminateCondition;
	}
}
