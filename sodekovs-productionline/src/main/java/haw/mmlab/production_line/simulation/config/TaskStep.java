/**
 * 
 */
package haw.mmlab.production_line.simulation.config;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Container for a step in a task. A step contains the executing robot and the
 * action the robot is executing.
 * 
 * @author thomas
 */
public class TaskStep {

	private String robot = null;
	private String action = null;

	/**
	 * @return the robot
	 */
	@XmlAttribute(name = "robot")
	public String getRobot() {
		return robot;
	}

	/**
	 * @param robot
	 *            the robot to set
	 */
	public void setRobot(String robot) {
		this.robot = robot;
	}

	/**
	 * @return the action
	 */
	@XmlAttribute(name = "action")
	public String getAction() {
		return action;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
}