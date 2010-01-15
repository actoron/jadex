package jadex.bdi.simulation.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ParameterSweeping")
public class ParameterSweeping {
	
	private String type;
	private Configuration configuration;
	private int parameterSweepCounter = 0;
	private int currentValue = 0;

	@XmlAttribute(name="type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name="Configuration")
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Needed, in case the parameter is swept to indicated current value.
	 * @return
	 */
	public int getParameterSweepCounter() {
		return parameterSweepCounter;
	}

	public void setParameterSweepCounter(int parameterSweepCounter) {
		this.parameterSweepCounter = parameterSweepCounter;
	}
	
	public void incrementParameterSweepCounter(){
		this.parameterSweepCounter++;
	}

	/**
	 * Denotes the current value of the parameter that is swept
	 * @return
	 */
	public int getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}

	
}
