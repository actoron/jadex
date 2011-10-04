package haw.mmlab.production_line.logging.database;

import haw.mmlab.production_line.logging.Agent;
import haw.mmlab.production_line.logging.AgentState;
import haw.mmlab.production_line.logging.InterpretedRun;
import haw.mmlab.production_line.logging.LogEntry;
import haw.mmlab.production_line.logging.Run;
import haw.mmlab.production_line.logging.State;
import haw.mmlab.production_line.logging.TimeSlot;
import haw.mmlab.production_line.state.DeficientState;
import haw.mmlab.production_line.state.MainState;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Utility class for interpreting the logging results in the database and preparing them for plotting.
 * 
 * @author thomas
 * 
 */
public class DatabaseInterpreter {

	/**
	 * The path of the measurement folder.
	 */
	private static final String DIR = "measurement/";

	/**
	 * The path to the DeficientState output.
	 */
	private static final String DEFICIENT_FILE = DIR + "deficient.dat";

	/**
	 * The path to the MainState output.
	 */
	private static final String MAIN_FILE = DIR + "main.dat";

	/**
	 * @return - the {@link Connection} to the database from the {@link DatabaseConnection}
	 */
	private static Connection getConnection() {
		return DatabaseConnection.getConnection();
	}

	/**
	 * Return all the runs from the Archive_Log table as a {@link List} of {@link Run}s.
	 * 
	 * @return a {@link List} of {@link Run}s
	 */
	public List<Run> getRuns() {
		List<Run> runs = new ArrayList<Run>();

		String query = "SELECT run, agentId, agentType, intervalTime, mainState, deficientState, noRoles, bufferLoad, bufferCapacity FROM Log";

		try {
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);

			int currentRunNo = 1;
			Run run = new Run();
			while (rs.next()) {
				String agentId = rs.getString("agentId");
				String agentType = rs.getString("agentType");
				int intervalTime = rs.getInt("intervalTime");
				int runNo = rs.getInt("run");

				int deficientState = rs.getInt("deficientState");
				int mainState = rs.getInt("mainState");
				int noRoles = rs.getInt("noRoles");
				int bufferLoad = rs.getInt("bufferLoad");
				int bufferCapacity = rs.getInt("bufferCapacity");
				LogEntry entry = new LogEntry();
				entry.setAgent(new Agent(agentId, agentType));
				entry.setIntervalTime(intervalTime);

				State state = new State();
				state.setDeficientState(deficientState);
				state.setMainState(mainState);
				state.setNoRoles(noRoles);
				state.setBufferLoad(bufferLoad);
				state.setBufferCapacity(bufferCapacity);

				entry.setState(state);

				if (runNo > currentRunNo) {
					runs.add(run);

					run = new Run();
					currentRunNo = runNo;
				}

				run.addEntry(entry);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return runs;
	}

	/**
	 * Return one {@link Run} from the Archive_Log table.
	 * 
	 * @param runId
	 *            - the given Id of the {@link Run}
	 * @return - the {@link Run} or <code>null</code> if no run for the given id exists.
	 */
	public Run getRun(int runId) {
		String query = "SELECT agentId, agentType, intervalTime, mainState, deficientState, noRoles, bufferLoad, bufferCapacity FROM Log WHERE runid=" + runId;

		try {
			Statement stmt = getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);

			Run run = new Run();
			run.setId(runId);
			while (rs.next()) {
				String agentId = rs.getString("agentId");
				String agentType = rs.getString("agentType");
				int intervalTime = rs.getInt("intervalTime");

				int deficientState = rs.getInt("deficientState");
				int mainState = rs.getInt("mainState");
				int noRoles = rs.getInt("noRoles");
				int bufferLoad = rs.getInt("bufferLoad");
				int bufferCapacity = rs.getInt("bufferCapacity");

				LogEntry entry = new LogEntry();
				entry.setAgent(new Agent(agentId, agentType));
				entry.setIntervalTime(intervalTime);

				State state = new State();
				state.setDeficientState(deficientState);
				state.setMainState(mainState);
				state.setNoRoles(noRoles);
				state.setBufferLoad(bufferLoad);
				state.setBufferCapacity(bufferCapacity);

				entry.setState(state);

				run.addEntry(entry);
			}

			return run;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Interprets the given {@link Run}.
	 * 
	 * @param run
	 *            - the given {@link Run}
	 * @return - the {@link InterpretedRun}
	 */
	public InterpretedRun interpretRun(Run run) {
		InterpretedRun interpretedRun = new InterpretedRun();
		Map<String, State> temp = initializeTemp(run.getAgents());

		for (int i = 0; i <= run.getHighestIntervalTime(); i++) {
			List<AgentState> agentStates = new ArrayList<AgentState>();

			for (Agent agent : run.getAgents()) {
				LogEntry entry = run.getEntry(agent.getAgentId(), i);
				
				AgentState agentState = new AgentState();
				agentState.setAgent(agent);

				if (entry != null) {
					agentState.setState(entry.getState());

					temp.put(agent.getAgentId(), entry.getState());
				} else {
					agentState.setState(temp.get(agent.getAgentId()));
				}

				agentStates.add(agentState);
			}

			interpretedRun.addTimeSlot(new TimeSlot(new Long(i), agentStates));
		}

		return interpretedRun;
	}

	/**
	 * Initializes the Map with the last state entries for all agent in a Run, with the start states.
	 * 
	 * @param agents
	 *            - a {@link List} of Agents in a Run.
	 * @return the initialized map.
	 */
	private Map<String, State> initializeTemp(List<Agent> agents) {
		Map<String, State> tmp = new HashMap<String, State>();

		for (Agent agent : agents) {
			tmp.put(agent.getAgentId(), new State());
		}

		return tmp;
	}

	/**
	 * Generates the measurement output file for the given input map as interpreted from {@link DatabaseInterpreter#interpretRun(Run)}. Only the DeficientStates will be written to the output file.
	 * 
	 * @param input
	 *            - see above
	 * @param deficientFile
	 *            - the deficientFile
	 */
	public void generateDeficientOutput(InterpretedRun input, File deficientFile) {
		try {
			PrintWriter deficientWriter = new PrintWriter(new BufferedWriter(new FileWriter(deficientFile)));
			deficientWriter.println("time\tdeficient_by_break\tdeficient_by_change\tnot_deficient");

			for (TimeSlot timeSlot : input.getTimeSlots()) {
				List<AgentState> agentStates = timeSlot.getAgentStates();
				StringBuilder line = new StringBuilder(timeSlot.getTime() + "\t");

				int deficient_by_break = 0, not_deficient = 0, deficient_by_change = 0;
				for (AgentState agentState : agentStates) {
					switch (agentState.getState().getDeficientState()) {
					case DeficientState.DEFICIENT_BY_BREAK:
						deficient_by_break++;
						break;
					case DeficientState.DEFICIENT_BY_CHANGE:
						deficient_by_change++;
						break;
					case DeficientState.NOT_DEFICIENT:
						not_deficient++;
						break;
					default:
						break;
					}
				}

				line.append(deficient_by_break + "\t");
				line.append(deficient_by_change + "\t");
				line.append(not_deficient);

				deficientWriter.println(line);
				deficientWriter.flush();
			}

			deficientWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates the measurement output file for the given input map as interpreted from {@link DatabaseInterpreter#interpretRun(Run)}. Only the DeficientStates will be written to the output file.
	 * 
	 * @param input
	 *            - see above
	 */
	public void generateDeficientOutput(InterpretedRun input) {
		File deficientFile = new File(DEFICIENT_FILE);

		this.generateDeficientOutput(input, deficientFile);
	}

	/**
	 * Generates the measurement output file for the given input map as interpreted from {@link DatabaseInterpreter#interpretRun(Run)}. Only the MainStates will be written to the output file.
	 * 
	 * @param input
	 *            - see above
	 * @param mainFile
	 *            - the main file.
	 */
	public void generateMainOutput(InterpretedRun input, File mainFile) {
		try {
			PrintWriter mainWriter = new PrintWriter(new BufferedWriter(new FileWriter(mainFile)));
			mainWriter.println("time\trunning\trunning_idle\twaiting_for_reconf");

			for (TimeSlot timeSlot : input.getTimeSlots()) {
				List<AgentState> agentStates = timeSlot.getAgentStates();
				StringBuilder line = new StringBuilder(timeSlot.getTime() + "\t");

				int running = 0, running_idle = 0, waiting_for_reconf = 0;
				for (AgentState agentState : agentStates) {
					switch (agentState.getState().getMainState()) {
					case MainState.RUNNING_IDLE:
						running_idle++;
						break;
					case MainState.RUNNING:
						running++;
						break;
					case MainState.WAITING_FOR_RECONF:
						waiting_for_reconf++;
						break;
					default:
						break;
					}
				}

				line.append(running + "\t");
				line.append(running_idle + "\t");
				line.append(waiting_for_reconf);

				mainWriter.println(line);
				mainWriter.flush();
			}

			mainWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates the measurement output file for the given input map as interpreted from {@link DatabaseInterpreter#interpretRun(Run)}. Only the MainStates will be written to the output file.
	 * 
	 * @param input
	 *            - see above
	 */
	public void generateMainOutput(InterpretedRun input) {
		File mainFile = new File(MAIN_FILE);

		generateMainOutput(input, mainFile);
	}

	/**
	 * Main method brings all the functionality of this class together.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		DatabaseInterpreter interpreter = new DatabaseInterpreter();

		System.out.println("Please insert the ids of the runs you would like to interprete (comma separated or hyphen separated for ranges, no spaces):");

		try {
			Scanner s = new Scanner(System.in);
			String input = s.next();

			List<Integer> runIds = new ArrayList<Integer>();
			if (input.contains(",")) {
				StringTokenizer tok = new StringTokenizer(input, ",");
				while (tok.hasMoreTokens()) {
					runIds.add(Integer.parseInt(tok.nextToken()));
				}
			} else if (input.contains("-")) {
				StringTokenizer tok = new StringTokenizer(input, "-");
				Integer min = Integer.parseInt(tok.nextToken());
				Integer max = Integer.parseInt(tok.nextToken());

				while (min <= max) {
					runIds.add(min);
					min++;
				}
			} else {
				Integer min = Integer.parseInt(input);
				runIds.add(min);
			}

			List<Run> runs = new ArrayList<Run>();
			System.out.println("Fetching runs from the database...");
			for (Integer runId : runIds) {
				Run run = interpreter.getRun(runId);
				System.out.println("...run " + runId + " fetched.");
				runs.add(run);
			}

			List<InterpretedRun> interpretedRuns = new ArrayList<InterpretedRun>();
			System.out.println("Interpreting runs...");
			for (Run run : runs) {
				InterpretedRun result = interpreter.interpretRun(run);
				System.out.println("...run " + run.getId() + " interpreted.");
				interpretedRuns.add(result);
			}

			File defFile = new File("deficient_overall.dat");
			File mainFile = new File("main_overall.dat");

			System.out.println("Generating overall output files...");
			interpreter.generateDeficientOutput(interpretedRuns, defFile);
			interpreter.generateMainOutput(interpretedRuns, mainFile);
			System.out.println("...finished");

			File defPlotFile = new File("deficient_overall.plt");
			File mainPlotFile = new File("main_overall.plt");

			System.out.println("Writing Plot files...");
			interpreter.writeDeficientPlotFile(interpretedRuns, defFile, defPlotFile);
			interpreter.writeMainPlotFile(interpretedRuns, mainFile, mainPlotFile);
			System.out.println("...finished");
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n------ The End ------");
	}

	/**
	 * Returns the number of successful reconfigurations in the given {@link InterpretedRun}.
	 * 
	 * @param run
	 *            - the given interpreted run
	 * @return the number of successful reconfigurations in the run
	 */
	public int getSuccessfulReconfigurations(InterpretedRun run) {
		int reconfs = 0;

		int oldNotDefs = run.getAgentCount();
		for (TimeSlot timeSlot : run.getTimeSlots()) {
			int notDef = timeSlot.getNotDeficients();

			if (notDef > oldNotDefs) {
				reconfs++;
			}

			oldNotDefs = notDef;
		}

		return reconfs;
	}

	/**
	 * Returns the number if deficient by change in the given {@link InterpretedRun}.
	 * 
	 * @param run
	 *            - the given interpreted run
	 * @return the number of deficient by change
	 */
	public int getDeficientsByChange(InterpretedRun run) {
		int defByChange = 0;

		int oldDeficients = 0;
		for (TimeSlot timeSlot : run.getTimeSlots()) {
			int deficients = timeSlot.getDeficientsByChange();

			if (deficients > oldDeficients) {
				defByChange++;
			}

			oldDeficients = deficients;
		}

		return defByChange;
	}

	/**
	 * Returns the number if deficient by breaks in the given {@link InterpretedRun}.
	 * 
	 * @param run
	 *            - the given interpreted run
	 * @return the number of deficient by breaks
	 */
	public int getDeficientsByBreak(InterpretedRun run) {
		int defByBreak = 0;

		int oldDeficients = 0;
		for (TimeSlot timeSlot : run.getTimeSlots()) {
			int deficients = timeSlot.getDeficientsByBreak();

			if (deficients > oldDeficients) {
				defByBreak++;
			}

			oldDeficients = deficients;
		}

		return defByBreak;
	}

	/**
	 * Writes all overall number of deficients from the given {@link InterpretedRun} to the given out {@link File}.
	 * 
	 * @param run
	 *            - the given {@link InterpretedRun}
	 * @param outFile
	 *            the given out file
	 */
	public void generateDeficientOverallOutput(InterpretedRun run, File outFile) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
			writer.println("defByBreak\tdefByChange\treconfs");

			StringBuilder line = new StringBuilder();
			line.append(getDeficientsByBreak(run));
			line.append("\t" + getDeficientsByChange(run));
			line.append("\t" + getSuccessfulReconfigurations(run));

			writer.println(line);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes all the main data to the given output {@link File} from the given {@link List} of {@link InterpretedRun}s.
	 * 
	 * @param runs
	 *            - the given List of InterpretedRuns
	 * @param outputFile
	 *            - the output file
	 */
	public void generateMainOutput(List<InterpretedRun> runs, File outputFile) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));

			// write header
			writer.print("time");
			for (@SuppressWarnings("unused")
			InterpretedRun interpretedRun : runs) {
				writer.print("\tdef_by_break\tdef_by_change\tnot_def");
			}
			writer.print("\n");

			// write content
			int smallestSize = getSmallestSize(runs);
			List<Iterator<TimeSlot>> iterators = getIterators(runs);

			for (int i = 0; i < smallestSize; i++) {
				// the time
				StringBuilder line = new StringBuilder(i + "\t");

				for (Iterator<TimeSlot> iterator : iterators) {
					TimeSlot timeSlot = iterator.next();
					List<AgentState> agentStates = timeSlot.getAgentStates();

					int running = 0, running_idle = 0, waiting_for_reconf = 0;
					for (AgentState agentState : agentStates) {
						switch (agentState.getState().getMainState()) {
						case MainState.RUNNING_IDLE:
							running_idle++;
							break;
						case MainState.RUNNING:
							running++;
							break;
						case MainState.WAITING_FOR_RECONF:
							waiting_for_reconf++;
							break;
						default:
							break;
						}
					}

					line.append(running + "\t");
					line.append(running_idle + "\t");
					line.append(waiting_for_reconf + "\t");
				}

				writer.println(line);
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes all the deficient data to the given output {@link File} from the given {@link List} of {@link InterpretedRun}s.
	 * 
	 * @param runs
	 *            - the given List of InterpretedRuns
	 * @param outputFile
	 *            - the output file
	 */
	public void generateDeficientOutput(List<InterpretedRun> runs, File outputFile) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));

			// write header
			writer.print("time");
			for (@SuppressWarnings("unused")
			InterpretedRun interpretedRun : runs) {
				writer.print("\trunning\trunning_idle\twaiting_for_reconf");
			}
			writer.print("\n");

			// write content
			int smallestSize = getSmallestSize(runs);
			List<Iterator<TimeSlot>> iterators = getIterators(runs);

			for (int i = 0; i < smallestSize; i++) {
				// the time
				StringBuilder line = new StringBuilder(i + "\t");

				for (Iterator<TimeSlot> iterator : iterators) {
					TimeSlot timeSlot = iterator.next();
					List<AgentState> agentStates = timeSlot.getAgentStates();

					int deficient_by_break = 0, not_deficient = 0, deficient_by_change = 0;
					for (AgentState agentState : agentStates) {
						switch (agentState.getState().getDeficientState()) {
						case DeficientState.DEFICIENT_BY_BREAK:
							deficient_by_break++;
							break;
						case DeficientState.DEFICIENT_BY_CHANGE:
							deficient_by_change++;
							break;
						case DeficientState.NOT_DEFICIENT:
							not_deficient++;
							break;
						default:
							break;
						}
					}

					line.append(deficient_by_break + "\t");
					line.append(deficient_by_change + "\t");
					line.append(not_deficient + "\t");
				}

				writer.println(line);
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a {@link List} of {@link Iterator}s for all the {@link TimeSlot}s from the given {@link List} of {@link InterpretedRun}s.
	 * 
	 * @param runs
	 *            - the given List of InterpretedRuns
	 * @return the {@link List} of {@link Iterator}s
	 */
	private List<Iterator<TimeSlot>> getIterators(List<InterpretedRun> runs) {
		List<Iterator<TimeSlot>> iterators = new ArrayList<Iterator<TimeSlot>>();

		for (InterpretedRun interpretedRun : runs) {
			iterators.add(interpretedRun.getTimeSlots().iterator());
		}

		return iterators;
	}

	/**
	 * Returns the size of the smallest {@link InterpretedRun} from the given {@link List} of {@link InterpretedRun}s.
	 * 
	 * @param runs
	 *            - the given {@link List} of {@link InterpretedRun}s
	 * @return the size of the smallest {@link InterpretedRun}
	 */
	private int getSmallestSize(List<InterpretedRun> runs) {
		int smallestSize = -1;
		for (InterpretedRun interpretedRun : runs) {
			if (smallestSize < 0 || smallestSize > interpretedRun.getSize()) {
				smallestSize = interpretedRun.getSize();
			}
		}

		return smallestSize;
	}

	/**
	 * Writes the deficient plot file for the given {@link List} of {@link InterpretedRun}s and the given def {@link File}.
	 * 
	 * @param runs
	 *            - the given {@link List} of {@link InterpretedRun}s
	 * @param defFile
	 *            - the given def {@link File}
	 * @param plotFile
	 *            - the plot file which will be written
	 */
	public void writeDeficientPlotFile(List<InterpretedRun> runs, File defFile, File plotFile) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(plotFile)));

			writer.println("set grid");

			int size = runs.size();
			// def by break
			String defByBreakLine = "plot \"" + defFile.getName() + "\" using 1:((";
			int startNo = 2;
			for (int i = 0; i < size; i++) {
				if (i > 0) {
					defByBreakLine += "+";
				}
				defByBreakLine += "$" + startNo;
				startNo += 3;
			}
			defByBreakLine += ")/" + size + ") w lp title \"deficient_by_break\"";
			writer.println(defByBreakLine);

			// def by change
			String defByChangeLine = "replot \"" + defFile.getName() + "\" using 1:((";
			startNo = 3;
			for (int i = 0; i < size; i++) {
				if (i > 0) {
					defByChangeLine += "+";
				}
				defByChangeLine += "$" + startNo;
				startNo += 3;
			}
			defByChangeLine += ")/" + size + ") w lp title \"deficient_by_change\"";
			writer.println(defByChangeLine);

			// not def
			String notDefLine = "replot \"" + defFile.getName() + "\" using 1:((";
			startNo = 4;
			for (int i = 0; i < size; i++) {
				if (i > 0) {
					notDefLine += "+";
				}
				notDefLine += "$" + startNo;
				startNo += 3;
			}
			notDefLine += (")/" + size + ") w lp title \"not_deficient\"");
			writer.println(notDefLine);

			writer.println("pause -1");
			writer.println("set output \"deficient_overall.png\"");
			writer.println("set terminal png ");
			writer.println("replot");

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the main plot file for the given {@link List} of {@link InterpretedRun}s and the given main {@link File}.
	 * 
	 * @param runs
	 *            - the given {@link List} of {@link InterpretedRun}s
	 * @param mainFile
	 *            - the given main {@link File}
	 * @param plotFile
	 *            - the plot file which will be written
	 */
	public void writeMainPlotFile(List<InterpretedRun> runs, File mainFile, File plotFile) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(plotFile)));

			writer.println("set grid");

			int size = runs.size();
			// running(busy)
			String bussyLine = "plot \"" + mainFile.getName() + "\" using 1:((";
			int startNo = 2;
			for (int i = 0; i < size; i++) {
				if (i > 0) {
					bussyLine = bussyLine.concat("+");
				}
				bussyLine = bussyLine.concat("$" + startNo);
				startNo += 3;
			}
			bussyLine = bussyLine.concat(")/" + size + ") w lp title \"running(busy)\"");
			writer.println(bussyLine);

			// running(idle)
			String idleLine = "replot \"" + mainFile.getName() + "\" using 1:((";
			startNo = 3;
			for (int i = 0; i < size; i++) {
				if (i > 0) {
					idleLine = idleLine.concat("+");
				}
				idleLine = idleLine.concat("$" + startNo);
				startNo += 3;
			}
			idleLine = idleLine.concat(")/" + size + ") w lp title \"running(idle)\"");
			writer.println(idleLine);

			// waiting for reconf
			String reconfLine = "replot \"" + mainFile.getName() + "\" using 1:((";
			startNo = 4;
			for (int i = 0; i < size; i++) {
				if (i > 0) {
					reconfLine = reconfLine.concat("+");
				}
				reconfLine = reconfLine.concat("$" + startNo);
				startNo += 3;
			}
			reconfLine = reconfLine.concat(")/" + size + ") w lp title \"waiting_for_reconf\"");
			writer.println(reconfLine);

			writer.println("pause -1");
			writer.println("set output \"main_overall.png\"");
			writer.println("set terminal png ");
			writer.println("replot");

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sums up the total number of roles which are applied to a specific time.
	 * 
	 * @param result
	 *            the given {@link InterpretedRun}
	 * @param file
	 *            the file to which the result shoud be written
	 */
	public void generateNoRolesOutput(InterpretedRun result, File file) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			writer.println("time\tnoRoles");

			for (TimeSlot time : result.getTimeSlots()) {
				List<AgentState> agentStates = time.getAgentStates();
				StringBuilder line = new StringBuilder(time.getTime() + "\t");

				int noRoles = 0;
				for (AgentState agentState : agentStates) {
					noRoles += agentState.getState().getNoRoles();
				}

				line.append(noRoles);

				writer.println(line);
				writer.flush();
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calculates the max workload (number of roles divided by the buffer size) from all agents to a specific time.
	 * 
	 * @param result
	 *            the given {@link InterpretedRun}
	 * @param bufferSizes
	 *            a Map with the buffer sizes of all the agents
	 * @param file
	 *            the file to which the result shoud be written
	 */
	public void generateMaxWorkloadOutput(InterpretedRun result, Map<String, Integer> bufferSizes, File file) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			writer.println("time\tmaxWorkload");

			for (TimeSlot timeSlot : result.getTimeSlots()) {
				List<AgentState> agentStates = timeSlot.getAgentStates();
				StringBuilder line = new StringBuilder(timeSlot.getTime() + "\t");

				double maxWorkload = getMaxWorkload(agentStates, bufferSizes);
				line.append(maxWorkload);

				writer.println(line);
				writer.flush();
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the max workload (number of roles divided by the buffer size) from the given {@link List} of {@link AgentState}s.
	 * 
	 * @param agentStates
	 *            the given {@link List} of {@link AgentState}s
	 * @param bufferSizes
	 *            a Map with the buffer sizes of all the agents
	 * @return the max workload (number of roles divided by the buffer size)
	 */
	private double getMaxWorkload(List<AgentState> agentStates, Map<String, Integer> bufferSizes) {
		double maxWorkload = 0;

		for (AgentState agentState : agentStates) {
			Agent agent = agentState.getAgent();
			State state = agentState.getState();

			if (agent.getAgentType().equals("robot")) {
				int bufferSize = bufferSizes.get(agent.getAgentId());
				int noRoles = state.getNoRoles();

				double workload = (double) noRoles / (double) bufferSize;

				if (workload > maxWorkload) {
					maxWorkload = workload;
				}
			}
		}

		return maxWorkload;
	}
}