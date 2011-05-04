/**
 * 
 */
package haw.mmlab.production_line.configuration.export;

import haw.mmlab.production_line.configuration.Capability;
import haw.mmlab.production_line.configuration.Condition;
import haw.mmlab.production_line.configuration.Task;

import java.util.List;

/**
 * A helper class for the dot export of the resource flow graph.
 * 
 * @author thomas
 */
public class SimpleAgent {

	public static final String TYPE_ROBOT = "robot";
	public static final String TYPE_TRANSPORT = "transport";

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	private String predecessor = null;
	private String successor = null;
	private String agentId = null;
	private String taskId = null;
	private String type = null;

	/**
	 * @return the predecessor
	 */
	public String getPredecessor() {
		return predecessor;
	}

	/**
	 * @param predecessor
	 *            the predecessor to set
	 */
	public void setPredecessor(String predecessor) {
		this.predecessor = predecessor;
	}

	/**
	 * @return the successor
	 */
	public String getSuccessor() {
		return successor;
	}

	/**
	 * @param successor
	 *            the successor to set
	 */
	public void setSuccessor(String successor) {
		this.successor = successor;
	}

	/**
	 * @return the agentId
	 */
	public String getAgentId() {
		return agentId;
	}

	/**
	 * @param agentId
	 *            the agentId to set
	 */
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	/**
	 * @return the taskId
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agentId == null) ? 0 : agentId.hashCode());
		result = prime * result + ((predecessor == null) ? 0 : predecessor.hashCode());
		result = prime * result + ((successor == null) ? 0 : successor.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		SimpleAgent other = (SimpleAgent) obj;
		if (agentId == null) {
			if (other.agentId != null)
				return false;
		} else if (!agentId.equals(other.agentId))
			return false;
		if (predecessor == null) {
			if (other.predecessor != null)
				return false;
		} else if (!predecessor.equals(other.predecessor))
			return false;
		if (successor == null) {
			if (other.successor != null)
				return false;
		} else if (!successor.equals(other.successor))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
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
		return "SimpleAgent [" + (agentId != null ? "agentId=" + agentId + ", " : "")
				+ (predecessor != null ? "predecessor=" + predecessor + ", " : "")
				+ (successor != null ? "successor=" + successor + ", " : "")
				+ (taskId != null ? "taskId=" + taskId + ", " : "") + (type != null ? "type=" + type : "") + "]";
	}

	/**
	 * Adds the number of already applied operations from the given
	 * {@link Condition} to the if of the given {@link Task}.
	 * 
	 * @param condition
	 *            the given {@link Condition}
	 * @param task
	 *            the given {@link Task}
	 */
	public void setTaskId(Condition condition, Task task) {
		List<Capability> states = condition.getState();

		if (states == null || states.isEmpty()) {
			this.setTaskId(task.getId() + "-0");
		} else {
			this.setTaskId(task.getId() + "-" + states.size());
		}
	}
}