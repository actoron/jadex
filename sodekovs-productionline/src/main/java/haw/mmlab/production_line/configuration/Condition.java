package haw.mmlab.production_line.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * A conditions contains a target agent, a {@link List} of {@link Capability}
 * which should be applied and belongs to a task id.
 * 
 * @author thomas
 */
public class Condition {

	private String targetAgent = null;

	private List<Capability> state = new ArrayList<Capability>();

	private String taskId = null;

	/**
	 * @return the targetAgent
	 */
	@XmlElement(name = "target_agent")
	public String getTargetAgent() {
		return targetAgent;
	}

	/**
	 * @param targetAgent
	 *            the targetAgent to set
	 */
	public void setTargetAgent(String targetAgent) {
		this.targetAgent = targetAgent;
	}

	/**
	 * @return the state
	 */
	@XmlElementWrapper(name = "states")
	@XmlElement(name = "state")
	public List<Capability> getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(List<Capability> state) {
		this.state = state;
	}

	/**
	 * @return the taskId
	 */
	@XmlElement(name = "task_id")
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId
	 *            the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void createState(String[] actions) {
		List<Capability> state = new ArrayList<Capability>();
		if (actions != null) {
			for (String action : actions) {
				Capability cap = new Capability(action);

				state.add(cap);
			}
		}

		this.state = state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((targetAgent == null) ? 0 : targetAgent.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Condition other = (Condition) obj;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (targetAgent == null) {
			if (other.targetAgent != null)
				return false;
		} else if (!targetAgent.equals(other.targetAgent))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Condition ["
				+ (state != null ? "state=" + state.subList(0, Math.min(state.size(), maxLen)) + ", " : "")
				+ (targetAgent != null ? "targetAgent=" + targetAgent + ", " : "")
				+ (taskId != null ? "taskId=" + taskId : "") + "]";
	}

	/**
	 * Sets the state to the given {@link List} of {@link Capability} and the
	 * one given {@link Capability}.
	 * 
	 * @param state
	 *            - the given {@link List} of {@link Capability}
	 * @param capability
	 *            - the one given {@link Capability}
	 */
	public void setState(List<Capability> state, Capability capability) {
		this.state = new ArrayList<Capability>();
		this.state.addAll(state);
		this.state.add(capability);
	}
}