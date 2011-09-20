/**
 * 
 */
package haw.mmlab.production_line.simulation;

import haw.mmlab.production_line.configuration.Agent;
import haw.mmlab.production_line.configuration.Capability;
import haw.mmlab.production_line.configuration.Condition;
import haw.mmlab.production_line.configuration.ProductionLineConfiguration;
import haw.mmlab.production_line.configuration.Robot;
import haw.mmlab.production_line.configuration.Role;
import haw.mmlab.production_line.configuration.Task;
import haw.mmlab.production_line.configuration.Transport;
import haw.mmlab.production_line.simulation.config.SimulationConfig;
import haw.mmlab.production_line.simulation.config.TaskConf;
import haw.mmlab.production_line.simulation.config.TaskStep;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.bind.JAXBException;

/**
 * This class generates a {@link ProductionLineConfiguration} and writes it to a file. The generation is based on simulation configuration file, which is passed as parameter.
 * 
 * @author Peter
 * 
 */
public class SimulationGenerator {
	private SimulationConfig config = null;
	private Random randomizer = null;
	private String[] actions = null;
	private ProductionLineConfiguration plc = null;
	private Map<Capability, Integer> capCounter = null;
	private Map<String, Robot> robots = null;
	private Map<String, Transport> transports = null;
	private Integer robotCount = 0;

