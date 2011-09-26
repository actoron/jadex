package haw.mmlab.production_line.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	/** The robots (i.e. production machines) that are available. */
	private List<Robot> robots = new ArrayList<Robot>();

	/** The connections between robots. */
	private List<Transport> transports = new ArrayList<Transport>();

	/** The tasks. */
	private List<Task> tasks = new ArrayList<Task>();

	/** The count of runs */
	private int runCount = 0;

	/** The logging interval for the timelord agent */
	private int timelordInterval = 0;

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

	@XmlElement(name = "timelord_interval")
	public int getTimelordInterval() {
		return timelordInterval;
	}

	public void setTimelordInterval(int timelordInterval) {
		this.timelordInterval = timelordInterval;
	}
}