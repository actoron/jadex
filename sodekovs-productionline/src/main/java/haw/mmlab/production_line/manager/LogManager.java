package haw.mmlab.production_line.manager;

import haw.mmlab.production_line.common.FileUtils;
import haw.mmlab.production_line.configuration.Capability;
import haw.mmlab.production_line.configuration.ProductionLineConfiguration;
import haw.mmlab.production_line.configuration.Robot;
import haw.mmlab.production_line.configuration.export.DotWriter;
import haw.mmlab.production_line.logging.InterpretedRun;
import haw.mmlab.production_line.logging.Run;
import haw.mmlab.production_line.logging.database.DatabaseInterpreter;
import haw.mmlab.production_line.logging.database.DatabaseLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The LogManager backups the relevant log and configuration files before and after a simulation run, also statistics are written to the database and the database is cleaned up after a simulation run.
 * 
 * @author thomas
 */
public class LogManager {

	/**
	 * The path of the simulation folder.
	 */
	private static final String SIMULATION_DIR = "simulation/";

	/**
	 * The name of the DeficientState output file.
	 */
	private static final String DEFICIENT_FILE = "deficient.dat";

	/**
	 * The name of the MainState output file.
	 */
	private static final String MAIN_FILE = "main.dat";

	/**
	 * The name of the number of roles output file.
	 */
	private static final String NO_ROLES_FILE = "noRoles.dat";

	/**
	 * The name of the max workload output file.
	 */
	private static final String MAX_WORKLOAD_FILE = "maxWorkload.dat";

	/**
	 * The name of the overall deficient file.
	 */
	private static final String DEFICIENT_OVERALL_FLE = "deficient-overall.dat";

	/**
	 * The name of the workpiece data file.
	 */
	private static final String WORKPIECE_FILE = "workpiece.dat";

	/**
	 * The path to the gnuplot template files.
	 */
	private static final String PLOT_FILE_PATH = "src/main/java/haw/mmlab/production_line/logging/plot/template/";

	/**
	 * The name of the deficient plot file.
	 */
	private static final String DEFICIENT_PLOT_FILE = "deficient.plt";

	/**
	 * The name of the main plot file.
	 */
	private static final String MAIN_PLOT_FILE = "main.plt";

	/**
	 * The name of the number of roles plot file.
	 */
	private static final String NO_ROLES_PLOT_FILE = "noRoles.plt";

	/**
	 * The name of the max workload plot file.
	 */
	private static final String MAX_WORKLOAD_PLOT_FILE = "maxWorkload.plt";

	/** The production line configuration */
	private ProductionLineConfiguration plc = null;

	/**
	 * Constructor.
	 * 
	 * @param plc
	 *            The production line configuration
	 */
	public LogManager(ProductionLineConfiguration plc) {
		this.plc = plc;
	}

	/**
	 * Writes the statistics of the current simulation run to the database before the run and generates the log files.
	 * 
	 * @param confFile
	 *            The path to the configuration file
	 */
	public void beforeSimulation(String confFile) {
		createStatistics();
		generateLogFiles(confFile);
	}

	/**
	 * Create the statistics for the current run.
	 */
	private void createStatistics() {
		Map<String, Integer> capCounter = new HashMap<String, Integer>();
		double n = 0;
		double redundancy = 0;
		int roleCount = 0;
		double workload = 0;

		for (Robot robot : plc.getRobots()) {
			for (Capability cap : robot.getCapabilities()) {
				Integer count = capCounter.get(cap);
				if (count == null) {
					count = 0;
				}
				capCounter.put(cap.getId(), count++);
				n++;
			}

			workload += (double) robot.getRoles().size() / (double) robot.getBufferSize();
		}

		workload /= plc.getRobots().size();

		for (Robot robot : plc.getRobots()) {
			redundancy = redundancy + ((double) robot.getCapabilities().size() / (double) capCounter.size());
			roleCount += robot.getRoles().size();
		}

		redundancy = redundancy / plc.getRobots().size();

		DatabaseLogger.getInstance().insertMetadata(redundancy, plc.getRobots().size(), plc.getTransports().size(), capCounter.size(), roleCount, "noStrategy", workload);
	}

	/**
	 * Generates the Log Files before the simulation run.
	 * 
	 * @param confFile
	 *            - the path to the configuration file
	 */
	private void generateLogFiles(String confFile) {
		int runId = DatabaseLogger.getInstance().getRunId();
		backupConfFile(runId, confFile);
		generateGraph(runId, plc);
	}