	/**
	 * Creates a new SimulationGenerator that works on the given configuration file.
	 * 
	 * @param confFile
	 *            The file name of the configuration file.
	 */
	public SimulationGenerator(String confFile) {
		try {
			// read from XML:
			config = (SimulationConfig) deco4mas.util.xml.XmlUtil.retrieveFromXML(SimulationConfig.class, confFile);
			capCounter = new HashMap<Capability, Integer>();
			randomizer = new Random();
			robots = new HashMap<String, Robot>();
			transports = new HashMap<String, Transport>();
			generateConfiguration();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the {@link ProductionLineConfiguration} generated out of the configuration.
	 * 
	 * @return The {@link ProductionLineConfiguration}
	 */
	public ProductionLineConfiguration getProductionLineConfiguration() {
		return plc;
	}

	/**
	 * Saves the generated {@link ProductionLineConfiguration} to the given file.
	 * 
	 * @param filename
	 */
	public void saveProductionLineConfiguration(String filename) {
		try {
			deco4mas.util.xml.XmlUtil.saveAsXML(plc, filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates the configuration.
	 */
	private void generateConfiguration() {
		plc = new ProductionLineConfiguration();

		Integer count = config.getRunCount();
		if (count != null && count > 0) {
			plc.setRunCount(count);
		} else {
			plc.setRunCount(1);
		}

		count = config.getErrorTimeout();
		if (count != null && count >= 0) {
			plc.setErrorTimeout(count);
		} else {
			plc.setErrorTimeout(60000);
		}

		generateRobotsAndTransports();
		generateCommunicationGraph();
		plc.setTasks(generateTasks(config.getWorkpieceCount()));

		Integer procMin = config.getProcTimeMin();
		Integer procMax = config.getProcTimeMax();
		if (procMin == null || procMin < 0)
			procMin = 500;

		if (procMax == null || procMax < procMin)
			procMax = procMin;

		Integer redMin = config.getRedundancyMin();
		Integer redMax = config.getRedundancyMax();
		if (redMin == null || redMin < 1)
			redMin = 1;

		if (redMax == null || procMax < redMin)
			redMax = redMin;

		Integer bufferMin = config.getBufferSizeMin();
		Integer bufferMax = config.getBufferSizeMax();
		if (bufferMin == null || bufferMin < 1)
			bufferMin = 5;

		if (bufferMax == null || bufferMax < bufferMin)
			bufferMax = bufferMin;

		generateProcessingTime(procMin, procMax);
		generateBufferSize(bufferMin, bufferMax);
		generateReduncantCapabilities(redMin, redMax);

		plc.addRobots(robots.values());
		plc.addTransports(transports.values());

		for (Capability cap : capCounter.keySet()) {
			if (capCounter.get(cap) <= 1)
				System.err.println("WARNING: " + cap + " is only once available in the system");
		}
	}

	/**
	 * Sets the buffer size for all robots within the given bounds.
	 * 
	 * @param bufferMin
	 *            the lower bound
	 * @param bufferMax
	 *            the upper bound
	 */
	private void generateBufferSize(Integer bufferMin, Integer bufferMax) {
		for (Robot robot : robots.values()) {
			int bs = getRandom(bufferMin, bufferMax);
			int rc = robot.getRoles().size();

			robot.setBufferSize(rc > bs ? rc : bs);
		}
	}

	/**
	 * Generates the redundant capabilities for the agents within the given bounds.
	 * 
	 * @param redMin
	 *            the lower bound
	 * @param redMax
	 *            the upper bound
	 */
	private void generateReduncantCapabilities(Integer redMin, Integer redMax) {
		for (Robot robot : robots.values()) {
			List<Capability> capabilities = robot.getCapabilities();
			robot.setCapabilities(generateAgentCapabilities(capabilities, redMin, redMax));
		}
	}

	/**
	 * Sets the processing time for all agents within the given bounds.
	 * 
	 * @param procMin
	 *            the lower bound
	 * @param procMax
	 *            the upper bound
	 */
	private void generateProcessingTime(int procMin, int procMax) {
		List<Agent> agents = new ArrayList<Agent>();
		agents.addAll(robots.values());
		agents.addAll(transports.values());

		for (Agent agent : agents) {
			for (Role role : agent.getRoles()) {
				role.setProcessingTime(getRandom(procMin, procMax));
			}
		}
	}

	/**
	 * Generates the communication graph.
	 */
	private void generateCommunicationGraph() {
		List<Agent> agents = new ArrayList<Agent>();
		agents.addAll(robots.values());
		agents.addAll(transports.values());

		Agent first = agents.get(0);
		Agent previous = first;
		for (int i = 1; i < agents.size() - 1; i++) {
			Agent agent = agents.get(i);
			agent.setInput(previous.getAgentId());
			previous.setOutput(agent.getAgentId());

			previous = agent;
		}
		Agent last = agents.get(agents.size() - 1);
		last.setOutput(first.getAgentId());
		last.setInput(previous.getAgentId());
		first.setInput(last.getAgentId());
		previous.setOutput(last.getAgentId());
	}

	/**
	 * Generates all the robots and transports with their roles.
	 */
	private void generateRobotsAndTransports() {
		int transportId = 1;

		List<TaskConf> tasks = config.getTasks();
		for (TaskConf taskConf : tasks) {
			String taskId = String.valueOf(taskConf.getId());

			Transport producer = new Transport();
			producer.setAgentId("Producer" + taskConf.getId());
			Role prodRole = new Role();
			Condition prodPrecon = new Condition();
			prodPrecon.setTaskId(taskId);
			Condition prodPostcon = new Condition();
			prodPostcon.setTaskId(taskId);
			prodRole.setPrecondition(prodPrecon);
			prodRole.setPostcondition(prodPostcon);
			producer.getRoles().add(prodRole);

			List<Capability> preState = new ArrayList<Capability>();
			List<Capability> postState = new ArrayList<Capability>();

			Transport previousTransport = producer;
			Robot lastRobot = null;
			Condition lastPostcon = null;
			if (taskConf.getNoSteps() != null) {
				for (int i = 1; i <= taskConf.getNoSteps(); i++) {
					List<Capability> capPool = generateCaps(taskConf.getNoCaps());

					postState.add(capPool.get((i - 1) % capPool.size()));
					String robotId = "GRobot" + ++robotCount;
					Robot robot = new Robot();
					robot.setAgentId(robotId);
					robots.put(robotId, robot);
					Capability cap = capPool.get((i - 1) % capPool.size());
					if (!robot.getCapabilities().contains(cap)) {
						robot.getCapabilities().add(cap);
					}
					Role robotRole = new Role();
					robotRole.setCapability(cap);
					Condition robotPrecon = new Condition();
					robotPrecon.setTaskId(taskId);
					robotPrecon.getState().addAll(preState);
					robotPrecon.setTargetAgent(previousTransport.getAgentId());
					Condition robotPostcon = new Condition();
					robotPostcon.setTaskId(taskId);
					robotPostcon.getState().addAll(postState);
					robotRole.setPrecondition(robotPrecon);
					robotRole.setPostcondition(robotPostcon);
					robot.getRoles().add(robotRole);

					lastRobot = robot;
					lastPostcon = robotPostcon;

					previousTransport.getRoles().get(0).getPostcondition().setTargetAgent(robot.getAgentId());

					if (i < taskConf.getNoSteps()) {
						Transport transport = new Transport();
						transport.setAgentId("Transport" + transportId++);
						Role transportRole = new Role();
						Condition transportPrecon = new Condition();
						transportPrecon.setTaskId(taskId);
						transportPrecon.getState().addAll(postState);
						transportPrecon.setTargetAgent(robot.getAgentId());
						Condition transportPostcon = new Condition();
						transportPostcon.setTaskId(taskId);
						transportPostcon.getState().addAll(postState);
						transportRole.setPrecondition(transportPrecon);
						transportRole.setPostcondition(transportPostcon);
						transport.getRoles().add(transportRole);

						transports.put(transport.getAgentId(), transport);

						previousTransport = transport;

						robotPostcon.setTargetAgent(transport.getAgentId());
					}

					preState.clear();
					preState.addAll(postState);
				}
			} else {
				for (int i = 0; i < taskConf.getSteps().size(); i++) {
					TaskStep step = taskConf.getSteps().get(i);
					postState.add(new Capability(step.getAction()));

					Robot robot = null;
					if (robots.containsKey(step.getRobot())) {
						robot = robots.get(step.getRobot());
					} else {
						robot = new Robot();
						robot.setAgentId(step.getRobot());
						robots.put(robot.getAgentId(), robot);
					}
					Capability cap = new Capability(step.getAction());
					if (!robot.getCapabilities().contains(cap)) {
						robot.getCapabilities().add(cap);
					}
					Role robotRole = new Role();
					robotRole.setCapability(new Capability(step.getAction()));
					Condition robotPrecon = new Condition();
					robotPrecon.setTaskId(taskId);
					robotPrecon.getState().addAll(preState);
					robotPrecon.setTargetAgent(previousTransport.getAgentId());
					Condition robotPostcon = new Condition();
					robotPostcon.setTaskId(taskId);
					robotPostcon.getState().addAll(postState);
					robotRole.setPrecondition(robotPrecon);
					robotRole.setPostcondition(robotPostcon);
					robot.getRoles().add(robotRole);

					lastRobot = robot;
					lastPostcon = robotPostcon;

					previousTransport.getRoles().get(0).getPostcondition().setTargetAgent(robot.getAgentId());

					if (i < taskConf.getSteps().size() - 1) {
						Transport transport = new Transport();
						transport.setAgentId("Transport" + transportId++);
						Role transportRole = new Role();
						Condition transportPrecon = new Condition();
						transportPrecon.setTaskId(taskId);
						transportPrecon.getState().addAll(postState);
						transportPrecon.setTargetAgent(robot.getAgentId());
						Condition transportPostcon = new Condition();
						transportPostcon.setTaskId(taskId);
						transportPostcon.getState().addAll(postState);
						transportRole.setPrecondition(transportPrecon);
						transportRole.setPostcondition(transportPostcon);
						transport.getRoles().add(transportRole);

						transports.put(transport.getAgentId(), transport);

						previousTransport = transport;

						robotPostcon.setTargetAgent(transport.getAgentId());
					}

					preState.clear();
					preState.addAll(postState);
				}
			}

			Transport consumer = new Transport();
			consumer.setAgentId("Consumer" + taskConf.getId());
			Role consumRole = new Role();
			Condition consumPrecon = new Condition();
			consumPrecon.setTaskId(taskId);
			consumPrecon.setTargetAgent(lastRobot.getAgentId());
			consumPrecon.setState(postState);
			Condition consumPostcon = new Condition();
			consumPostcon.setTaskId(taskId);
			consumPostcon.setState(postState);
			consumRole.setPrecondition(consumPrecon);
			consumRole.setPostcondition(consumPostcon);
			consumer.getRoles().add(consumRole);

			lastPostcon.setTargetAgent(consumer.getAgentId());

			transports.put(producer.getAgentId(), producer);
			transports.put(consumer.getAgentId(), consumer);
		}
	}

	/**
	 * Returns a list with noCaps {@link Capability}s.
	 * 
	 * @param noCaps
	 *            The number of capabilities which should be created
	 * @return a list with noCaps {@link Capability}s
	 */
	private List<Capability> generateCaps(Integer noCaps) {
		List<Capability> caps = new ArrayList<Capability>();

		for (int i = 1; i <= noCaps; i++) {
			Capability cap = new Capability("Cap" + String.valueOf(i));
			caps.add(cap);
		}

		return caps;
	}

	/**
	 * Generates a random list of capabilities. The size of the list is between minCaps and maxCaps. The currentCaps are contained in the list in every case.
	 * 
	 * @param currentCaps
	 *            The capabilities that must be contained in the list.
	 * @param minCaps
	 *            The minimum amount of capabilities.
	 * @param maxCaps
	 *            The maximum amount of capabilities.
	 * @return The list of capabilities.
	 */
	private List<Capability> generateAgentCapabilities(List<Capability> currentCaps, int minCaps, int maxCaps) {
		List<Capability> caps = new ArrayList<Capability>();
		List<Capability> available = Capability.createList(new ArrayList<String>(Arrays.asList(actions)));
		int count = getRandom(minCaps, maxCaps);

		if (count >= available.size()) {
			caps.addAll(available);
			if (!caps.containsAll(currentCaps)) {
				throw new IllegalStateException("The current capabilities were not found in the list of available capabilities");
			}
		} else {

			caps.addAll(currentCaps);
			available.removeAll(currentCaps);
			while (caps.size() < count && available.size() > 0) {
				caps.add(available.remove(randomizer.nextInt(available.size())));
			}
		}

		for (Capability cap : caps) {
			putToCapCounter(cap);
		}

		return caps;
	}

	/**
	 * Generates a random number between min (inclusive) and max (inclusive).
	 * 
	 * @param min
	 *            The minimal number.
	 * @param max
	 *            The maximal number.
	 * @return A random number between min and max.
	 */
	private int getRandom(int min, int max) {
		if (min == max)
			return min;

		return min + randomizer.nextInt(max + 1 - min);
	}

	/**
	 * Generates the list of tasks for the configuration.
	 * 
	 * @return The list of tasks.
	 */
	private List<Task> generateTasks(Integer maxWPCount) {
		List<TaskConf> taskConfs = config.getTasks();
		List<Task> tasks = new ArrayList<Task>();
		Set<String> actions = new HashSet<String>();

		for (TaskConf taskConf : taskConfs) {
			Task task = new Task();

			if (taskConf.getNoSteps() != null) {
				for (int i = 1; i <= taskConf.getNoCaps(); i++) {
					task.addOperation("Cap" + String.valueOf(i));

					actions.add("Cap" + String.valueOf(i));
				}
			} else {
				List<TaskStep> steps = taskConf.getSteps();
				for (TaskStep taskStep : steps) {
					task.addOperation(taskStep.getAction());

					actions.add(taskStep.getAction());
				}
			}

			if (maxWPCount != null && maxWPCount > 0)
				task.setMaxWorkpieceCount(maxWPCount);

			task.setId(String.valueOf(taskConf.getId()));
			tasks.add(task);
		}

		this.actions = new String[actions.size()];
		this.actions = actions.toArray(this.actions);

		return tasks;
	}

	/**
	 * Counts how many agents contain the capability.
	 * 
	 * @param capability
	 */
	private void putToCapCounter(Capability capability) {
		Integer counter = capCounter.get(capability);
		if (counter == null) {
			counter = 0;
		}
		capCounter.put(capability, ++counter);
	}

	public static void main(String[] args) {
		String input = null;
		String output = null;
		for (int i = 0; i < args.length; i++) {
			if ("-input".equalsIgnoreCase(args[i]) && ++i < args.length) {
				input = args[i];
			} else if ("-output".equalsIgnoreCase(args[i]) && ++i < args.length) {
				output = args[i];
			}
		}

		SimulationGenerator sg = new SimulationGenerator(input);
		sg.saveProductionLineConfiguration(output);
	}
}
