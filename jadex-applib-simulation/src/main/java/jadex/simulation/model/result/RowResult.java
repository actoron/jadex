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
	private String optimizationName;
	private String optimizationValue;
	

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
	
	public String getOptimizationName() {
		return optimizationName;
	}

	public void setOptimizationName(String optimizationName) {
		this.optimizationName = optimizationName;
	}

	public String getOptimizationValue() {
		return optimizationValue;
	}

	public void setOptimizationValue(String optimizationValue) {
		this.optimizationValue = optimizationValue;
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
	
	
	public String toStringShort(){
		
		StringBuffer buffer = new StringBuffer();		
		buffer.append("Row Number: ");
		buffer.append(getId());			
		buffer.append("\n");
		buffer.append("Optimization: Parameter Name and Value: ");
		buffer.append(getOptimizationName());
		buffer.append(" - ");
		buffer.append(getOptimizationValue());
		buffer.append("\n");
		buffer.append("Results of Experiment: ");			
		buffer.append("\n");
					
		for(ExperimentResult experiment : getExperimentsResults()){				
			buffer.append(experiment.toStringShort());			
			buffer.append("\n");	
		}
		
		return buffer.toString();				
}
	
}
