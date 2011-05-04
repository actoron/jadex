/**
 * 
 */
package haw.mmlab.production_line.strategies;

import haw.mmlab.production_line.configuration.Buffer;
import haw.mmlab.production_line.configuration.Role;
import haw.mmlab.production_line.domain.HelpRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains all the agent's data which is important to evaluate an {@link HelpRequest}.
 * 
 * @author thomas
 */
public class AgentData {

	private List<Role> roles = null;
	private String[] capabilities = null;
	private Buffer buffer = null;

	/**
	 * Constructor
	 */
	public AgentData() {
		super();
		this.roles = new ArrayList<Role>();
	}

	/**
	 * @return the roles
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * @param roles
	 *            the roles to set
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	/**
	 * @param capabilities
	 *            the capabilities to set
	 */
	public void setCapabilities(String[] capabilities) {
		this.capabilities = capabilities;
	}

	/**
	 * @return the capabilities
	 */
	public String[] getCapabilities() {
		return capabilities;
	}

	/**
	 * @return the buffer
	 */
	public Buffer getBuffer() {
		return buffer;
	}

	/**
	 * @param buffer
	 *            the buffer to set
	 */
	public void setBuffer(Buffer buffer) {
		this.buffer = buffer;
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
		result = prime * result + ((buffer == null) ? 0 : buffer.hashCode());
		result = prime * result + Arrays.hashCode(capabilities);
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
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
		AgentData other = (AgentData) obj;
		if (buffer == null) {
			if (other.buffer != null)
				return false;
		} else if (!buffer.equals(other.buffer))
			return false;
		if (!Arrays.equals(capabilities, other.capabilities))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
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
		return "AgentData [" + (buffer != null ? "buffer=" + buffer + ", " : "")
				+ (capabilities != null ? "capabilities=" + Arrays.asList(capabilities).subList(0, Math.min(capabilities.length, maxLen)) + ", " : "")
				+ (roles != null ? "roles=" + roles.subList(0, Math.min(roles.size(), maxLen)) : "") + "]";
	}
}