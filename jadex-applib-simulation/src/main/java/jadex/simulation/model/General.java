package jadex.simulation.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="General")
public class General {
	
	private int rows;
	private int parallelRuns;
	private boolean delta=true;
	private StartTime startTime;
	private TerminateCondition terminateCondition;
	
	@XmlAttribute(name="rows")
	public int getRows() {
		return rows;
	}
	
	public void setRows(int rows) {
		this.rows = rows;
	}
	
	@XmlAttribute(name="parallelRuns")
	public int getParallelRuns() {
		return parallelRuns;
	}
	
	public void setParallelRuns(int parallelRuns) {
		this.parallelRuns = parallelRuns;
	}
	
	@XmlAttribute(name="delta")
	public boolean isDelta() {
		return delta;
	}
	public void setDelta(boolean delta) {
		this.delta = delta;
	}

	@XmlElement(name="StartTime")
	public StartTime getStartTime() {
		return startTime;
	}

	public void setStartTime(StartTime startTime) {
		this.startTime = startTime;
	}

	@XmlElement(name="TerminateCondition")
	public TerminateCondition getTerminateCondition() {
		return terminateCondition;
	}

	public void setTerminateCondition(TerminateCondition terminateCondition) {
		this.terminateCondition = terminateCondition;
	}

}
