package haw.mmlab.production_line.simulation;

import haw.mmlab.production_line.simulation.config.BufferSize;
import haw.mmlab.production_line.simulation.config.ProcessTime;
import haw.mmlab.production_line.simulation.config.Redundancy;
import haw.mmlab.production_line.simulation.config.SimulationConfig;
import haw.mmlab.production_line.simulation.config.TaskConf;
import haw.mmlab.production_line.simulation.config.TaskStep;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

import java.util.ArrayList;
import java.util.List;

/**
 * A Simulation manager to start multiple simulation runs with changing redundancy rate and workload parameters.
 * 
 * @author Thomas Preisler
 */
public class SimulationManager {

	private static final int NUMBER_OF_RUNS_PER_CONFIG = 10;

	private static final int NUMBER_OF_TASKS = 10;

	private static final int NUMBER_OF_ROBOTS = 10;

	private static final int NUMBER_OF_WORKPIECES = 0;

	private static final int TIMELORD_INTERVAL = 0;

	private static final int MIN_PROCESSING_TIME = 0;

	private static final int MAX_PROCESSING_TIME = 0;

	private static final int START_REDUNDANCY_RATE = 10;

	private static final int START_WORKLOAD = 10;

	private static final int STOP_REDUNDANCY_RATE = 100;

	private static final int STOP_WORKLOAD = 100;

	private static final int RECONF_MSG_DELAY_TIME = 0;

	private static final String OUTPUT_FILE_PATH = "conf/generated.conf.xml";

	private int redRate = START_REDUNDANCY_RATE;

	private int workload = START_WORKLOAD;

	private static int run = 1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final SimulationManager manager = new SimulationManager();

