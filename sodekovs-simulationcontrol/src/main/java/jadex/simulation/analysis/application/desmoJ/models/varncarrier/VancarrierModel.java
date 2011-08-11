package jadex.simulation.analysis.application.desmoJ.models.varncarrier;

import java.util.Map;
import java.util.TreeMap;

import desmoj.core.dist.RealDistExponential;
import desmoj.core.dist.RealDistUniform;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.SimTime;
import desmoj.core.util.Parameterizable;
import desmoj.extensions.experimentation.reflect.MutableFieldAccessPoint;
import desmoj.core.statistic.Histogram;
import desmoj.core.statistic.TimeSeries;
/**
 * This class is the "main" class of the vancarrier_1st_p model.
 * It is derived from desmoj.model and consists of all the
 * needed infrastructural issues of a DESMO-J based model.
 * It provides all random streams , queues, descriptions,
 * and simulation steering needed.
 * See the method comments for further information. 
 *
 * Creation date: (29.03.00 14:11:29)
 * @author: Olaf Neidhardt
 */
public class VancarrierModel extends Model implements Parameterizable {
	/**
	* Random stream used to draw an arrival time for the next truck.
	* See Vancarrier_1st_p_model.init() method for stream parameters.
	*/
	private RealDistExponential truckArrivalTime;
	/**
	* Random stream used to draw a service time for this truck.
	* Describes the time needed by the VC to get and load the container
	* on the truck.
	* See Vancarrier_1st_p_model.init() method for stream parameters.
	*/
	private RealDistUniform serviceTime;
	/**
	* A waiting queue object is used to represent the parking area for
	* the trucks.
	* Every time a truck arrives it is inserted into this queue (it parks)
	* and will be removed by the VC for service.
	*
	* This way all necessary basic statistics are monitored by the queue.
	*/
	protected ProcessQueue<Truck> truckQueue;
	/**
	* A waiting queue object is used to represent the parking spot for
	* the VC.
	* If there is no truck waiting for service the VC will return here
	* and wait for the next truck to come.
	* (Note: We don't use a status field here due to statistical reasons.)
	*
	* This way all idle time statistics of the VC are monitored by the queue.
	*/
	protected ProcessQueue<VC> idleVCQueue;

	/** Model parameter: number of VCs ---NEW */
	protected int vcNumber;

	/** Records numbers of arrived */
	protected TimeSeries trucksArrived;
	
	/** Records numbers of serviced trucks */
	protected TimeSeries trucksServiced;
	
	/** Records truck wait times */
	protected Histogram waitTimeHistogram;

	/** Number of arrived trucks ---NEW*/
	protected int arrivedTrucks = 0;

	/** Number of finished trucks ---NEW*/
	protected int servicedTrucks = 0;

	/**
	 * Vancarrier_1st_p_model constructor.
	 *
	 * Creates a new Vancarrier_1st_p model with calling
	 * the superclasses constructor.
	 *
	 * @param owner desmoj.Model
	 * @param modelname java.lang.String
	 * @param showInReport boolean
	 * @param showInTrace boolean
	 */
	public VancarrierModel(
		Model owner,
		String modelName,
		boolean showInReport,
		boolean showInTrace) {
		super(owner, modelName, showInReport, showInTrace);
		trucksArrived = new TimeSeries(this, "arrived", new SimTime(0.0), new SimTime(15000), false);
		trucksServiced = new TimeSeries(this, "finished", new SimTime(0.0), new SimTime(15000), false);
		waitTimeHistogram = new Histogram(this, "Truck Wait Times", 0, 16, 10, true, false);
		vcNumber = 2;
	}

	/** 
	 * 	Default Constructor for use with Launcher. 
	 * 	showInReport and showInTrace are set to true. 
	 */
	public VancarrierModel() {
		this(null, "VCModel", true, true);
	}

