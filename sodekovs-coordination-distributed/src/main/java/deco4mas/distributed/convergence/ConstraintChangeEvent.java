/**
 * 
 */
package deco4mas.distributed.convergence;

/**
 * Constraint change event containing information about a changed constraint value.
 * 
 * @author Thomas Preisler
 */
public class ConstraintChangeEvent {

	private String constraint = null;

	private Object value = null;

	private ConvergenceMicroAgent source = null;

	/**
	 * Default Constructor.
	 * 
	 * @param constraint
	 *            the given constraint
	 * @param value
	 *            the given value
	 * @param source
	 *            the given source
	 */
	public ConstraintChangeEvent(String constraint, Object value, ConvergenceMicroAgent source) {
		this.constraint = constraint;
		this.value = value;
		this.source = source;
	}

	/**
	 * @return the constraint
	 */
	public String getConstraint() {
		return constraint;
	}

	/**
	 * @param constraint
	 *            the constraint to set
	 */
	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the source
	 */
	public ConvergenceMicroAgent getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(ConvergenceMicroAgent source) {
		this.source = source;
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
		result = prime * result + ((constraint == null) ? 0 : constraint.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		ConstraintChangeEvent other = (ConstraintChangeEvent) obj;
		if (constraint == null) {
			if (other.constraint != null)
				return false;
		} else if (!constraint.equals(other.constraint))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
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
		return "ConstraintChangeEvent [constraint=" + constraint + ", value=" + value + ", source=" + source + "]";
	}
}