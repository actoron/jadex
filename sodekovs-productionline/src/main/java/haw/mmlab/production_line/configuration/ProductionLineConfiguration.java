package haw.mmlab.production_line.configuration;

import haw.mmlab.production_line.common.AgentConstants;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The production line simulation configuration.<br>
 * <br>
 * This data structure represents the layout of the production line.
 * 
 * @author thomas
 */
@XmlRootElement(name = "production_line_setup")
public class ProductionLineConfiguration {

	/** The file ending for the configuration files */
	private static final String FILE_ENDING = ".conf.xml";

	/** The robots (i.e. production machines) that are available. */
	private List<Robot> robots = new ArrayList<Robot>();

	/** The connections between robots. */
	private List<Transport> transports = new ArrayList<Transport>();

	/** The tasks. */
	private List<Task> tasks = new ArrayList<Task>();

	/** The count of runs */
	private int runCount = 0;

	/** The timeout within workpieces must arrive */
	private int errorTimeout = 0;

	@XmlElementWrapper(name = "robots")
	@XmlElement(name = "robot")
	public List<Robot> getRobots() {
		return robots;
	}

	public void setRobots(List<Robot> robots) {
		this.robots = robots;
	}

	public void addRobot(Robot r) {
		this.robots.add(r);
	}

	public void addRobots(Collection<Robot> robots) {
		this.robots.addAll(robots);
	}

	@XmlElementWrapper(name = "transports")
	@XmlElement(name = "transport")
	public List<Transport> getTransports() {
		return transports;
	}

	public void setTransports(List<Transport> transports) {
		this.transports = transports;
	}

	public void addTransport(Transport t) {
		this.transports.add(t);
	}

	public void addTransports(Collection<Transport> transports) {
		this.transports.addAll(transports);
	}

	@XmlElementWrapper(name = "tasks")
	@XmlElement(name = "task")
	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public void addTast(Task t) {
		this.tasks.add(t);
	}

