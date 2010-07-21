package jadex.simulation.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The top class..
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
//	private ArrayList<Observer> observerList;
	
	/* List of dataproviders*/
	private ArrayList<Dataprovider> dataproviderList ;
	
	/* List of dataconsumers*/
	private ArrayList<Dataconsumer> dataconsumerList ;	

	/* Contains run configuration*/
	private RunConfiguration runConfiguration;
	
	/* Contains configuration for optimization aspects*/
	private Optimization optimization;
	
	/* Contains list of strings that represent the imports to be done.*/
	private ArrayList<String> importList;

	
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

//	@XmlElementWrapper(name="Observers")
//	@XmlElement(name="Observer")
//	public ArrayList<Observer> getObserverList() {
//		return observerList;
//	}
//
//	public void setObserverList(ArrayList<Observer> observerList) {
//		this.observerList = observerList;
//	}
	
	@XmlElementWrapper(name="Dataproviders")
	@XmlElement(name="Dataprovider")
	public ArrayList<Dataprovider> getDataproviderList() {
		return dataproviderList;
	}

	public void setDataproviderList(ArrayList<Dataprovider> dataproviderList) {
		this.dataproviderList = dataproviderList;
	}

	@XmlElementWrapper(name="Dataconsumers")
	@XmlElement(name="Dataconsumer")
	public ArrayList<Dataconsumer> getDataconsumerList() {
		return dataconsumerList;
	}

	public void setDataconsumerList(ArrayList<Dataconsumer> dataconsumerList) {
		this.dataconsumerList = dataconsumerList;
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

	@XmlElementWrapper(name="Imports")
	@XmlElement(name="Import")
	public ArrayList<String> getImportList() {
		return importList;
	}

	public void setImportList(ArrayList<String> importList) {
		this.importList = importList;
	}
	
	


}
