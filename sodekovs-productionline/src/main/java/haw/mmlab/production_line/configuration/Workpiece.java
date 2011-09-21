/**
 * 
 */
package haw.mmlab.production_line.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The work piece that is processed by the robots / transporters. Robots add operations to the work piece.
 * 
 * @author Peter
 */
public class Workpiece implements Serializable {
	private static final long serialVersionUID = -7700269818620913241L;

	private int id = -1;

	private List<Capability> operations = null;

	private Task task = null;

	private int opCount;

	public Workpiece() {
		operations = new ArrayList<Capability>();
		task = new Task();
		opCount = 0;
	}

	public Workpiece(int id) {
		this();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void addOperation(Capability operation) {
		if (operation == null) {
			return;
		}

		if (opCount >= task.getOperations().size()) {
			throw new IllegalStateException("could not perform another action. All action are already applied to the work piece.");
		}

		if (opCount < 0) {
			opCount = 0;
		}

		Capability nextOp = task.getOperations().get(opCount);

		if (nextOp != null && operation.equals(nextOp)) {
			operations.add(operation);
			opCount++;
		} else {
			throw new IllegalArgumentException("Operation (" + operation + ") not allowed. Expected operation: " + nextOp);
		}
	}

	/**
	 * @return the operations
	 */
	public List<Capability> getOperations() {
		return operations;
	}

	/**
	 * @param operations
	 *            the operations to set
	 */
	public void setOperations(List<Capability> operations) {
		this.operations = operations;
	}

	public int getOpCount() {
		return opCount;
	}

	public void setOpCount(int opCount) {
		this.opCount = opCount;
	}

	/**
	 * @return the task
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * @param task
	 *            the task to set
	 */
	public void setTask(Task task) {
		this.task = task;
	}

	public boolean isFinished() {
		return operations.equals(task.getOperations());
	}

	public boolean isNextOperation(Capability capability) {
		if (capability == null) {
			return true;
		}

		Capability nextOp = task.getOperations().get(opCount < 0 ? 0 : opCount);
		if (nextOp != null && nextOp.equals(capability)) {
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Workpiece [id=" + id + ", opCount=" + opCount /*
															 * + ", " + (operations != null ? "operations=" + operations.subList(0, Math.min(operations.size(), maxLen)) + ", " : "") + (task != null ?
															 * "task=" + task : "")
															 */+ "]";
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
		result = prime * result + id;
		result = prime * result + opCount;
		result = prime * result + ((operations == null) ? 0 : operations.hashCode());
		result = prime * result + ((task == null) ? 0 : task.hashCode());
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
		Workpiece other = (Workpiece) obj;
		if (id != other.id)
			return false;
		if (opCount != other.opCount)
			return false;
		if (operations == null) {
			if (other.operations != null)
				return false;
		} else if (!operations.equals(other.operations))
			return false;
		if (task == null) {
			if (other.task != null)
				return false;
		} else if (!task.equals(other.task))
			return false;
		return true;
	}
}