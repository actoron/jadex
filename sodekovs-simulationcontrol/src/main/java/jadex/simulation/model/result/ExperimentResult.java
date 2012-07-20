package jadex.simulation.model.result;

import jadex.simulation.model.ObservedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import sodekovs.util.misc.TimeConverter;

/**
 * Class contains the results of one experiment
 * @author Vilenica
 *
 */
@XmlRootElement(name = "ExperimentResults")
public class ExperimentResult extends IResult{
		
	private String optimizationConfiguration;
	//private String optimizationParameterName;
	//List contains events observed by the DataProvider/DataConsumer. The list is not sorted by object instance, object properties etc.
	private ArrayList<ObservedEvent> events = new ArrayList<ObservedEvent>();

	//Contains the observed events, but transformed as follows:
	//The HashMap key is the name (id) of the object instance. The value contains again a HashMap. The key, of this second HashMap, is
	// the observed property of the object instance. The corresponding value is a ArrayList which contains the observed values, sorted ascendingly by time.	
	private HashMap<String, HashMap<String, ArrayList<Object>>> sortedObserveEventsMap;
	
	public ExperimentResult(){
		super();
	}
	
	public ExperimentResult(long startTime, long endTime, String experimentID, String name, String optimizationConfiguration, ArrayList<ObservedEvent> observedEvents){
		this.starttime = startTime;
		this.endtime = endTime;
		this.id = experimentID;
		this.name = name;
		this.optimizationConfiguration = optimizationConfiguration;
		//this.optimizationParameterName = optimizationParameterName;
		this.events = observedEvents;
	}
	
	@XmlElementWrapper(name="ObservedEvents")
	@XmlElement(name="ObservedEvent")	
	public ArrayList<ObservedEvent> getEvents() {
		sortEventlist();
		return events;
	}
	
	public void setEvents(ArrayList<ObservedEvent> events) {
		this.events = events;
	}
	
	public void addEvent(ObservedEvent event) {
		this.events.add(event);
	}			
	
	public String getOptimizationConfiguration() {
		return optimizationConfiguration;
	}
	public void setOptimizationConfiguration(String optimizationConfiguration) {
		this.optimizationConfiguration = optimizationConfiguration;
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
	 * Contains the observed events, but transformed as follows:
 	 * The HashMap key is the name (id) of the object instance. The value contains again a HashMap. The key, of this second HashMap, is
	 * the observed property of the object instance. The corresponding value is a ArrayList which contains the observed values, sorted ascendingly by time.
	 * @return
	 */
	public HashMap<String, HashMap<String, ArrayList<Object>>> getSortedObserveEventsMap() {
		return sortedObserveEventsMap;
	}
	
	/**
	 * Contains the observed events, but transformed as follows:
 	 * The HashMap key is the name (id) of the object instance. The value contains again a HashMap. The key, of this second HashMap, is
	 * the observed property of the object instance. The corresponding value is a ArrayList which contains the observed values, sorted ascendingly by time.	
	 * @param sortedObserveEventsMap
	 */
	public void setSortedObserveEventsMap(HashMap<String, HashMap<String, ArrayList<Object>>> sortedObserveEventsMap) {
		this.sortedObserveEventsMap = sortedObserveEventsMap;
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
		buffer.append(getOptimizationConfiguration());			
		return buffer.toString();
		
	}
	
	/**
	 * Short Version. Hack: Works only if one data has been observed per experiment!
	 */
	public String toStringShort(){
		StringBuffer buffer = new StringBuffer();		
//		buffer.append("Duration: ");
//		buffer.append(getDuraration() / 1000);
//		buffer.append(" sec");
//		buffer.append("\n");
//		buffer.append("Values for Observed Data ");		
//		buffer.append("\n");
		
//		sortEventlist();
//		for(ObservedEvent event : getEvents()){
//			buffer.append(event.getRelativeTimestamp());
//			buffer.append("\t");
//			for (Iterator<String> it = event.getObservedObjectProperties().keySet().iterator(); it.hasNext();) {
//				String key = it.next();
//				buffer.append(key);
//				buffer.append(" -> observed value: ");
//				buffer.append(event.getObservedObjectProperties().get(key));
//				buffer.append("\n");
//			}
//			buffer.append(event.getValue());			
//			buffer.append("\n");	
//		}
		
		buffer.append(sortedObserveEventsToString());
		
		return buffer.toString();
		
	}
	
	/**
	 * Helper method, to transform sorted results HashMap into a String which can easily be read
	 * @return
	 */
	private String sortedObserveEventsToString(){
		StringBuffer buffer = new StringBuffer();
		 
		buffer.append("Results for object instances and their observed properties:");
		buffer.append("\n");
		
		//Get object instance
		for (Iterator<String> it1 = sortedObserveEventsMap.keySet().iterator(); it1.hasNext();) {
			String objectInstancesMapKey = it1.next();
			HashMap<String, ArrayList<Object>> instanceProperties =  sortedObserveEventsMap.get(objectInstancesMapKey);
			
			buffer.append("\t");
			buffer.append(objectInstancesMapKey);
			buffer.append(":\n");
			
			//Get their properties
			for (Iterator<String> it2 = instanceProperties.keySet().iterator(); it2.hasNext();) {
				String objectInstancePropertiesMapKey = it2.next();
				ArrayList<Object> instancePropertyList =  instanceProperties.get(objectInstancePropertiesMapKey);
			
				buffer.append("\t\t");
				buffer.append(objectInstancePropertiesMapKey);
				buffer.append(": ");
				
				//Get values for this property
				for(Object value : instancePropertyList){
					buffer.append(value);
					buffer.append(",");
				}
				buffer.append("\n");
			}
			buffer.append("\n");
		}
		
		return buffer.toString();
	}
	
	/**
	 * Returns the list of observed events ascendingly ordered.
	 */
	public void sortEventlist(){
		Collections.sort(this.events, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return new Long(((ObservedEvent) arg0).getRelativeTimestamp()).compareTo(new Long(((ObservedEvent) arg1).getRelativeTimestamp()));
			}
		});
	}	
	
	/**
	 * Returns the latest measured value for the DataName specified by the key
	 * @param key
	 * @return
	 */
	//HACK 19-7-12
//	public String getLastValueFor(String key){
//		
//		for(ObservedEvent event: this.getEvents()){
//			if(event.getDataName().equalsIgnoreCase(key)){
//				return event.getValue();
//			}
//		}		
//		return "dataName not found";
//	}
}