	/**
	 * Describes what the model does
	 */
	public String description() {
		return "This is the Vancarrier_1st_P_Model description,"
			+ "which means it is the first VanCarrier model in a "
			+ "process oriented version."
			+ " "
			+ "This model is a service station model located at a "
			+ "container harbour. Conatinertrucks will arrive and "
			+ "require the loading of a container. A Vancarrier (VC) is "
			+ "on duty and will head off to find the required container "
			+ "in the storage. He will then deliver the container to the "
			+ "truck. The truck then leaves the area."
			+ "In case the VC is busy, the truck waits "
			+ "for his turn on the parking-lot."
			+ "If the VC is idle, it waits on his own parking spot for the "
			+ "truck to come.";
	}
	/**
	 * doInitialSchedules method comment.
	 *
	 * This method is used to place all events or processes on the
	 * internal event list of the simulator, which are necessary to start
	 * the simulation.
	 *
	 * In this case a first truck arrival and the vancarrier are neceesary entries.
	 */
	public void doInitialSchedules() {

		VC vancarrier = null;
		//create the servicer, here make a vancarrier
		for (int i = 0; i < vcNumber; i++) {
			vancarrier = new VC(this, "Van Carrier", true);

			//put the vancarrier on duty with placing it on the event list first
			//it will deactivate itself into waiting status  
			//for the first truck right after activation
			vancarrier.activate(new SimTime(0.0));
			//ATTENTION:
			//Don't use SimTime.NOW or SimTime.now() here, because it
			//leads to "strange" results due to a DESMO-J weakness here.
		}

		//create a truck spring
		TruckGenerator firstarrival =
			new TruckGenerator(this, "TruckArrival", false);

		//place the truck generator on the eventlist, in order to
		//start producing truck arrivals when the first truck comes
		//
		//Note : superclass of TruckGenerator is ExternalEvent, 
		//therefore we must use "schedule" instead of "activate"
		firstarrival.schedule(new SimTime(getTruckArrivalTime()));

	}
	/**
	 * Returns a sample out of the random stream used to measure
	 * time needed to find the container for this truck in the
	 * storage and the time the VC needs to get it to the truck. 
	 * 
	 * Creation date: (30.03.00 12:06:04)
	 * @return double    a serviceTime sample
	 */
	public double getServiceTime() {
		return serviceTime.sample();
	}
	/**
	 * Returns a sample out of the random stream used to measure
	 * the next truck arrival time. 
	 *
	 * Creation date: (30.03.00 12:00:05)
	 * @return double       a truckArrivalTime sample
	 */
	public double getTruckArrivalTime() {
		return truckArrivalTime.sample();
	}
	/**
	 * This method is used to initalize all 
	 * DESMO-J infrastructure we use
	 */
	public void init() {

		//initalizing the serviceTimeStream
		//Parameters:
		//this 	     					=  belongs to this model
		//"ServiceTimeStream" = the name of the stream
		//3.0									= minimum time in minutes to deliver a container
		//7.0								= maximum time in minutes to deliver a container
		//true								= show in report?
		//false								= show in trace?
		serviceTime =
			new RealDistUniform(
				this,
				"ServiceTimeStream",
				3.0,
				7.0,
				true,
				false);

		//initalizing the truckArrivalTimeStream
		//Parameters:
		//this 	     					=  belongs to this model
		//"TruckArrivalTimeStream" = the name of the stream
		//3.0									= mean time in minutes between arrival of trucks
		//true								= show in report?
		//false								= show in trace?
		truckArrivalTime =
			new RealDistExponential(
				this,
				"TruckArrivalTimeStream",
				3.0,
				true,
				false);

		//necessary because a inter-arrival time can not be negative, but
		//a sample of an exponential distribution can...
		truckArrivalTime.setNonNegative(true);

		//initalizing the truckQueue
		//Parameters:
		//this 	     					= belongs to this model
		//"Truck Queue"       = the name of the Queue
		//true								= show in report?
		//false								= show in trace?
		truckQueue = new ProcessQueue<Truck>(this, "Truck Queue", true, false);

		//initalizing the idleVCQueue
		//Parameters:
		//this 	     					= belongs to this model
		//"idle VC Queue"       = the name of the Queue
		//true								= show in report?
		//false								= show in trace?
		idleVCQueue = new ProcessQueue<VC>(this, "idle VC Queue", true, false);
	}
	/**
	 * Starts the application.
	 *
	 * In DESMO-J used to 
	 *    - instantiate the experiment
	 *    - instantiate the model
	 *    - connect the model to the experiment
	 *		- steer length of simulation and outputs
	 *		- start the simulation
	 *		- set the ending criteria (normally the time)
	 *		- initiate reporting
	 *		- clean up the experiment
	 *
	 * @param args  : is an array of command-line arguments
	 */
	public static void main(java.lang.String[] args) {

		// make a new experiment
		// ATTENTION!
		// Use as experiment name a OS filename compatible string!!
		// Otherwise your simulation will crash!!
		Experiment vc_1st_p_Experiment =
			new Experiment("The_Vancarrier_1st_P_Experiment");

		// make a new model
		// null as first parameter because it is the main model and has no mastermodel
		VancarrierModel vc_1st_p_Model =
			new VancarrierModel(
				null,
				"The 1st Vancarrier Model (P)",
				true,
				false);

		// connect Experiment and Model
		vc_1st_p_Model.connectToExperiment(vc_1st_p_Experiment);

		// now set time the trace/debug output may stop 
		// ATTENTION!
		// Don't use to long periods. Otherwise a huge HTML page will 
		// be created which crashes Netscape :-)
		//vc_1st_p_Experiment.tracePeriod(new SimTime(0.0), new SimTime(500));

		//vc_1st_p_Experiment.debugPeriod(new SimTime(0.0), new SimTime(500));

		// now set the time this simulation should stop at 
		// let him work 1500 Minutes
		vc_1st_p_Experiment.stop(new SimTime(30000));
		vc_1st_p_Experiment.setShowProgressBar(false);

		// start the Experiment with start time 0.0
		vc_1st_p_Experiment.start();

		// --> now the simulation is running until it reaches its ending criteria
		// ...
		// ...
		// <-- after reaching ending criteria, the main thread returns here

		// print the report about the already existing reporters into the report file
		vc_1st_p_Experiment.report();

		// stop all threads still alive and close all output files
		vc_1st_p_Experiment.finish();
//		vc_1st_p_Experiment = null;
//		vc_1st_p_Model = null;
//		System.out.println(
//			"Total: "
//				+ Runtime.getRuntime().totalMemory()
//				+ "\tFree: "
//				+ Runtime.getRuntime().freeMemory()
//				+ "\tUsed: "
//				+ (Runtime.getRuntime().totalMemory()
//					- Runtime.getRuntime().freeMemory()));
	}

	/** 
	 * Returns the model parameters:
	 * vcNumber		: Number of VCs working in the yard.
	 */
	public Map createParameters() {
		Map pm = new TreeMap();
		pm.put("vcNumber", new MutableFieldAccessPoint("vcNumber", this));
		return pm;
	}
}
