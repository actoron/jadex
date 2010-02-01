package jadex.simulation.model.result;

import jadex.simulation.model.ObservedEvent;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RowResults")
public class RowResult extends IResult {

	private ArrayList<ExperimentResult> experimentResults = new ArrayList<ExperimentResult>();
	

	@XmlElementWrapper(name="Experiments")
	@XmlElement(name="Experiment")	
	public ArrayList<ExperimentResult> getExperimentsResults() {
		return experimentResults;
	}
	
	public void setExperimentsResults(ArrayList<ExperimentResult> experimentResults) {
		this.experimentResults = experimentResults;
	}
	
	public void addExperimentsResults(ExperimentResult experimentResult) {
		this.experimentResults.add(experimentResult);
	}
	

	
	/**
	 * Returns the duration of the row
	 * @return
	 */
	@XmlAttribute(name="RowDuration")
	public long getDuraration(){		
		return getEndtime() - getStarttime();		
	}
	
	@XmlAttribute(name="RowNumber")
	public String getId() {
		return id;
	}

	@XmlAttribute(name="Name")
	public String getName() {
		return name;
	}
	
}
