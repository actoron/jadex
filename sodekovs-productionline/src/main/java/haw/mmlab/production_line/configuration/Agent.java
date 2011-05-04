package haw.mmlab.production_line.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

/**
 * An agent contains a {@link List} of {@link Role}s the agent currently
 * applies, a {@link List} of {@link Capability} the agent is able to apply and
 * an agentId.
 * 
 * @author thomas
 */
public class Agent {

	private List<Role> roles = new ArrayList<Role>();

	private List<Capability> capabilities = new ArrayList<Capability>();

	private String agentId = null;

	private String input = null;

	private String output = null;

	/**
	 * @return the roles
	 */
	@XmlElementWrapper(name = "roles")
	@XmlElement(name = "role")
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
	 * @return the capabilities
	 */
	@XmlElementWrapper(name = "capabilities")
	@XmlElement(name = "capability")
	public List<Capability> getCapabilities() {
		return capabilities;
	}

	/**
	 * @param capabilities
	 *            the capabilities to set
	 */
	public void setCapabilities(List<Capability> capabilities) {
		this.capabilities = capabilities;
	}

	@XmlTransient
	public List<String> getCapabilityStrings() {
		List<String> result = new ArrayList<String>();
		for (Capability cap : this.capabilities) {
			result.add(cap.getId());
		}
		return result;
	}

	/**
	 * @return the agentId
	 */
	@XmlAttribute(name = "id")
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
	 * @return the input
	 */
	@XmlElement(name = "input")
	public String getInput() {
		return input;
	}

	/**
	 * @param input
	 *            the input to set
	 */
	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * @return the output
	 */
	@XmlElement(name = "output")
	public String getOutput() {
		return output;
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
		result = prime * result + ((capabilities == null) ? 0 : capabilities.hashCode());
		result = prime * result + ((input == null) ? 0 : input.hashCode());
		result = prime * result + ((output == null) ? 0 : output.hashCode());
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
		Agent other = (Agent) obj;
		if (agentId == null) {
			if (other.agentId != null)
				return false;
		} else if (!agentId.equals(other.agentId))
			return false;
		if (capabilities == null) {
			if (other.capabilities != null)
				return false;
		} else if (!capabilities.equals(other.capabilities))
			return false;
		if (input == null) {
			if (other.input != null)
				return false;
		} else if (!input.equals(other.input))
			return false;
		if (output == null) {
			if (other.output != null)
				return false;
		} else if (!output.equals(other.output))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		return true;
	}

	/**
	 * @param output
	 *            the output to set
	 */
	public void setOutput(String output) {
		this.output = output;
	}

	public void addRole(Role role) {
		this.roles.add(role);
	}

	public void addCapability(Capability capability) {
		this.capabilities.add(capability);
	}

	public void addCapability(String capability) {
		this.capabilities.add(new Capability(capability));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Agent ["
				+ (agentId != null ? "agentId=" + agentId + ", " : "")
				+ (capabilities != null ? "capabilities="
						+ capabilities.subList(0, Math.min(capabilities.size(), maxLen)) + ", " : "")
				+ (input != null ? "input=" + input + ", " : "") + (output != null ? "output=" + output + ", " : "")
				+ (roles != null ? "roles=" + roles.subList(0, Math.min(roles.size(), maxLen)) : "") + "]";
	}
}