	@XmlElement(name = "run_count")
	public int getRunCount() {
		return runCount;
	}

	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}

	@XmlElement(name = "error_condition_timeout")
	public int getErrorTimeout() {
		return errorTimeout;
	}

	public void setErrorTimeout(int errorTimeout) {
		this.errorTimeout = errorTimeout;
	}

	public static void main(String[] args) {
		System.out.println("Welcome to the future of self-adaptiv production lines!");
		System.out.println("You have the following possibilities to create a start configuration:");
		System.out
				.println("\t- A concrete example with three robots and four transporters, each robot has one capability (press c).");

		System.out.println("Your choice:");

		Scanner s = new Scanner(System.in);
		String input = s.nextLine();

		System.out.println("Please insert now the desired file name (note: " + FILE_ENDING
				+ " will be added automatically:");
		String fileName = s.nextLine();
		fileName = fileName + FILE_ENDING;

		if (input.equalsIgnoreCase("c")) {
			createConcreteExample(fileName);
		} else {
			System.out.println("You failed a very simple task, maybe you should try again.");
		}

		System.out.println("The End");
	}

	/**
	 * Creates a concrete example with three robots and four transporters, each
	 * robot has one capability.
	 * 
	 * @param fileName
	 *            - the name of the file which should be written
	 */
	private static void createConcreteExample(String fileName) {
		System.out.println("Creating default example:");

		ProductionLineConfiguration plc = new ProductionLineConfiguration();

		// capabilities:
		Capability drill = new Capability("drill");
		Capability insert = new Capability("insert");
		Capability tighten = new Capability("tighten");

		// task:
		Task task = new Task();
		task.setId("task1");
		List<Capability> operations = new ArrayList<Capability>();
		operations.add(drill);
		operations.add(insert);
		operations.add(tighten);
		task.setOperations(operations);

		plc.addTast(task);

		// robots:
		Robot robot1 = new Robot();
		robot1.setAgentId("Robot1");
		robot1.setCapabilities(operations);
		Role role1 = new Role();
		role1.setCapability(drill);
		Condition pre1 = new Condition();
		pre1.setTargetAgent(AgentConstants.PRODUCER);
		String[] preState1 = null;
		pre1.createState(preState1);
		Condition post1 = new Condition();
		post1.setTargetAgent("Transport2");
		String[] postState1 = { "drill" };
		post1.createState(postState1);
		role1.setPrecondition(pre1);
		role1.setPostcondition(post1);
		role1.setTaskId("task1");
		robot1.addRole(role1);
		robot1.setInput(AgentConstants.PRODUCER);
		robot1.setOutput("Transport2");

		Robot robot2 = new Robot();
		robot2.setAgentId("Robot2");
		robot2.setCapabilities(operations);
		Role role2 = new Role();
		role2.setCapability(insert);
		Condition pre2 = new Condition();
		pre2.setTargetAgent("Transport2");
		String[] preState2 = { "drill" };
		pre2.createState(preState2);
		Condition post2 = new Condition();
		post2.setTargetAgent("Transport3");
		String[] postState2 = { "drill", "insert" };
		post2.createState(postState2);
		role2.setPrecondition(pre2);
		role2.setPostcondition(post2);
		role2.setTaskId("task1");
		robot2.addRole(role2);
		robot2.setInput("Transport2");
		robot2.setOutput("Transport3");

		Robot robot3 = new Robot();
		robot3.setAgentId("Robot3");
		robot3.setCapabilities(operations);
		Role role3 = new Role();
		role3.setCapability(tighten);
		Condition pre3 = new Condition();
		pre3.setTargetAgent("Transport3");
		String[] preState3 = { "drill", "insert" };
		pre3.createState(preState3);
		Condition post3 = new Condition();
		post3.setTargetAgent(AgentConstants.CONSUMER);
		String[] postState3 = { "drill", "insert", "tighten" };
		post3.createState(postState3);
		role3.setPrecondition(pre3);
		role3.setPostcondition(post3);
		role3.setTaskId("task1");
		robot3.addRole(role3);
		robot3.setInput("Transport3");
		robot3.setOutput(AgentConstants.CONSUMER);

		plc.addRobot(robot1);
		plc.addRobot(robot2);
		plc.addRobot(robot3);

		// transports:
		Transport cart1 = new Transport();
		cart1.setAgentId(AgentConstants.PRODUCER);
		cart1.setCapabilities(null);
		Role role4 = new Role();
		role4.setCapability(null);
		Condition pre4 = new Condition();
		pre4.setTargetAgent(null);
		String[] preState4 = null;
		pre4.createState(preState4);
		Condition post4 = new Condition();
		post4.setTargetAgent("Robot1");
		String[] postState4 = null;
		post4.createState(postState4);
		role4.setPrecondition(pre4);
		role4.setPostcondition(post4);
		role4.setTaskId("task1");
		cart1.addRole(role4);
		cart1.setInput(AgentConstants.CONSUMER);
		cart1.setOutput("Robot1");

		Transport cart2 = new Transport();
		cart2.setAgentId("Transport2");
		cart2.setCapabilities(null);
		Role role5 = new Role();
		role5.setCapability(null);
		Condition pre5 = new Condition();
		pre5.setTargetAgent("Robot1");
		String[] preState5 = { "drill" };
		pre5.createState(preState5);
		Condition post5 = new Condition();
		post5.setTargetAgent("Robot2");
		String[] postState5 = { "drill" };
		post5.createState(postState5);
		role5.setPrecondition(pre5);
		role5.setPostcondition(post5);
		role5.setTaskId("task1");
		cart2.addRole(role5);
		cart1.setInput("Robot1");
		cart1.setOutput("Robot2");

		Transport cart3 = new Transport();
		cart3.setAgentId("Transport3");
		cart3.setCapabilities(null);
		Role role6 = new Role();
		role6.setCapability(null);
		Condition pre6 = new Condition();
		pre6.setTargetAgent("Robot2");
		String[] preState6 = { "drill", "insert" };
		pre6.createState(preState6);
		Condition post6 = new Condition();
		post6.setTargetAgent("Robot3");
		String[] postState6 = { "drill", "insert" };
		post6.createState(postState6);
		role6.setPrecondition(pre6);
		role6.setPostcondition(post6);
		role6.setTaskId("task1");
		cart3.addRole(role6);
		cart1.setInput("Robot2");
		cart1.setOutput("Robot3");

		Transport cart4 = new Transport();
		cart4.setAgentId(AgentConstants.CONSUMER);
		cart4.setCapabilities(null);
		Role role7 = new Role();
		role7.setCapability(null);
		Condition pre7 = new Condition();
		pre7.setTargetAgent("Robot3");
		String[] preState7 = { "drill", "insert", "tighten" };
		pre7.createState(preState7);
		Condition post7 = new Condition();
		post7.setTargetAgent(null);
		String[] postState7 = { "drill", "insert", "tighten" };
		post7.createState(postState7);
		role7.setPrecondition(pre7);
		role7.setPostcondition(post7);
		role7.setTaskId("task1");
		cart4.addRole(role7);
		cart1.setInput("Robot3");
		cart1.setOutput(AgentConstants.PRODUCER);

		plc.addTransport(cart1);
		plc.addTransport(cart2);
		plc.addTransport(cart3);
		plc.addTransport(cart4);

		try {
			deco4mas.util.xml.XmlUtil.saveAsXML(plc, fileName);
			System.out.println(fileName + " written");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}