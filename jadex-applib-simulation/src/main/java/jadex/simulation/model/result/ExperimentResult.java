package jadex.simulation.model.result;

import jadex.simulation.helper.TimeConverter;
import jadex.simulation.model.ObservedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class contains the results of one experiment
 * @author Vilenica
 *
 */
@XmlRootElement(name = "ExperimentResults")
public class ExperimentResult extends IResult{
		
	private String optimizationValue;
	private String optimizationParameterName;
	private ArrayList<ObservedEvent> events = new ArrayList<ObservedEvent>();
	
	public ExperimentResult(){
		super();
	}
	
	public ExperimentResult(long startTime, long endTime, String experimentID, String name, String optimizationValue, String optimizationParameterName, ArrayList<ObservedEvent> observedEvents){
		this.starttime = startTime;
		this.endtime = endTime;
		this.id = experimentID;
		this.name = name;
		this.optimizationValue = optimizationValue;
		this.optimizationParameterName = optimizationParameterName;
		this.events = observedEvents;
	}
	
	@XmlElementWrapper(name="ObservedEvents")
	@XmlElement(name="ObservedEvent")	
	public ArrayList<ObservedEvent> getEvents() {
		return events;
	}
	
	public void setEvents(ArrayList<ObservedEvent> events) {
		this.events = events;
	}
	
	public void addEvent(ObservedEvent event) {
		this.events.add(event);
	}			
	
	public String getOptimizationValue() {
		return optimizationValue;
	}
	public void setOptimizationValue(String optimizationValue) {
		this.optimizationValue = optimizationValue;
	}
	public String getOptimizationParameterName() {
		return optimizationParameterName;
	}
	public void setOptimizationParameterName(String optimizationParameterName) {
		this.optimizationParameterName = optimizationParameterName;
	}
	
	/**
	 * Returns the duration of the experiment
	 * @return
	 */
	@XmlAttribute(name="ExperimentDuration")
	public long getDuraration(){		
		return getEndtime() - getStarttime();		
	}
	
	@XmlAttribute(name="ExperimentId")
	public String getId() {
		return id;
	}

	@XmlAttribute(name="ExperimentName")
	public String getName() {
		return name;
	}
		
	
	/**
	 * Extended Version
	 */
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Name and ID: ");
		buffer.append(getName());
		buffer.append(" - ");
		buffer.append(getId());
		buffer.append("\n");
		buffer.append("Start and End Time: ");
		buffer.append(TimeConverter.longTime2DateString(getStarttime()));
		buffer.append(" - ");
		buffer.append(TimeConverter.longTime2DateString(getEndtime()));
		buffer.append("\n");
		buffer.append("Duration: ");
		buffer.append(getDuraration() / 1000);
		buffer.append(" sec");
		buffer.append("\n");
		buffer.append("Optimization: Parameter Name and Value: ");
		buffer.append(getOptimizationParameterName());
		buffer.append(" - ");
		buffer.append(getOptimizationValue());
				
		return buffer.toString();
		
	}
	
	/**
	 * Short Version. Hack: Works only if one data has been observed per experiment!
	 */
	public String toStringShort(){
		StringBuffer buffer = new StringBuffer();		
		buffer.append("Duration: ");
		buffer.append(getDuraration() / 1000);
		buffer.append(" sec");
		buffer.append("\n");
//		buffer.append("Optimization: Parameter Name and Value: ");
//		buffer.append(getOptimizationParameterName());
//		buffer.append(" - ");
//		buffer.append(getOptimizationValue());
//		buffer.append("\n");
		buffer.append("Values and relative TimeStamps for Observed Data " + events.get(0).getNameOfObservedData());		
		buffer.append("\n");
		
		sortEventlist();
		for(ObservedEvent event : getEvents()){
			buffer.append(event.getRelativeTimestamp());
			buffer.append("\t");
			buffer.append(event.getValue());			
			buffer.append("\n");	
		}
		
		return buffer.toString();
		
	}
	
	/**
	 * Returns the list of observed events ascendingly ordered.
	 */
	public void sortEventlist(){
		Collections.sort(getEvents(), new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return new Long(((ObservedEvent) arg0).getRelativeTimestamp()).compareTo(new Long(((ObservedEvent) arg1).getRelativeTimestamp()));
			}
		});
	}
}


