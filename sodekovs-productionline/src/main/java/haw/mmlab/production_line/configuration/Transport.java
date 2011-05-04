package haw.mmlab.production_line.configuration;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The connection between robots.
 * 
 * @author thomas
 */
@XmlRootElement(name = "transport")
public class Transport extends Agent {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Transport ["
				+ (getAgentId() != null ? "getAgentId()=" + getAgentId() + ", " : "")
				+ (getCapabilities() != null ? "getCapabilities()="
						+ getCapabilities().subList(0, Math.min(getCapabilities().size(), maxLen)) + ", " : "")
				+ (getRoles() != null ? "getRoles()=" + getRoles().subList(0, Math.min(getRoles().size(), maxLen)) : "")
				+ "]";
	}
}