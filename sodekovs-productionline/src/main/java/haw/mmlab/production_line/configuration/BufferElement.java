/**
 * 
 */
package haw.mmlab.production_line.configuration;

/**
 * A BufferElement composed of a {@link Workpiece} and the according
 * {@link Role} which has processed the {@link Workpiece}.
 * 
 * @author thomas
 */
public class BufferElement {

	private Workpiece workpiece = null;

	private Role role = null;

	public BufferElement() {
		this(null, null);
	}

	public BufferElement(Workpiece workpiece, Role role) {
		this.workpiece = workpiece;
		this.role = role;
	}

	/**
	 * @return the workpiece
	 */
	public Workpiece getWorkpiece() {
		return workpiece;
	}

	/**
	 * @param workpiece
	 *            the workpiece to set
	 */
	public void setWorkpiece(Workpiece workpiece) {
		this.workpiece = workpiece;
	}

	/**
	 * @return the role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(Role role) {
		this.role = role;
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
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((workpiece == null) ? 0 : workpiece.hashCode());
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
		BufferElement other = (BufferElement) obj;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (workpiece == null) {
			if (other.workpiece != null)
				return false;
		} else if (!workpiece.equals(other.workpiece))
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
		return "BufferElement [" + (role != null ? "role=" + role + ", " : "")
				+ (workpiece != null ? "workpiece=" + workpiece : "") + "]";
	}
}