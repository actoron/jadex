package jadex.simulation.helper;

/**
 * Contains constants used within this project.
 * @author Ante Vilenica
 *
 */
public class Constants {

	public static final String SIMULATION_NAME = "SimulationName";
	//The Start/End Time of the whole Simulation
	public static final String SIMULATION_START_TIME = "SimulationStartTime";	
	public static final String SIMULATION_END_TIME = "SimulationEndTime";
	
	//The Start/End Time of ONE Experiment
	public static final String EXPERIMENT_START_TIME = "ExperimentStartTime";	
	public static final String EXPERIMENT_END_TIME = "ExperimentEndTime";
	
	public static final String TOTAL_EXPERIMENT_COUNTER = "Total_Experiment_Counter";
	//The number of the experiment within one row
	public static final String ROW_EXPERIMENT_COUNTER = "Row_Experiment_Counter";
	public static final String EXPERIMENT_ROW_COUNTER = "Experiment_Row_Counter";
	
	//The number of experiments for each row
	public static final String EXPERIMENTS_PER_ROW_TO_DO = "Experiments_Per_Row_To_Do";
	//The number of rows to do
	public static final String ROWS_TO_DO = "Rows_To_Do";
	
	//The id of the experiment according to following format: numberOfRow.numberOfExperimentInThisRow
//	public static final String EXPERIMENT_ID = "Experiment_id";
	
	//Containing the facts for a simulation client.
	public static final String SIMULATION_FACTS_FOR_CLIENT = "Simulation_Facts_For_Client";
	
	//Containing the parsed xml configuration file.
	public static final String SIMULATION_CONFIGURATION = "Simulation_Configuration";
	
	//List that contains observers observing onChange
	public static final String ON_CHANGE_OBSERVER_LIST = "onChangeObserversList";
	
	//List that contains observers observing onChange
	public static final String PERIODICAL_OBSERVER_LIST = "periodicalObserversList";
	
	//List that contains observed events, e.g. produced by the observers
	public static final String OBSERVED_EVENTS_LIST = "observedEvents";
	
	//Map that contains lists with observed events, e.g. produced by the observers. a list is associated with a timestamp when the events where observed. 
	public static final String OBSERVED_EVENTS_MAP = "observedEventsMap";
												      	
	//Property of an ISpace_Object
	public static final String PROPERTY  = "Property";
	
	// The name/type of the client simulation agent
	public static final String CLIENT_SIMULATION_AGENT = "ClientSimulator";
	
	// Map that contains the results (= observed events) of the simulation experiments of one row
//	public static final String RESULTS_OF_ROW = "ResultsOfRow";
	
	//The path of a file
	public static final String APPLICATION_FILE_PATH = "FilePath";
	public static final String CONFIGURATION_FILE_PATH = "ConfigurationPath";

	// XML file as String
	public static final String APPLICATION_FILE_AS_XML_STRING = "ApplicationFileAsXMLString";
//	public static final String CONFIGURATION_FILE_AS_XML_STRING = "ConfigurationFileAsXMLString";

	//Denotes that the time expression is given as relative time (= duration)
	public static final String RELATIVE_TIME_EXPRESSION = "relative";
	
	//Denotes that the time expression is given as absolute time
	public static final String ABSOLUTE_TIME_EXPRESSION = "absolute";
	
	//Denotes that the observer should periodically observe/evaluate the data object
	public static final String PERIODICAL_EVALUATION_MODE = "periodical";
	
	//Denotes that the observer should observe/evaluate the data object onChange
	public static final String ON_CHANGE_EVALUATION_MODE = "onChange";
	
	//-- Denotes the three types that are available for parameter sweeping: list, space, algorithm
	// Use of a parameter space with: start, end and step values
	public static final String OPTIMIZATION_TYPE_LIST = "list";
	// Use of a list of parameters
	public static final String OPTIMIZATION_TYPE_SPACE = "space";
	// Use of a dedicated optimization algorithm
	public static final String OPTIMIZATION_TYPE_ALGORITHM = "algorithm";
	//The name of the class dedicated to consuming data for simulations
	public static final String SIMULATION_DATA_CONSUMER = "SimulationDataConsumer";	
	//The name of the element
	public static final String DATAPROVIDER = "dataprovider";
	
	//If there is more than one instance of this object that should be observed, than this String denotes the ID of the object to be distinguished.
	public static final String MULTIPLE_INSTANCE_Id = "multipleInstanceId";
	//Complex Event: the observed event contains several properties, not just one
	public static final String IS_COMPLEX_EVENT  = "isComplexEvent";
	
//	List that contains mean values
	public static final String MEAN_VALUE_LIST  = "meanValueList";
	
//	Mean values
	public static final String MEAN_VALUE  = "meanValue";
	
//	Mean difference between real and sim data
	public static final String MEAN_VALUE_DIFF_BETWEEN_SIM_AND_REAL_DATA  = "meanValueDiffBetweenSimAndRealData";
	
//	List that contains median values
	public static final String MEDIAN_VALUE_LIST  = "medianValueList";
	
//	Median values
	public static final String MEDIAN_VALUE  = "medianValue";
	
//	List that contains sample variance values
	public static final String SAMPLE_VARIANCE_VALUE_LIST  = "sampleVarianceValueList";
	
//	Sample variance values
	public static final String SAMPLE_VARIANCE_VALUE  = "sampleVarianceValue";

//	List that contains the single values of the observed object
	public static final String SINGLE_OBSERVED_VALUES_LIST  = "singleObservedValuesList";
	
	
		
}