	/**
	 * Backups the conf file to the given runId simulation directory.
	 * 
	 * @param runId
	 *            - the given run id
	 * @param confFile
	 *            - the path to the configuration file
	 */
	private void backupConfFile(int runId, String confFile) {
		File dir = new File(SIMULATION_DIR + "run-" + runId);
		dir.mkdir();

		if (confFile != null) {
			File file = new File(confFile);

			if (file.exists() && dir.exists() && dir.isDirectory()) {
				File outFile = new File(dir, file.getName());

				try {
					FileUtils.copyFile(file, outFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Generates the .dot file and the .png file for the communication graph and the resource flow graph from the given {@link ProductionLineConfiguration} and run id.
	 * 
	 * @param runId
	 *            - the run id
	 * @param plc
	 *            - the given {@link ProductionLineConfiguration}
	 */
	private void generateGraph(int runId, ProductionLineConfiguration plc) {
		String cgFilePrefix = SIMULATION_DIR + "run-" + runId + "/communication_graph";
		String rfgFilePrefix = SIMULATION_DIR + "run-" + runId + "/resource_flow_graph_start";

		DotWriter writer = new DotWriter();
		writer.exportDotFileCG(cgFilePrefix, plc);
		writer.generatePNG(cgFilePrefix);

		writer.exportDotFileRFG(rfgFilePrefix, plc);
		writer.generatePNG(rfgFilePrefix);
	}

	/**
	 * Starts the interpretation of a run.
	 * 
	 * @param runId
	 *            - the run id.
	 */
	private void interpreteData(int runId) {
		Map<String, Integer> bufferSizes = getBufferSizes(plc);

		DatabaseLogger logger = DatabaseLogger.getInstance();

		if (logger.isLoggingEnabled()) {
			DatabaseInterpreter interpreter = new DatabaseInterpreter();
			Run run = interpreter.getRun(runId);
			InterpretedRun result = interpreter.interpretRun(run);

			File dir = new File(SIMULATION_DIR + "run-" + runId);

			File deficientFile = new File(dir, DEFICIENT_FILE);
			File mainFile = new File(dir, MAIN_FILE);
			File deficientOverallFile = new File(dir, DEFICIENT_OVERALL_FLE);
			File noRolesFile = new File(dir, NO_ROLES_FILE);
			File maxWorkloadFile = new File(dir, MAX_WORKLOAD_FILE);

			interpreter.generateDeficientOutput(result, deficientFile);
			interpreter.generateMainOutput(result, mainFile);
			interpreter.generateDeficientOverallOutput(result, deficientOverallFile);
			interpreter.generateNoRolesOutput(result, noRolesFile);
			interpreter.generateMaxWorkloadOutput(result, bufferSizes, maxWorkloadFile);
		}
	}

	/**
	 * Creates a <code>Map<String, Integer></code> where the buffer size of all robots is stored es value, the robot's agents id is the key.
	 * 
	 * @param plc
	 *            the {@link ProductionLineConfiguration} from which the Map is created
	 * @return a <code>Map<String, Integer></code> which the buffer size of all robots
	 */
	private Map<String, Integer> getBufferSizes(ProductionLineConfiguration plc) {
		Map<String, Integer> bufferSizes = new HashMap<String, Integer>();

		for (Robot robot : plc.getRobots()) {
			bufferSizes.put(robot.getAgentId(), robot.getBufferSize());
		}

		return bufferSizes;
	}

	/**
	 * Interprets the data of a simulation run after the run, cleans up the database and backups files with information about the last run.
	 * 
	 * @param producedMap
	 *            a map with the number of workpieces which were produced in the tasks
	 * @param consumedMap
	 *            a map with the number of workpieces which were consumed in the tasks
	 */
	public void afterSimulation(Map<String, Integer> producedMap, Map<String, Integer> consumedMap) {
		int runId = DatabaseLogger.getInstance().cleanupDatabase();

		interpreteData(runId);
		writeWorkpieceFile(runId, producedMap, consumedMap);
		copyPlotFiles(runId);
		generateResourceFlowGraph(runId);
	}

	/**
	 * Writes the workpiece log data to the workpiece data file.
	 * 
	 * @param runId
	 *            - the given run id
	 */
	private void writeWorkpieceFile(int runId, Map<String, Integer> producedMap, Map<String, Integer> consumedMap) {
		File dir = new File(SIMULATION_DIR + "run-" + runId);
		File wpFile = new File(dir, WORKPIECE_FILE);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(wpFile));
			writer.write("produced\tconsumed\n");

			Integer produced = 0;
			for (Integer p : producedMap.values()) {
				produced += p;
			}

			Integer consumed = 0;
			for (Integer c : consumedMap.values()) {
				consumed += c;
			}

			writer.write(produced.intValue() + "\t" + consumed.intValue() + "\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Copies the plot file to the simulation directory with the given run id.
	 * 
	 * @param runId
	 *            - the given run id
	 */
	private void copyPlotFiles(int runId) {
		File dir = new File(SIMULATION_DIR + "run-" + runId);

		File deficientTemplatePlotFile = new File(PLOT_FILE_PATH + DEFICIENT_PLOT_FILE);
		File mainTemplatePlotFile = new File(PLOT_FILE_PATH + MAIN_PLOT_FILE);
		File noRolesTemplatePlotFile = new File(PLOT_FILE_PATH + NO_ROLES_PLOT_FILE);
		File maxWorkloadTemplatePlotFile = new File(PLOT_FILE_PATH, MAX_WORKLOAD_PLOT_FILE);

		File deficientPlotFile = new File(dir, DEFICIENT_PLOT_FILE);
		File mainPlotFile = new File(dir, MAIN_PLOT_FILE);
		File noRolesPlotFile = new File(dir, NO_ROLES_PLOT_FILE);
		File maxWorkloadPlotFile = new File(dir, MAX_WORKLOAD_PLOT_FILE);

		try {
			FileUtils.copyFile(deficientTemplatePlotFile, deficientPlotFile);
			FileUtils.copyFile(mainTemplatePlotFile, mainPlotFile);
			FileUtils.copyFile(noRolesTemplatePlotFile, noRolesPlotFile);
			FileUtils.copyFile(maxWorkloadTemplatePlotFile, maxWorkloadPlotFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate the graph for the resource flow.
	 * 
	 * @param runId
	 *            - the given run id.
	 */
	private void generateResourceFlowGraph(int runId) {
		String rfgFilePrefix = SIMULATION_DIR + "run-" + runId + "/resource_flow_graph_end";

		DotWriter writer = new DotWriter();
		writer.exportDotFileRFG(rfgFilePrefix, plc);
		writer.generatePNG(rfgFilePrefix);
	}
}
