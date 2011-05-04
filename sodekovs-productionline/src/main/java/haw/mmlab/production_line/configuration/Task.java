package haw.mmlab.production_line.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * A Task contains a {@link List} of {@link Capability} and an identifier.
 * 
 * @author thomas
 */
public class Task {

	private String id = null;

	private List<Capability> operations = new ArrayList<Capability>();

	/** The maximum amount of work pieces for this task */
	private int maxWorkpieceCount = 0;

	/**
	 * @return the id
	 */
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the operations
	 */
	@XmlElementWrapper(name = "operations")
	@XmlElement(name = "operation")
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

	/**
	 * @return the maxWorkpiceCount
	 */
	@XmlAttribute(name = "max_workpiece_count")
	public int getMaxWorkpieceCount() {
		return maxWorkpieceCount;
	}

	/**
	 * @param maxWorkpiceCount
	 *            the maxWorkpiceCount to set
	 */
	public void setMaxWorkpieceCount(int maxWorkpiceCount) {
		this.maxWorkpieceCount = maxWorkpiceCount;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + maxWorkpieceCount;
		result = prime * result + ((operations == null) ? 0 : operations.hashCode());
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
		Task other = (Task) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (maxWorkpieceCount != other.maxWorkpieceCount)
			return false;
		if (operations == null) {
			if (other.operations != null)
				return false;
		} else if (!operations.equals(other.operations))
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
		return "Task [id=" + id + ", maxWorkpieceCount=" + maxWorkpieceCount + ", operations=" + operations + "]";
	}

	public void addOperation(String cap) {
		Capability capability = new Capability();
		capability.setId(cap);

		this.operations.add(capability);
	}

	/**
	 * Return all operations in this task until (exclusive) the given index.
	 * 
	 * @param i
	 *            - the given index.
	 * @return all operations in this task until (exclusive) the given index
	 */
	public List<Capability> getOperations(int i) {
		List<Capability> result = new ArrayList<Capability>();

		for (int j = 0; j < i; j++) {
			result.add(operations.get(j));
		}

		return result;
	}
}