		// start the simulation
		@SuppressWarnings("rawtypes")
		DefaultResultListener killListener = new DefaultResultListener() {

			@Override
			public void resultAvailable(Object result) {
				if (run < NUMBER_OF_RUNS_PER_CONFIG) {
					run++;
					manager.startSimulation(this, manager.redRate, manager.workload);
				} else {
					if (manager.redRate <= STOP_REDUNDANCY_RATE) {
						if (manager.workload >= STOP_WORKLOAD) {
							manager.workload = START_WORKLOAD;
							manager.redRate += 10;
						} else {
							manager.workload += 10;
						}

						if (manager.redRate <= STOP_REDUNDANCY_RATE) {
							manager.startSimulation(this, manager.redRate, manager.workload);
						}
					}
				}
			}
		};
		manager.startSimulation(killListener, manager.redRate, manager.workload);
	}

	/**
	 * Starts the Jadex Platform and blocks until the platform is started.
	 * 
	 * @return the {@link IExternalAccess} of the platform
	 */
	private IExternalAccess startJadexPlatform() {
		IExternalAccess platform = (IExternalAccess) Starter.createPlatform(null).get(new ThreadSuspendable());
		return platform;
	}

	/**
	 * Gets the {@link IComponentManagementService} from the given platform (blocking call).
	 * 
	 * @param platform
	 *            the given platform
	 * @return the {@link IComponentManagementService}
	 */
	private IComponentManagementService getCMS(IExternalAccess platform) {
		IComponentManagementService cms = (IComponentManagementService) platform.scheduleStep(new IComponentStep<IComponentManagementService>() {

			@Override
			public IFuture<IComponentManagementService> execute(IInternalAccess ia) {
				return SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			}
		}).get(new ThreadSuspendable());

		return cms;
	}

	/**
	 * Starts the ProductionLine application.
	 * 
	 * @param cms
	 *            the {@link IComponentManagementService} who should start the application
	 * @param killListener
	 *            the {@link DefaultResultListener} who should be called when the application was killed
	 * @return the applications {@link IComponentIdentifier}
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private IComponentIdentifier startApplication(IComponentManagementService cms, DefaultResultListener killListener) {
		IComponentIdentifier ci = cms.createComponent("ProductionLine", "haw/mmlab/production_line/ProductionLine.application.xml", null, killListener).get(new ThreadSuspendable());
		return ci;
	}

	/**
	 * Creates a {@link SimulationConfig} with the given parameters.
	 * 
	 * @param noTasks
	 *            the number of tasks
	 * @param noRobots
	 *            the number of robots
	 * @param workload
	 *            the workload as a 10% value (e.g. 10%, 20%, 30%, ...)
	 * @param redundancyRate
	 *            the redundancyRate as a 10% value (e.g. 10%, 20%, 30%, ...)
	 * @param noWorkpieces
	 *            the number of workpieces
	 * @param minProcessTime
	 *            the minimum processing time
	 * @param maxProcessTime
	 *            the maximum processing time
	 * @param noRuns
	 *            the number of runs
	 * @param timelordInterval
	 *            the length of the timing interval
	 * @param reconfTime
	 *            the delay time for the reconfiguration messages
	 * @return the resulting {@link SimulationConfig}
	 */
	private SimulationConfig generateConfig(int noTasks, int noRobots, int workload, int redundancyRate, int noWorkpieces, int minProcessTime, int maxProcessTime, int noRuns, int timelordInterval,
			int reconfTime) {
		// create a new SimulationConfig
		SimulationConfig config = new SimulationConfig();

		// add a number of Tasks
		List<TaskConf> tasks = new ArrayList<TaskConf>();
		for (int i = 1; i <= noTasks; i++) {
			TaskConf task = new TaskConf();
			task.setId(i);

			List<TaskStep> steps = new ArrayList<TaskStep>();
			for (int j = 10; j <= workload; j += 10) {
				for (int k = 1; k <= noRobots; k++) {
					TaskStep step = new TaskStep();
					step.setRobot("Robot" + k + "_Task" + i);
					step.setAction("Cap" + k + "_Task" + i);

					steps.add(step);
				}
			}

			task.setSteps(steps);
			tasks.add(task);
		}
		config.setTasks(tasks);

		// set the robots buffer size
		BufferSize bufferSize = new BufferSize();
		bufferSize.setMin(10);
		bufferSize.setMax(10);
		config.setBufferSize(bufferSize);

		// set the number of workpieces
		config.setWorkpieceCount(noWorkpieces);

		// set the process time
		ProcessTime processTime = new ProcessTime();
		processTime.setMin(minProcessTime);
		processTime.setMax(maxProcessTime);
		config.setProcessTime(processTime);

		// set the number of runs for this configuration
		config.setRunCount(noRuns);

		// set the timelord interval time
		config.setTimelordInterval(timelordInterval);

		// set the redundancy rate
		double redRate = redundancyRate / 100.0;
		int noCaps = noTasks * noRobots;
		noCaps *= redRate;
		Redundancy redundancy = new Redundancy();
		redundancy.setMin(noCaps);
		redundancy.setMax(noCaps);
		config.setRedundancy(redundancy);

		// set the reconf delay time
		config.setReconfTime(reconfTime);

		return config;
	}

	/**
	 * Starts a simulation run for the given redundancy rate and workload.
	 * 
	 * @param killListener
	 *            the {@link DefaultResultListener} who should be called when the application was killed
	 * @param redRate
	 *            the given redundancy rate
	 * @param workload
	 *            the given workload
	 */
	@SuppressWarnings("rawtypes")
	private void startSimulation(DefaultResultListener killListener, int redRate, int workload) {
		// start the jadex platform
		IExternalAccess platform = startJadexPlatform();

		// get the CMS
		IComponentManagementService cms = getCMS(platform);

		// create the config for this redRate and workload parameters
		SimulationConfig config = generateConfig(NUMBER_OF_TASKS, NUMBER_OF_ROBOTS, workload, redRate, NUMBER_OF_WORKPIECES, MIN_PROCESSING_TIME, MAX_PROCESSING_TIME, 1, TIMELORD_INTERVAL,
				RECONF_MSG_DELAY_TIME);
		SimulationGenerator generator = new SimulationGenerator(config);
		generator.saveProductionLineConfiguration(OUTPUT_FILE_PATH);

		// start the application
		startApplication(cms, killListener);
	}
}