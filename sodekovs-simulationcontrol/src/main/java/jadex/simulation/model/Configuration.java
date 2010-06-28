package jadex.simulation.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Configuration")
public class Configuration {

	private int start;
	private int end;
	private int step;
	private String classname;
	private String values;
	private ArrayList<String> valuesAsList;
	
	@XmlAttribute(name="start")
	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	@XmlAttribute(name="end")
	public int getEnd() {
		return end;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	@XmlAttribute(name="step")
	public int getStep() {
		return step;
	}
	
	public void setStep(int step) {
		this.step = step;
	}
	
	@XmlAttribute(name="class")
	public String getClassname() {
		return classname;
	}
	
	
	public void setClassname(String classname) {
		this.classname = classname;
	}

	@XmlAttribute(name="values")
	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}
	
	/**
	 * Returns the values, initially "only" a string of values, as a arraylist of separated values.
	 * @return
	 */
	public ArrayList<String> getValuesAsList() {
		if(valuesAsList == null){
			valuesAsList = new ArrayList<String>();
			String tmpValues = this.values;
			
			while(tmpValues.indexOf(";") != -1){
				valuesAsList.add(tmpValues.substring(0, tmpValues.indexOf(";")));
				tmpValues = tmpValues.substring(tmpValues.indexOf(";")+1);
			}		
		}
		return this.valuesAsList;	
	}
}
