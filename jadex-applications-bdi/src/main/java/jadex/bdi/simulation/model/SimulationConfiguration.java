package jadex.bdi.simulation.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The top class.
 * @author Vilenica
 *
 */
//@XmlRootElement(namespace = "http://simulationconf.jadex.informatik.uni-hamburg.de/")
@XmlRootElement(name = "SimulationConfiguration")
public class SimulationConfiguration {
	
	/* Name of the Simulation*/
	private String name;
	
	/* Reference of the application.xml*/
	private String applicationReference;
	
	/* Reference of the configuration of the application.xml to start.*/
	private String applicationConfiguration;	
		
	/* List of observers*/
	private ArrayList<Observer> observerList;
	
	/* Contains run configuration*/
	private RunConfiguration runConfiguration;
	
	/* Contains configuration for optimization aspects*/
	private Optimization optimization;

	
	//--methods
	
	
	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute(name="applicationReference")
	public String getApplicationReference() {
		return applicationReference;
	}

	public void setApplicationReference(String reference) {
		this.applicationReference = reference;
	}

	@XmlElementWrapper(name="Observers")
	@XmlElement(name="Observer")
	public ArrayList<Observer> getObserverList() {
		return observerList;
	}

	public void setObserverList(ArrayList<Observer> observerList) {
		this.observerList = observerList;
	}

	@XmlElement(name="RunConfiguration")
	public RunConfiguration getRunConfiguration() {
		return runConfiguration;
	}

	public void setRunConfiguration(RunConfiguration runConfiguration) {
		this.runConfiguration = runConfiguration;
	}
	
	@XmlAttribute(name="applicationConfiguration")
	public String getApplicationConfiguration() {
		return applicationConfiguration;
	}

	public void setApplicationConfiguration(String applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

	@XmlElement(name="Optimization")
	public Optimization getOptimization() {
		return optimization;
	}

	public void setOptimization(Optimization optimization) {
		this.optimization = optimization;
	}
	
	


}
